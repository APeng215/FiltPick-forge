package net.apeng.filtpick.networking.packet;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.capability.FiltList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SynFiltListS2CPacket extends SynFiltListAbstractPacket {

    public SynFiltListS2CPacket(FiltList filtList) {
        super(filtList);
    }

    public SynFiltListS2CPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handler(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() ->
                // Make sure it's only executed on the physical client
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(this.getFiltList(), supplier))
        );
        supplier.get().setPacketHandled(true);
    }

    public void handlePacket(FiltList serverFiltList, Supplier<NetworkEvent.Context> supplier) {
        // Do stuff
        FiltPick.CLIENT_FILT_LIST.copyFrom(serverFiltList);
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Syn filtlist S2C completed"));
    }
}

