package com.spielpark.steve.leaguechat.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.spielpark.steve.leaguechat.chatpage.MessageDB;
import com.spielpark.steve.leaguechat.mainpage.actMainPage;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendsAdapter;

import org.jivesoftware.smack.SmackException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
public class ChatService extends IntentService {

    private static LoLChat api;
    private static String userName;
    public static Friend updated;
    public static Friend updated2;
    public ChatService() {
        super(ChatService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            throw new NullPointerException("ChatService received a null intent.");
        }
        switch(intent.getAction()) {
            case "DO_LOGIN" : {
                handleLogin(intent.getExtras().getString("username"), intent.getExtras().getCharArray("password"));
                intent.getExtras().remove("password");
                break;
            }
            case "DO_LOGOUT" : {
                handleLogout();
                break;
            }
            case "SEND_MESSAGE" : {
                sendMessage(intent.getExtras().getString("friendName"), intent.getExtras().getString("message"));
            }
        }
    }

    private void sendMessage(String to, String message) {
        Friend toSend = api.getFriendByName(to);
        toSend.sendMessage(message);
    }

    public static List<Friend> getOnlineFriends() {
        return api.getOnlineFriends();
    }

    public static Friend getFriendByName(String name) {
        return api.getFriendByName(name);
    }
    public static String getUserName() {
        return userName;
    }

    private void handleLogout() {
        try {
            this.api.disconnect();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void setUpChatlistener() {
        if (api == null) {
            throw new NullPointerException("API has not been initialized.");
        }
        api.addChatListener(new ChatListener() {
            @Override
            public void onMessage(Friend friend, String message) {
                sendBroadcast("message_received", friend.getName(), message);
            }
        });
    }

    private void setupFriendsListener() {
        if (api == null) {
            throw new NullPointerException("API has not been initialized.");
        }
        api.addFriendListener(new FriendListener() {
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
        });
    }
    private void handleLogin(final String u, final char[] pw) {
        ChatService.this.userName = u;
        LoLChat.init(getApplicationContext());
        AsyncTask loginTask = new AsyncTask<Void, Void, Void>() {
            boolean loggedIn;

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setupFriendsListener();
                setUpChatlistener();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    sendBroadcast("login_status_update", "Assigning API..");
                    api = new LoLChat(ChatServer.NA, false);
                    loggedIn = api.login(u, new String(pw));
                    if (loggedIn) {
                        sendBroadcast("login_status_update", "Logged in!");
                        api.reloadRoster();
                        sendBroadcast("login_status_update", "Redirecting to Main Chat page..");
                        sendBroadcast("login_transition");
                    }
                    Log.d("LOGGING IN: SERVICE", "Sending broadcast, should be logged in..");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    public void sendBroadcast(String action, String... messages) {
        Intent i = new Intent();
        i.setAction(action);
        for (int j = 0; j < messages.length; j++) {
            i.putExtra("arg" + j, messages[j]);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }
}
