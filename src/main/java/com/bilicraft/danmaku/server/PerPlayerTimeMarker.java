//package com.bilicraft.comments.server;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.server.network.ServerPlayerEntity;
//
//public class PerPlayerTimeMarker {
//    private static final String TAGBASE = "BcC|S:PerPlayerTimer";
//
//    private final String name;
//    private final boolean persistent;
//
//    public PerPlayerTimeMarker(String name)
//    {
//        this.name = name;
//        this.persistent = false;
//    }
//
//    public PerPlayerTimeMarker(String name, boolean persistent)
//    {
//        this.name = name;
//        this.persistent = persistent;
//    }
//
//    public boolean checkTimeIfValid(PlayerEntity player, long time, long delay)
//    {
//        return this.checkTimeIfValid(player, time, delay, true);
//    }
//
//    public boolean checkTimeIfValid(PlayerEntity player, long time, long delay, boolean auto)
//    {
//        long lastMark = getLastMark(player);
//        if (time < lastMark)
//        {
//            this.markTime(player, time);
//            return false;
//        }
//        else if (lastMark + delay <= time)
//        {
//            if (auto)
//                this.markTime(player, time);
//            return true;
//        }
//        return false;
//    }
//
//    public void clear(PlayerEntity player)
//    {
//        this.getBase(player).putLong(name, 0L);
//    }
//
//    private NbtCompound getBase(PlayerEntity player)
//    {
//        if (persistent){
//            if (!tmp.contains(PlayerEntity.PERSISTED_NBT_TAG, 10)) {
//                tmp.put(EntityPlayer.PERSISTED_NBT_TAG, new NbtCompound());
//            }
//            tmp = tmp.getCompound(EntityPlayer.PERSISTED_NBT_TAG);
//        }
//        if (!tmp.contains(TAGBASE, 10)) {
//            tmp.put(TAGBASE, new NbtCompound());
//        }
//        return tmp.getCompound(TAGBASE);
//    }
//
//    public long getLastMark(PlayerEntity player)
//    {
//        return this.getBase(player).getLong(name);
//    }
//
//    public void markTime(PlayerEntity player, long time)
//    {
//        this.getBase(player).putLong(name, time);
//    }
//
//}
