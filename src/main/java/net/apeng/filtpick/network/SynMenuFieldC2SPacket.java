package net.apeng.filtpick.network;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynMenuFieldC2SPacket {

    private final int displayedRowStartIndex;

    public SynMenuFieldC2SPacket(int displayedRowStartIndex) {
        this.displayedRowStartIndex = displayedRowStartIndex;
    }

    public SynMenuFieldC2SPacket(FriendlyByteBuf buf) {
        this.displayedRowStartIndex = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(displayedRowStartIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        if (supplier.get().getSender().containerMenu instanceof FiltPickMenu filtPickMenu) {
            filtPickMenu.setDisplayedRowStartIndexAndUpdate(displayedRowStartIndex);
        } else {
            FiltPick.LOGGER.warn("FiltPick menu is not opened but receive SynMenuFieldC2SPacket!");
        }
    }
}
