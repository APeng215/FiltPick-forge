package net.apeng.filtpick.networking;

import net.apeng.filtpick.FiltPick;
import net.apeng.filtpick.networking.packet.OpenFiltScreenC2SPacket;
import net.apeng.filtpick.networking.packet.ResetFiltListC2SPacket;
import net.apeng.filtpick.networking.packet.SynFiltListS2CPacket;
import net.apeng.filtpick.networking.packet.SynFiltModesC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetWorkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FiltPick.MOD_ID, "main"),
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
                OpenFiltScreenC2SPacket.class,
                OpenFiltScreenC2SPacket::encode,
                OpenFiltScreenC2SPacket::new,
                OpenFiltScreenC2SPacket::handler
        );
        INSTANCE.registerMessage(
                id(),
                SynFiltModesC2SPacket.class,
                SynFiltModesC2SPacket::encode,
                SynFiltModesC2SPacket::new,
                SynFiltModesC2SPacket::handler
        );
        INSTANCE.registerMessage(
                id(),
                SynFiltListS2CPacket.class,
                SynFiltListS2CPacket::encode,
                SynFiltListS2CPacket::new,
                SynFiltListS2CPacket::handler
        );
        INSTANCE.registerMessage(
                id(),
                ResetFiltListC2SPacket.class,
                ResetFiltListC2SPacket::encode,
                ResetFiltListC2SPacket::new,
                ResetFiltListC2SPacket::handler
        );
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
