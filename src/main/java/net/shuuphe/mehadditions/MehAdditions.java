package net.shuuphe.mehadditions;

import net.fabricmc.api.ModInitializer;
import net.shuuphe.mehadditions.network.SelectRaceWithStaffPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MehAdditions implements ModInitializer {
	public static final String MOD_ID = "mehadditions";
	public static final Logger LOGGER = LoggerFactory.getLogger("mehadditions");

	@Override
	public void onInitialize() {
		ModEffects.register();
		ModEntityTypes.register();
		ModItems.register();
		ModBlocks.register();
		ModRecipes.register();
		ModScreenHandlers.register();
		ModEvents.register();
		SelectRaceWithStaffPacket.register();
		LOGGER.info("Meh-Additions initializing...");
	}
}