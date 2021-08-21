package com.bilicraft.danmaku.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;


public class C2SCommunicateManager {

    public static void send(Identifier channelName, PacketByteBuf buf) {
        ClientPlayNetworking.send(channelName, buf);
    }

//    public static void sendAll(MinecraftServer server, Identifier channelName, PacketByteBuf buf){
//        server.getPlayerManager()
//                .getPlayerList()
//                .forEach(player -> {
//                    C2SCommunicateManager.send(player, channelName, buf);
//                });
//    }
//
//
//    public static void sendAll(Identifier channelName, PacketByteBuf buf){
//        MinecraftServer server = MinecraftClient.getInstance().getServer();
//        C2SCommunicateManager.sendAll(server,channelName,buf);
//    }
}
