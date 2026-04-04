package net.shuuphe.mehadditions;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.screen.CraftingAltarScreenHandler;
import net.shuuphe.mehadditions.screen.OriginsTableScreenHandler;

public class ModScreenHandlers {

    public static ScreenHandlerType<OriginsTableScreenHandler> ORIGINS_TABLE;
    public static ScreenHandlerType<CraftingAltarScreenHandler> CRAFTING_ALTAR;

    public static void register() {
        ORIGINS_TABLE = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(MehAdditions.MOD_ID, "origins_table"),
                new ScreenHandlerType<>(OriginsTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
        CRAFTING_ALTAR = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of(MehAdditions.MOD_ID, "crafting_altar"),
                new ScreenHandlerType<>(CraftingAltarScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    }
}