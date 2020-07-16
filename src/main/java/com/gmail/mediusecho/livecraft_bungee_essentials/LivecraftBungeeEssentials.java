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
import com.gmail.mediusecho.fusion.properties.LangKey;
import com.gmail.mediusecho.livecraft_bungee_essentials.commands.BackCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.commands.ReloadCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.config.BungeeConfig;
import com.gmail.mediusecho.livecraft_bungee_essentials.manager.TeleportManager;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.HomeModule;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.TeleportModule;
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

    private LuckPerms luckPermsApi;

    private List<Module> moduleList;
    private BungeeCommandFramework commandFramework;
    private TeleportManager teleportManager;

    private BungeeConfig config;
    private Map<UUID, BungeeConfig> playerConfigMap;

    @Override
    public void onEnable ()
    {
        instance = this;
        config = BungeeUtil.getPluginConfig();
        playerConfigMap = new HashMap<>();

        luckPermsApi = LuckPermsProvider.get();

        getProxy().registerChannel("lce:message");

        commandFramework = new BungeeCommandFramework(this, this);
        commandFramework.registerDefaultLangKey(LangKey.NO_PERMISSION, Lang.NO_PERMISSION.key);
        commandFramework.registerMainCommand(new ReloadCommand(this), "lcb.command.reload");
        commandFramework.registerMainCommand(new BackCommand(this), "lcb.command.back");

        teleportManager = new TeleportManager(this);

        moduleList = new ArrayList<>();
        moduleList.add(new TeleportModule(this));
        moduleList.add(new HomeModule(this));

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
