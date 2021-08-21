package com.bilicraft.danmaku;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class DanmakuText {
    private String string;
    private String code;
    private List<DanmakuText> subText;

    public DanmakuText() {
        this("");
    }

    public DanmakuText(String string, String code, List<DanmakuText> subText) {
        this.string = string;
        this.code = code;
        this.subText = subText;
    }

    public DanmakuText(String string, String code) {
        this(string, code, new ArrayList<>());
    }


    public DanmakuText(String string) {
        this(string, "");
    }


    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCode(List<Formatting> formattings) {
        StringBuilder codes = new StringBuilder();
        for (Formatting f : formattings) {
            codes.append(f.getCode());
        }
        this.code = codes.toString();
    }

    public List<DanmakuText> getSubText() {
        return subText;
    }

    public void setSubText(List<DanmakuText> subText) {
        this.subText = subText;
    }

    public void append(DanmakuText text) {
        this.subText.add(text);
    }

    public void append(String text) {
        this.append(new DanmakuText(text, "", new ArrayList<>()));
    }

    public LiteralText getRenderableText() {
        LiteralText result = new LiteralText(string);
        List<Formatting> formattings = new ArrayList<>();
        for (char code : this.code.toCharArray()) {
            formattings.add(Formatting.byCode(code));
        }

        if (formattings.size() > 0) {
            result.formatted(formattings.toArray(new Formatting[0]));
        }

        for (DanmakuText sub : subText) {
            result.append(sub.getRenderableText());
        }

        return result;
    }

}
