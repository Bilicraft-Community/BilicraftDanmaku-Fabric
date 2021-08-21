package com.bilicraft.danmaku.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.bilicraft.danmaku.utils.PatternUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.player.PlayerEntity;

public class JsonPlayerList
{

    private static final ParameterizedType type = new ParameterizedType()
    {

        @Override
        public Type[] getActualTypeArguments()
        {
            return new Type[] { String.class };
        }

        @Override
        public Type getOwnerType()
        {
            return null;
        }

        @Override
        public Type getRawType()
        {
            return HashSet.class;
        }

    };

    private final Gson gson;

    public File currentFile;
    protected Set<String> list = new HashSet<>();

    public JsonPlayerList()
    {
        this(new GsonBuilder().setPrettyPrinting().create());
    }

    public JsonPlayerList(Gson gson)
    {
        this.gson = gson;
    }

    public void add(PlayerEntity player)
    {
        list.add(player.getUuidAsString());
        list.add(PatternUtils.stripControlCodes(player.getName().asString()));
    }

    public void add(String string)
    {
        list.add(string);
    }

    public void clear()
    {
        list.clear();
    }

    public boolean contains(PlayerEntity player)
    {
        return list.contains(player.getUuidAsString()) || list.contains(PatternUtils.stripControlCodes(player.getName().asString()));
    }

    public boolean contains(String string)
    {
        return list.contains(string);
    }

    public void loadFile(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException
    {
        FileReader reader = null;
        try
        {
            reader = new FileReader(file);
            list = gson.fromJson(reader, type);
        }
        finally
        {
            if (reader != null)
                try
                {
                    reader.close();
                }
                catch (IOException ignored)
                {
                }
            currentFile = file;
        }
    }

    public void remove(PlayerEntity player)
    {
        list.remove(player.getUuidAsString());
        list.remove(PatternUtils.stripControlCodes(player.getName().asString()));
    }

    public void remove(String string)
    {
        list.remove(string);
    }

    public void saveFile(File file) throws IOException
    {
        FileWriter writer = null;
        try
        {
            writer = new FileWriter(file);
            gson.toJson(list, type, writer);
            writer.close();
        }
        finally
        {
            if (writer != null)
                try
                {
                    writer.close();
                }
                catch (IOException ignored)
                {
                }
        }
    }

}
