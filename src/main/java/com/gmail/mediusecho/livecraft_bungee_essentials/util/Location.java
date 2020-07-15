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

import com.google.common.io.ByteArrayDataInput;
import org.jetbrains.annotations.NotNull;

public class Location {

    private final String worldName;
    private final String serverName;

    private final double x;
    private final double y;
    private final double z;
    private final double yaw;
    private final double pitch;

    public Location (String worldName, String serverName, double x, double y, double z, double yaw, double pitch)
    {
        this.worldName = worldName;
        this.serverName = serverName;

        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location (String serverName, @NotNull ByteArrayDataInput in) {
        this(in.readUTF(), serverName, in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble());
    }

    public Location ()
    {
        this("", "", 0, 0, 0, 0, 0);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public String toString() {
        return "Location(" + "x:" + x + " y:" + y + " z:" + z + " yaw:" + yaw + " pitch:" + pitch + ")";
    }

}
