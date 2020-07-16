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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.commands;

import com.gmail.mediusecho.fusion.annotations.*;
import com.gmail.mediusecho.fusion.command.BungeeCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.HomeModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@MainCommand
@Command(argument = "homes")
public class HomesCommand extends CommandListener {

    private final HomeModule homeModule;

    public HomesCommand (final HomeModule homeModule)
    {
        this.homeModule = homeModule;
    }

    @Default
    @Permission(permission = "lcb.command.modules.home.list")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void listHomes (@NotNull BungeeCommandSender sender)
    {
        ProxiedPlayer player = sender.getPlayer();
        UUID id = player.getUniqueId();
        List<String> homeNames = homeModule.getHomeNames(id);
        int homeLimit = homeModule.getHomeLimit(player);
        int homeCount = homeModule.getHomeCount(id);

        StringBuilder sb = new StringBuilder();
        for (String name : homeNames) {
            sb.append(name).append(", ");
        }
        sb.setLength(sb.length() - 2);
        String homes = sb.toString();

        // Unlimited
        if (homeLimit == -1) {
            Lang.HOME_UNLIMITED_LIST.sendTo(player,
                    "{1}", homeCount, "{2}", homes);
        }

        else {
            Lang.HOME_LIST.sendTo(player, "{1}", homeCount, "{2}", homeLimit, "{3}", homes);
        }
    }

}
