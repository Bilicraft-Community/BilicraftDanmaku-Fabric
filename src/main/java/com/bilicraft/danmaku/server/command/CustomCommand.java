package com.bilicraft.danmaku.server.command;

import com.mojang.brigadier.CommandDispatcher;

public interface CustomCommand<T>{
    void register(CommandDispatcher<T> dispatcher);
}
