package net.shuuphe.mehadditions.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Hanafubuki extends Item {

    public static final int CHARGE_TICKS  = 40;
    public static final int MAX_USE_TICKS = 99999;

    private static final int EFFECT_DURATION = 60;
    private static final int DASH_TICKS      = 8;
    private static final double DASH_SPEED   = 1.4;

    private static final Map<UUID, double[]> DASHING = new HashMap<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<Map.Entry<UUID, double[]>> it = DASHING.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, double[]> entry = it.next();
                double[] state = entry.getValue();
                ServerPlayerEntity sp = server.getPlayerManager().getPlayer(entry.getKey());
                if (sp == null) { it.remove(); continue; }
                state[0]--;
                if (state[0] <= 0) { it.remove(); continue; }
                sp.setVelocity(state[1] * DASH_SPEED, state[2] * DASH_SPEED, state[3] * DASH_SPEED);
                sp.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(sp));
            }
        });
    }

    public Hanafubuki(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return MAX_USE_TICKS;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;

        int elapsed = MAX_USE_TICKS - remainingUseTicks;
        if (elapsed < CHARGE_TICKS) return false;

        if (world.isClient()) return true;

        float pitch = Math.max(player.getPitch(), -75f);
        float yaw   = player.getYaw();
        double pitchRad = Math.toRadians(pitch);
        double yawRad   = Math.toRadians(yaw);
        double dx = -Math.sin(yawRad) * Math.cos(pitchRad);
        double dy = -Math.sin(pitchRad);
        double dz =  Math.cos(yawRad) * Math.cos(pitchRad);
        Vec3d dashDir = new Vec3d(dx, dy, dz).normalize();
        Vec3d start   = player.getEntityPos();
        Vec3d end     = start.add(dashDir.multiply(7.0));

        Box corridor = new Box(
                Math.min(start.x, end.x) - 1.0, Math.min(start.y, end.y) - 1.0, Math.min(start.z, end.z) - 1.0,
                Math.max(start.x, end.x) + 1.0, Math.max(start.y, end.y) + 1.0, Math.max(start.z, end.z) + 1.0
        );

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class, corridor,
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : targets) {
            player.attack(target);
            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NAUSEA, EFFECT_DURATION, 0, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.POISON, EFFECT_DURATION, 0, false, true, true));
        }

        if (player instanceof ServerPlayerEntity sp) {
            DASHING.put(sp.getUuid(), new double[]{DASH_TICKS, dashDir.x, dashDir.y, dashDir.z});
            sp.setVelocity(dashDir.x * DASH_SPEED, dashDir.y * DASH_SPEED, dashDir.z * DASH_SPEED);
            sp.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(sp));
        }

        ((ServerWorld) world).playSound(null, start.x, start.y, start.z,
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST,
                SoundCategory.PLAYERS, 0.8f, 1.2f);

        Hand activeHand = player.getActiveHand();
        stack.damage(2, player, activeHand == Hand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND);

        return true;
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, attacker.getActiveHand() == Hand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND);
    }
}