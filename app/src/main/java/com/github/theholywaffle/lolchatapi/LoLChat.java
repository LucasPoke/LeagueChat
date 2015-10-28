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
public class LoLChat {

    private final XMPPTCPConnection connection;
    private final ArrayList<ChatListener> chatListeners = new ArrayList<>();
    private final ArrayList<FriendListener> friendListeners = new ArrayList<>();
    private boolean stop = false;

    private String status = "";
    private Presence.Type type = Presence.Type.available;
    private Presence.Mode mode = Presence.Mode.chat;

    private ChatConnection chatConnection;
    private FriendSearch friendSearch;

    /**
     * Represents a single connection to a League of Legends chatserver.
     *
     * @param server               The chatserver of the region you want to connect to
     * @param acceptFriendRequests True will automatically accept all friend requests. False will
     *                             ignore all friend requests. NOTE: automatic accepting of
     *                             requests causes the name of the new friend to be null.
     */
    public LoLChat(ChatServer server, boolean acceptFriendRequests) throws IOException {
        Roster.setDefaultSubscriptionMode(acceptFriendRequests ? SubscriptionMode.accept_all : SubscriptionMode.manual);
        ConnectionConfiguration config = new ConnectionConfiguration(server.host, 5223, "pvp.net");
        SASLAuthentication.supportSASLMechanism("PLAIN");
        SASLAuthentication.supportSASLMechanism("KERBEROS_V4");
        SASLAuthentication.supportSASLMechanism("GSSAPI");
        SASLAuthentication.supportSASLMechanism("SKEY");
        SASLAuthentication.supportSASLMechanism("EXTERNAL");
        SASLAuthentication.supportSASLMechanism("CRAM-MD5");
        SASLAuthentication.supportSASLMechanism("ANONYMOUS");
        SASLAuthentication.supportSASLMechanism("OTP");
        SASLAuthentication.supportSASLMechanism("GSS-SPNEGO");
        SASLAuthentication.supportSASLMechanism("SECURID");
        SASLAuthentication.supportSASLMechanism("NTLM");
        SASLAuthentication.supportSASLMechanism("NMAS_LOGIN");
        SASLAuthentication.supportSASLMechanism("NMAS_AUTHEN");
        SASLAuthentication.supportSASLMechanism("DIGEST-MD5");
        SASLAuthentication.supportSASLMechanism("9798-U-RSA-SHA1-ENC");
        SASLAuthentication.supportSASLMechanism("9798-M-RSA-SHA1-ENC");
        SASLAuthentication.supportSASLMechanism("9798-U-DSA-SHA1");
        SASLAuthentication.supportSASLMechanism("9798-M-DSA-SHA1");
        SASLAuthentication.supportSASLMechanism("9798-U-ECDSA-SHA1");
        SASLAuthentication.supportSASLMechanism("9798-M-ECDSA-SHA1");
        SASLAuthentication.supportSASLMechanism("KERBEROS_V5");
        SASLAuthentication.supportSASLMechanism("NMAS-SAMBA-AUTH");
        SASLAuthentication.supportSASLMechanism("SCRAM-*");
        SASLAuthentication.supportSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.supportSASLMechanism("SCRAM-SHA-1-PLUS");
        SASLAuthentication.supportSASLMechanism("GS2-*");
        SASLAuthentication.supportSASLMechanism("GS2-KRB5");
        SASLAuthentication.supportSASLMechanism("GS2-KRB5-PLUS");
        SASLAuthentication.supportSASLMechanism("SPNEGO");
        SASLAuthentication.supportSASLMechanism("SPNEGO-PLUS");
        SASLAuthentication.supportSASLMechanism("SAML20");
        SASLAuthentication.supportSASLMechanism("OPENID20");
        SASLAuthentication.supportSASLMechanism("EAP-AES128");
        SASLAuthentication.supportSASLMechanism("EAP-AES128-PLUS");
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        config.setSocketFactory(new DummySSLSocketFactory());
        config.setCompressionEnabled(true);
        connection = new XMPPTCPConnection(config);
        try {
            connection.connect();
        } catch (XMPPException | SmackException e) {
            Log.wtf("debug", "Failed to connect to " + connection.getHost(), e);
        }
        addListeners();

        chatConnection = new ChatConnection (connection);
        friendSearch = new FriendSearch (connection);


        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!stop) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }).start();
    }


    //__________metodos relacionados a login (implementados pela classe ChatConnection)

    public void disconnect() throws SmackException.NotConnectedException {
        chatConnection.disconnect();
    }

    public boolean login(String username, String password) throws IOException {
        return chatConnection.login(username, password);
    }


    //__________metodos relacionados a busca de amigos (implementados pela classe FriendSeach)


    public FriendGroup getDefaultFriendGroup() {
        return friendSearch.getDefaultFriendGroup();
    }

    public Friend getFriendById(String xmppAddress) {
        return friendSearch.getFriendById(xmppAddress);
    }

    public Friend getFriendByName(String name) {
        return friendSearch.getFriendByName(name);
    }

    public FriendGroup getFriendGroupByName(String name) {
        return friendSearch.getFriendGroupByName(name);
    }

    public List<FriendGroup> getFriendGroups() {
        return friendSearch.getFriendGroups();
    }

    public List<Friend> getFriends() {
        return friendSearch.getFriends();
    }

    public List<Friend> getOfflineFriends() {
        return friendSearch.getOfflineFriends();
    }

    public List<Friend> getOnlineFriends() {
        return friendSearch.getOnlineFriends();
    }


    //__________metodos relacionados a listeners

    /**
     * Adds a ChatListener that listens to messages from all your friends.
     */
    public void addChatListener(ChatListener chatListener) {
        chatListeners.add(chatListener);
    }

    /**
     * Adds a FriendListener that listens to changes from all your friends. Such
     * as logging in, starting games, ...
     */
    public void addFriendListener(FriendListener friendListener) {
        friendListeners.add(friendListener);
    }

    private synchronized void addListeners() {
        connection.getRoster().addRosterListener(new RosterListener() {

            private HashMap<String, Presence.Type> typeUsers = new HashMap<>();
            private HashMap<String, Presence.Mode> modeUsers = new HashMap<>();
            private HashMap<String, String> statusUsers = new HashMap<>();

            public void entriesAdded(Collection<String> e) {
            }

            public void entriesDeleted(Collection<String> e) {
            }

            public void entriesUpdated(Collection<String> e) {
            }

            public void presenceChanged(Presence p) {
                String from = p.getFrom();
                if (from != null) {
                    p = connection.getRoster().getPresence(from);
                    final Friend friend = getFriendById(from);
                    if (friend != null) {
                        for (final FriendListener l : friendListeners) {
                            final Presence.Type previousType = typeUsers.get(from);
                            if (p.getType() == Presence.Type.available
                                    && (previousType == null || previousType != Presence.Type.available)) {
                                l.onFriendJoin(friend);
                            } else if (p.getType() == Presence.Type.unavailable
                                    && (previousType == null || previousType != Presence.Type.unavailable)) {
                                l.onFriendLeave(friend);
                                typeUsers.remove(friend);
                                modeUsers.remove(friend);
                                statusUsers.remove(friend);
                                return;
                            }
                            final Presence.Mode previousMode = modeUsers.get(from);
                            //Log.d("LoLChat/PresenceChanged", "Name: " + friend.getName() + "..PType: " + previousType + "..PMode: " + previousMode);
                            //Log.d("LoLChat/PresenceChanged", "Name: " + friend.getName() + "..Type: " + p.getType() + "..Mode: " + p.getMode());
                            if (p.getMode() == Presence.Mode.chat
                                    && (previousMode == null || previousMode != Presence.Mode.chat)) {
                                l.onFriendAvailable(friend);
                            } else if (p.getMode() == Presence.Mode.away
                                    && (previousMode == null || previousMode != Presence.Mode.away)) {
                                l.onFriendAway(friend);
                            } else if (p.getMode() == Presence.Mode.dnd
                                    && (previousMode == null || previousMode != Presence.Mode.dnd)) {
                                l.onFriendBusy(friend);
                            }
                            final String status = p.getStatus();
                            if (status != null) {
                                if (status != statusUsers.get(from)) {
                                    l.onFriendStatusChange(friend);
                                }
                            }
                            typeUsers.put(from, p.getType());
                            modeUsers.put(from, p.getMode());
                            statusUsers.put(from, p.getStatus());
                        }
                    }
                }
            }
        });

        ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListener() {

            @Override
            public void chatCreated(Chat c, boolean locally) {
                final Friend friend = getFriendById(c.getParticipant());
                if (friend != null) {
                    c.addMessageListener(new MessageListener() {

                        @Override
                        public void processMessage(Chat chat, Message msg) {
                            for (ChatListener c : chatListeners) {
                                if (msg.getBody() != null && msg.getType() == Message.Type.chat) {
                                    c.onMessage(friend, msg.getBody());
                                }
                            }
                        }
                    });
                } else {
                    Log.wtf("debug", "Friend is null in chat creation");
                }

            }
        });
    }

    /**
     * Removes the ChatListener from the list and will no longer be called.
     */
    public void removeChatListener(ChatListener chatListener) {
        chatListeners.remove(chatListener);
    }

    /**
     * Removes the FriendListener from the list and will no longer be called.
     */
    public void removeFriendListener(FriendListener friendListener) {
        friendListeners.remove(friendListener);
    }

    //_________metodos relacionados a updates no status

    /**
     * Changes your ChatMode (e.g. ingame, away, available)
     *
     * @see com.github.theholywaffle.lolchatapi.ChatMode
     */
    public void setChatMode(ChatMode chatMode) {
        this.mode = chatMode.mode;
        updateStatus();
    }

    /**
     * Change your appearance to offline.
     */
    public void setOffline() {
        this.type = Presence.Type.unavailable;
        updateStatus();
    }

    /**
     * Change your appearance to online.
     */
    public void setOnline() {
        this.type = Presence.Type.available;
        updateStatus();
    }

    /**
     * Update your own status with current level, ranked wins...
     * <p/>
     * Create an Status object (without constructor arguments) and call the
     * several ".set" methods on it to customise it. Finally pass this Status
     * object back to this method
     *
     * @param status Your custom Status object
     * @see LolStatus
     */
    public void setStatus(LolStatus status) {
        this.status = status.toString();
        updateStatus();
    }

    private void updateStatus() {
        Presence newPresence = new Presence(type, status, 1, mode);
        try {
            connection.sendPacket(newPresence);
        } catch (SmackException.NotConnectedException e) {
            Log.wtf("debug", "e");
        }
    }


    //__________metodos relacionados a smack

    public static SmackAndroid init(Context context) {
        return SmackAndroid.init(context);
    }

    public void reloadRoster() {
        try {
            connection.getRoster().reload();
        } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException e) {
            Log.wtf("debug", e);
        }
    }
}
