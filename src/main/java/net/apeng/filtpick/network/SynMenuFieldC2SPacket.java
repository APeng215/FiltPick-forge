package net.apeng.filtpick.network;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.getSender().containerMenu instanceof FiltPickMenu filtPickMenu) {
                filtPickMenu.setDisplayedRowOffsetAndUpdate(displayedRowStartIndex);
                filtPickMenu.broadcastFullState(); // Respond is important, making sure everything is synchronized.
            } else {
                FiltPick.LOGGER.warn("FiltPick menu is not opened but receive SynMenuFieldC2SPacket!");
            }
        });
        context.setPacketHandled(true);
    }
}
