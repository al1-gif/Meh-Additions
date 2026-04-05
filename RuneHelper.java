package net.shuuphe.mehadditions.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.shuuphe.mehadditions.item.RunePouchItem;

public final class RuneHelper {

    private RuneHelper() {}

    public static boolean hasRune(PlayerEntity player, Item runeItem) {
        for (int i = 0, size = player.getInventory().size(); i < size; i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(runeItem)) return true;
            if (s.getItem() instanceof RunePouchItem
                    && RunePouchItem.hasPouchWithRune(s, runeItem)) return true;
        }
        return false;
    }

    public static boolean consumeRune(PlayerEntity player, Item runeItem,
                                      RegistryWrapper.WrapperLookup reg) {
        int size = player.getInventory().size();

        for (int i = 0; i < size; i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(runeItem)) {
                s.decrement(1);
                return true;
            }
        }

        for (int i = 0; i < size; i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.getItem() instanceof RunePouchItem
                    && RunePouchItem.consumeRuneFromPouch(s, runeItem, reg))
                return true;
        }

        return false;
    }
}