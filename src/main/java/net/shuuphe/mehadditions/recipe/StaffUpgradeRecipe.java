package net.shuuphe.mehadditions.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.ModRecipes;
import net.shuuphe.mehadditions.util.StaffDataHelper;

public class StaffUpgradeRecipe implements Recipe<CraftingRecipeInput> {

    public ShapedRecipe getInner() { return inner; }

    final ShapedRecipe inner;
    final String raceId;

    public StaffUpgradeRecipe(ShapedRecipe inner, String raceId) {
        this.inner  = inner;
        this.raceId = raceId;
    }

    @Override
    public RecipeType<StaffUpgradeRecipe> getType() { return ModRecipes.ORIGINS_TABLE_UPGRADE; }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (!inner.matches(input, world)) return false;
        for (int i = 0; i < input.getWidth() * input.getHeight(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isOf(ModItems.ORIGIN_STAFF)) {
                return !StaffDataHelper.hasRace(stack, raceId);
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        ItemStack result = new ItemStack(ModItems.ORIGIN_STAFF);
        for (int i = 0; i < input.getWidth() * input.getHeight(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isOf(ModItems.ORIGIN_STAFF)) {
                result.setDamage(stack.getDamage());
                StaffDataHelper.copyFrom(stack, result);
                break;
            }
        }
        StaffDataHelper.addRace(result, raceId);
        return result;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        return inner.getIngredientPlacement();
    }

    @Override
    public RecipeSerializer<? extends Recipe<CraftingRecipeInput>> getSerializer() {
        return ModRecipes.STAFF_UPGRADE;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public String getGroup() { return inner.getGroup(); }


    public static class Serializer implements RecipeSerializer<StaffUpgradeRecipe> {

        public static final MapCodec<StaffUpgradeRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        ShapedRecipe.Serializer.CODEC.forGetter(r -> r.inner),
                        com.mojang.serialization.Codec.STRING.fieldOf("race").forGetter(r -> r.raceId)
                ).apply(instance, StaffUpgradeRecipe::new));

        public static final PacketCodec<RegistryByteBuf, StaffUpgradeRecipe> PACKET_CODEC =
                PacketCodec.tuple(
                        ShapedRecipe.Serializer.PACKET_CODEC, r -> r.inner,
                        PacketCodecs.STRING, r -> r.raceId,
                        StaffUpgradeRecipe::new
                );

        @Override
        public MapCodec<StaffUpgradeRecipe> codec() { return CODEC; }

        @Override
        public PacketCodec<RegistryByteBuf, StaffUpgradeRecipe> packetCodec() { return PACKET_CODEC; }
    }
}