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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.ping;

import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.Permission;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.ping.groups.Group;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.ping.groups.RandomGroup;
import com.gmail.mediusecho.livecraft_bungee_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.StringUtil;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingModule extends Module {

    private final Pattern pingPattern = Pattern.compile("\\B@\\w+");

    private List<Group> groupList;
    private List<String> whitelistedCommands;

    private String playerHighlight;
    private boolean checkCommands;

    public PingModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.PING_ENABLED);
        groupList = new ArrayList<>();
    }

    @Override
    protected void onReload()
    {
        checkCommands = Settings.PING_COMMANDS_ENABLED.getValue();
        playerHighlight = ChatColor.translateAlternateColorCodes('&', Settings.PING_PLAYER_HIGHLIGHT.getValue());

        Configuration config = plugin.getConfig();
        whitelistedCommands = config.getStringList(Settings.PING_WHITELISTED_COMMANDS_POINTER.getPath());
        groupList.clear();

        if (Settings.PING_RANDOM_GROUP_ENABLED.getValue())
        {
            String path = Settings.PING_RANDOM_GROUP_POINTER.getPath();
            String highlight = config.getString(path + ".highlight");
            List<String> aliases = config.getStringList(path + ".aliases");

            Group group = new RandomGroup("random", highlight);
            for (String alias : aliases) {
                group.addAlias(alias);
            }
            groupList.add(group);
        }

        if (Settings.PING_GROUPS_ENABLED.getValue())
        {
            Collection<? extends String> groups = config.getSection(Settings.PING_GROUPS_POINTER.getPath()).getKeys();
            for (String key : groups)
            {
                String path = Settings.PING_GROUPS_POINTER.getPath() + "." + key;

                String highlight = config.getString(path + ".highlight");
                List<String> aliases = config.getStringList(path + ".aliases");
                Group group = new Group(key, highlight);

                for (String alias : aliases) {
                    group.addAlias(alias);
                }

                this.groupList.add(group);
            }
        }
    }

    @EventHandler
    public void onChat (@NotNull ChatEvent event)
    {
        Connection connection = event.getSender();
        ProxiedPlayer player = null;

        if (connection instanceof ProxiedPlayer)
        {
            ProxiedPlayer p = (ProxiedPlayer)connection;
            if (!Permission.PING_CHAT.hasPermission(p)) {
                return;
            }
            plugin.log(p.getName() + " has permission");
            player = p;
        }

        if (event.isCommand() && checkCommands)
        {
            String command = event.getMessage().split(" ")[0];
            if (!whitelistedCommands.contains(command)) {
                return;
            }
        }

        plugin.log("parsing pings for chat event");
        event.setMessage(parsePings(player, event.getMessage()));
    }

    public String parsePings (ProxiedPlayer player, String string)
    {
        Matcher matcher = pingPattern.matcher(string);
        List<ProxiedPlayer> pingedPlayers = new ArrayList<>();
        ProxyServer proxyServer = ProxyServer.getInstance();

        while (matcher.find())
        {
            String mention = matcher.group(0);
            String name = mention.replace("@", "").toLowerCase();

            // Check groups
            for (Group group : groupList) {
                string = group.parseMessage(mention, name, string, player, pingedPlayers);
            }

            // Look for players by their nickname
            Collection<ProxiedPlayer> matchedPlayers = proxyServer.matchPlayer(name);
            if (matchedPlayers.size() == 0 && Settings.PING_SEARCH_NICKNAMES.getValue())
            {
                for (ProxiedPlayer p : proxyServer.getPlayers())
                {
                    String strippedName = ChatColor.stripColor(p.getDisplayName().toLowerCase());
                    if (strippedName.contains(name)) {
                        matchedPlayers.add(p);
                    }
                }
            }

            plugin.log("matched players: " + matchedPlayers.size());

            if (matchedPlayers.size() == 1)
            {
                ProxiedPlayer p = matchedPlayers.iterator().next();
                if (p != null && p.isConnected() && !pingedPlayers.contains(p))
                {
                    String prefix = string.substring(0, string.indexOf(mention));
                    String suffix = StringUtil.getLastUsedColorCode(prefix);

                    string = string.replaceFirst(mention, playerHighlight.replace("{1}", mention) + suffix);
                    pingedPlayers.add(p);
                }
            }
        }

        Map<String, StringBuilder> pingedServers = new HashMap<>();
        for (ProxiedPlayer p : pingedPlayers)
        {
            String serverName = p.getServer().getInfo().getName();
            if (!pingedServers.containsKey(serverName)) {
                pingedServers.put(serverName, new StringBuilder());
            }
            pingedServers.get(serverName).append(p.getName()).append(",");
        }

        for (Map.Entry<String, StringBuilder> entry : pingedServers.entrySet())
        {
            ServerInfo serverInfo = proxyServer.getServerInfo(entry.getKey());
            if (serverInfo == null) {
                continue;
            }

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ping");

            StringBuilder sb = entry.getValue();
            sb.setLength(sb.length() - 1);
            out.writeUTF(sb.toString());

            serverInfo.sendData("lce:message", out.toByteArray());
        }

        return string;
    }
}
