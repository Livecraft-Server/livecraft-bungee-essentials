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

package com.gmail.mediusecho.livecraft_bungee_essentials.settings;

public class Settings {

    /** Emotes **/
    public static final SettingsValue<Boolean> EMOTES_ENABLED = new SettingsValue<>("modules.emote.enabled", true);
    public static final SettingsValue<Boolean> EMOTE_COMMANDS_ENABLED = new SettingsValue<>("modules.emote.commands.enabled", true);
    public static final SettingsPointer EMOTE_COMMAND_WHITELIST_POINTER = new SettingsPointer("modules.emote.commands.whitelisted-commands");
    public static final SettingsPointer EMOTE_EMOTES_POINTER = new SettingsPointer("modules.emote.emotes");

    /** Homes **/
    public static final SettingsValue<Boolean> HOMES_ENABLED = new SettingsValue<>("modules.homes.enabled", true);

    /** Markdown **/
    public static final SettingsValue<Boolean> MARKDOWN_ENABLED = new SettingsValue<>("modules.markdown.enabled", true);
    public static final SettingsValue<Boolean> MARKDOWN_COMMANDS_ENABLED = new SettingsValue<>("modules.markdown.commands.enabled", true);
    public static final SettingsPointer MARKDOWN_COMMAND_WHITELIST_POINTER = new SettingsPointer("modules.markdown.commands.whitelisted-commands");
    public static final SettingsPointer MARKDOWN_FORMATS_POINTER = new SettingsPointer("modules.markdown.formats");

    /** Motd **/
    public static final SettingsValue<Boolean> MOTD_ENABLED = new SettingsValue<>("modules.motd.enabled", true);
    public static final SettingsValue<Boolean> MOTD_MAINTENANCE_MODE = new SettingsValue<>("modules.motd.maintenance.enabled", false);
    public static final SettingsValue<String> MOTD_MAINTENANCE_MOTD = new SettingsValue<>("modules.motd.maintenance.motd", "");
    public static final SettingsPointer MOTD_POINTER = new SettingsPointer("modules.motd.motds");
    public static final SettingsValue<Integer> MOTD_LINE_WIDTH = new SettingsValue<>("modules.motd.line-width", 250);
    public static final SettingsValue<String> MOTD_FALLBACK = new SettingsValue<>("modules.motd.fallback-motd", "");
    public static final SettingsValue<String> MOTD_KICK_MESSAGE = new SettingsValue<>("modules.motd.maintenance.kick-message", "");
    public static final SettingsPointer MOTD_WHITELIST_POINTER = new SettingsPointer("modules.motd.maintenance.whitelist");
    public static final SettingsValue<Boolean> PING_GROUPS_ENABLED = new SettingsValue<>("modules.ping.groups.enabled", true);
    public static final SettingsValue<Boolean> PING_RANDOM_GROUP_ENABLED = new SettingsValue<>("modules.ping.special-pings.random.enabled", true);
    public static final SettingsPointer PING_GROUPS_POINTER = new SettingsPointer("modules.ping.groups.groups");
    public static final SettingsPointer PING_RANDOM_GROUP_POINTER = new SettingsPointer("modules.ping.special-pings.random");
    public static final SettingsPointer PING_WHITELISTED_COMMANDS_POINTER = new SettingsPointer("modules.ping.commands.whitelist");

    /** Ping **/
    public static final SettingsValue<Boolean> PING_ENABLED  = new SettingsValue<>("modules.ping.enabled", true);
    public static final SettingsValue<Boolean> PING_COMMANDS_ENABLED = new SettingsValue<>("modules.ping.commands.enabled", true);
    public static final SettingsValue<Boolean> PING_SEARCH_NICKNAMES = new SettingsValue<>("modules.ping.include-nicknames", true);
    public static final SettingsValue<String> PING_PLAYER_HIGHLIGHT = new SettingsValue<>("modules.ping.player-highlight", "");

    /** Teleport **/
    public static final SettingsValue<Boolean> TELEPORT_ENABLED = new SettingsValue<>("modules.teleport.enabled", true);
    public static final SettingsValue<Integer> TELEPORT_TIMEOUT = new SettingsValue<>("modules.teleport.timeout", 120);

    /** Warps **/
    public static final SettingsValue<Boolean> WARPS_ENABLED = new SettingsValue<>("modules.warps.enabled", true);
}
