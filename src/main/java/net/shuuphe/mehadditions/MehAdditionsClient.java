package net.shuuphe.mehadditions;

import com.shuuphe.mehorigins.client.screen.RaceGuideScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.shuuphe.mehadditions.client.HudRenderer;
import net.shuuphe.mehadditions.client.screen.AdditionsGuideScreen;
import net.shuuphe.mehadditions.client.screen.CraftingAltarScreen;
import net.shuuphe.mehadditions.client.screen.OriginsTableScreen;
import net.shuuphe.mehadditions.item.OriginStaffItem;

public class MehAdditionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ((OriginStaffItem) ModItems.ORIGIN_STAFF).clientOpenScreen =
                stack -> net.minecraft.client.MinecraftClient.getInstance()
                        .setScreen(new net.shuuphe.mehadditions.client.screen.StaffScreen(stack));
        HandledScreens.register(ModScreenHandlers.ORIGINS_TABLE, OriginsTableScreen::new);
        EntityRendererRegistry.register(ModEntityTypes.RUNE_ARROW, ArrowEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.CRAFTING_ALTAR, CraftingAltarScreen::new);
        EntityRendererRegistry.register(ModEntityTypes.LUMIDOUCE_FIREBALL, FlyingItemEntityRenderer::new);

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen.getClass() == RaceGuideScreen.class) {
                client.setScreen(new AdditionsGuideScreen());
            }
        });

        HudRenderer.register();
    }
}