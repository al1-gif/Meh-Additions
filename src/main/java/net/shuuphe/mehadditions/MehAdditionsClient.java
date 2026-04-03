package net.shuuphe.mehadditions;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.shuuphe.mehadditions.client.screen.OriginsTableScreen;

public class MehAdditionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.ORIGINS_TABLE, OriginsTableScreen::new);
    }
}