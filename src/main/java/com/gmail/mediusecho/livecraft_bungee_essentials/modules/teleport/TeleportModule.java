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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport;

import com.gmail.mediusecho.fusion.BungeeCommandFramework;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.manager.TeleportManager;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.commands.*;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.request.RequestType;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.request.TeleportRequest;
import com.gmail.mediusecho.livecraft_bungee_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.Location;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportModule extends Module {

    private final TeleportManager teleportManager;

    // Store the most recent teleport request a player has received
    private Map<UUID, TeleportRequest> teleportRequestMap;
    // Store the most recent player id this player has sent a teleport request to.
    private Map<UUID, UUID> teleportRequestSentMap;

    private int teleportTimeout;

    public TeleportModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.TELEPORT_ENABLED);
        teleportManager = plugin.getTeleportManager();
        teleportRequestMap = new HashMap<>();
        teleportRequestSentMap = new HashMap<>();

        BungeeCommandFramework commandFramework = plugin.getCommandFramework();
        commandFramework.registerMainCommand(new TeleportAskCommand(this), "lcb.command.modules.teleport.ask");
        commandFramework.registerMainCommand(new TeleportAcceptCommand(this), "lcb.command.modules.teleport.accept");
        commandFramework.registerMainCommand(new TeleportHereCommand(this), "lcb.command.modules.teleport.here");
        commandFramework.registerMainCommand(new TeleportDenyCommand(this), "lcb.command.modules.teleport.deny");
        commandFramework.registerMainCommand(new TeleportCancelCommand(this), "lcb.command.modules.teleport.cancel");
    }

    @Override
    protected void onReload()
    {
        teleportTimeout = Settings.TELEPORT_TIMEOUT.getValue();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage (@NotNull PluginMessageEvent event)
    {
        if (!event.getTag().equalsIgnoreCase("lce:message")) {
            return;
        }

        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer)event.getReceiver();
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        String id = in.readUTF();

        // return if these values are not correct
        if (!subChannel.equalsIgnoreCase("request-location") || !id.equalsIgnoreCase("location")) {
            return;
        }

        UUID playerId = player.getUniqueId();
        String serverName = player.getServer().getInfo().getName();

        if (teleportRequestMap.containsKey(playerId))
        {
            Location location = new Location(serverName, in);
            TeleportRequest request = teleportRequestMap.get(playerId);

            request.setLocation(location);
            return;
        }

        if (teleportRequestSentMap.containsKey(playerId))
        {
            UUID targetId = teleportRequestSentMap.get(playerId);
            plugin.log("found teleport request for " + targetId.toString());
            if (!teleportRequestMap.containsKey(targetId)) {
                return;
            }

            Location location = new Location(serverName, in);
            TeleportRequest request = teleportRequestMap.get(targetId);

            request.setLocation(location);
        }
    }

    /**
     * Attempts to retrieve the player from the
     * bungee proxy with the given name.
     *
     * @param name
     *      The name of the player to get.
     * @return
     *      ProxiedPlayer
     */
    public ProxiedPlayer getPlayer (String name) {
        return plugin.getProxy().getPlayer(name);
    }

    /**
     * Requests that playerA teleport to playerB.
     * A -> B
     *
     * @param playerA
     *      The player that will teleport.
     * @param playerB
     *      The player to teleport to.
     */
    public void teleportAsk (ProxiedPlayer playerA, ProxiedPlayer playerB)
    {
        TeleportRequest request = getTeleportRequest(playerA, playerB, RequestType.TPA);

        teleportRequestMap.put(playerB.getUniqueId(), request);
        teleportRequestSentMap.put(playerA.getUniqueId(), playerB.getUniqueId()); // Let the module know that player a sent a request to player b
        showTeleportAskMessage(playerB, playerA.getName());
        showTeleportRequestSentMessage(playerA, playerB.getName());
    }

    /**
     * Requests that playerB teleport to playerA
     * A <- B
     *
     * @param playerA
     *      The player to teleport to.
     * @param playerB
     *      The player that will teleport.
     */
    public void teleportHere (ProxiedPlayer playerA, ProxiedPlayer playerB)
    {
        TeleportRequest request = getTeleportRequest(playerA, playerB, RequestType.TPAHERE);

        teleportRequestMap.put(playerB.getUniqueId(), request);
        teleportRequestSentMap.put(playerA.getUniqueId(), playerB.getUniqueId());
        showTeleportHereMessage(playerB, playerA.getName());
        showTeleportRequestSentMessage(playerA, playerB.getName());
    }

    /**
     * Accepts the pending teleport request and teleports
     * the player.
     *
     * @param player
     *      The player to teleport
     */
    public void teleportAccept (@NotNull ProxiedPlayer player)
    {
        UUID id = player.getUniqueId();

        // Return here if there are no pending requests to accept
        if (!teleportRequestMap.containsKey(id))
        {
            player.sendMessage(Lang.TELEPORT_PENDING_ERROR.get());
            return;
        }

        TeleportRequest request = teleportRequestMap.get(id);
        if (request == null)
        {
            teleportRequestMap.remove(id);
            player.sendMessage(Lang.TELEPORT_PENDING_ERROR.get());
            return;
        }

        if (request.hasTimedOut(teleportTimeout))
        {
            player.sendMessage(Lang.TELEPORT_REQUEST_TIMED_OUT.get());
            teleportRequestMap.remove(id);
            teleportRequestSentMap.remove(request.getSendingPlayer().getUniqueId());
            return;
        }

        ProxiedPlayer p = request.getRequestType().equals(RequestType.TPA) ? request.getRequestedPlayer() : request.getSendingPlayer();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("request-location"); // Let the spigot plugin know we want a location
        out.writeUTF("location"); // Let this module know that it will receive a location
        out.writeUTF(p.getUniqueId().toString());

        plugin.log("sending location request for player: " + p.getName() + " on server: " + p.getServer().getInfo().getName());
        p.getServer().sendData("lce:message", out.toByteArray());
    }

    /**
     * Denies a pending teleport request and
     * messages both players.
     *
     * @param player
     *      The player that was requested.
     */
    public void teleportDeny (@NotNull ProxiedPlayer player)
    {
        UUID id = player.getUniqueId();
        if (!teleportRequestMap.containsKey(id))
        {
            player.sendMessage(Lang.TELEPORT_PENDING_ERROR.get());
            return;
        }

        TeleportRequest request = teleportRequestMap.remove(id);
        teleportRequestSentMap.remove(request.getSendingPlayer().getUniqueId());

        if (request.hasTimedOut(teleportTimeout))
        {
            player.sendMessage(Lang.TELEPORT_REQUEST_TIMED_OUT.get());
            return;
        }

        request.getSendingPlayer().sendMessage(Lang.TELEPORT_REQUEST_DENIED_FROM.get("{1}", player.getName()));
        player.sendMessage(Lang.TELEPORT_REQUEST_DENIED.get());
    }

    /**
     * Cancels an existing teleport request
     *
     * @param player
     *      The player that sent the request
     */
    public void teleportCancel (@NotNull ProxiedPlayer player)
    {
        UUID id = player.getUniqueId();
        if (!teleportRequestSentMap.containsKey(id))
        {
            player.sendMessage(Lang.TELEPORT_PENDING_ERROR.get());
            return;
        }

        UUID targetId = teleportRequestSentMap.remove(id);
        TeleportRequest request = teleportRequestMap.remove(targetId);
        if (request == null)
        {
            player.sendMessage(Lang.TELEPORT_PENDING_ERROR.get());
            return;
        }

        if (request.hasTimedOut(teleportTimeout))
        {
            player.sendMessage(Lang.TELEPORT_REQUEST_TIMED_OUT.get());
            return;
        }

        request.getRequestedPlayer().sendMessage(Lang.TELEPORT_REQUEST_CANCELLED_FROM.get("{1}", player.getName()));
        player.sendMessage(Lang.TELEPORT_REQUEST_CANCELLED.get());
    }

    private void showTeleportAcceptedMessage (@NotNull ProxiedPlayer player) {
        player.sendMessage(Lang.TELEPORT_REQUEST_ACCEPTED.get());
    }

    private void showTeleportAcceptedFromMessage (@NotNull ProxiedPlayer player, String name) {
        player.sendMessage(Lang.TELEPORT_REQUEST_ACCEPTED_FROM.get("{1}", name));
    }

    private void showTeleportingMessage (@NotNull ProxiedPlayer player) {
        player.sendMessage(Lang.TELEPORT_TELEPORTING.get());
    }

    private void showTeleportingToPlayerMessage (@NotNull ProxiedPlayer player, String name) {
        player.sendMessage(Lang.TELEPORT_TELEPORTING_TO_PLAYER.get("{1}", name));
    }

    private void showTeleportRequestSentMessage (@NotNull ProxiedPlayer player, String name)
    {
        player.sendMessage(Lang.TELEPORT_REQUEST_SENT.get("{1}", name));
        player.sendMessage(Lang.TELEPORT_REQUEST_CANCEL_TIP.get());
    }

    private void showTeleportAskMessage (@NotNull ProxiedPlayer player, String name)
    {
        player.sendMessage(Lang.TELEPORT_ASK.get("{1}", name));
        player.sendMessage(Lang.TELEPORT_REQUEST_ACCEPT_TIP.get());
        player.sendMessage(Lang.TELEPORT_REQUEST_DENY_TIP.get());
        player.sendMessage(Lang.TELEPORT_REQUEST_TIMEOUT_TIP.get("{1}", teleportTimeout));
    }

    private void showTeleportHereMessage (@NotNull ProxiedPlayer player, String name)
    {
        player.sendMessage(Lang.TELEPORT_HERE.get("{1}", name));
        player.sendMessage(Lang.TELEPORT_REQUEST_ACCEPT_TIP.get());
        player.sendMessage(Lang.TELEPORT_REQUEST_DENY_TIP.get());
        player.sendMessage(Lang.TELEPORT_REQUEST_TIMEOUT_TIP.get("{1}", teleportTimeout));
    }

    @NotNull
    private TeleportRequest getTeleportRequest (ProxiedPlayer playerA, ProxiedPlayer playerB, RequestType type)
    {
        return new TeleportRequest(playerA, playerB, type, (r) ->
        {
            showTeleportAcceptedMessage(playerB);
            showTeleportAcceptedFromMessage(playerA, playerB.getName());
            showTeleportingToPlayerMessage(playerA, playerB.getName());

            teleportRequestSentMap.remove(playerA.getUniqueId());
            teleportRequestMap.remove(playerB.getUniqueId());

            Server server = type.equals(RequestType.TPA) ? playerB.getServer() : playerA.getServer();
            ProxiedPlayer player = type.equals(RequestType.TPA) ? playerA : playerB;
            teleportManager.sendTeleportRequest(server.getInfo(), player, r.getLocation());
        });
    }
}
