package net.shuuphe.mehadditions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.ModEffects;
import net.shuuphe.mehadditions.ModEntityTypes;
import net.shuuphe.mehadditions.util.RuneType;

public class RuneArrowEntity extends ArrowEntity {

    private static final TrackedData<Integer> RUNE_TYPE =
            DataTracker.registerData(RuneArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public RuneArrowEntity(EntityType<? extends RuneArrowEntity> type, World world) {
        super(type, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(RUNE_TYPE, 0);
    }

    public static RuneArrowEntity create(World world, LivingEntity shooter, RuneType runeType) {
        RuneArrowEntity arrow = new RuneArrowEntity(ModEntityTypes.RUNE_ARROW, world);
        arrow.setOwner(shooter);
        arrow.setPosition(shooter.getX(), shooter.getBodyY(0.7), shooter.getZ());
        arrow.dataTracker.set(RUNE_TYPE, runeType.ordinal());
        arrow.setDamage(10.0);
        arrow.pickupType = PickupPermission.DISALLOWED;
        return arrow;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity target = entityHitResult.getEntity();
        if (!(target instanceof LivingEntity living)) return;
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) return;

        RuneType type = RuneType.fromOrdinal(this.dataTracker.get(RUNE_TYPE));
        switch (type) {
            case FROST     -> applyFrost(living, serverWorld);
            case LIGHTNING -> spawnLightning(serverWorld, living.getX(), living.getY(), living.getZ());
            default        -> {}
        }
    }

    private void applyFrost(LivingEntity entity, ServerWorld world) {
        entity.addStatusEffect(new StatusEffectInstance(ModEffects.FROST, 200, 0));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 3));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 2));
        entity.setFrozenTicks(300);
    }

    private void spawnLightning(ServerWorld world, double x, double y, double z) {
        for (int i = 0; i < 6; i++) {
            double ox = (world.getRandom().nextDouble() - 0.5) * 4.0;
            double oz = (world.getRandom().nextDouble() - 0.5) * 4.0;
            net.minecraft.entity.LightningEntity bolt =
                    new net.minecraft.entity.LightningEntity(EntityType.LIGHTNING_BOLT, world);
            bolt.refreshPositionAfterTeleport(x + ox, y, z + oz);
            world.spawnEntity(bolt);
        }
    }
}