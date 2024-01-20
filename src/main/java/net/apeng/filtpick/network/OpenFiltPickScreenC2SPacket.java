package net.apeng.filtpick.network;

import net.apeng.filtpick.gui.screen.FiltPickMenu;
import net.apeng.filtpick.mixinduck.FiltListContainer;
import net.apeng.filtpick.gui.util.ExtendedMenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class OpenFiltPickScreenC2SPacket {

    public OpenFiltPickScreenC2SPacket(){}
    public OpenFiltPickScreenC2SPacket(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {}
    public void handle(CustomPayloadEvent.Context context) {
        context.getSender().openMenu(new ExtendedMenuProvider() {

            @Override
            public boolean shouldClose() {
                return false;
            }

            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                return new FiltPickMenu(pContainerId, pPlayerInventory, ((FiltListContainer)pPlayer).getFiltList(), ((FiltListContainer)pPlayer).getFiltListPropertyDelegate());
            }

        });
    }

}
