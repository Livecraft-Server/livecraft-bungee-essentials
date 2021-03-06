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

import com.gmail.mediusecho.fusion.api.BungeeCommandSender;
import com.gmail.mediusecho.fusion.api.MainCommand;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.fusion.api.commands.CommandListener;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.WarpModule;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@MainCommand(permission = "lcb.command.modules.warp.delete")
@Command(argument = "delwarp", contexts = "#warp")
public class DeleteWarpCommand extends CommandListener {

    @Inject private WarpModule warpModule;

    @Default
    @Permission(permission = "lcb.command.modules.warp.delete")
    public void deleteWarp (@NotNull BungeeCommandSender sender, String warpName)
    {
        if (warpModule.deleteWarp(warpName)) {
            Lang.WARP_DELETED.sendTo(sender.getCommandSender(), "{1}", warpName);
        } else {
            Lang.WARP_MISSING.sendTo(sender.getCommandSender());
        }
    }

    @Context(context = "#warp", providingContext = true)
    public List<String> getWarps (BungeeCommandSender sender) {
        return new ArrayList<>(warpModule.getWarpNames());
    }

}
