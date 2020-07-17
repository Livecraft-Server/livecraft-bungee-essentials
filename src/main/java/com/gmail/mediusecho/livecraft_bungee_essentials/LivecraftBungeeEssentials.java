/*
 * Copyright (c) 2020 Jacob (MediusEcho)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gmail.mediusecho.livecraft_bungee_essentials;

import com.gmail.mediusecho.fusion.BungeeCommandFramework;
import com.gmail.mediusecho.fusion.LanguageProvider;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.properties.LangKey;
import com.gmail.mediusecho.livecraft_bungee_essentials.commands.BackCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.commands.ReloadCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.config.BungeeConfig;
import com.gmail.mediusecho.livecraft_bungee_essentials.manager.TeleportManager;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.emote.EmoteModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.HomeModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.markdown.MarkdownModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.motd.MotdModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.TeleportModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.WarpModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.BungeeUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class LivecraftBungeeEssentials extends Plugin implements LanguageProvider {

    public static LivecraftBungeeEssentials instance;

    private Map<CommandListener, String> registeredCommands;
    private LuckPerms luckPermsApi;

    private List<Module> moduleList;
    private BungeeCommandFramework commandFramework;
    private TeleportManager teleportManager;

    private TeleportModule teleportModule;
    private HomeModule homeModule;
    private WarpModule warpModule;

    private BungeeConfig config;
    private BungeeConfig warpConfig;
    private Map<UUID, BungeeConfig> playerConfigMap;

    @Override
    public void onEnable ()
    {
        instance = this;
        config = BungeeUtil.getPluginConfig();
        warpConfig = BungeeUtil.getWarpConfig();
        playerConfigMap = new HashMap<>();

        registeredCommands = new HashMap<>();
        luckPermsApi = LuckPermsProvider.get();
        teleportManager = new TeleportManager(this);

        getProxy().registerChannel("lce:message");

        teleportModule = new TeleportModule(this);
        homeModule = new HomeModule(this);
        warpModule = new WarpModule(this);

        moduleList = new ArrayList<>();
        moduleList.add(teleportModule);
        moduleList.add(homeModule);
        moduleList.add(warpModule);
        moduleList.add(new MotdModule(this));
        moduleList.add(new MarkdownModule(this));
        moduleList.add(new EmoteModule(this));

        commandFramework = new BungeeCommandFramework(this, this);
        commandFramework.registerDefaultLangKey(LangKey.NO_PERMISSION, Lang.NO_PERMISSION.key);
        commandFramework.registerDefaultLangKey(LangKey.PLAYER_ONLY, Lang.PLAYER_ONLY.key);
        commandFramework.registerDefaultLangKey(LangKey.UNKNOWN_COMMAND, Lang.UNKNOWN_COMMAND.key);
        commandFramework.registerDependency(LivecraftBungeeEssentials.class, this);
        commandFramework.registerDependency(TeleportManager.class, teleportManager);
        commandFramework.registerDependency(TeleportModule.class, teleportModule);
        commandFramework.registerDependency(HomeModule.class, homeModule);
        commandFramework.registerDependency(WarpModule.class, warpModule);

        registeredCommands.put(new ReloadCommand(), "lcb.command.reload");
        registeredCommands.put(new BackCommand(), "lcb.command.back");

        for (Map.Entry<CommandListener, String> commands : registeredCommands.entrySet()) {
            commandFramework.registerMainCommand(commands.getKey(), commands.getValue());
        }

        for (Module m : moduleList) {
            m.reload();
        }
    }

    public void reload ()
    {
        config.reload();
        for (Module m : moduleList) {
            m.reload();
        }
    }

    /**
     * Adds a command to the pending registration map.
     * These commands will only get registered if they're added
     * before the #onEnable method completes.
     *
     * @param commandListener
     *      The command to register.
     * @param permission
     *      The permission to register this command under.
     */
    public void registerMainCommand (CommandListener commandListener, String permission) {
        registeredCommands.put(commandListener, permission);
    }

    /**
     * Returns this plugins config.yml configuration
     * file
     *
     * @return
     *      Configuration
     */
    public Configuration getConfig () {
        return config.getConfig();
    }

    /**
     * Returns this plugins warp.yml configuration
     *
     * @return
     */
    public BungeeConfig getWarpConfig () {
        return warpConfig;
    }

    /**
     * Returns this plugins command framework
     *
     * @return
     *      BungeeCommandFramework
     */
    public BungeeCommandFramework getCommandFramework () {
        return commandFramework;
    }

    /**
     * Returns this plugins TeleportManager
     *
     * @return
     *      TeleportManager
     */
    public TeleportManager getTeleportManager () {
        return teleportManager;
    }

    /**
     * Returns the LuckPerms api object
     *
     * @return
     *      LuckPerms api
     */
    public LuckPerms getLuckPermsApi () {
        return luckPermsApi;
    }

    /**
     * Guarantees a valid BungeeConfig object for this player.
     * Will create a new configuration file if none are found.
     *
     * @param id
     *      The players UUID
     * @return
     *      BungeeConfig
     */
    public BungeeConfig getGuaranteedPlayerConfig (UUID id)
    {
        if (playerConfigMap.containsKey(id)) {
            return playerConfigMap.get(id);
        }

        BungeeConfig playerConfig = BungeeUtil.getPlayerConfig(id);
        playerConfigMap.put(id, playerConfig);
        return playerConfig;
    }

    @Override
    public String getLangTranslation(@NotNull String s)
    {
        String value = config.getConfig().getString(s, "");
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public void log (Object obj) {
        this.getLogger().log(Level.INFO, "[LivecraftBungeeEssentials] " + obj.toString());
    }
}
