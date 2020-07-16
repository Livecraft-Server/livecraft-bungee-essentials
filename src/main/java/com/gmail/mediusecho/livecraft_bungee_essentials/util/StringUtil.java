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

package com.gmail.mediusecho.livecraft_bungee_essentials.util;

import com.gmail.mediusecho.livecraft_bungee_essentials.LivecraftBungeeEssentials;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class StringUtil {

    private static final LivecraftBungeeEssentials plugin = LivecraftBungeeEssentials.instance;

    /**
     * Returns the largest permission integer for the give player.
     *
     * @param player
     *      Player to check
     * @param permissionPrefix
     *      Permission to check
     * @return
     *      Largest integer found for the given permission
     */
    public static int getIntegerValueForPermission (@NotNull ProxiedPlayer player, @NotNull String permissionPrefix)
    {
        LuckPerms api = plugin.getLuckPermsApi();
        User user = api.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return 0;
        }

        int maxSize = 0;
        Collection<Node> nodes = user.resolveInheritedNodes(QueryOptions.nonContextual());
        for (Node node : nodes)
        {
            if (!(node instanceof PermissionNode)) {
                continue;
            }
            PermissionNode permissionNode = (PermissionNode)node;
            String permission = permissionNode.getPermission();

            if (permission.startsWith(permissionPrefix))
            {
                String s = permission.split(permissionPrefix)[1];
                maxSize = Math.max(maxSize, Integer.parseInt(s));
            }
        }
        return maxSize;
    }
}
