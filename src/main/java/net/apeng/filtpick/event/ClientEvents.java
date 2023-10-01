package net.apeng.filtpick.event;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.gui.FiltMenu;
import net.apeng.filtpick.gui.FiltScreen;
import net.apeng.filtpick.networking.NetWorkHandler;
import net.apeng.filtpick.networking.packet.OpenFiltScreenC2SPacket;
import net.apeng.filtpick.networking.packet.SynFiltListAbstractPacket;
import net.apeng.filtpick.networking.packet.SynFiltListC2SPacket;
import net.apeng.filtpick.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = FiltPick.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeEvents {
        @SubscribeEvent
        // Event is on the Forge event bus only on the physical client
        public static void onKeyPressed(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
                while (KeyBinding.OPEN_FILTLIST_MAP.consumeClick()) {
                    // Execute logic to perform on click here
                    NetWorkHandler.sendToServer(new OpenFiltScreenC2SPacket());
                }
                while (KeyBinding.SET_ITEM_MAP.consumeClick()) {
                    // Execute logic to perform on click here
                    FiltPick.CLIENT_FILT_LIST.setStackInSlot(0, Minecraft.getInstance().player.getMainHandItem());
                    NetWorkHandler.sendToServer(new SynFiltListC2SPacket(FiltPick.CLIENT_FILT_LIST));
                }

            }
        }

    }

    @Mod.EventBusSubscriber(modid = FiltPick.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.SET_ITEM_MAP);
            event.register(KeyBinding.OPEN_FILTLIST_MAP);
        }


        @SubscribeEvent
        public static void gatherData(GatherDataEvent event) {

        }

        @SubscribeEvent
        public static void registerScreens(FMLClientSetupEvent event) {
            event.enqueueWork(
                    () -> MenuScreens.register(FiltMenu.MENU_TYPE.get(), FiltScreen::new)
            );
        }
    }
}
