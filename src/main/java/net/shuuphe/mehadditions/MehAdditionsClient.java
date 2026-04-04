package net.shuuphe.mehadditions;

import com.shuuphe.mehorigins.client.screen.RaceGuideScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.shuuphe.mehadditions.client.screen.CraftingAltarScreen;
import net.shuuphe.mehadditions.client.screen.OriginsTableScreen;
import net.shuuphe.mehadditions.client.screen.AdditionsGuideScreen;

public class MehAdditionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.ORIGINS_TABLE, OriginsTableScreen::new);
        EntityRendererRegistry.register(ModEntityTypes.RUNE_ARROW, ArrowEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.CRAFTING_ALTAR, CraftingAltarScreen::new);
        EntityRendererRegistry.register(ModEntityTypes.LUMIDOUCE_FIREBALL, FlyingItemEntityRenderer::new);

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen.getClass() == RaceGuideScreen.class) {
                client.setScreen(new AdditionsGuideScreen());
            }
        });
    }
}