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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Default;
import com.gmail.mediusecho.fusion.annotations.MainCommand;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.command.BungeeCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.TeleportModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

@MainCommand
@Command(argument = "tpahere", contexts = "@player")
public class TeleportHereCommand extends CommandListener {

    private final TeleportModule teleportModule;

    public TeleportHereCommand (final TeleportModule teleportModule)
    {
        this.teleportModule = teleportModule;
    }

    @Default
    @Permission(permission = "lcb.command.modules.teleport.here")
    public void teleportHere (@NotNull BungeeCommandSender sender)
    {
        ProxiedPlayer player = sender.getPlayer();
        String playerName = sender.getArgument(0);
        ProxiedPlayer targetPlayer = teleportModule.getPlayer(playerName);

        if (targetPlayer == null)
        {
            sender.sendMessage(Lang.UNKNOWN_PLAYER.get("{1}", playerName));
            return;
        }

        if (player.getName().equalsIgnoreCase(targetPlayer.getName()))
        {
            sender.sendMessage(Lang.TELEPORT_REQUEST_SELF.get());
            return;
        }

        teleportModule.teleportHere(player, targetPlayer);
    }
}
