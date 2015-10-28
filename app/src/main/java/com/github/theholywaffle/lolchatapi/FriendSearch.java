package com.github.theholywaffle.lolchatapi;

import android.content.Context;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.github.theholywaffle.lolchatapi.wrapper.FriendGroup;
import com.jivesoftware.util.ssl.DummySSLSocketFactory;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPTCPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FriendSearch {

    private final XMPPTCPConnection connection;

    public FriendSearch (XMPPTCPConnection connection) {
        this.connection = connection;
    }
    /**
     * @return default FriendGroup
     */
    public FriendGroup getDefaultFriendGroup() {
        return getFriendGroupByName("**Default");
    }

    /**
     * Gets your friend based on his XMPPAddress
     *
     * @param xmppAddress For example sum12345678@pvp.net
     * @return The corresponding Friend or null if user is not found or he is
     * not a friend of you
     */
    public Friend getFriendById(String xmppAddress) {
        return new Friend(this, connection,
                connection.getRoster().getEntry(StringUtils.parseBareAddress(xmppAddress)));
    }

    /**
     * Gets a friend based on his name. The name is case insensitive.
     *
     * @param name The name of your friend, for example "Dyrus"
     * @return The corresponding Friend object or null if user is not found or
     * he is not a friend of you
     */
    public Friend getFriendByName(String name) {
        for (Friend f : getFriends()) {
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Get a FriendGroup by name, for example "Duo Partners". The name is case
     * sensitive!
     *
     * @param name The name of your group
     * @return The corresponding FriendGroup or null if not found
     */
    public FriendGroup getFriendGroupByName(String name) {
        RosterGroup g = connection.getRoster().getGroup(name);
        if (g != null) {
            return new FriendGroup(this, connection, g);
        }
        return null;
    }

    /**
     * Get all your FriendGroups
     *
     * @return A List of all your FriendGroups
     */
    public List<FriendGroup> getFriendGroups() {
        ArrayList<FriendGroup> groups = new ArrayList<>();
        for (RosterGroup g : connection.getRoster().getGroups()) {
            groups.add(new FriendGroup(this, connection, g));
        }
        return groups;
    }

    /**
     * Get all your friends, both online and offline
     *
     * @return A List of all your Friends
     */
    public List<Friend> getFriends() {
        ArrayList<Friend> friends = new ArrayList<>();
        for (RosterEntry e : connection.getRoster().getEntries()) {
            friends.add(new Friend(this, connection, e));
        }
        return friends;
    }

    /**
     * Get all your friends who are offline.
     *
     * @return A list of all your offline Friends
     */
    public List<Friend> getOfflineFriends() {
        List<Friend> f = getFriends();
        Iterator<Friend> i = f.iterator();
        while (i.hasNext()) {
            Friend friend = i.next();
            if (friend.isOnline()) {
                i.remove();
            }
        }
        return f;
    }

    /**
     * Get all your friends who are online.
     *
     * @return A list of all your online Friends
     */
    public List<Friend> getOnlineFriends() {
        List<Friend> f = getFriends();
        Iterator<Friend> i = f.iterator();
        while (i.hasNext()) {
            Friend friend = i.next();
            if (!friend.isOnline()) {
                i.remove();
            }
        }
        return f;
    }
}


