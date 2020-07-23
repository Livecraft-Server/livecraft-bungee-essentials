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
import com.gmail.mediusecho.fusion.api.MainCommand;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.fusion.api.commands.Argument;
import com.gmail.mediusecho.fusion.api.commands.CommandListener;
import com.gmail.mediusecho.fusion.api.commands.Sender;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.HomeModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import sun.plugin2.main.server.Plugin;

import java.util.List;

@MainCommand(permission = "lcb.command.modules.home.delete")
@Command(argument = "delhome", contexts = "#homes")
@ArgumentPolicy(Argument.RELAXED)
public class DeleteHomeCommand extends CommandListener {

    @Inject private HomeModule homeModule;

    @Default
    @Permission(permission = "lcb.command.modules.home.delete")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void deleteDefaultHome (@NotNull BungeeCommandSender sender, @Default("home") String home)
    {
        ProxiedPlayer player = sender.getPlayer();
        if (homeModule.deleteHome(player, home)) {
            Lang.HOME_DELETED.sendTo(player, "{1}", home);
        } else {
            Lang.HOME_DELETE_ERROR.sendTo(player, "{1}", home);
        }
    }

    @Context(context = "#homes", providingContext = true)
    public List<String> getHomes (@NotNull BungeeCommandSender sender) {
        return homeModule.getHomeNames(sender.getPlayer().getUniqueId());
    }
}
