package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.capability.FiltListProvider;
import net.apeng.filtpick.gui.FiltMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class OpenFiltScreenC2SPacket {
    private FriendlyByteBuf buf;

    public OpenFiltScreenC2SPacket() {
    }

    public OpenFiltScreenC2SPacket(FriendlyByteBuf buf) {
        this.buf = buf;
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            context.getSender().getCapability(FiltListProvider.FILT_LIST).ifPresent(serverFiltList -> {
                //Server Logic
                context.getSender().sendSystemMessage(Component.literal("Open Screen"));
                NetworkHooks.openScreen(context.getSender(), new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> new FiltMenu(containerId, playerInventory, new ItemStackHandler(27), new SimpleContainerData(2)),
                        Component.translatable("menu.title.filtpick")));

            });
        });
        context.setPacketHandled(true);
    }
}
