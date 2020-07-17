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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.emote;

import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.Permission;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.settings.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoteModule extends Module {

    private final Pattern emotePattern = Pattern.compile(":([^ ].*?):");
    private final Map<String, String> emoteMap;

    private List<String> whitelistedCommands;

    private boolean checkCommands;

    public EmoteModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.EMOTES_ENABLED);

        emoteMap = new HashMap<>();
        whitelistedCommands = new ArrayList<>();
    }

    @Override
    protected void onReload()
    {
        checkCommands = Settings.EMOTE_COMMANDS_ENABLED.getValue();

        emoteMap.clear();
        Configuration config = plugin.getConfig();
        Collection<? extends String> emotes = config.getSection(Settings.EMOTE_EMOTES_POINTER.getPath()).getKeys();
        for (String emote : emotes)
        {
            String path = Settings.EMOTE_EMOTES_POINTER.getPath() + "." + emote;
            String e = config.getString(path);
            if (e != null) {
                emoteMap.put(":" + emote + ":", ChatColor.translateAlternateColorCodes('&', e));
            }
        }
    }

    @EventHandler
    public void onChat (@NotNull ChatEvent event)
    {
        Connection connection = event.getSender();
        if (connection instanceof ProxiedPlayer)
        {
            ProxiedPlayer player = (ProxiedPlayer)connection;
            if (!Permission.EMOTE_CHAT.hasPermission(player)) {
                return;
            }
        }

        if (event.isCommand())
        {
            String command = event.getMessage().split(" ")[0];
            if (!whitelistedCommands.contains(command)) {
                return;
            }
        }

        event.setMessage(parseEmotes(event.getMessage()));
    }

    /**
     * Searches the input string for any emote matches.
     *
     * @param message
     *      The message to parse
     * @return
     *      The parsed message
     */
    public String parseEmotes (String message)
    {
        Matcher matcher = emotePattern.matcher(message);
        while (matcher.find())
        {
            String emote = matcher.group(0);
            if (emoteMap.containsKey(emote)) {
                message = message.replace(emote, emoteMap.get(emote));
            }
        }
        return message;
    }
}
