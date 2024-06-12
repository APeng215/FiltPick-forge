package net.apeng.filtpick.network;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynMenuFieldS2CRespondPacket {

    private final int displayedRowStartIndex;

    /**
     * @param displayedRowStartIndex Displayed row starting index for client side
     */
    public SynMenuFieldS2CRespondPacket(int displayedRowStartIndex) {
        this.displayedRowStartIndex = displayedRowStartIndex;
    }

    /**
     * Decoder. Read displayedRowStartIndex from buf.
     * @param buf
     */
    public SynMenuFieldS2CRespondPacket(FriendlyByteBuf buf) {
        this.displayedRowStartIndex = buf.readInt();
    }

    /**
     * Encode buf by SynMenuFieldS2CRespondPacket
     * @param buf
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(displayedRowStartIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        if (Minecraft.getInstance().player.containerMenu instanceof FiltPickMenu filtPickMenu) {
            filtPickMenu.setDisplayedRowOffsetAndUpdate(displayedRowStartIndex);
        } else {
            FiltPick.LOGGER.warn("FiltPick menu is not opened but receive SynMenuFieldS2CRespondPacket!");
        }
        supplier.get().setPacketHandled(true);
    }
}
