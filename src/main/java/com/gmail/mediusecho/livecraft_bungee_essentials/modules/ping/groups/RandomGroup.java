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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.ping.groups;

import com.gmail.mediusecho.livecraft_bungee_essentials.Permission;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.StringUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RandomGroup extends Group {

    public RandomGroup(String name, String highlight)
    {
        super(name, highlight);
    }

    @Override
    public String parseMessage(String mention, String name, String message, @Nullable ProxiedPlayer player, List<ProxiedPlayer> pingedPlayers)
    {
        if (player != null)
        {
            if (!Permission.PING_CHAT_NOTIFY_GROUP.hasPermission(player, this.name)) {
                return message;
            }
        }

        if (!aliases.contains(name)) {
            return message;
        }

        Collection<? extends ProxiedPlayer> players = ProxyServer.getInstance().getPlayers();
        Optional<? extends ProxiedPlayer> optionalPlayer = players.stream().skip((int) (players.size() * Math.random())).findFirst();

        if (optionalPlayer.isPresent())
        {
            ProxiedPlayer p = optionalPlayer.get();

            String prefix = message.substring(0, message.indexOf(mention));
            String suffix = StringUtil.getLastUsedColorCode(prefix);

            message = message.replaceFirst(mention, highlight.replace("{1}", "@" + p.getName()) + suffix);
            pingedPlayers.add(p);
        }

        return message;
    }

}