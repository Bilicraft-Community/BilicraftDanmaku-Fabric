package com.bilicraft.danmaku.client.gui.screen;

import com.bilicraft.danmaku.BilicraftDanmaku;
import com.bilicraft.danmaku.client.CommentManager;
import com.bilicraft.danmaku.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CommentScreen extends Screen {
    public enum ControlCodeArea{
        c0("[&&]0", 11, 12, 16, 16), // 0 - BLACK
        c1("[&&]1", 26, 12, 16, 16), // 1 - DARK_BLUE
        c2("[&&]2", 41, 12, 16, 16), // 2 - DARK_GREEN
        c3("[&&]3", 56, 12, 16, 16), // 3 - DARK_AQUA
        c4("[&&]4", 71, 12, 16, 16), // 4 - DARK_RED
        c5("[&&]5", 86, 12, 16, 16), // 5 - DARK_PURPLE
        c6("[&&]6", 101, 12, 16, 16), // 6 - GOLD
        c7("[&&]7", 116, 12, 16, 16), // 7 - GRAY
        c8("[&&]8", 11, 27, 16, 16), // 8 - DARK_GRAY
        c9("[&&]9", 26, 27, 16, 16), // 9 - BLUE
        ca("[&&]a", 41, 27, 16, 16), // a - GREEN
        cb("[&&]b", 56, 27, 16, 16), // b - AQUA
        cc("[&&]c", 71, 27, 16, 16), // c - RED
        cd("[&&]d", 86, 27, 16, 16), // d - LIGHT_PURPLE
        ce("[&&]e", 101, 27, 16, 16), // e - YELLOW
        cf("[&&]f", 116, 27, 16, 16), // f - WHITE
        // ck
        cl("[&&]l", 136, 22, 20, 21), // l - BOLD
        cm("[&&]m", 202, 22, 20, 20), // m - STRIKETHROUGH
        cn("[&&]n", 183, 23, 15, 18), // n - UNDERLINE
        co("[&&]o", 160, 22, 19, 20), // o - ITALIC
        cr("[&&]r", 225, 21, 21, 22); // r - RESET

        public final String code;
        public final Rect rect;

        ControlCodeArea(String c, int x, int y, int w, int h)
        {
            code = c;
            rect = new Rect(x, y, w, h);
        }
    }

    public enum LifespanControlArea{
        c0(20, 187, 14), // ++
        c1(2, 203, 9), // +
        c2(-20, 214, 14), // --
        c3(-2, 230, 9); // -

        public final int m;
        public final Rect rect;

        LifespanControlArea(int modifier, int x, int w)
        {
            m = modifier;
            rect = new Rect(x, 106, w, 9);
        }
    }

    public enum ModeArea{
        m0(0, 8, 61), // 0
        m1(1, 68, 60), // 1
        m2(2, 127, 60), // 2
        m3(3, 186, 61); // 3

        public final int id;
        public final Rect rect;

        ModeArea(int i, int x, int w)
        {
            id = i;
            rect = new Rect(x, 47, w, 46);
        }
    }

    public static class Rect{
        public final double x;
        public final double y;
        public final int w;
        public final int h;

        public Rect()
        {
            x = y = w = h = 0;
        }

        public Rect(double x, double y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public boolean checkCollision(double pointX, double pointY)
        {
            return !(pointX < x || pointY < y || pointX > (x + w) || pointY > (y + h));
        }

        public boolean isMouseHovering(double mouseX, double mouseY)
        {
            return (mouseX >= x && mouseY >= y && mouseX < (x + w) && mouseY < (y + h));
        }
    }

    private static final Identifier resTexture = new Identifier("bilicraftdanmaku","textures/gui/comment/gui.png");

    public static boolean settingsOpened = false;
    public static int mode = 0;
    public static int lifespan = 200;



    public CommentScreen() {
        super(new TranslatableText("commit_screen.title"));
    }

    protected TextFieldWidget inputField;
    protected Rect areaSettingsButton;
    protected Rect areaSettings;

    @Override
    public boolean shouldPause(){
        return false;
    }

    @Override
    public void render(MatrixStack matrices,int mouseX, int mouseY, float delta){
        //drawRect(2, height - 14, width - 2, height - 2, Integer.MIN_VALUE);
        this.setFocused(this.inputField);
        this.inputField.setTextFieldFocused(true);
        fill(matrices, 2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(-2147483648));
        this.inputField.render(matrices, mouseX, mouseY, delta);

        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderUtils.bindTexture(resTexture);
        if (areaSettingsButton.isMouseHovering(mouseX, mouseY)) {
            RenderUtils.drawRectTexture(matrices, areaSettingsButton.x, areaSettingsButton.y, 28, 28, 28, 0);
        }
        else {
            RenderUtils.drawRectTexture(matrices, areaSettingsButton.x, areaSettingsButton.y, 28, 28, 0, 0);
        }

        if (settingsOpened){
            RenderUtils.drawRectTexture(matrices, areaSettings.x, areaSettings.y, areaSettings.w, areaSettings.h, 0, 28);
            double x = mouseX - areaSettings.x;
            double y = mouseY - areaSettings.y;
            for (ControlCodeArea area : ControlCodeArea.values()){
                if (area.rect.isMouseHovering(x, y)){
                    RenderUtils.drawRectTexture(matrices, areaSettings.x + area.rect.x, areaSettings.y + area.rect.y, area.rect.w, area.rect.h, area.rect.x, 154 + area.rect.y);
                    break;
                }
            }
            for (ModeArea area : ModeArea.values()){
                if (area.id == mode){
                    RenderUtils.drawRectTexture(matrices, areaSettings.x + area.rect.x, areaSettings.y + area.rect.y, area.rect.w, area.rect.h, area.rect.x, 154 + area.rect.y);
                    break;
                }
            }
            for (ModeArea area : ModeArea.values()){
                if (area.rect.isMouseHovering(x, y)){
                    RenderUtils.drawRectTexture(matrices, areaSettings.x + area.rect.x, areaSettings.y + area.rect.y, area.rect.w, area.rect.h, area.rect.x, 154 + area.rect.y);
                    break;
                }
            }
            for (LifespanControlArea area : LifespanControlArea.values()){
                if (area.rect.isMouseHovering(x, y)){
                    RenderUtils.drawRectTexture(matrices, areaSettings.x + area.rect.x, areaSettings.y + area.rect.y, area.rect.w, area.rect.h, area.rect.x, 154 + area.rect.y);
                    break;
                }
            }
            this.textRenderer.drawWithShadow(matrices,
                    String.format("%.1f", (float) lifespan / 20F),
                    (float)areaSettings.x + 33, (float)areaSettings.y + 105, 0xFFFFFF);
        }

        super.render(matrices,mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        this.inputField.tick();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 1.0D) {
            amount = 1.0D;
        }

        if (amount < -1.0D) {
            amount = -1.0D;
        }

        if (!hasShiftDown()) {
            amount *= 7.0D;
        }

        this.client.inGameHud.getChatHud().scroll(amount);
        return true;
    }

    @Override
    public void init(){
        this.client.keyboard.setRepeatEvents(true);
        this.inputField = new TextFieldWidget(this.textRenderer, 4, this.height - 12, this.width - 4, 12, new TranslatableText("chat.editBox"));
        this.inputField.setMaxLength(100);
        this.inputField.setDrawsBackground(false);
        this.inputField.setTextFieldFocused(true);
        this.inputField.setFocusUnlocked(false);
        areaSettingsButton = new Rect(width - 32, height - 44, 28, 28);
        areaSettings = new Rect(width - 258, height - 171, 254, 126);
        //this.setInitialFocus(this.inputField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 256) {
            this.client.setScreen(null);
            return true;
        } else if (keyCode != 257 && keyCode != 335) {
            if (keyCode == 266) {
                this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
                return true;
            } else if (keyCode == 267) {
                this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
                return true;
            } else {
                return false;
            }
        } else {
            String string = this.inputField.getText().trim();
            if (!string.isEmpty()) {
                CommentManager.INSTANCE.sendRequest(mode, lifespan, string);
            }
            this.client.setScreen(null);
            return true;
        }
    }

    @Override
    public boolean mouseClicked(double par1, double par2, int par3){
        inputField.mouseClicked(par1, par2, par3);

        if (areaSettingsButton.isMouseHovering(par1, par2)){
            settingsOpened = !settingsOpened;
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        if (settingsOpened){
            double x = par1 - areaSettings.x;
            double y = par2 - areaSettings.y;
            for (ControlCodeArea area : ControlCodeArea.values())
            {
                if (area.rect.isMouseHovering(x, y))
                {
                    writeText(area.code);
                    break;
                }
            }
            for (ModeArea area : ModeArea.values())
            {
                if (area.rect.isMouseHovering(x, y))
                {
                    mode = area.id;
                    break;
                }
            }
            for (LifespanControlArea area : LifespanControlArea.values())
            {
                if (area.rect.isMouseHovering(x, y))
                {
                    lifespan += area.m;
                    break;
                }
            }
        }
        return super.mouseClicked(par1, par2, par3);
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        this.client.inGameHud.getChatHud().resetScroll();
    }

//    public void updateScreen() {
//        inputField.updateCursorCounter();
//    }

    public void writeText(String par1Str) {
        inputField.write(par1Str);
        String text = inputField.getText();
        text = text.replace("[&&]","\u00a7");
        inputField.setText(text);
//        int cursorPosition = inputField.getCursor();
//        int selectionEnd = 0;
//        int maxStringLength = 256;
//        try {
//            Field selectionEndField = TextFieldWidget.class.getDeclaredField("selectionEnd");
//            selectionEndField.setAccessible(true);
//            selectionEnd = selectionEndField.getInt(inputField);
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        String text = inputField.getText();
//        String s1 = "";
//        int i = Math.min(cursorPosition, selectionEnd);
//        int j = Math.max(cursorPosition, selectionEnd);
//        int k = maxStringLength - text.length() - (i - selectionEnd);
//        if (text.length() > 0)
//        {
//            s1 = s1 + text.substring(0, i);
//        }
//        int l;
//        if (k < par1Str.length())
//        {
//            s1 = s1 + par1Str.substring(0, k);
//            l = k;
//        }
//        else
//        {
//            s1 = s1 + par1Str;
//            l = par1Str.length();
//        }
//        if (text.length() > 0 && j < text.length())
//            s1 = s1 + text.substring(j);
//        text = s1;
//        inputField.setText(text);
//        inputField.moveCursor(i - selectionEnd + l);
    }
}
