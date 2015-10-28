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

/**
 * This and all the files in the module have been developed by Bert De Geyter (https://github
 * .com/TheHolyWaffle) and are protected by the Apache GPLv3 license.
 */
public class ChatConnection {

    private final XMPPTCPConnection connection;

    public ChatConnection (XMPPTCPConnection connection) {
        this.connection = connection;
    }
    /**
     * Disconnects from chatserver and releases all resources.
     */
    public void disconnect() throws SmackException.NotConnectedException {
        connection.disconnect();
        stop = true;
    }

    /**
     * Logs in to the chat server without replacing the official connection of
     * the League of Legends client. This call is asynchronous.
     *
     * @return true if login is successful, otherwise false
     */
    public boolean login(String username, String password) throws IOException {
        return login(username, password, false);
    }

    /**
     * Logs in to the chat server. This call is asynchronous.
     *
     * @param replaceLeague True will disconnect you account from the League of Legends
     *                      client. False allows you to have another connection next to
     *                      the official connection in the League of Legends client.
     * @return True if login was successful
     */

    public boolean login(String username, String password, boolean replaceLeague)
            throws IOException {
        connection.setPacketReplyTimeout(60000);
        try {
            if (replaceLeague) {
                connection.login(username, "AIR_" + password, "xiff");
            } else {
                connection.login(username, "AIR_" + password);
            }
        } catch (SASLErrorException e) {
            return Boolean.FALSE; //Wrong credentials
        } catch (XMPPException | SmackException e) {
            Log.wtf("debug", e);
        }
        return connection.isAuthenticated();
    }
}