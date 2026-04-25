package net.shuuphe.mehadditions;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.shuuphe.mehadditions.item.TranceItem;

public class ModEvents {

    private static LootPool.Builder createRunePool(float min, float max) {
        return LootPool.builder()
                .rolls(UniformLootNumberProvider.create(min, max))
                .with(ItemEntry.builder(ModItems.FIRE_STONE))
                .with(ItemEntry.builder(ModItems.LIGHTNING_STONE))
                .with(ItemEntry.builder(ModItems.FROST_STONE));
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) return;

            if (LootTables.BURIED_TREASURE_CHEST.equals(key)) {
                tableBuilder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.EYES_OF_ORIGIN)));
            }
            LootPool.Builder pool = null;

            if (key.equals(LootTables.BURIED_TREASURE_CHEST)
                    || key.equals(LootTables.VILLAGE_TOOLSMITH_CHEST)
                    || key.equals(LootTables.VILLAGE_WEAPONSMITH_CHEST)
                    || key.equals(LootTables.VILLAGE_ARMORER_CHEST)
                    || key.equals(LootTables.VILLAGE_CARTOGRAPHER_CHEST)) {
                pool = createRunePool(3, 4);
            } else if (key.equals(LootTables.SHIPWRECK_TREASURE_CHEST)) {
                pool = createRunePool(5, 6);
            } else if (key.equals(LootTables.JUNGLE_TEMPLE_CHEST)
                    || key.equals(LootTables.RUINED_PORTAL_CHEST)
                    || key.equals(LootTables.PILLAGER_OUTPOST_CHEST)) {
                pool = createRunePool(2, 3);
            } else if (key.equals(LootTables.ANCIENT_CITY_CHEST)) {
                pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(7, 9))
                        .conditionally(RandomChanceLootCondition.builder(0.4f))
                        .with(ItemEntry.builder(ModItems.FROST_STONE))
                        .with(ItemEntry.builder(ModItems.LIGHTNING_STONE))
                        .with(ItemEntry.builder(ModItems.FIRE_STONE));
            } else if (key.equals(LootTables.IGLOO_CHEST_CHEST)
                    || key.equals(LootTables.BASTION_BRIDGE_CHEST)
                    || key.equals(LootTables.BASTION_TREASURE_CHEST)) {
                pool = createRunePool(2, 3);
            } else if (key.equals(LootTables.DESERT_PYRAMID_CHEST)) {
                pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(3, 5))
                        .conditionally(RandomChanceLootCondition.builder(0.6f))
                        .with(ItemEntry.builder(ModItems.FIRE_STONE))
                        .with(ItemEntry.builder(ModItems.LIGHTNING_STONE))
                        .with(ItemEntry.builder(ModItems.FROST_STONE));
            } else if (key.equals(LootTables.END_CITY_TREASURE_CHEST)
                    || key.equals(LootTables.SIMPLE_DUNGEON_CHEST)
                    || key.equals(LootTables.STRONGHOLD_LIBRARY_CHEST)) {
                pool = LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(2, 3))
                        .conditionally(RandomChanceLootCondition.builder(0.4f))
                        .with(ItemEntry.builder(ModItems.FIRE_STONE))
                        .with(ItemEntry.builder(ModItems.LIGHTNING_STONE))
                        .with(ItemEntry.builder(ModItems.FROST_STONE));
            }

            if (pool != null) tableBuilder.pool(pool);
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;

            if (player.getStackInHand(hand).isOf(ModItems.CRIMSON_MOONS_SEMBLANCE)) {
                target.setOnFireFor(5);
                int hitCount = 1;
                for (LivingEntity nearby : world.getEntitiesByClass(
                        LivingEntity.class,
                        player.getBoundingBox().expand(3.5),
                        e -> e != player && e != target && e.isAlive())) {
                    nearby.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), 6.0f);
                    nearby.setOnFireFor(5);
                    hitCount++;
                }
                player.heal(hitCount * 6.0f);
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS,
                        1.0f, 0.8f + serverWorld.getRandom().nextFloat() * 0.4f);

            } else if (player.getStackInHand(hand).isOf(ModItems.FREEDOM_SWORN)) {
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                lightning.setPosition(target.getX(), target.getY(), target.getZ());
                serverWorld.spawnEntity(lightning);
            }

            return ActionResult.PASS;
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayerEntity) return;
            if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

            ItemStack held = player.getMainHandStack();
            if (!held.isOf(ModItems.TRANCE)) held = player.getOffHandStack();
            if (!held.isOf(ModItems.TRANCE)) return;

            if (!(entity.getEntityWorld() instanceof ServerWorld world)) return;

            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();

            world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    x, y + 1.0, z, 80, 0.4, 0.6, 0.4, 0.25);
            world.playSound(null, x, y, z,
                    SoundEvents.ITEM_TOTEM_USE, SoundCategory.NEUTRAL, 1.0f, 1.0f);

            int coalCount = 6 + world.getRandom().nextInt(15);
            for (int i = 0; i < coalCount; i++) {
                ItemEntity coal = new ItemEntity(world, x, y + 0.5, z, new ItemStack(Items.COAL, 1));
                coal.setVelocity(
                        (world.getRandom().nextDouble() - 0.5) * 0.7,
                        world.getRandom().nextDouble() * 0.5 + 0.2,
                        (world.getRandom().nextDouble() - 0.5) * 0.7);
                world.spawnEntity(coal);
            }
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.PASS;
            if (!(entity instanceof LivingEntity target)) return ActionResult.PASS;
            TranceItem.onPlayerAttack(sp, target);
            return ActionResult.PASS;
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity sp)) return true;
            if (source.getAttacker() instanceof LivingEntity attacker) {
                TranceItem.onPlayerHurt(sp, attacker);
            }
            return true;
        });
    }
}