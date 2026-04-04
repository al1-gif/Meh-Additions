package net.shuuphe.mehadditions.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class FrostStatusEffect extends StatusEffect {

    public FrostStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x00BFFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        entity.damage(world, world.getDamageSources().freeze(), 1.5f);
        entity.setFrozenTicks(Math.max(entity.getFrozenTicks(), 140));
        return true;
    }
}