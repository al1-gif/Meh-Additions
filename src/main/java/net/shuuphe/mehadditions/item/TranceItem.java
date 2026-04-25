package net.shuuphe.mehadditions.item;

import com.shuuphe.mehorigins.race.RaceManager;
import com.shuuphe.mehorigins.race.impl.RevenantRace;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.trance.TranceDataManager;
import net.shuuphe.mehadditions.trance.TranceMobRegistry;

import java.util.*;

public class TranceItem extends Item {

    private static final int    COOLDOWN_DEFAULT  = 200;
    private static final int    COOLDOWN_REVENANT = 80;
    private static final double CAPTURE_RANGE     = 5.0;

    public static final Map<UUID, Integer>       COOLDOWNS      = new HashMap<>();
    public static final Map<UUID, List<Integer>> SUMMONS        = new HashMap<>();
    public static final Set<Integer>             ALL_SUMMON_IDS = new HashSet<>();
    public static final Map<Integer, UUID>       SUMMON_OWNERS  = new HashMap<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            COOLDOWNS.entrySet().removeIf(e -> {
                int next = e.getValue() - 1;
                if (next <= 0) return true;
                e.setValue(next);
                return false;
            });

            for (Map.Entry<UUID, List<Integer>> entry : SUMMONS.entrySet()) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
                if (player == null) continue;

                ServerWorld world = (ServerWorld) player.getEntityWorld();

                Iterator<Integer> it = entry.getValue().iterator();
                while (it.hasNext()) {
                    int id = it.next();
                    var entity = world.getEntityById(id);
                    if (entity == null || !entity.isAlive()) {
                        ALL_SUMMON_IDS.remove(id);
                        SUMMON_OWNERS.remove(id);
                        it.remove();
                        continue;
                    }
                    if (entity instanceof LivingEntity le && le.isOnFire()) {
                        le.setFireTicks(0);
                    }

                    if (entity instanceof MobEntity mob) {
                        LivingEntity target = mob.getTarget();
                        if (target != null && (!target.isAlive()
                                || target == player
                                || (ALL_SUMMON_IDS.contains(target.getId())
                                && Objects.equals(SUMMON_OWNERS.get(target.getId()), entry.getKey())))) {
                            mob.setTarget(null);
                            target = null;
                        }
                        if (target == null) {
                            double dist = mob.squaredDistanceTo(player);
                            if (dist > 225.0) {
                                mob.getNavigation().startMovingTo(player, 1.2);
                            } else if (dist < 9.0) {
                                mob.getNavigation().stop();
                            }
                        }
                    }
                }
            }
        });
    }

    public TranceItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) return ActionResult.PASS;
        if (!(user instanceof ServerPlayerEntity player)) return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        boolean isRevenant = RaceManager.getRace(player) instanceof RevenantRace;
        String mode = TranceDataManager.getMode(stack);

        if (player.isSneaking()) {
            TranceDataManager.toggleMode(stack);
            String newMode = TranceDataManager.getMode(stack);
            player.sendMessage(Text.literal("Staff: " +
                    (newMode.equals(TranceDataManager.MODE_CAPTURE) ? "§cCapture Mode" : "§aSummon Mode")), true);
            return ActionResult.PASS;
        }

        if (mode.equals(TranceDataManager.MODE_CAPTURE)) {
            handleCapture(player, stack, world, isRevenant);
        } else {
            handleSummon(player, stack, world, isRevenant);
        }

        return ActionResult.PASS;
    }

    private void handleCapture(ServerPlayerEntity player, ItemStack stack, World world, boolean isRevenant) {
        LivingEntity target = getTargetedEntity(player, world);
        if (target == null) return;

        boolean isOwnedByTrance = ALL_SUMMON_IDS.contains(target.getId())
                && Objects.equals(SUMMON_OWNERS.get(target.getId()), player.getUuid());

        if (!isOwnedByTrance && !TranceMobRegistry.isCapturable(target, isRevenant)) {
            player.sendMessage(Text.literal("§cThis mob cannot be captured" +
                    (TranceMobRegistry.isRevenantOnly(target) ? " by your race." : ".")), true);
            return;
        }

        int pts = TranceMobRegistry.getPoints(target);
        if (pts < 0) pts = 20;

        int capacity = isRevenant ? TranceMobRegistry.CAPACITY_REVENANT : TranceMobRegistry.CAPACITY_DEFAULT;
        int used = TranceDataManager.getUsedPoints(stack);

        if (!isOwnedByTrance && used + pts > capacity) {
            player.sendMessage(Text.literal("§cNot enough capacity! (" + used + "/" + capacity + ")"), true);
            return;
        }

        if (!isOwnedByTrance) {
            if (isRevenant) {
                int cost = TranceMobRegistry.getCaptureSpellCost(target);
                int sp = RevenantRace.getSpellPower(player);
                if (sp < cost) {
                    player.sendMessage(Text.literal("§cNot enough Spell Power! Need " + cost + "."), true);
                    return;
                }
                RevenantRace.setSpellPower(player, sp - cost);
            } else {
                int hunger = player.getHungerManager().getFoodLevel();
                if (hunger < 6) {
                    player.sendMessage(Text.literal("§cNot enough hunger!"), true);
                    return;
                }
                player.getHungerManager().setFoodLevel(hunger - 6);
            }
            TranceDataManager.setUsedPoints(stack, used + pts);
        }

        ServerWorld serverWorld = (ServerWorld) world;
        NbtCompound equipment = saveEquipment(target, serverWorld);

        String typeId = Registries.ENTITY_TYPE.getId(target.getType()).toString();
        TranceDataManager.addMobEntry(stack, typeId, equipment);

        if (isOwnedByTrance) {
            TranceDataManager.setUsedPoints(stack, used + pts);
            List<Integer> list = SUMMONS.get(player.getUuid());
            if (list != null) list.remove((Integer) target.getId());
            ALL_SUMMON_IDS.remove(target.getId());
            SUMMON_OWNERS.remove(target.getId());
        }

        serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                target.getX(), target.getY() + 0.5, target.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
        serverWorld.spawnParticles(ParticleTypes.SOUL,
                target.getX(), target.getY() + 0.5, target.getZ(), 10, 0.2, 0.3, 0.2, 0.03);

        target.discard();
    }

    private void handleSummon(ServerPlayerEntity player, ItemStack stack, World world, boolean isRevenant) {
        List<String> mobs = TranceDataManager.getStoredMobs(stack);
        if (mobs.isEmpty()) return;

        int cd = COOLDOWNS.getOrDefault(player.getUuid(), 0);
        if (cd > 0) {
            player.sendMessage(Text.literal("§cCooldown: §r" + (cd / 20) + "s"), true);
            return;
        }

        int selectedIndex = TranceDataManager.getSelected(stack);
        if (selectedIndex >= mobs.size()) {
            selectedIndex = 0;
            TranceDataManager.setSelected(stack, 0);
        }

        String typeId = mobs.get(selectedIndex);
        EntityType<?> type = Registries.ENTITY_TYPE.get(Identifier.of(typeId));
        if (type == null) {
            player.sendMessage(Text.literal("§cFailed to summon mob."), true);
            return;
        }

        NbtCompound savedEquipment = TranceDataManager.getMobEquipment(stack, selectedIndex);

        ServerWorld serverWorld = (ServerWorld) world;
        var entity = type.create(serverWorld, net.minecraft.entity.SpawnReason.MOB_SUMMONED);
        if (!(entity instanceof MobEntity mob)) {
            player.sendMessage(Text.literal("§cFailed to summon mob."), true);
            return;
        }

        mob.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), 0);
        mob.setPersistent();
        mob.clearGoals(g -> g instanceof WanderAroundGoal || g instanceof WanderAroundFarGoal);

        serverWorld.spawnEntity(mob);
        RevenantRace.removeSummonAutoTargeting(mob);

        restoreEquipment(mob, savedEquipment, serverWorld);

        if (mob instanceof WardenEntity warden) {
            warden.getBrain().doExclusively(Activity.IDLE);
        }

        SUMMONS.computeIfAbsent(player.getUuid(), k -> new ArrayList<>()).add(mob.getId());
        ALL_SUMMON_IDS.add(mob.getId());
        SUMMON_OWNERS.put(mob.getId(), player.getUuid());

        serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(), player.getY() + 0.5, player.getZ(), 20, 0.3, 0.5, 0.3, 0.05);
        serverWorld.spawnParticles(ParticleTypes.SOUL,
                player.getX(), player.getY() + 0.5, player.getZ(), 10, 0.2, 0.3, 0.2, 0.03);

        int pts = TranceMobRegistry.getPoints(mob);
        if (pts < 0) pts = 20;
        TranceDataManager.removeMobAt(stack, selectedIndex);
        TranceDataManager.setUsedPoints(stack, Math.max(0, TranceDataManager.getUsedPoints(stack) - pts));

        COOLDOWNS.put(player.getUuid(), isRevenant ? COOLDOWN_REVENANT : COOLDOWN_DEFAULT);
    }

    private static NbtCompound saveEquipment(LivingEntity entity, ServerWorld world) {
        NbtCompound nbt = new NbtCompound();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                ItemStack.CODEC.encodeStart(
                        world.getRegistryManager().getOps(NbtOps.INSTANCE), stack
                ).result().ifPresent(encoded -> nbt.put(slot.getName(), encoded));
            }
        }
        return nbt;
    }

    private static void restoreEquipment(MobEntity mob, NbtCompound equipment, ServerWorld world) {
        if (equipment == null || equipment.isEmpty()) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            String key = slot.getName();
            if (equipment.contains(key)) {
                ItemStack.CODEC.parse(
                        world.getRegistryManager().getOps(NbtOps.INSTANCE), equipment.get(key)
                ).result().ifPresent(stack -> mob.equipStack(slot, stack));
            }
        }
    }

    public static void onPlayerAttack(ServerPlayerEntity player, LivingEntity target) {
        if (ALL_SUMMON_IDS.contains(target.getId())
                && Objects.equals(SUMMON_OWNERS.get(target.getId()), player.getUuid())) return;

        List<Integer> list = SUMMONS.get(player.getUuid());
        if (list == null || list.isEmpty()) return;

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        for (int id : list) {
            var entity = world.getEntityById(id);
            if (entity instanceof MobEntity mob) mob.setTarget(target);
        }
    }

    public static void onPlayerHurt(ServerPlayerEntity player, LivingEntity attacker) {
        if (ALL_SUMMON_IDS.contains(attacker.getId())) return;
        onPlayerAttack(player, attacker);
    }

    public static void onPlayerDisconnect(ServerPlayerEntity player) {
        List<Integer> list = SUMMONS.remove(player.getUuid());
        if (list == null || list.isEmpty()) return;

        ServerWorld world = (ServerWorld) player.getEntityWorld();
        ItemStack tranceStack = findTranceStack(player);

        for (int id : list) {
            var entity = world.getEntityById(id);
            ALL_SUMMON_IDS.remove(id);
            SUMMON_OWNERS.remove(id);
            if (entity == null || !entity.isAlive()) continue;

            if (tranceStack != null && entity instanceof LivingEntity le) {
                String typeId = Registries.ENTITY_TYPE.getId(le.getType()).toString();
                int pts = TranceMobRegistry.getPoints(le);
                if (pts < 0) pts = 20;
                NbtCompound equipment = saveEquipment(le, world);
                TranceDataManager.addMobEntry(tranceStack, typeId, equipment);
                TranceDataManager.setUsedPoints(tranceStack,
                        TranceDataManager.getUsedPoints(tranceStack) + pts);
            }
            entity.discard();
        }

        COOLDOWNS.remove(player.getUuid());
    }

    private static ItemStack findTranceStack(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.getItem() instanceof TranceItem) return s;
        }
        return null;
    }

    private LivingEntity getTargetedEntity(ServerPlayerEntity player, World world) {
        Vec3d start = player.getEyePos();
        Vec3d look  = player.getRotationVector();
        Vec3d end   = start.add(look.multiply(CAPTURE_RANGE));

        Box searchBox = player.getBoundingBox().stretch(look.multiply(CAPTURE_RANGE)).expand(1.0);

        List<LivingEntity> candidates = world.getEntitiesByClass(
                LivingEntity.class, searchBox, e -> e != player && e.isAlive());

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (LivingEntity candidate : candidates) {
            Box entityBox = candidate.getBoundingBox().expand(0.3);
            Optional<Vec3d> hit = entityBox.raycast(start, end);
            if (hit.isPresent()) {
                double dist = start.squaredDistanceTo(hit.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = candidate;
                }
            }
        }
        return closest;
    }
    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, attacker.getActiveHand() == Hand.MAIN_HAND
                ? net.minecraft.entity.EquipmentSlot.MAINHAND
                : net.minecraft.entity.EquipmentSlot.OFFHAND);
    }
}