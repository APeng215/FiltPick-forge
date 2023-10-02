package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltListProvider;
import net.apeng.filtpick.gui.FiltMenu;
import net.apeng.filtpick.networking.NetWorkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class ResetFiltListC2SPacket {
    public ResetFiltListC2SPacket() {
    }

    public ResetFiltListC2SPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handler(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(
                serverFiltList -> {
                    serverFiltList.reset();
                    NetWorkHandler.sendToClient(new SynFiltListS2CPacket(serverFiltList), context.getSender());
                }));

        context.setPacketHandled(true);
    }
}
