package net.apeng.filtpick;

import com.mojang.logging.LogUtils;
import net.apeng.filtpick.config.FiltPickClientConfig;
import net.apeng.filtpick.config.FiltPickServerConfig;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.apeng.filtpick.gui.screen.FiltPickScreen;
import net.apeng.filtpick.network.NetworkHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FiltPick.ID)
public class FiltPick {

    public static final String ID = "filtpick";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;
    private static final IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    public static FiltPickClientConfig CLIENT_CONFIG;
    public static FiltPickServerConfig SERVER_CONFIG;

    public FiltPick() {
        registerNetwork();
        registerMenu();
        registerScreen();
        registerConfigs();
    }

    private static void registerNetwork() {
        NetworkHandler.registerAll();
    }

    private static void registerConfigs() {
        registerClientConfig();
        registerServerConfig();
    }

    private static void registerClientConfig() {
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT_CONFIG = FiltPickClientConfig.getInstance(clientBuilder); // Create singleton instance
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientBuilder.build());
    }

    private static void registerServerConfig() {
        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
        SERVER_CONFIG = FiltPickServerConfig.getInstance(serverBuilder); // Create singleton instance
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverBuilder.build());
    }

    private void registerScreen() {
        MOD_EVENT_BUS.addListener(this::registerMenuScreen);
    }

    private void registerMenu() {
        FiltPickMenu.REGISTER.register(MOD_EVENT_BUS);
    }

    private void registerMenuScreen(FMLClientSetupEvent event) {
        // MenuScreens#register is not thread-safe, so it needs to be called inside #enqueueWork provided by the parallel dispatch event.
        event.enqueueWork(
                () -> MenuScreens.register(FiltPickMenu.TYPE.get(), FiltPickScreen::new)
        );
    }

}
