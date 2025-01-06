package net.apeng.filtpick;

import com.mojang.logging.LogUtils;
import net.apeng.filtpick.config.FiltPickConfig;
import net.apeng.filtpick.gui.screen.FiltPickScreen;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
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
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FiltPick.ID)
public class FiltPick {

    public static final String ID = "filtpick";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int CONTAINER_SIZE = 9 * 6; // TODO: Make it configurable.
    public static final int FILTLIST_DISPLAYED_ROW_NUM = 5; // TODO: Make it configurable.

    private static final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    private static final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    private static final Pair<FiltPickConfig, ForgeConfigSpec> CONFIG_PAIR = new ForgeConfigSpec.Builder()
            .configure(FiltPickConfig::new);
    public static final FiltPickConfig CONFIG = CONFIG_PAIR.getLeft();
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_PAIR.getRight();

    public FiltPick() {
        registerNetwork();
        registerMenu();
        registerScreen();
        registerConfig();
    }

    private static void registerNetwork() {
        NetworkHandler.registerAll();
    }

    private static void registerConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC, "filtpick.toml");
    }

    private void registerScreen() {
        modEventBus.addListener(this::registerMenuScreen);
    }

    private void registerMenu() {
        FiltPickMenu.REGISTER.register(modEventBus);
    }

    private void registerMenuScreen(FMLClientSetupEvent event) {
        // MenuScreens#register is not thread-safe, so it needs to be called inside #enqueueWork provided by the parallel dispatch event.
        event.enqueueWork(
                () -> MenuScreens.register(FiltPickMenu.TYPE.get(), FiltPickScreen::new)
        );
    }

}
