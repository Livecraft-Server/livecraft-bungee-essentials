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

package com.gmail.mediusecho.livecraft_bungee_essentials;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Lang {

    /** Misc **/
    RELOAD ("misc.messages.reload"),
    UNKNOWN_PLAYER ("misc.messages.unknown-player"),
    TELEPORT_BACK ("misc.messages.back"),
    TELEPORT_BACK_ERROR ("misc.messages.back-error"),

    /** Home **/
    HOME_SET ("modules.homes.messages.set"),
    HOME_SET_MULTIPLE ("modules.homes.messages.set-multiple"),
    HOME_SET_ERROR ("modules.homes.messages.home-set-error"),
    HOME_DELETED ("modules.homes.messages.delete"),
    HOME_DELETE_ERROR ("modules.homes.messages.delete-error"),
    HOME_MISSING ("modules.homes.messages.missing"),
    HOME_RESERVED ("modules.homes.messages.reserved"),
    HOME_LIMIT_REACHED ("modules.homes.messages.limit-reached"),
    HOME_LIST ("modules.homes.messages.list"),
    HOME_UNLIMITED_LIST ("modules.homes.messages.list-unlimited"),

    /** Teleport **/
    TELEPORT_ERROR ("modules.teleport.messages.error"),
    TELEPORT_PENDING_ERROR ("modules.teleport.messages.pending-error"),
    TELEPORT_REQUEST_SENT ("modules.teleport.messages.request-sent"),
    TELEPORT_REQUEST_ACCEPTED ("modules.teleport.messages.request-accepted"),
    TELEPORT_REQUEST_ACCEPTED_FROM ("modules.teleport.messages.request-accepted-from"),
    TELEPORT_REQUEST_DENIED ("modules.teleport.messages.request-denied"),
    TELEPORT_REQUEST_DENIED_FROM ("modules.teleport.messages.request-denied-from"),
    TELEPORT_REQUEST_CANCELLED ("modules.teleport.messages.request-cancel"),
    TELEPORT_REQUEST_CANCELLED_FROM ("modules.teleport.messages.request-cancel-from"),
    TELEPORT_REQUEST_CANCEL_TIP ("modules.teleport.messages.request-cancel-tip"),
    TELEPORT_REQUEST_SELF ("modules.teleport.messages.request-self"),
    TELEPORT_REQUEST_TIMED_OUT ("modules.teleport.messages.request-timed-out"),
    TELEPORT_REQUEST_ACCEPT_TIP ("modules.teleport.messages.request-accept-tip"),
    TELEPORT_REQUEST_DENY_TIP ("modules.teleport.messages.request-deny-tip"),
    TELEPORT_REQUEST_TIMEOUT_TIP ("modules.teleport.messages.request-timeout-tip"),
    TELEPORT_ASK ("modules.teleport.messages.tpa"),
    TELEPORT_HERE ("modules.teleport.messages.tpahere"),
    TELEPORT_TELEPORTING ("modules.teleport.messages.teleporting"),
    TELEPORT_TELEPORTING_TO_PLAYER ("modules.teleport.messages.teleporting-to-player");

    private final LivecraftBungeeEssentials plugin = LivecraftBungeeEssentials.instance;
    private final String key;

    private Lang (final String key)
    {
        this.key = key;
    }

    @NotNull
    @Contract(" -> new")
    public BaseComponent get () {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', getValue()));
    }

    @NotNull
    public BaseComponent get (@NotNull Object... parameters)
    {
        // Return the un-parsed value if the parameters length is not even
        if ((parameters.length & 1) != 0) {
            return get();
        }

        String value = getValue();
        for (int i = 0; i < parameters.length; i+=2) {
            value = value.replace(parameters[i].toString(), parameters[i+1].toString());
        }
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', value));
    }

    /**
     * Returns this Lang's message
     *
     * @return
     *      String
     */
    private String getValue () {
        return plugin.getConfig().getString(key, "");
    }

}
