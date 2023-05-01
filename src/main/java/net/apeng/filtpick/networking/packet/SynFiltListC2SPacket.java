package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltList;
import net.apeng.filtpick.capability.FiltListProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynFiltListC2SPacket {
    private final FiltList clientFiltList = new FiltList(27);

    public SynFiltListC2SPacket(FiltList filtList) {
        this.clientFiltList.copyFrom(filtList);
    }

    public SynFiltListC2SPacket(FriendlyByteBuf buf) {
        clientFiltList.setWhitelistModeOn(buf.readBoolean());
        clientFiltList.setDestructionModeOn(buf.readBoolean());
        int slots = buf.readInt();
        for (int i = 0; i < slots; i++) {
            clientFiltList.setStackInSlot(i, buf.readItem());
        }

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(clientFiltList.isWhitelistModeOn());
        buf.writeBoolean(clientFiltList.isDestructionModeOn());
        buf.writeInt(clientFiltList.getSlots());
        for (int i = 0; i < clientFiltList.getSlots(); i++) {
            buf.writeItemStack(clientFiltList.getStackInSlot(i), false);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(serverFiltList -> {
                //Server Logic
                serverFiltList.copyFrom(clientFiltList);
                context.getSender().sendSystemMessage(serverFiltList.getStackInSlot(0).getDisplayName());
            });
        });
        context.setPacketHandled(true);
    }
}
