package net.apeng.filtpick.network;

import net.apeng.filtpick.FiltPick;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetWorkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FiltPick.ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int packetID = 0;

    private static int id() {
        return packetID++;
    }

    public static void register() {
        INSTANCE.registerMessage(
                id(),
                OpenFiltPickScreenC2SPacket.class,
                OpenFiltPickScreenC2SPacket::encode,
                OpenFiltPickScreenC2SPacket::new,
                OpenFiltPickScreenC2SPacket::handle
        );
    }

    public static <MSG> void send2Server(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void send2Client(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
