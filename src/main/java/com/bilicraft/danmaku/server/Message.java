package com.bilicraft.danmaku.server;

public enum Message {
    msgInternalError("BcC_InternalError"),
    msgNotInWhitelist("BcC_NotInWhitelist"),
    msgInBlacklist("BcC_InBlacklist"),
    msgTooFastToComment("BcC_TooFastToComment"),
    msgInvalidArguments("BcC_InvalidArguments"),
    msgBroadcastUsage("BcC_Broadcast_Usage"),
    msgWhitelistUsageAdd("BcC_Whitelist_Usage_Add"),
    msgWhitelistUsageRemove("BcC_Whitelist_Usage_Remove"),
    msgWhitelistAdded("BcC_Whitelist_Added"),
    msgWhitelistRemoved("BcC_Whitelist_Removed"),
    msgBlacklistUsageAdd("BcC_Blacklist_Usage_Add"),
    msgBlacklistUsageRemove("BcC_Blacklist_Usage_Remove"),
    msgBlacklistAdded("BcC_Blacklist_Added"),
    msgBlacklistRemoved("BcC_Blacklist_Removed"),
    msgNoPermission("BcC_NoPermission"),
    msgReloaded("BcC_Reloaded"),
    msgReloadUsage("BcC_Reload_Usage");

    public final String key;

    Message(String key)
    {
        this.key = key;
    }
}
