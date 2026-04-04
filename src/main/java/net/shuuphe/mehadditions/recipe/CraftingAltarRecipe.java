package net.shuuphe.mehadditions.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.ModRecipes;

import java.util.List;

public class CraftingAltarRecipe implements Recipe<CraftingAltarInput> {

    private final Ingredient first;
    private final Ingredient second;
    private final ItemStack result;

    public CraftingAltarRecipe(Ingredient first, Ingredient second, ItemStack result) {
        this.first = first;
        this.second = second;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingAltarInput input, World world) {
        return (first.test(input.first()) && second.test(input.second())) ||
                (first.test(input.second()) && second.test(input.first()));
    }

    @Override
    public ItemStack craft(CraftingAltarInput input, RegistryWrapper.WrapperLookup lookup) {
        return result.copy();
    }

    // NOT @Override — Recipe interface no longer declares getResult()
    public ItemStack getResult() { return result; }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        // forShapeless replaces the removed IngredientPlacement.of(List)
        return IngredientPlacement.forShapeless(List.of(first, second));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public RecipeBookCategory getRecipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }

    @Override
    public RecipeSerializer<CraftingAltarRecipe> getSerializer() { return ModRecipes.CRAFTING_ALTAR_SERIALIZER; }

    @Override
    public RecipeType<CraftingAltarRecipe> getType() { return ModRecipes.CRAFTING_ALTAR; }

    public static class Serializer implements RecipeSerializer<CraftingAltarRecipe> {
        public static final MapCodec<CraftingAltarRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC.fieldOf("first").forGetter(r -> r.first),
                        Ingredient.CODEC.fieldOf("second").forGetter(r -> r.second),
                        ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(r -> r.result)
                ).apply(instance, CraftingAltarRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, CraftingAltarRecipe> PACKET_CODEC =
                PacketCodec.tuple(
                        Ingredient.PACKET_CODEC, r -> r.first,
                        Ingredient.PACKET_CODEC, r -> r.second,
                        ItemStack.PACKET_CODEC, r -> r.result,
                        CraftingAltarRecipe::new
                );

        @Override public MapCodec<CraftingAltarRecipe> codec() { return CODEC; }
        @Override public PacketCodec<RegistryByteBuf, CraftingAltarRecipe> packetCodec() { return PACKET_CODEC; }
    }
}