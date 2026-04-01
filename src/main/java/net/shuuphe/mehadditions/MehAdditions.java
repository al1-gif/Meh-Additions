package net.shuuphe.mehadditions;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MehAdditions implements ModInitializer {
	public static final String MOD_ID = "mehadditions";
	public static final Logger LOGGER = LoggerFactory.getLogger("mehadditions");

	@Override
	public void onInitialize() {
		ModItems.register();
		ModEvents.register();
		LOGGER.info("Meh-Additions initializing...");
	}
}