package com.bilicraft.danmaku.client;

import com.bilicraft.bilicraftdanmaku.protocol.CommonDanmakuType;
import com.bilicraft.bilicraftdanmaku.protocol.Packet;
import com.bilicraft.bilicraftdanmaku.protocol.client.ClientDanmakuPacket;
import com.bilicraft.bilicraftdanmaku.protocol.server.ServerDanmakuPacket;
import com.bilicraft.danmaku.BilicraftDanmaku;
import com.bilicraft.danmaku.BilicraftDanmakuClient;
import com.bilicraft.danmaku.DanmakuText;
import com.bilicraft.danmaku.client.gui.screen.CommentScreen;
import com.bilicraft.danmaku.utils.C2SCommunicateManager;
import com.bilicraft.danmaku.utils.JsonUtil;
import com.bilicraft.danmaku.utils.PatternUtils;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Formatting;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommentManager{

    public static final CommentManager INSTANCE = new CommentManager();

    private static final String TARGET = "BilicraftComment|Server";
    private static final String LOCAL = "BilicraftComment|Client";
    private static final Gson gson = new Gson();

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

//    private FMLEventChannel channel;

    protected final List<Comment> comments = new CopyOnWriteArrayList<>();
    protected final KeyBinding keyOpenCommentGui;
    private long ticks = 0L;

    private CommentManager()
    {
        //MinecraftForge.EVENT_BUS.register(this);

        keyOpenCommentGui = new KeyBinding("key.openCommentGui", 89, "key.categories.multiplayer");
        KeyBindingHelper.registerKeyBinding(keyOpenCommentGui);

        //BilicraftComments.init();
        this.inputHandler();
        this.networkHandler();
        this.tickHandler();
    }

    public void inputHandler(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(keyOpenCommentGui.isPressed()){
                if(client != null && client.currentScreen == null){
                    client.setScreen(new CommentScreen());
                }
            }
        });
    }

    public void networkHandler(){
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> comments.clear());

        ClientPlayNetworking.registerGlobalReceiver(BilicraftDanmaku.BILICRAFTCOMMENT, (client, handler, buf, responseSender) -> {
//            String jsonString = new String(buf.getWrittenBytes());
//            //String jsonString = buf.readText().asString();
//            JsonObject json = new Gson().fromJson(jsonString,JsonObject.class);
//            JsonObject danmakuJson = new JsonObject();
            String serverMessageString = new String(buf.getWrittenBytes(), StandardCharsets.UTF_8);
            serverMessageString = serverMessageString.substring(serverMessageString.indexOf("{"));

            if(!JsonUtil.isJson(serverMessageString)){
                System.out.println("接收到来自服务器的无效弹幕响应: "+serverMessageString);
                return;
            }

            ClientDanmakuPacket clientDanmakuPacket = Packet.deserialize(serverMessageString,ClientDanmakuPacket.class);

            DanmakuText danmakuText = gson.fromJson(clientDanmakuPacket.getJsonText(),DanmakuText.class);

//            int mode = buf.readInt();
//            int lifespan = buf.readInt();
//            boolean showSenderNameOnComment = buf.readBoolean();
//            String sender = buf.readText().asString();
//            String text = buf.readText().asString();
            System.out.println("CommentRecived:" + clientDanmakuPacket.getJsonText());
            if (!PatternUtils.stripControlCodes(clientDanmakuPacket.getJsonText()).isEmpty()) {
                Comment comment = new Comment(clientDanmakuPacket.getType(), danmakuText.getRenderableText(), clientDanmakuPacket.getLifespan(), clientDanmakuPacket.getPlayerName(), clientDanmakuPacket.isShowName(), ticks);
                comment.onAdd();
                comments.add(comment);
            }
        });
    }

    //RenderGameOverlayEvent.Post event
    public void renderHook(MatrixStack matrices, float delta) {
        if (!comments.isEmpty()){
            Comment.prepare();
            for (Comment comment : comments){
                if (comment.isDead(ticks)){
                    comment.onRemove();
                    comments.remove(comment);
                    continue;
                }
                comment.update(ticks, delta);
                comment.draw(matrices);
            }
        }
    }

    public void sendRequest(int mode, int lifespan, String s){
        try{
//            PacketByteBuf buf = PacketByteBufs.create();
//            buf.writeInt(mode).writeInt(lifespan);
//            buf.writeText(new LiteralText(s));
//
            DanmakuText text = parseText(s);

            ServerDanmakuPacket danmakuPacket =  ServerDanmakuPacket.builder().type(this.getByCode(mode))
                    .lifespan(lifespan).jsonText(new Gson().toJson(text,DanmakuText.class)).build();

//            String jj = new Gson().toJson(text,DanmakuText.class);
//
//            JsonObject json = new JsonObject();
//            json.addProperty("mode",mode);
//            json.addProperty("lifespan",lifespan);
//            json.addProperty("text",jj);
            PacketByteBuf buf = PacketByteBufs.create();
            //buf.writeString(json.toString());
            buf.writeByteArray(danmakuPacket.serializeBytes());
            C2SCommunicateManager.send(BilicraftDanmaku.BILICRAFTCOMMENT, buf);
        }catch (Exception e){
            BilicraftDanmaku.INSTANCE.getLogger().fatal("error sending comment request: " + e);
        }
    }

    public void tickHandler(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> ticks = ticks + 1);
    }

    public DanmakuText parseText(String str){
        DanmakuText root = new DanmakuText();
        if(!str.contains("§")){
            root = new DanmakuText(str);
        }else{
            String formattings = "";
            String[] tempArray = str.split("§");
            for (int i = 0;i<tempArray.length;i++){
                if(i > 0){
                    Formatting formatting = Formatting.byCode(tempArray[i].toCharArray()[0]);
                    if(formatting == Formatting.RESET){
                        formattings = "";
                    }else{
                        formattings += tempArray[i].toCharArray()[0];
                    }
                    if(tempArray[i].length() > 1){
                        root.append(new DanmakuText(tempArray[i].substring(1),formattings));
                    }
                }else{
                    root.append(new DanmakuText(tempArray[i],formattings));
                }
            }
        }
        return root;
    }

    public CommonDanmakuType getByCode(int code){
        return switch (code) {
            case 1 -> CommonDanmakuType.TOP;
            case 2 -> CommonDanmakuType.BOTTOM;
            case 3 -> CommonDanmakuType.RESERVE;
            default -> CommonDanmakuType.NORMAL;
        };
    }

}
