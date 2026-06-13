package net.armoredmobs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmoredMobsMod implements ModInitializer {
    public static final String MOD_ID = "armoredmobs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ArmoredMobsHandler.register();
        LOGGER.info("[ArmoredMobs] Mod chargé !");
    }
}
