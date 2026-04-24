package net.shuuphe.mehadditions.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.shuuphe.mehadditions.ModEffects;

public class BluntForceItem extends Item {

    private static final int BLOOD_LOSS_DURATION = 120;

    public BluntForceItem(Settings settings) {
        super(settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.addStatusEffect(new StatusEffectInstance(
                ModEffects.BLOOD_LOSS, BLOOD_LOSS_DURATION, 0, false, true, true));
        stack.damage(1, attacker, attacker.getActiveHand() == Hand.MAIN_HAND
                ? EquipmentSlot.MAINHAND
                : EquipmentSlot.OFFHAND);
    }
}