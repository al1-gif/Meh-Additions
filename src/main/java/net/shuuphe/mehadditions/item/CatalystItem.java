package net.shuuphe.mehadditions.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;

import java.util.List;

public class CatalystItem extends Item {

    private static final int COOLDOWN_TICKS = 80;
    private static final float SONIC_RANGE = 8.0f;
    private static final float SONIC_DAMAGE = 20.0f;
    private static final double CONE_DOT = 0.2;

    public CatalystItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(stack)) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;

            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_WARDEN_SONIC_CHARGE,
                    SoundCategory.PLAYERS, 3.0f, 1.0f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_WARDEN_SONIC_BOOM,
                    SoundCategory.PLAYERS, 3.0f, 1.0f);

            var lookVec = user.getRotationVector();
            double ex = user.getX() + lookVec.x * 2;
            double ey = user.getEyeY();
            double ez = user.getZ() + lookVec.z * 2;

            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM,
                    ex, ey, ez,
                    1,
                    0, 0, 0,
                    0);

            for (int i = 1; i <= 5; i++) {
                serverWorld.spawnParticles(ParticleTypes.POOF,
                        user.getX() + lookVec.x * (i * 1.5),
                        user.getEyeY(),
                        user.getZ() + lookVec.z * (i * 1.5),
                        4, 0.3, 0.3, 0.3, 0.05);
            }

            List<LivingEntity> targets = world.getEntitiesByClass(
                    LivingEntity.class,
                    user.getBoundingBox().expand(SONIC_RANGE),
                    e -> e != user && e.isAlive()
            );

            for (LivingEntity target : targets) {
                Vec3d toTarget = new Vec3d(
                        target.getX() - user.getX(),
                        target.getY() - user.getY(),
                        target.getZ() - user.getZ()
                ).normalize();

                double dot = toTarget.dotProduct(lookVec);
                if (dot > CONE_DOT) {
                    target.damage(serverWorld,
                            serverWorld.getDamageSources().sonicBoom(user),
                            SONIC_DAMAGE);
                    target.addVelocity(lookVec.x * 2.0, 0.4, lookVec.z * 2.0);
                    target.velocityDirty = true;
                }
            }

            EquipmentSlot slot = (hand == Hand.MAIN_HAND)
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;
            stack.damage(1, user, slot);
            user.getItemCooldownManager().set(stack, COOLDOWN_TICKS);
        }

        return ActionResult.SUCCESS;
    }
}