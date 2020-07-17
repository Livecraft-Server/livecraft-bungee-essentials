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

    /** Teleport **/
    public static final SettingsValue<Boolean> TELEPORT_ENABLED = new SettingsValue<>("modules.teleport.enabled", true);
    public static final SettingsValue<Integer> TELEPORT_TIMEOUT = new SettingsValue<>("modules.teleport.timeout", 120);

    /** Warps **/
    public static final SettingsValue<Boolean> WARPS_ENABLED = new SettingsValue<>("modules.warps.enabled", true);
}
