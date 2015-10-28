/*******************************************************************************
 * Copyright (c) 2014 Bert De Geyter (https://github.com/TheHolyWaffle).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Bert De Geyter (https://github.com/TheHolyWaffle)
 ******************************************************************************/
package com.github.theholywaffle.lolchatapi.listeners;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

/**
 * This and all the files in the module have been developed by Bert De Geyter (https://github.com/TheHolyWaffle) and are protected by the Apache GPLv3 license.
 */
public class FriendListenerImpl implements FriendListener{

    @Override
    public void onFriendLeave(Friend friend) {
        if (updated == null) {
            updated = friend;
        } else {
            updated2 = friend;
        }
        sendBroadcast("friend_status_change", friend.getName(), "leave");
    }

    @Override
    public void onFriendJoin(Friend friend) {
        if (updated == null) {
            updated = friend;
        } else {
            updated2 = friend;
        }
        sendBroadcast("friend_status_change", friend.getName(), "join");
    }

    @Override
    public void onFriendAvailable(Friend friend) {
        if (updated == null) {
            updated = friend;
        } else {
            updated2 = friend;
        }
        sendBroadcast("friend_status_change", friend.getName(), "available");
    }

    @Override
    public void onFriendAway(Friend friend) {
        if (updated == null) {
            updated = friend;
        } else {
            updated2 = friend;
        }
        sendBroadcast("friend_status_change", friend.getName(), "away");
    }

    @Override
    public void onFriendBusy(Friend friend) {
        if (updated == null) {
            updated = friend;
        } else {
            updated2 = friend;
        }
        sendBroadcast("friend_status_change", friend.getName(), "busy");
    }

    @Override
    public void onFriendStatusChange(Friend friend) {
        if (updated == null) {
            updated = friend;
        } else {
            updated2 = friend;
        }
        sendBroadcast("friend_status_change", friend.getName(), "status");
    }

}
