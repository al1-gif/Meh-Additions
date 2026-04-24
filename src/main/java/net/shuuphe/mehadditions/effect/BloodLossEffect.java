package net.shuuphe.mehadditions.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class BloodLossEffect extends StatusEffect {

    public BloodLossEffect() {
        super(StatusEffectCategory.HARMFUL, 0xAA0000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        entity.damage(world, world.getDamageSources().magic(), 1.5f);
        return true;
    }
}