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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.commands;

import com.gmail.mediusecho.fusion.annotations.*;
import com.gmail.mediusecho.fusion.command.BungeeCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.WarpModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@MainCommand
@Command(argument = "warp", contexts = "#warp")
public class WarpCommand extends CommandListener {

    @Inject private WarpModule warpModule;

    @Default
    @Permission(permission = "lcb.command.modules.warp.to.#warp lcb.command.modules.warp.to.all")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void warp (@NotNull BungeeCommandSender sender)
    {
        ProxiedPlayer player = sender.getPlayer();
        String warpName = sender.getArgument(0);

        warpModule.teleportToWarp(player, warpName);
    }

    @Context(context = "#warp", providingContext = true)
    public List<String> getWarps (@NotNull BungeeCommandSender sender)
    {
        if (sender.hasPermission("lcb.command.modules.warp.to.all")){
            return new ArrayList<>(warpModule.getWarpNames());
        }

        List<String> warps = new ArrayList<>();
        for (String warp : warpModule.getWarpNames())
        {
            if (sender.hasPermission("lcb.command.modules.warp.to." + warp)) {
                warps.add(warp);
            }
        }
        return warps;
    }
}
