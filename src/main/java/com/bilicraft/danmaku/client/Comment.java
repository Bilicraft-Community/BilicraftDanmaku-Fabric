package com.bilicraft.danmaku.client;

import com.bilicraft.bilicraftdanmaku.protocol.CommonDanmakuType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class Comment {
    public static MinecraftClient client = null;
    public static TextRenderer renderer = null;

    public static int width = 320;
    public static int height = 240;
    public static int numSlots = 18;

    public static boolean isSlotOccupied(int slot, CommonDanmakuType mode, Comment exclusion, int stress, long ticks) {
        for (Comment c : CommentManager.INSTANCE.comments) {
            if (c.slot != slot || c.mode != mode || c == exclusion) {
                continue;
            }
            switch (mode) {
                case NORMAL:
                    if (c.x + (c.textWidth() >> stress) + 2 > width)
                        return true;
                    break;
                case TOP:
                case BOTTOM:
                    if (c.lifespan <= 0 || (ticks < c.ticksCreated + ((c.lifespan + c.expandedLife) >> stress)))
                        return true;
                    break;
                case RESERVE:
                    int w = c.textWidth();
                    if (c.x + (0 >> stress) < 2)
                        return true;
                    break;
            }
        }
        return false;
    }

    public static void prepare()
    {
        client = MinecraftClient.getInstance();
        renderer = client.textRenderer;

        width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        height = MinecraftClient.getInstance().getWindow().getScaledHeight();
//        width = res.getScaledWidth();
//        height = res.getScaledHeight();
        numSlots = (int) ((float) (height - 60) / (float) (renderer.fontHeight + 1));
    }

    public final CommonDanmakuType mode;
    public final Text text;
    public final long lifespan;
    public final long ticksCreated;
    public final String sender;
    public final boolean showSenderNameOnComment;

    public int slot = -1;
    public int expandedLife = 0;

    public int x;
    public int y;
    public int color; // unused
    public boolean shadow; // unused

    public Comment(CommonDanmakuType mode, Text text, long lifespan, String sender, boolean showSenderNameOnComment, long ticks){
        this.mode = mode;
        this.text = text;
        this.lifespan = lifespan;
        this.ticksCreated = ticks;
        this.sender = sender;
        this.showSenderNameOnComment = showSenderNameOnComment;
    }

    public void assignSlot(long ticks) {
        if (slot == -1){
            int s = 0;
            switch (mode){
                case NORMAL: // Normal
                    do{
                        for (int i = 0; i < numSlots; i++) {
                            int j = (int) Math.round(Math.random() * (numSlots - 1));
                            if (!isSlotOccupied(j, mode, this, s, ticks)) {
                                slot = j;
                                break;
                            }
                        }
                        if (slot != -1)
                            break;
                    }
                    while (s++ < 3);
                    break;
                case TOP: // Top
                    do{
                        for (int i = 0; i < numSlots; i++){
                            if (!isSlotOccupied(i, mode, this, s, ticks)){
                                slot = i;
                                break;
                            }
                        }
                        if (slot != -1) {
                            break;
                        }
                    }while (s++ < 3);
                    break;
                case BOTTOM: // Bottom
                    do{
                        for (int i = numSlots - 1; i >= 0; i--){
                            if (!isSlotOccupied(i, mode, this, s, ticks)){
                                slot = i;
                                break;
                            }
                        }
                        if (slot != -1) {
                            break;
                        }
                    }while (s++ < 3);
                    break;
                case RESERVE: // Backward
                    do{
                        for (int i = 0; i < numSlots; i++){
                            int j = (int) Math.round(Math.random() * (numSlots - 1));
                            if (!isSlotOccupied(j, mode, this, s, ticks)){
                                slot = j;
                                break;
                            }
                        }
                        if (slot != -1) {
                            break;
                        }
                    }while (s++ < 3);
                    break;
            }
        }
        if (slot == -1) {
            expandedLife = Math.max(0, (int) (ticks - ticksCreated));
        }
    }

    public void draw(MatrixStack matrixStack){
        matrixStack.push();
        matrixStack.translate( x, y, 0.0D);
        renderer.drawWithShadow(matrixStack,showSenderNameOnComment? new LiteralText(sender +": " ).append(text) : text, 0, 0, 0xFFFFFF);
        matrixStack.pop();
    }

    public boolean isDead(long ticks){
        if (slot == -1) {
            return false;
        }
        return lifespan > 0 && (ticks >= ticksCreated + lifespan + expandedLife);
    }

    public void onAdd(){
        prepare();
        update(ticksCreated, 0F);
    }

    public void onRemove() {
        slot = -1;
        try{
            // client.inGameHud.getChatHud().addMessage(new LiteralText("<"+ sender +"> ").append(text));
        }catch (Throwable ignored){

        }
    }

    public int textWidth(){
        return renderer.getWidth(showSenderNameOnComment? new LiteralText(sender +": " ).append(text) : text);
    }

    public void update(long ticks, float partialTicks){
        if (slot == -1) {
            assignSlot(ticks);
        }
        if (slot != -1){
            float f1 = Math.min(((float) (ticks - ticksCreated) + partialTicks) / (float) (lifespan + expandedLife), 1.0F);
            float f2;
            int w = textWidth();
            switch (mode) {
                case NORMAL -> {
                    f2 = 1F - f1;
                    x = (int) (f2 * (width + w)) - w;
                }
                case TOP, BOTTOM -> x = (width - w) >> 1;
                case RESERVE -> {
                    f2 = f1;
                    x = (int) (f2 * (width + w)) - w;
                }
            }
            y = 2 + slot * (renderer.fontHeight + 1);
        }
    }
}
