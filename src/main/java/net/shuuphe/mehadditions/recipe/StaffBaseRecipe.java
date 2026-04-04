package net.shuuphe.mehadditions.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.ModRecipes;

public class StaffBaseRecipe implements Recipe<CraftingRecipeInput> {

    public ShapedRecipe getInner() { return inner; }

    final ShapedRecipe inner;

    public StaffBaseRecipe(ShapedRecipe inner) { this.inner = inner; }

    @Override public RecipeType<StaffBaseRecipe> getType() { return ModRecipes.ORIGINS_TABLE_BASE; }
    @Override public boolean isIgnoredInRecipeBook() { return true; }
    @Override public boolean matches(CraftingRecipeInput input, World world) { return inner.matches(input, world); }
    @Override public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup r) { return inner.craft(input, r); }
    @Override public IngredientPlacement getIngredientPlacement() { return inner.getIngredientPlacement(); }
    @Override public RecipeSerializer<? extends Recipe<CraftingRecipeInput>> getSerializer() { return ModRecipes.STAFF_BASE; }
    @Override public RecipeBookCategory getRecipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    @Override public String getGroup() { return inner.getGroup(); }

    public static class Serializer implements RecipeSerializer<StaffBaseRecipe> {
        public static final MapCodec<StaffBaseRecipe> CODEC =
                ShapedRecipe.Serializer.CODEC.xmap(StaffBaseRecipe::new, r -> r.inner);
        public static final PacketCodec<RegistryByteBuf, StaffBaseRecipe> PACKET_CODEC =
                ShapedRecipe.Serializer.PACKET_CODEC.xmap(StaffBaseRecipe::new, r -> r.inner);

        @Override public MapCodec<StaffBaseRecipe> codec() { return CODEC; }
        @Override public PacketCodec<RegistryByteBuf, StaffBaseRecipe> packetCodec() { return PACKET_CODEC; }
    }
}