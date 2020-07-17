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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.markdown;

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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MarkdownModule extends Module {

    private Set<MarkdownFormat> formatSet;
    private List<String> whitelistedCommands;

    private boolean checkCommands;

    public MarkdownModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.MARKDOWN_ENABLED);
        formatSet = new TreeSet<>();
    }

    @Override
    protected void onReload()
    {
        checkCommands = Settings.MARKDOWN_COMMANDS_ENABLED.getValue();

        formatSet.clear();
        Configuration config = plugin.getConfig();

        whitelistedCommands = config.getStringList(Settings.MARKDOWN_COMMAND_WHITELIST_POINTER.getPath());

        Collection<? extends String> formats = config.getSection(Settings.MARKDOWN_FORMATS_POINTER.getPath()).getKeys();
        for (String format : formats)
        {
            plugin.log("loading format: " + format);
            String path = Settings.MARKDOWN_FORMATS_POINTER.getPath() + "." + format + ".";
            String regex = config.getString(path + "regex", "");
            String replacement = config.getString(path + "replacement", "");
            int priority = config.getInt(path + "priority", 0);

            formatSet.add(new MarkdownFormat(format, regex, replacement, priority));
        }
    }

    @EventHandler
    public void onChat (@NotNull ChatEvent event)
    {
        Connection connection = event.getSender();
        if ((connection instanceof ProxiedPlayer))
        {
            ProxiedPlayer player = (ProxiedPlayer)connection;
            if (!Permission.MARKDOWN_CHAT.hasPermission(player)) {
                return;
            }
        }

        if (event.isCommand())
        {
            if (!checkCommands) {
                return;
            }

            String command = event.getMessage().split(" ")[0];
            if (!whitelistedCommands.contains(command)) {
                return;
            }
        }

        event.setMessage(parseMarkdown(event.getMessage()));
    }

    /**
     * Checks this string for any markdown and
     * applies it.
     *
     * @param string
     *      The string to parse.
     * @return
     *      The string with markdown parsed.
     */
    public String parseMarkdown (String string)
    {
        for (MarkdownFormat format : formatSet) {
            string = format.parseMarkdown(string);
        }
        return string;
    }
}
