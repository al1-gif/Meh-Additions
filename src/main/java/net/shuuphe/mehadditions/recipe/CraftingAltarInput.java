package net.shuuphe.mehadditions.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record CraftingAltarInput(ItemStack first, ItemStack second) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> first;
            case 1 -> second;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() { return 2; }
}