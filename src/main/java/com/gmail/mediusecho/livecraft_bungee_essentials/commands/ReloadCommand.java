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

package com.gmail.mediusecho.livecraft_bungee_essentials.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Default;
import com.gmail.mediusecho.fusion.annotations.MainCommand;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.command.BungeeCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;

@MainCommand
@Command(argument = "lcbreload")
public class ReloadCommand extends CommandListener {

    private final LivecraftBungeeEssentials plugin;

    public ReloadCommand (final LivecraftBungeeEssentials plugin)
    {
        this.plugin = plugin;
    }

    @Default
    @Permission(permission = "lcb.command.reload")
    public void reload (BungeeCommandSender sender)
    {
        plugin.reload();
    }
}