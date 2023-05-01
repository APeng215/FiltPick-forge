package net.apeng.filtpick.networking;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.networking.packet.OpenFiltScreenC2SPacket;
import net.apeng.filtpick.networking.packet.SynFiltListC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetWorkHandler {
    private static SimpleChannel INSTANCE;
    private static int packetID = 0;

    private static int id() {
        return packetID++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(FiltPick.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(SynFiltListC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SynFiltListC2SPacket::new)
                .encoder(SynFiltListC2SPacket::encode)
                .consumerMainThread(SynFiltListC2SPacket::handle)
                .add();
        net.messageBuilder(OpenFiltScreenC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(OpenFiltScreenC2SPacket::new)
                .encoder(OpenFiltScreenC2SPacket::encode)
                .consumerMainThread(OpenFiltScreenC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
