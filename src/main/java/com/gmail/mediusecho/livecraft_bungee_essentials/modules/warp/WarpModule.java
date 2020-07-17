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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp;

import com.gmail.mediusecho.fusion.BungeeCommandFramework;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.config.BungeeConfig;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.commands.DeleteWarpCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.commands.SetWarpCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.warp.commands.WarpCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.Location;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WarpModule extends Module {

    private Map<String, Warp> warpMap;
    private Map<UUID, String> pendingWarpMap;

    public WarpModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.WARPS_ENABLED);
        warpMap = new HashMap<>();
        pendingWarpMap = new HashMap<>();

        plugin.registerMainCommand(new SetWarpCommand(), "lcb.command.modules.warp.set");
        plugin.registerMainCommand(new WarpCommand(), "lcb.command.modules.warp.to");
        plugin.registerMainCommand(new DeleteWarpCommand(), "lcb.command.modules.warp.delete");
    }

    @Override
    protected void onReload()
    {
        warpMap.clear();

        BungeeConfig warpConfig = plugin.getWarpConfig();
        Configuration config = warpConfig.getConfig();
        Collection<? extends String> warpKeys = config.getSection("warps").getKeys();

        for (String key : warpKeys)
        {
            String path = "warps." + key + ".";
            String serverName = config.getString(path + "server", "");
            String worldName = config.getString(path + "world", "");
            double x = config.getDouble(path + "x", 0);
            double y = config.getDouble(path + "y", 0);
            double z = config.getDouble(path + "z", 0);
            double yaw = config.getDouble(path + "yaw", 0);
            double pitch = config.getDouble(path + "pitch", 0);
            Location location = new Location(worldName, serverName, x, y, z, yaw, pitch);

            Warp warp = new Warp(key, location);
            warpMap.put(key, warp);
        }
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

        if (!subChannel.equalsIgnoreCase("request-location") && !id.equalsIgnoreCase("warp")) {
            return;
        }

        addPendingWarp(player, in);
    }

    /**
     * Returns a list of all the warp names currently loaded
     *
     * @return
     *      List of names
     */
    public Set<String> getWarpNames () {
        return warpMap.keySet();
    }

    /**
     * Attempts to delete a warp with the given name.
     *
     * @param name
     *      The name of the warp location to delete.
     * @return
     *      True if the warp was deleted.
     */
    public boolean deleteWarp (String name)
    {
        if (!warpMap.containsKey(name)) {
            return false;
        }
        warpMap.remove(name);

        BungeeConfig warpConfig = plugin.getWarpConfig();
        Configuration config = warpConfig.getConfig();

        config.set("warps." + name, null);
        warpConfig.save();

        return true;
    }

    /**
     * Adds a temporary pending warp and sends a request for the warp location
     * through the bungee proxy.
     *
     * @param player
     *      The player setting the warp.
     * @param name
     *      The name of the warp.
     */
    public void setPendingWarp (@NotNull ProxiedPlayer player, String name)
    {
        UUID id = player.getUniqueId();
        pendingWarpMap.put(id, name);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("request-location");
        out.writeUTF("warp");
        out.writeUTF(id.toString());
        player.getServer().sendData("lce:message", out.toByteArray());
    }

    /**
     * Teleports this player to the warp name provided.
     *
     * @param player
     *      The player to teleport.
     * @param name
     *      The name of the warp to teleport to.
     */
    public void teleportToWarp (ProxiedPlayer player, String name)
    {
        if (!warpMap.containsKey(name))
        {
            Lang.WARP_MISSING.sendTo(player, "{1}", name);
            return;
        }

        Warp warp = warpMap.get(name);
        if (warp == null)
        {
            Lang.WARP_MISSING.sendTo(player, "{1}", name);
            return;
        }

        Lang.WARP_TELEPORTING.sendTo(player, "{1}", name);
        plugin.getTeleportManager().sendTeleportRequest(warp.getServerInfo(plugin), player, warp.getLocation());
    }

    /**
     * Adds a new warp location to the warp configuration file.
     *
     * @param warp
     *      The new warp location to add
     */
    private void addWarp (@NotNull Warp warp)
    {
        warpMap.put(warp.getName(), warp);

        BungeeConfig warpConfig = plugin.getWarpConfig();
        Configuration config = warpConfig.getConfig();
        Location location = warp.getLocation();
        String path = "warps." + warp.getName() + ".";

        config.set(path + "server", location.getServerName());
        config.set(path + "world", location.getWorldName());
        config.set(path + "x", location.getX());
        config.set(path + "y", location.getY());
        config.set(path + "z", location.getZ());
        config.set(path + "yaw", location.getYaw());
        config.set(path + "pitch", location.getPitch());
        warpConfig.save();
    }

    /**
     * Creates a new warp location from the pending warp name.
     *
     * @param player
     *      The player who initially set the warp
     * @param in
     *      The location data received from the bungee proxy
     */
    private void addPendingWarp (@NotNull ProxiedPlayer player, ByteArrayDataInput in)
    {
        UUID id = player.getUniqueId();
        if (!pendingWarpMap.containsKey(id)) {
            return;
        }

        String name = pendingWarpMap.remove(id);
        String serverName = player.getServer().getInfo().getName();
        Warp warp = new Warp(name, new Location(serverName, in));

        addWarp(warp);
        Lang.WARP_SET.sendTo(player, "{1}", name);
    }
}
