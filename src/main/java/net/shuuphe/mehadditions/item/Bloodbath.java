package net.shuuphe.mehadditions.item;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class Bloodbath extends Item {

    private static final float KNOCKBACK_RANGE = 3.5F;
    private static final float KNOCKBACK_POWER = 0.7F;
    private static final float HEAVY_SMASH_THRESHOLD = 5.0F;

    public Bloodbath(Settings settings) {
        super(settings);
    }

    /**
     * Call this in your item registration:
     *   .attributeModifiers(Bloodbath.createAttributeModifiers())
     */
    public static AttributeModifiersComponent createAttributeModifiers() {
        return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 5.0, Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, -3.4, Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND)
                .build();
    }

    public static boolean shouldDealAdditionalDamage(LivingEntity attacker) {
        return attacker.fallDistance > 1.5F && !attacker.isGliding();
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        Entity source = damageSource.getSource();
        if (!(source instanceof LivingEntity living)) return 0.0F;
        if (!shouldDealAdditionalDamage(living)) return 0.0F;

        double fall = living.fallDistance;
        double bonus;

        if (fall <= 3.0) {
            bonus = 4.0 * fall;
        } else if (fall <= 8.0) {
            bonus = 12.0 + 2.0 * (fall - 3.0);
        } else {
            bonus = 22.0 + (fall - 8.0);
        }

        return (float) bonus;
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!shouldDealAdditionalDamage(attacker)) return;
        if (!(attacker.getEntityWorld() instanceof ServerWorld serverWorld)) return;
        attacker.setVelocity(attacker.getVelocity().withAxis(Axis.Y, 0.01));
        if (attacker instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.currentExplosionImpactPos = getExplosionImpactPos(serverPlayer);
            serverPlayer.setIgnoreFallDamageFromCurrentExplosion(true);
            serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer));
        }

        if (target.isOnGround()) {
            if (attacker instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.setSpawnExtraParticlesOnFall(true);
            }
            SoundEvent sound = attacker.fallDistance > HEAVY_SMASH_THRESHOLD
                    ? SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY
                    : SoundEvents.ITEM_MACE_SMASH_GROUND;
            serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    sound, attacker.getSoundCategory(), 1.0F, 1.0F);
        } else {
            serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.ITEM_MACE_SMASH_AIR, attacker.getSoundCategory(), 1.0F, 1.0F);
        }

        knockbackNearbyEntities(serverWorld, attacker, target);
    }
    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (shouldDealAdditionalDamage(attacker)) {
            attacker.onLanding();
        }
    }

    private Vec3d getExplosionImpactPos(ServerPlayerEntity player) {
        return player.shouldIgnoreFallDamageFromCurrentExplosion()
                && player.currentExplosionImpactPos != null
                && player.currentExplosionImpactPos.y <= player.getEntityPos().y
                ? player.currentExplosionImpactPos
                : player.getEntityPos();
    }

    private static void knockbackNearbyEntities(World world, Entity attacker, Entity attacked) {
        world.syncWorldEvent(2013, attacked.getSteppingPos(), 750);

        world.getEntitiesByClass(
                LivingEntity.class,
                attacked.getBoundingBox().expand(KNOCKBACK_RANGE),
                getKnockbackPredicate(attacker, attacked)
        ).forEach(entity -> {
            Vec3d offset = entity.getEntityPos().subtract(attacked.getEntityPos());
            double power = getKnockback(attacker, entity, offset);
            Vec3d velocity = offset.normalize().multiply(power);

            if (power > 0.0) {
                entity.addVelocity(velocity.x, KNOCKBACK_POWER, velocity.z);
                if (entity instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer));
                }
            }
        });
    }

    private static Predicate<LivingEntity> getKnockbackPredicate(Entity attacker, Entity attacked) {
        return entity -> {
            boolean notSpectator = !entity.isSpectator();
            boolean notInvolved  = entity != attacker && entity != attacked;
            boolean notTeammate  = !attacker.isTeammate(entity);

            boolean notOwnedPet = true;
            if (entity instanceof TameableEntity tameable
                    && attacked instanceof LivingEntity livingAttacked
                    && tameable.isTamed()
                    && tameable.isOwner(livingAttacked)) {
                notOwnedPet = false;
            }

            boolean notMarker = !(entity instanceof ArmorStandEntity stand && stand.isMarker());

            boolean inRange = attacked.squaredDistanceTo(entity) <= Math.pow(KNOCKBACK_RANGE, 2.0);

            return notSpectator && notInvolved && notTeammate && notOwnedPet && notMarker && inRange;
        };
    }

    private static double getKnockback(Entity attacker, LivingEntity attacked, Vec3d offset) {
        return (KNOCKBACK_RANGE - offset.length())
                * KNOCKBACK_POWER
                * (attacker.fallDistance > HEAVY_SMASH_THRESHOLD ? 2.0 : 1.0)
                * (1.0 - attacked.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
    }
}