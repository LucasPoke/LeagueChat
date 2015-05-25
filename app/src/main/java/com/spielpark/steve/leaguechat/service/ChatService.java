package com.spielpark.steve.leaguechat.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.github.theholywaffle.lolchatapi.wrapper.FriendGroup;
import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.chatpage.MessageDB;
import com.spielpark.steve.leaguechat.chatpage.actChatPage;
import com.spielpark.steve.leaguechat.mainpage.actMainPage;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendInfo;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendsAdapter;
import com.spielpark.steve.leaguechat.usersettings.Settings;

import org.jivesoftware.smack.SmackException;

import java.io.IOException;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
public class ChatService extends IntentService {
    private static LoLChat api;
    private static String userName;
    public static StringBuilder pendingFriends = new StringBuilder();
    public static Friend updated;
    public static Friend updated2;
    private static ChatListener chatListener;
    private static FriendListener friendListener;
    public ChatService() {
        super(ChatService.class.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MessageDB.getInstance(this).close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            throw new NullPointerException("ChatService received a null intent.");
        }
        switch(intent.getAction()) {
            case "DO_LOGIN" : {
                handleLogin(intent.getExtras().getString("username"), intent.getExtras().getCharArray("password"), ChatServer.valueOf(intent.getExtras().getString("region")));
                intent.getExtras().remove("password");
                break;
            }
            case "DO_LOGOUT" : {
                handleLogout();
                break;
            }
            case "SEND_MESSAGE" : {
                sendMessage(intent.getExtras().getString("friendName"), intent.getExtras().getString("message"));
                break;
            }
            case "NOTIFICATION_REMOVED" : {
                pendingFriends.setLength(0);
                Log.d("ChSrv/onHandleIntent", "Notification cleared by user.");
            }
            case "REMOVE_NOTIFICATION" : {
                String name = intent.getExtras().getString("name");
                if (pendingFriends.toString().contains(name)) {
                    pendingFriends.setLength(0);
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(256);
                }
            }
        }
    }

    public static List<FriendGroup> getFriendGroups() {
        return api.getFriendGroups();
    }

    public static Friend getFriendByName(String name) {
        return api.getFriendByName(name);
    }
    public static String getUserName() {
        return userName;
    }

    private void handleLogout() {
        try {
            api.disconnect();
            api.removeChatListener(chatListener);
            api.removeFriendListener(friendListener );
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void playSound() {
        try {
            MediaPlayer player = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd("sounds/message.mp3");
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String to, String message) {
        Friend toSend = api.getFriendByName(to);
        toSend.sendMessage(message);
    }

    private void receiveMessage(Friend from, String message) {
        MessageDB db = MessageDB.getInstance(this);
        SQLiteDatabase write = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MessageDB.TableEntry.COLUMN_TO, ChatService.getUserName());
        cv.put(MessageDB.TableEntry.COLUMN_FROM, from.getName());
        cv.put(MessageDB.TableEntry.COLUMN_MESSAGE, message);
        cv.put(MessageDB.TableEntry.COLUMN_TIME, System.currentTimeMillis());
        cv.put(MessageDB.TableEntry.COLUMN_PROFILE, from.getStatus().getProfileIconId());
        write.insert(MessageDB.TableEntry.TABLE_NAME, null, cv);
        makeNotification(from.getName(), message);
        for (FriendInfo inf : FriendsAdapter.getInfo()) {
            if (inf.getName().equals(from) && !(inf.isPendingMessage())) {
                Log.d("aMP/receiveMessage", "Pending message for: " + from.getName());
                inf.setPendingMessage(true);
                break;
            }
        }
        sendBroadcast("message_received");
    }

    public static void setStatus(LolStatus l) {
        api.setStatus(l);
    }

    private void makeNotification(String from, String message) {
        playSound();
        if (pendingFriends.toString().equals(from)) pendingFriends.setLength(0);
        if (pendingFriends.toString().contains(from)) return;
        boolean multiple = pendingFriends.length() > 0;
        Intent intent = new Intent(this, multiple ? actMainPage.class : actChatPage.class);
        if (!multiple)
            intent.putExtra("friendName", from);
        TaskStackBuilder stack = TaskStackBuilder.create(this);
        stack.addParentStack(multiple ? actMainPage.class : actChatPage.class);
        stack.addNextIntent(intent);
        PendingIntent pIntent = stack.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        Intent broadcastIntent = new Intent(this, ChatService.class);
        broadcastIntent.setAction("NOTIFICATION_REMOVED");
        PendingIntent pIntentBroadcast = PendingIntent.getService(this, 512, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder bldr = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentText(multiple ? pendingFriends.append(", ").append(from) : message.length() > 30 ? message.substring(0, 29) + "..." : message)
                .setContentTitle(multiple ? "Multiple Messages" : "Message From: " + pendingFriends.append(from).toString())
                .setSmallIcon(R.drawable.chatico_pending)
                .setContentIntent(pIntent)
                .setLights(0xDDE5531F, 100, 2000)
                .setTicker(from)
                .setDeleteIntent(pIntentBroadcast);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(256, bldr.build());
    }

    public static Cursor queryDB(String fName, Context ctx) {
        String[] args = new String[] {fName, fName};
        SQLiteDatabase dBase = MessageDB.getInstance(ctx).getWritableDatabase();
        return dBase.query(MessageDB.TableEntry.TABLE_NAME, null, "_from LIKE ? OR _to LIKE ? COLLATE NOCASE", args, null, null, null, null);
    }

    private void setUpChatlistener() {
        if (api == null) {
            throw new NullPointerException("API has not been initialized.");
        }
        chatListener = new ChatListener() {
            @Override
            public void onMessage(Friend friend, String message) {
                sendBroadcast("message_received", friend.getName());
                receiveMessage(friend, message);
            }
        };
        api.addChatListener(chatListener);
    }

    private void setupFriendsListener() {
        if (api == null) {
            throw new NullPointerException("API has not been initialized.");
        }
        friendListener = new FriendListener() {
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
        };
        api.addFriendListener(friendListener);
    }
    private void handleLogin(final String u, final char[] pw, final ChatServer region) {
        userName = u;
        LoLChat.init(getApplicationContext());
        new AsyncTask<Void, Void, Void>() {
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
                    api = new LoLChat(region, false);
                    loggedIn = api.login(u, new String(pw));
                    if (loggedIn) {
                        sendBroadcast("login_status_update", "Logged in!");
                        api.reloadRoster();
                        sendBroadcast("login_status_update", "Redirecting to Main C hat page..");
                        Settings.updateStatus();
                        sendBroadcast("login_transition");
                    }
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
