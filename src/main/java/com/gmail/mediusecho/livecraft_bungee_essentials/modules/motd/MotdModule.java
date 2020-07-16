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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.motd;

import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.settings.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotdModule extends Module {

    private final Pattern boldPattern = Pattern.compile("&l(.*?)&");

    private int lineWidth;
    private String maintenanceReason;
    private MotdData maintenanceMotd;
    private boolean maintenanceMode;

    private List<MotdData> motdDataList;
    private List<String> maintenanceWhitelist;
    private Map<Character, Integer> characterWidthMap;

    // Stores the last used ip address of each player.
    // This is necessary to use the %player% variable in motd's
    // as there is no way to query which player is pinging the server
    // until after they have connected.
    private Map<String, String> ipLookup;

    public MotdModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.MOTD_ENABLED);

        motdDataList = new ArrayList<>();
        maintenanceWhitelist = new ArrayList<>();
        characterWidthMap = new HashMap<>();
        ipLookup = new HashMap<>();

        setCharacterWidth(".i';:!|,", 1);
        setCharacterWidth("l", 2);
        setCharacterWidth("\"[]{}() *", 3);
        setCharacterWidth("abcdefghjkmnopqrstuvwxyz1234567890-_#$%^&/?", 4);
        setCharacterWidth("<>", 5);
        setCharacterWidth("@", 6);
        setCharacterWidth("❤", 7);
    }

    @Override
    protected void onReload()
    {
        lineWidth = Settings.MOTD_LINE_WIDTH.getValue();
        maintenanceReason = ChatColor.translateAlternateColorCodes('&', Settings.MOTD_KICK_MESSAGE.getValue());
        maintenanceMotd = new MotdData(Settings.MOTD_MAINTENANCE_MOTD.getValue(), this);
        maintenanceMode = Settings.MOTD_MAINTENANCE_MODE.getValue();

        motdDataList.clear();
        maintenanceWhitelist.clear();
        Configuration config = plugin.getConfig();

        maintenanceWhitelist.addAll(config.getStringList(Settings.MOTD_WHITELIST_POINTER.getPath()));
        motdDataList.add(new MotdData(Settings.MOTD_FALLBACK.getValue(), this));
        for (String motd : config.getStringList(Settings.MOTD_POINTER.getPath())) {
            motdDataList.add(new MotdData(motd, this));
        }
    }

    @EventHandler
    public void onServerPing (@NotNull ProxyPingEvent event)
    {
        if (!maintenanceMode)
        {
            String address = event.getConnection().getAddress().getAddress().toString();
            MotdData data = getNextMotd(address);
            String motd = data.getMotd();

            // This motd contains a dynamic %player% variable that needs to be centered after it's set
            if (!data.isCached()) {
                motd = getCenteredMotd(motd.replace("%player%", ipLookup.get(address)));
            }

            event.getResponse().setDescription(motd);
        }

        else {
            event.getResponse().setDescription(maintenanceMotd.getMotd());
        }

        int count = plugin.getProxy().getOnlineCount();
        int index = 0;
        int max = plugin.getProxy().getConfig().getPlayerLimit();
        ServerPing.PlayerInfo[] playerInfo = new ServerPing.PlayerInfo[count];
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            playerInfo[index++] = new ServerPing.PlayerInfo(player.getName(), player.getUniqueId());
        }
        event.getResponse().setPlayers(new ServerPing.Players(max, count, playerInfo));
    }

    @EventHandler
    public void onPlayerLogin (@NotNull LoginEvent event)
    {
        String name = event.getConnection().getName();
        if (maintenanceMode)
        {
            if (!maintenanceWhitelist.contains(name))
            {
                event.setCancelled(true);
                event.setCancelReason(new TextComponent(maintenanceReason));
                return;
            }
        }

        InetSocketAddress address = event.getConnection().getAddress();
        if (address == null) {
            return;
        }
        ipLookup.put(address.getAddress().toString(), name);
    }

    @NotNull
    public String getCenteredMotd (@NotNull String motd)
    {
        String[] splitMotd = motd.split("/n");
        String[] lines = new String[]{"", ""};

        int count = Math.min(splitMotd.length, 2);
        for (int i = 0; i < count; i++)
        {
            String line = splitMotd[i] + "&r";
            String s = ChatColor.translateAlternateColorCodes('&',line);
            String ss = ChatColor.stripColor(s);

            int width = 0;
            for (int j = 0; j < ss.length(); j++) {
                width += characterWidthMap.getOrDefault(Character.toLowerCase(ss.charAt(j)), 0) + 1;
            }

            Matcher matcher = boldPattern.matcher(line);
            while (matcher.find()) {
                width += matcher.group(1).replaceAll(" ", "").length();
            }

            lines[i] = centerText(s, width, lineWidth);
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.equals("")) {
                sb.append(line).append("\n");
            }
        }

        sb.setLength(sb.length() - 3);
        return sb.toString();
    }

    @NotNull
    private MotdData getNextMotd (@NotNull String ipAddress)
    {
        MotdData data = motdDataList.get(new Random().nextInt(motdDataList.size()));

        // Skip the player name motd if the players ip address is unknown
        if (!ipLookup.containsKey(ipAddress))
        {
            int attempts = 0;
            int limit = motdDataList.size();
            while (!data.isCached())
            {
                data = motdDataList.get(new Random().nextInt(motdDataList.size()));
                attempts++;

                // Return the fallback motd
                if (attempts >= limit) {
                    return motdDataList.get(0);
                }
            }
        }

        return data;
    }

    @NotNull
    private String centerText(String text, int width, int lineLength)
    {
        StringBuilder builder = new StringBuilder(text);
        int distance = ((lineLength - width) / 2) / 4;
        for (int ignored = 0; ignored < distance; ++ignored) {
            builder.insert(0, ' ');
        }
        return builder.toString();
    }

    @Contract(pure = true)
    private void setCharacterWidth (@NotNull String characters, int width)
    {
        for (int i = 0; i < characters.length(); i++) {
            characterWidthMap.put(characters.charAt(i), width);
        }
    }
}
