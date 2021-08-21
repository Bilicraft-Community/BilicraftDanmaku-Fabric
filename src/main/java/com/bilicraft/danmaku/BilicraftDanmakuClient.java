package com.bilicraft.danmaku;

import com.bilicraft.danmaku.client.CommentManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BilicraftDanmakuClient implements ClientModInitializer{
	public static BilicraftDanmakuClient INSTANCE;

	@Override
	public void onInitializeClient() {
		System.out.println("Client Side init!!!!");
		CommentManager.setup();
		INSTANCE = this;
	}

}
