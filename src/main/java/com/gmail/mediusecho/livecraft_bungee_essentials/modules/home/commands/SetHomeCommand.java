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

import com.gmail.mediusecho.fusion.api.BungeeCommandSender;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.fusion.api.commands.Argument;
import com.gmail.mediusecho.fusion.api.commands.CommandListener;
import com.gmail.mediusecho.fusion.api.commands.Sender;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.HomeModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

@MainCommand
@Command(argument = "sethome", contexts = "name...")
@ArgumentPolicy(Argument.RELAXED)
public class SetHomeCommand extends CommandListener {

    @Inject private HomeModule homeModule;

    @Default
    @Permission(permission = "lcb.command.modules.home.set")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void setHome (@NotNull BungeeCommandSender sender) {
        setHome(sender.getPlayer(), "home");
    }

    @Context(context = "name...")
    @Permission(permission = "lcb.command.modules.home.set.multiple", permissionDeniedKey = "modules.homes.messages.set-multiple-permission")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void setHomeName (@NotNull BungeeCommandSender sender) {
        setHome(sender.getPlayer(), sender.getArgument(0));
    }

    private void setHome (ProxiedPlayer player, @NotNull String homeName)
    {
        // Prevent players from manually setting their bed home
        if (homeName.equalsIgnoreCase("bed"))
        {
            Lang.HOME_RESERVED.sendTo(player, "{1}", homeName);
            return;
        }

        int homeCount = homeModule.getHomeCount(player.getUniqueId());
        int homeLimit = homeModule.getHomeLimit(player);

        if (homeLimit != -1 && homeCount >= homeLimit)
        {
            Lang.HOME_LIMIT_REACHED.sendTo(player, "{1}", homeLimit);
            return;
        }

        homeModule.setPendingHome(player, homeName);
    }
}
