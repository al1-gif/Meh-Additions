package net.shuuphe.mehadditions;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.recipe.StaffBaseRecipe;
import net.shuuphe.mehadditions.recipe.StaffUpgradeRecipe;

public class ModRecipes {
    public static RecipeType<StaffBaseRecipe> ORIGINS_TABLE_BASE;
    public static RecipeSerializer<StaffBaseRecipe> STAFF_BASE;

    public static final RecipeType<StaffUpgradeRecipe> ORIGINS_TABLE_UPGRADE =
            Registry.register(Registries.RECIPE_TYPE,
                    Identifier.of(MehAdditions.MOD_ID, "origins_table_upgrade"),
                    new RecipeType<StaffUpgradeRecipe>() {
                        @Override public String toString() { return "mehadditions:origins_table_upgrade"; }
                    });

    public static final RecipeSerializer<StaffUpgradeRecipe> STAFF_UPGRADE =
            new StaffUpgradeRecipe.Serializer();

    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER,
                Identifier.of(MehAdditions.MOD_ID, "staff_upgrade"),
                STAFF_UPGRADE);
        ORIGINS_TABLE_BASE = Registry.register(Registries.RECIPE_TYPE,
                Identifier.of(MehAdditions.MOD_ID, "staff_base"), new RecipeType<>() {});
        STAFF_BASE = Registry.register(Registries.RECIPE_SERIALIZER,
                Identifier.of(MehAdditions.MOD_ID, "staff_base"), new StaffBaseRecipe.Serializer());
    }
}