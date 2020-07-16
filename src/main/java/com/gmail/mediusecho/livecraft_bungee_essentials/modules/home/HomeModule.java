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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.home;

import com.gmail.mediusecho.fusion.BungeeCommandFramework;
import com.gmail.mediusecho.livecraft_bungee_essentials.Lang;
import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import com.gmail.mediusecho.livecraft_bungee_essentials.config.BungeeConfig;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.commands.DeleteHomeCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.commands.HomeCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.modules.home.commands.SetHomeCommand;
import com.gmail.mediusecho.livecraft_bungee_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.Location;
import com.gmail.mediusecho.livecraft_bungee_essentials.util.StringUtil;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomeModule extends Module {

    private Map<UUID, Map<String, Home>> homeMap;
    private Map<UUID, String> pendingHomeMap;

    public HomeModule(LivecraftBungeeEssentials plugin)
    {
        super(plugin, Settings.HOMES_ENABLED);
        homeMap = new HashMap<>();
        pendingHomeMap = new HashMap<>();

        BungeeCommandFramework commandFramework = plugin.getCommandFramework();
        commandFramework.registerMainCommand(new HomeCommand(this), "lcb.command.modules.home.teleport");
        commandFramework.registerMainCommand(new SetHomeCommand(this), "lcb.command.modules.home.set");
        commandFramework.registerMainCommand(new DeleteHomeCommand(this), "lcb.command.modules.home.delete");
    }

    @Override
    protected void onReload() { }

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

        if (!subChannel.equalsIgnoreCase("request-location")) {
            return;
        }

        // Set this players normal home
        if (id.equalsIgnoreCase("home")) {
            addPendingHome(player, in);
        }

        // Set this players bed home
        else if (id.equalsIgnoreCase("bed")) {
            addBedHome(player, in);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (@NotNull LoginEvent event)
    {
        UUID id = event.getConnection().getUniqueId();
        loadHomes(id);
    }

    @EventHandler
    public void onLeave (@NotNull PlayerDisconnectEvent event)
    {
        UUID id = event.getPlayer().getUniqueId();
        homeMap.remove(id);
    }

    /**
     * Adds a temporary pending home and sends a request for the home
     * location through the bungee proxy
     *
     * @param player
     *      The player setting this home
     * @param name
     *      The name of the home to set
     */
    public void setPendingHome (@NotNull ProxiedPlayer player, String name)
    {
        UUID id = player.getUniqueId();
        pendingHomeMap.put(id, name);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("request-location");
        out.writeUTF("home");
        out.writeUTF(id.toString());
        player.getServer().sendData("lce:message", out.toByteArray());
    }

    /**
     * Attempts to delete this home from the
     * players list of homes.
     *
     * @param player
     *      The player to delete from.
     * @param name
     *      The name of the home to delete.
     * @return
     *      True if the home was deleted.
     */
    public boolean deleteHome (@NotNull ProxiedPlayer player, String name)
    {
        UUID id = player.getUniqueId();
        if (!homeMap.containsKey(id) || !homeMap.get(id).containsKey(name)) {
            return false;
        }

        homeMap.get(id).remove(name);

        BungeeConfig playerConfig = plugin.getGuaranteedPlayerConfig(id);
        Configuration config = playerConfig.getConfig();

        config.set("homes." + name, null);
        playerConfig.save();
        return true;
    }

    /**
     * Teleports this player to the home they have saved.
     *
     * @param player
     *      The player to teleport.
     * @param name
     *      The home's name.
     */
    public void teleportHome (@NotNull ProxiedPlayer player, String name)
    {
        UUID id = player.getUniqueId();
        if (!homeMap.containsKey(id) || !homeMap.get(id).containsKey(name))
        {
            player.sendMessage(Lang.HOME_MISSING.get());
            return;
        }

        Home home = homeMap.get(id).get(name);
        player.sendMessage(Lang.TELEPORT_TELEPORTING.get());
        plugin.getTeleportManager().sendTeleportRequest(home.getServerInfo(plugin), player, home.getLocation());
    }

    /**
     * Returns a list of home names this player has.
     *
     * @param id
     *      The players UUID
     * @return
     *      A list of home names the player has set.
     */
    public List<String> getHomeNames (UUID id)
    {
        if (!homeMap.containsKey(id)) {
            return Collections.singletonList("");
        }
        return new ArrayList<>(homeMap.get(id).keySet());
    }

    /**
     * Returns how many homes this player has saved
     *
     * @param id
     *      The players UUID
     * @return
     *      Number of homes saved.
     */
    public int getHomeCount (UUID id)
    {
        if (!homeMap.containsKey(id)) {
            return 0;
        }

        // Loop through each home and skip the default value: 'bed'
        int count = 0;
        for (String names : homeMap.get(id).keySet())
        {
            if (names.equalsIgnoreCase("bed")) {
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * Get how many homes this player can set.
     *
     * @param player
     *      The player to check.
     * @return
     *      Number of homes allowed.
     */
    public int getHomeLimit (@NotNull ProxiedPlayer player)
    {
        if (player.hasPermission("lcb.command.modules.home.set.multiple.unlimited")) {
            return -1;
        }
        return StringUtil.getIntegerValueForPermission(player, "lcb.command.modules.home.set.multiple.");
    }

    /**
     * Loads this players saved homes into memory
     *
     * @param id
     *      The UUID of the player
     */
    private void loadHomes (UUID id)
    {
        if (homeMap.containsKey(id)) {
            homeMap.get(id).clear();
        } else {
            homeMap.put(id, new HashMap<>());
        }

        BungeeConfig playerConfig = plugin.getGuaranteedPlayerConfig(id);
        Configuration config = playerConfig.getConfig();
        Collection<? extends String> homes = config.getSection("homes").getKeys();
        Map<String, Home> playerHomeMap = homeMap.get(id);

        for (String home : homes)
        {
            String path = "homes." + home + ".";
            String serverName = config.getString(path + "server", "");
            String worldName = config.getString(path + "world", "");
            double x = config.getDouble(path + "x", 0);
            double y = config.getDouble(path + "y", 0);
            double z = config.getDouble(path + "z", 0);
            double yaw = config.getDouble(path + "yaw", 0);
            double pitch = config.getDouble(path + "pitch", 0);
            Location location = new Location(worldName, serverName, x, y, z, yaw, pitch);

            Home h = new Home(home, location);
            playerHomeMap.put(home, h);
        }
    }

    /**
     * Adds a new home to this players list
     *
     * @param id
     *      The players UUID
     * @param home
     *      The home to add
     */
    private void addHome (UUID id, Home home)
    {
        if (!homeMap.containsKey(id)) {
            homeMap.put(id, new HashMap<>());
        }
        homeMap.get(id).put(home.getName(), home);

        // Save this home to the players configuration file
        BungeeConfig playerConfig = plugin.getGuaranteedPlayerConfig(id);
        Configuration config = playerConfig.getConfig();
        Location location = home.getLocation();
        String path = "homes." + home.getName() + ".";

        config.set(path + "server", location.getServerName());
        config.set(path + "world", location.getWorldName());
        config.set(path + "x", location.getX());
        config.set(path + "y", location.getY());
        config.set(path + "z", location.getZ());
        config.set(path + "yaw", location.getYaw());
        config.set(path + "pitch", location.getPitch());
        playerConfig.save();
    }

    /**
     * Adds a new home to this players list.
     * Constructs the home from the pending home name
     * saved earlier.
     *
     * @param player
     *      The player to add this home to
     * @param in
     *      The location data received from the bungee proxy.
     */
    private void addPendingHome (@NotNull ProxiedPlayer player, ByteArrayDataInput in)
    {
        UUID id = player.getUniqueId();
        if (!pendingHomeMap.containsKey(id))
        {
            player.sendMessage(Lang.HOME_SET_ERROR.get());
            return;
        }

        String serverName = player.getServer().getInfo().getName();
        Location location = new Location(serverName, in);
        String homeName = pendingHomeMap.remove(id);

        addHome(id, new Home(homeName, location));

        Lang message = homeName.equalsIgnoreCase("home") ? Lang.HOME_SET : Lang.HOME_SET_MULTIPLE;
        player.sendMessage(message.get("{1}", homeName));
    /**
     * Sets this players bed home
     *
     * @param player
     *      The player to add this home to.
     * @param in
     *      The location data received from the bungee proxy
     */
    private void addBedHome (@NotNull ProxiedPlayer player, ByteArrayDataInput in)
    {
        UUID id = player.getUniqueId();
        String serverName = player.getServer().getInfo().getName();
        Location location = new Location(serverName, in);

        addHome(id, new Home("bed", location));
        Lang.HOME_BED_SET.sendTo(player);
    }
}
