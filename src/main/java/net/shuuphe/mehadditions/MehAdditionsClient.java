package net.shuuphe.mehadditions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.shuuphe.mehadditions.client.screen.CraftingAltarScreen;
import net.shuuphe.mehadditions.client.screen.OriginsTableScreen;

public class MehAdditionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.ORIGINS_TABLE, OriginsTableScreen::new);
        EntityRendererRegistry.register(ModEntityTypes.RUNE_ARROW, ArrowEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.CRAFTING_ALTAR, CraftingAltarScreen::new);
    }
}