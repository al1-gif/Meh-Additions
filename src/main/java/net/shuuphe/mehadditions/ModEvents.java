package net.shuuphe.mehadditions;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

import java.util.List;

public class ModEvents {

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (LootTables.BURIED_TREASURE_CHEST.equals(key)) {
                tableBuilder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(ModItems.EYES_OF_ORIGIN)));
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getStackInHand(hand).isOf(ModItems.CRIMSON_MOONS_SEMBLANCE)
                    && world instanceof ServerWorld serverWorld
                    && entity instanceof LivingEntity target) {
                target.setOnFireFor(5);
                int hitCount = 1;
                List<LivingEntity> swept = world.getEntitiesByClass(
                        LivingEntity.class,
                        player.getBoundingBox().expand(3.5),
                        e -> e != player && e != target && e.isAlive()
                );
                for (LivingEntity nearby : swept) {
                    nearby.damage(serverWorld, serverWorld.getDamageSources().playerAttack(player), 6.0f);
                    nearby.setOnFireFor(5);
                    hitCount++;
                }
                player.heal(hitCount * 6.0f);
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS,
                        1.0f, 0.8f + serverWorld.getRandom().nextFloat() * 0.4f);
            }
            return ActionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getStackInHand(hand).isOf(ModItems.FREEDOM_SWORN)
                    && world instanceof ServerWorld serverWorld
                    && entity instanceof LivingEntity target) {

                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                lightning.setPosition(target.getX(), target.getY(), target.getZ());
                serverWorld.spawnEntity(lightning);
            }
            return ActionResult.PASS;
        });
    }
}
