package net.shuuphe.mehadditions.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class FreedomSwornItem extends Item {

    public FreedomSwornItem(Settings settings) {
        super(settings);
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, attacker.getActiveHand() == Hand.MAIN_HAND
                ? net.minecraft.entity.EquipmentSlot.MAINHAND
                : net.minecraft.entity.EquipmentSlot.OFFHAND);
    }
}