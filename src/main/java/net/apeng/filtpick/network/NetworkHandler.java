package net.apeng.filtpick.network;

import net.apeng.filtpick.FiltPick;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = ChannelBuilder.named(ResourceLocation.tryBuild(FiltPick.ID, "network_channel")).simpleChannel();

    public static void registerAll() {
        INSTANCE.messageBuilder(OpenFiltPickScreenC2SPacket.class)
                .encoder(OpenFiltPickScreenC2SPacket::encode)
                .decoder(OpenFiltPickScreenC2SPacket::new)
                .consumerMainThread(OpenFiltPickScreenC2SPacket::handle)
                .add();
    }

    public static <MSG> void send2Server(MSG packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void send2Player(MSG packet, ServerPlayer serverPlayer) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(serverPlayer));
    }

}
