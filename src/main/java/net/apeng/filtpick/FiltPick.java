package net.apeng.filtpick;

import com.mojang.logging.LogUtils;
import net.apeng.filtpick.config.FPConfigManager;
import net.apeng.filtpick.gui.screen.FiltPickScreen;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.apeng.filtpick.network.NetWorkHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FiltPick.ID)
public class FiltPick {

    public static final String ID = "filtpick";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final FPConfigManager CONFIG_MANAGER = FPConfigManager.getInstance(FMLPaths.CONFIGDIR.get());
    private static final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    private static final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    public FiltPick() {
        NetWorkHandler.register();
        registerMenu();
        registerScreen();
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
