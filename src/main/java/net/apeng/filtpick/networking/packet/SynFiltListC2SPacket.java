package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltList;
import net.apeng.filtpick.capability.FiltListProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynFiltListC2SPacket extends SynFiltListAbstractPacket {


    public SynFiltListC2SPacket(FiltList filtList) {
        super(filtList);
    }

    public SynFiltListC2SPacket(FriendlyByteBuf buf) {
        super(buf);
    }


    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(serverFiltList -> {
                //Server Logic
                serverFiltList.copyFrom(this.getFiltList());
                context.getSender().sendSystemMessage(Component.literal("Syn filtlist completed"));
            });
        });
        context.setPacketHandled(true);
    }
}
