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

package com.gmail.mediusecho.livecraft_bungee_essentials.modules.teleport.request;

import com.gmail.mediusecho.livecraft_bungee_essentials.util.Location;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

public class TeleportRequest {

    private final ProxiedPlayer sendingPlayer;
    private final ProxiedPlayer requestedPlayer;
    private final Callback callback;
    private final RequestType type;
    private final long startTime;

    private Location location;

    public TeleportRequest (final ProxiedPlayer sendingPlayer, final ProxiedPlayer requestedPlayer, final RequestType type, final Callback callback)
    {
        this.sendingPlayer = sendingPlayer;
        this.requestedPlayer = requestedPlayer;
        this.type = type;
        this.callback = callback;
        this.startTime = System.nanoTime();
    }

    /**
     * Get the player sending the teleport request.
     *
     * @return
     *      ProxiedPlayer
     */
    public ProxiedPlayer getSendingPlayer () {
        return sendingPlayer;
    }

    /**
     * Get the player that was requested.
     *
     * @return
     *      ProxiedPlayer
     */
    public ProxiedPlayer getRequestedPlayer () {
        return requestedPlayer;
    }

    /**
     * Get which type of teleport request this is.
     *
     * @return
     *      RequestType
     */
    public RequestType getRequestType () {
        return type;
    }

    /**
     * Gets the requests teleport location.
     *
     * @return
     *      Location
     */
    @Nullable
    public Location getLocation () {
        return location;
    }

    /**
     * Set this requests teleport location.
     * Will call this requests callback method.
     *
     * @param location
     *      The location this player will teleport to
     */
    public void setLocation (Location location)
    {
        this.location = location;
        callback.onRequest(this);
    }

    /**
     * Checks to see if this teleport request has timed out.
     *
     * @param seconds
     *      How many seconds this request is valid for
     * @return
     *      True if this request has timed out
     */
    public boolean hasTimedOut (int seconds)
    {
        final long timePassed = System.nanoTime() - startTime;
        final long secondsPassed = timePassed / 1000000000;

        return secondsPassed > seconds;
    }

    @FunctionalInterface
    public interface Callback {
        void onRequest (TeleportRequest request);
    }

}
