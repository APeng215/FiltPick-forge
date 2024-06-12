package net.apeng.filtpick.network;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynMenuFieldC2SPacket {

    private final int displayedRowStartIndex;

    /**
     * @param displayedRowStartIndex Displayed row starting index for client side
     */
    public SynMenuFieldC2SPacket(int displayedRowStartIndex) {
        this.displayedRowStartIndex = displayedRowStartIndex;
    }

    /**
     * Decoder. Read displayedRowStartIndex from buf.
     * @param buf
     */
    public SynMenuFieldC2SPacket(FriendlyByteBuf buf) {
        this.displayedRowStartIndex = buf.readInt();
    }

    /**
     * Encode buf by SynMenuFieldC2SPacket
     * @param buf
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(displayedRowStartIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (supplier.get().getSender().containerMenu instanceof FiltPickMenu filtPickMenu) {
                filtPickMenu.setDisplayedRowOffsetAndUpdate(displayedRowStartIndex);
                filtPickMenu.broadcastFullState(); // Respond is important
            } else {
                FiltPick.LOGGER.warn("FiltPick menu is not opened but receive SynMenuFieldC2SPacket!");
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
