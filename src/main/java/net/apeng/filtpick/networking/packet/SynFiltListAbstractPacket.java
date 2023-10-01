package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.capability.FiltList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class SynFiltListAbstractPacket {
    private final FiltList filtList = new FiltList(27);

    public SynFiltListAbstractPacket(FiltList filtList) {
        this.filtList.copyFrom(filtList);
    }

    public SynFiltListAbstractPacket(FriendlyByteBuf buf) {
        filtList.setWhitelistModeOn(buf.readBoolean());
        filtList.setDestructionModeOn(buf.readBoolean());
        int slots = buf.readInt();
        for (int i = 0; i < slots; i++) {
            filtList.setStackInSlot(i, buf.readItem());
        }

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(filtList.isWhitelistModeOn());
        buf.writeBoolean(filtList.isDestructionModeOn());
        buf.writeInt(filtList.getSlots());
        for (int i = 0; i < filtList.getSlots(); i++) {
            buf.writeItemStack(filtList.getStackInSlot(i), false);
        }
    }

    public abstract void handle(Supplier<NetworkEvent.Context> supplier);

    public FiltList getFiltList() {
        return filtList;
    }
}
