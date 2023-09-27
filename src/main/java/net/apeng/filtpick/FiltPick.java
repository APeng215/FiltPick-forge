package net.apeng.filtpick;

import com.mojang.logging.LogUtils;
import net.apeng.filtpick.capability.FiltList;
import net.apeng.filtpick.gui.FiltMenu;
import net.apeng.filtpick.networking.NetWorkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FiltPick.MOD_ID)
public class FiltPick {
    public static final String MOD_ID = "filtpick";
    public static final FiltList CLIENT_FILT_LIST = new FiltList(27);
    public static final Logger LOGGER = LogUtils.getLogger();


    public FiltPick() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        FiltMenu.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        NetWorkHandler.register();
    }
}
