//package com.bilicraft.comments.server.command;
//
//import com.bilicraft.clientui.BilicraftClientUIServer;
//import com.bilicraft.comments.server.CommentServerManager;
//import com.bilicraft.comments.server.Message;
//import com.bilicraft.comments.server.Messenger;
//import com.mojang.brigadier.Command;
//import com.mojang.brigadier.CommandDispatcher;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.CommandSource;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.command.ServerCommandSource;
//
//import static net.minecraft.server.command.CommandManager.literal;
//
//public class CommandBlacklistAdd implements Command<ServerCommandSource>,CustomCommand<ServerCommandSource> {
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (args.length > 0)
//        {
//
//        }
//        else
//            throw new WrongUsageException(Message.msgBlacklistUsageAdd.key);
//    }
//
//    @Override
//    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
//        dispatcher.register(
//                literal(this.getName())
//                        .requires( cs -> {
//                            return cs.hasPermissionLevel(this.getRequiredPermissionLevel());
//                        })
//                        .executes(this)
//        );
//    }
//
//    public String getName()
//    {
//        return "bcc_blacklist_add";
//    }
//
//    @Override
//    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
//        try{
//            CommentServerManager.INSTANCE.blacklist.loadFile(CommentServerManager.INSTANCE.blacklist.currentFile);
//        }catch (Exception e){
//            BilicraftClientUIServer.logger.fatal("error loading blacklist file: " + e.toString());
//            throw new RuntimeException(e);
//        }
//        CommentServerManager.INSTANCE.blacklist.add(args[0]);
//        try{
//            CommentServerManager.INSTANCE.blacklist.saveFile(CommentServerManager.INSTANCE.blacklist.currentFile);
//        }catch (Exception e){
//            BilicraftClientUIServer.logger.fatal("error saving blacklist file: " + e.toString());
//            throw new RuntimeException(e);
//        }
//        Messenger.sendWithColor(sender, Message.msgBlacklistAdded, TextFormatting.RED, args[0]);
//        return 0;
//    }
//
//    public int getRequiredPermissionLevel() {
//        return 3;
//    }
//
////    @Override
////    public String getUsage(ICommandSender arg0)
////    {
////        return Message.msgBlacklistUsageAdd.key;
////    }
//}
