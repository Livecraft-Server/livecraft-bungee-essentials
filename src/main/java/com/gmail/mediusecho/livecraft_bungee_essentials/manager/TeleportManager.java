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

package com.gmail.mediusecho.livecraft_bungee_essentials.manager;

import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.config.BungeeConfig;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.Location;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager implements Listener {

    private final LivecraftBungeeEssentials plugin;
    private Map<UUID, Location> previousTeleportLocationMap;

    public TeleportManager (@NotNull final LivecraftBungeeEssentials plugin)
    {
        this.plugin = plugin;
        this.previousTeleportLocationMap = new HashMap<>();
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessage (@NotNull PluginMessageEvent event)
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

        // We are receiving our teleport request data for this player
        if (subChannel.equalsIgnoreCase("request-location"))
        {
            // Lets us know that we need to
            if (id.equalsIgnoreCase("query"))
            {
                UUID playerId = player.getUniqueId();
                String serverName = player.getServer().getInfo().getName();
                Location location = new Location(serverName, in);

                previousTeleportLocationMap.put(playerId, location);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (@NotNull LoginEvent event)
    {
        // Attempt to load this players previously stored location from their
        // configuration file.

        UUID id = event.getConnection().getUniqueId();
        BungeeConfig playerConfig = plugin.getGuaranteedPlayerConfig(id);
        Configuration config = playerConfig.getConfig();

        if (config.getSection("previous-location") == null) {
            return;
        }

        String path = "previous-location.";
        String serverName = config.getString(path + "server", "");
        String worldName = config.getString(path + "world", "");

        if (serverName.equals("") || worldName.equals("")) {
            return;
        }

        double x = config.getDouble(path + "x", 0);
        double y = config.getDouble(path + "y", 0);
        double z = config.getDouble(path + "z", 0);
        double yaw = config.getDouble(path + "yaw", 0);
        double pitch = config.getDouble(path + "pitch", 0);

        Location location = new Location(worldName, serverName, x, y, z, yaw, pitch);
        previousTeleportLocationMap.put(id, location);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect (@NotNull PlayerDisconnectEvent event)
    {
        // Save this players location to their configuration file.

        UUID id = event.getPlayer().getUniqueId();
        BungeeConfig playerConfig = plugin.getGuaranteedPlayerConfig(id);
        Configuration config = playerConfig.getConfig();
        Location previousLocation = previousTeleportLocationMap.get(id);

        if (previousLocation == null)
        {
            config.set("previous-location", null);
            playerConfig.save();
            return;
        }

        String path = "previous-location.";
        config.set(path + "server", previousLocation.getServerName());
        config.set(path + "world", previousLocation.getWorldName());
        config.set(path + "x", previousLocation.getX());
        config.set(path + "y", previousLocation.getY());
        config.set(path + "z", previousLocation.getZ());
        config.set(path + "yaw", previousLocation.getYaw());
        config.set(path + "pitch", previousLocation.getPitch());
        playerConfig.save();
    }

    /**
     * Teleports the player to the given location.
     * Sends a teleport message if the teleport location is
     * on a different server.
     *
     * @param info
     *      The info of the server we will teleport to.
     * @param player
     *      The player being teleported.
     * @param location
     *      The location in the server to teleport to.
     */
    public void sendTeleportRequest (@NotNull ServerInfo info, ProxiedPlayer player, Location location)
    {
        // Query for the players current location before teleporting
        sendLocationQueryMessage(player);

        String serverName = info.getName();
        String currentServerName = player.getServer().getInfo().getName();

        // Send the teleport message as is since the player is on the
        // the same server as the location
        if (serverName.equalsIgnoreCase(currentServerName))
        {
            sendTeleportMessage(info, player, location);
            return;
        }

        // Connect the player to the target server before sending the request
        player.connect(info, (success, throwable) ->
        {
            if (success) {
                sendTeleportMessage(info, player, location);
            }
        });
    }

    /**
     * Attempts to teleport the player to their previous location.
     * Returns false if the previous location is not set.
     *
     * @param player
     *      The player to teleport
     * @return
     *      True if the player was able to teleport
     */
    public boolean teleportToPreviousLocation (@NotNull ProxiedPlayer player)
    {
        UUID id = player.getUniqueId();
        if (!previousTeleportLocationMap.containsKey(id)) {
            return false;
        }

        Location previousLocation = previousTeleportLocationMap.remove(id);
        ServerInfo info = plugin.getProxy().getServerInfo(previousLocation.getServerName());

        if (info == null) {
            return false;
        }

        sendTeleportRequest(info, player, previousLocation);
        return true;
    }

    /**
     * Sends a teleport message through the Bungee proxy to the server
     * that the player is teleporting to.
     *
     * @param info
     *      The ServerInfo of the server that the player will teleport to.
     * @param player
     *      The player teleporting.
     * @param location
     *      The location the player will teleport to.
     */
    private void sendTeleportMessage (@NotNull ServerInfo info, @NotNull ProxiedPlayer player, @NotNull Location location)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("teleport"); // Let the spigot plugin know that we want to teleport
        out.writeUTF(player.getUniqueId().toString()); // Tell the spigot plugin who is teleporting
        out.writeUTF(location.getWorldName()); // Provide the location data
        out.writeDouble(location.getX());
        out.writeDouble(location.getY());
        out.writeDouble(location.getZ());
        out.writeDouble(location.getYaw());
        out.writeDouble(location.getPitch());

        info.sendData("lce:message", out.toByteArray());
    }

    /**
     * Sends a location query message through the Bungee proxy to the
     * server the player is currently on.
     *
     * @param player
     *      The player whose location we're requesting
     */
    private void sendLocationQueryMessage (@NotNull ProxiedPlayer player)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("request-location");
        out.writeUTF("query");
        out.writeUTF(player.getUniqueId().toString());
        player.getServer().sendData("lce:message", out.toByteArray());
    }

}
