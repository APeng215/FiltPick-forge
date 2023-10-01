package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltListProvider;
import net.apeng.filtpick.gui.FiltMenu;
import net.apeng.filtpick.networking.NetWorkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenFiltScreenC2SPacket {

    public OpenFiltScreenC2SPacket() {
    }

    public OpenFiltScreenC2SPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handler(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(
                serverFiltList -> {
                    NetWorkHandler.sendToClient(new SynFiltListS2CPacket(serverFiltList), context.getSender());
                }));
        context.enqueueWork(() -> context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(
                serverFiltList -> {
                    //Server Logic
                    NetworkHooks.openScreen(
                            context.getSender(),
                            new SimpleMenuProvider(
                                    (containerId, playerInventory, player) -> new FiltMenu(containerId, playerInventory, serverFiltList),
                                    Component.translatable("menu.title.filtpick")
                            )
                    );
                }));
        context.setPacketHandled(true);
    }
}
