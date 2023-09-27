package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltListProvider;
import net.apeng.filtpick.gui.FiltMenu;
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
                    //Server Logic
                    SimpleContainerData modes = new SimpleContainerData(2);
                    modes.set(0, serverFiltList.isWhitelistModeOnInt());
                    modes.set(1, serverFiltList.isDestructionModeOnInt());
                    NetworkHooks.openScreen(
                            context.getSender(),
                            new SimpleMenuProvider(
                                    (containerId, playerInventory, player) -> new FiltMenu(containerId, playerInventory, serverFiltList, modes),
                                    Component.translatable("menu.title.filtpick")
                            )
                    );
                }));
        context.setPacketHandled(true);
    }
}
