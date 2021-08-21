package com.bilicraft.danmaku.server;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class Messenger {

    public static void send(CommandOutput receiver,UUID uuid, Message msg)
    {
        Text tmp = new TranslatableText(msg.key, new Object[0]);
        receiver.sendSystemMessage(tmp,uuid);
    }

    public static void send(CommandOutput receiver,UUID uuid, Message msg, Object... objects)
    {
        Text tmp = new TranslatableText(msg.key, objects);
        receiver.sendSystemMessage(tmp,uuid);
    }

    public static void sendWithColor(CommandOutput receiver,UUID uuid, Message msg, Formatting color)
    {
        Text tmp = new TranslatableText(msg.key, new Object[0]);
        tmp.getStyle().withColor(color);
        receiver.sendSystemMessage(tmp,uuid);
    }

    public static void sendWithColor(CommandOutput receiver, UUID uuid, Message msg, Formatting color, Object... objects)
    {
        Text tmp = new TranslatableText(msg.key, objects);
        tmp.getStyle().withColor(color);
        receiver.sendSystemMessage(tmp,uuid);
    }
}
