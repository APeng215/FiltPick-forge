package net.apeng.filtpick.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A singleton
 */
public class FiltPickServerConfig {

    // Private static instance of the class (Singleton pattern)
    private static FiltPickServerConfig instance;

    // Configuration value for max items per filter
    public final ForgeConfigSpec.IntValue CONTAINER_SIZE;

    // Private constructor to prevent external instantiation
    private FiltPickServerConfig(ForgeConfigSpec.Builder builder) {
        CONTAINER_SIZE = builder.comment("The size of the filtpick list every player has.").defineInRange("container size", 9 * 9, 0, 900);
    }

    // Public static method to get the singleton instance
    public static FiltPickServerConfig getInstance(ForgeConfigSpec.Builder builder) {
        if (instance == null) {
            instance = new FiltPickServerConfig(builder);
        }
        return instance;
    }
}
