package net.shuuphe.mehadditions;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

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
            if (player.getStackInHand(hand).isOf(ModItems.FREEDOM_SWORN)
                    && world instanceof ServerWorld serverWorld) {
                boolean wasInvulnerable = player.isInvulnerable();
                player.setInvulnerable(true);

                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
                lightning.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
                serverWorld.spawnEntity(lightning);

                serverWorld.getServer().execute(() -> player.setInvulnerable(wasInvulnerable));
            }
            return ActionResult.PASS;
        });
    }
}