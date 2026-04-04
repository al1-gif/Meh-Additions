package net.shuuphe.mehadditions.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.ModEntityTypes;

public class LumidouceFireballEntity extends SmallFireballEntity {

    public LumidouceFireballEntity(EntityType<? extends SmallFireballEntity> type, World world) {
        super(type, world);
    }

    public static LumidouceFireballEntity create(World world, LivingEntity owner) {
        Vec3d look = owner.getRotationVec(1.0f);
        LumidouceFireballEntity fb = new LumidouceFireballEntity(ModEntityTypes.LUMIDOUCE_FIREBALL, world);
        fb.setOwner(owner);
        fb.refreshPositionAfterTeleport(
                owner.getX() + look.x * 0.5,
                owner.getEyeY(),
                owner.getZ() + look.z * 0.5
        );
        fb.setVelocity(look.x * 0.3, look.y * 0.3, look.z * 0.3);
        return fb;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!(this.getEntityWorld() instanceof ServerWorld serverWorld)) return;
        Entity target = entityHitResult.getEntity();
        target.damage(serverWorld, serverWorld.getDamageSources().magic(), 8.0f);
        target.setOnFireFor(5);
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.discard();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.isRemoved()) this.discard();
    }
}