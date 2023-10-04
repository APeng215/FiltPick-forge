package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltList;
import net.apeng.filtpick.capability.FiltListProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynFiltModesC2SPacket extends SynFiltListAbstractPacket {


    public SynFiltModesC2SPacket(FiltList filtList) {
        super(filtList);
    }

    public SynFiltModesC2SPacket(FriendlyByteBuf buf) {
        super(buf);
    }


    public void handler(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(serverFiltList -> {
                //Server Logic
                serverFiltList.copyModesFrom(this.getFiltList());
            });
        });
        context.setPacketHandled(true);
    }
}
