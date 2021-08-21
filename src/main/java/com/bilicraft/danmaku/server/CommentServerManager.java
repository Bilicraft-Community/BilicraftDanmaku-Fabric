package com.bilicraft.danmaku.server;

import com.bilicraft.bilicraftdanmaku.protocol.CommonDanmakuType;
import com.bilicraft.bilicraftdanmaku.protocol.Packet;
import com.bilicraft.bilicraftdanmaku.protocol.client.ClientDanmakuPacket;
import com.bilicraft.bilicraftdanmaku.protocol.server.ServerDanmakuPacket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.bilicraft.danmaku.BilicraftDanmaku;
import com.bilicraft.danmaku.utils.PatternUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;

public class CommentServerManager {
    public static final CommentServerManager INSTANCE = new CommentServerManager();

    public static final String TARGET = "BilicraftComment|Client";
    public static final String LOCAL = "BilicraftComment|Server";

    public static final int logLimit = 33554432;

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    public final JsonPlayerList whitelist = new JsonPlayerList();
    public final JsonPlayerList blacklist = new JsonPlayerList();
//    public final PerPlayerTimeMarker marker = new PerPlayerTimeMarker("timeComment", true);
    public final Logger chatLogger = Logger.getLogger(LOCAL);

    private CommentServerManager(){
        this.networkHook();
        try{
            whitelist.loadFile(new File(BilicraftDanmaku.rootDir, "BcC_Whitelist.json"));
        }catch (FileNotFoundException e) {
            try {
                whitelist.saveFile(whitelist.currentFile);
            } catch (IOException e1) {
                BilicraftDanmaku.INSTANCE.getLogger().fatal("error creating default whitelist file: " + e1);
                throw new RuntimeException(e1);
            }
        }catch (Exception e){
            BilicraftDanmaku.INSTANCE.getLogger().fatal("error loading whitelist file: " + e);
            throw new RuntimeException(e);
        }
        try{
            blacklist.loadFile(new File(BilicraftDanmaku.rootDir, "BcC_Blacklist.json"));
        }catch (FileNotFoundException e){
            try{
                blacklist.saveFile(blacklist.currentFile);
            }catch (IOException e1){
                BilicraftDanmaku.INSTANCE.getLogger().fatal("error creating default blacklist file: " + e1);
                throw new RuntimeException(e1);
            }
        }catch (Exception e){
            BilicraftDanmaku.INSTANCE.getLogger().fatal("error loading blacklist file: " + e);
            throw new RuntimeException(e);
        }
        try{
            File logPath = new File(BilicraftDanmaku.rootDir, "BcC_CommentLog_%g.log");
            chatLogger.addHandler(new FileHandler(logPath.getPath(), logLimit, 4, true){
                {
                    setLevel(Level.ALL);
                    setFormatter(new Formatter()
                    {
                        final String LINE_SEPARATOR = System.getProperty("line.separator");
                        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        @Override
                        public String format(LogRecord record)
                        {
                            return dateFormat.format(record.getMillis()) +
                                    " " +
                                    record.getMessage() +
                                    LINE_SEPARATOR;
                        }
                    });
                }

                @Override
                public synchronized void close() throws SecurityException
                {
                }
            });
        }
        catch (Exception e)
        {
            BilicraftDanmaku.INSTANCE.getLogger().fatal("error adding comment logger: " + e);
        }
    }

    public PacketByteBuf createDisplayRequest(int mode, int lifespan, String s){
        try{
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(mode)
                    .writeInt(lifespan)
                    .writeCharSequence(s, Charset.defaultCharset());
            return buf;
        }catch (Exception e){
            BilicraftDanmaku.INSTANCE.getLogger().fatal("error creating display request: " + e);
            return null;
        }
    }

    public void networkHook(){
        ServerPlayNetworking.registerGlobalReceiver(BilicraftDanmaku.BILICRAFTCOMMENT,(server, player, handler, buf, responseSender) -> {
            UUID uuid = player.getUuid();
//            if (ServerConfigs.whitelistMode && !whitelist.contains(sender)){
//                Messenger.sendWithColor(sender, uuid, Message.msgNotInWhitelist, Formatting.RED);
//                return;
//            }
//            if (blacklist.contains(sender)){
//                Messenger.sendWithColor(sender, uuid, Message.msgInBlacklist, Formatting.RED);
//                return;
//            }
//            if (!marker.checkTimeIfValid(sender, sender.world.getTime(), ServerConfigs.commentInterval, false)){
//                Messenger.sendWithColor(sender, uuid, Message.msgTooFastToComment, Formatting.RED);
//                return;
//            }
            //String jsonString = buf.readString();
            String buffString = new String(buf.getWrittenBytes(), StandardCharsets.UTF_8);
            buffString = buffString.substring(buffString.indexOf("{"));
            ServerDanmakuPacket danmakuPacket = Packet.deserialize(buffString, ServerDanmakuPacket.class);
            CommonDanmakuType mode = danmakuPacket.getType();
            long lifespan = danmakuPacket.getLifespan();
            String text = danmakuPacket.getJsonText();

//            if (!ServerConfigs.isModeAllowed(mode) || lifespan < ServerConfigs.minLifespan || lifespan > ServerConfigs.maxLifespan || PatternUtils.stripControlCodes(text).isEmpty()) {
//                Messenger.sendWithColor(sender, uuid, Message.msgInvalidArguments, Formatting.RED);
//                return;
//            }
            chatLogger.info(String.format("[username:%s] [mode:%s] [lifespan:%d] %s", PatternUtils.stripControlCodes(player.getName().asString()), mode.toString(), lifespan, text));
            //marker.markTime(sender, sender.world.getTime());


//            buf2.writeInt(mode).writeInt(lifespan).writeBoolean(ServerConfigs.showSenderNameOnComment);
//            buf2.writeText(sender.getDisplayName());
            //finalBuf.writeString(json.toString());
//            PacketByteBuf finalBuf = buf2;

            ClientDanmakuPacket packet = ClientDanmakuPacket
                    .builder()
                    .type(danmakuPacket.getType())
                    .sender(player.getUuid())
                    .playerName(player.getDisplayName().asString())
                    .showName(ServerConfigs.showSenderNameOnComment)
                    .jsonText(danmakuPacket.getJsonText())
                    .lifespan(danmakuPacket.getLifespan())
                    .build();

            PacketByteBuf finalBuf = new PacketByteBuf(Unpooled.buffer());
            finalBuf.writeByteArray(packet.serializeBytes());

            server.execute(() -> server.getPlayerManager()
                    .getPlayerList()
                    .forEach(revicer -> ServerPlayNetworking.send(revicer, BilicraftDanmaku.BILICRAFTCOMMENT, finalBuf)));

//            FMLProxyPacket packet = createDisplayRequest(mode, lifespan, ServerConfigs.appendUsername ? PatternUtils.stripControlCodes(sender.getName().asString()) + ": " + text : text);
//            channel.sendToAll(packet);
        });
    }

//    public void registerCommands(FMLServerStartingEvent event)
//    {
//        event.registerServerCommand(new CommandReload());
//        event.registerServerCommand(new CommandBroadcast());
//        event.registerServerCommand(new CommandWhitelistAdd());
//        event.registerServerCommand(new CommandWhitelistRemove());
//        event.registerServerCommand(new CommandBlacklistAdd());
//        event.registerServerCommand(new CommandBlacklistRemove());
//    }
}
