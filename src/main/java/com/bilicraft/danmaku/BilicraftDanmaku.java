package com.bilicraft.danmaku;

import com.bilicraft.danmaku.server.CommentServerManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class BilicraftDanmaku implements ModInitializer {
    //TODO:通道命名空间统一修改为“bilicraft”而不是“bilicraftclientui”
    public static final String modid = "bilicraftclientui";
    public static final Identifier BILICRAFTCOMMENT = new Identifier(modid, "bilicraftdanmaku");
    private final Logger logger = LogManager.getFormatterLogger("BilicraftDanmaku");
    public static File rootDir;
    public static BilicraftDanmaku INSTANCE;
    @Override
    public void onInitialize() {
        System.out.println("Bilicraft Danmaku Mod Server Side init!!!!");
        CommentServerManager.setup();
    }

    public Logger getLogger() {
        return logger;
    }
}
