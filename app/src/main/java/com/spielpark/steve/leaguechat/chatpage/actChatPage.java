package com.spielpark.steve.leaguechat.chatpage;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.service.ChatService;

public class actChatPage extends ListActivity {
    private static MessageDB db;
    private static ChatAdapter mAdapter;
    private static Cursor cursor;
    private static String friendName;
    private ChatReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.layout_chatpage);
        }
        db = MessageDB.getInstance(this);
        friendName = getIntent().getExtras().getString("friendName");
        cursor = getCursor();
        mAdapter = new ChatAdapter(this, cursor, 0);
        setListAdapter(mAdapter);
        setUpReceiver();
        getListView().smoothScrollToPosition(mAdapter.getCount());
        findViewById(R.id.edtMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        actChatPage.this.getListView().smoothScrollToPosition(actChatPage.mAdapter.getCount());
                    }
                }, 200);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.notifyDataSetChanged();
        Log.d("actChatPage/onPause", "Went back..");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiver = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.setTitle(friendName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    private void setUpReceiver() {
        Log.d("actMainPage", "Setting up receiver.");
        receiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("friend_status_change");
        filter.addAction("friend_request");
        filter.addAction("message_received");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private Cursor getCursor() {
        String[] args = new String[] {friendName, friendName};
        SQLiteDatabase dBase = db.getWritableDatabase();
        cursor = dBase.query(MessageDB.TableEntry.TABLE_NAME, null, "_from LIKE ? OR _to LIKE ? COLLATE NOCASE", args, null, null, null, null);
        return cursor;
    }

    private void receiveMessage(String from, String message) {
        SQLiteDatabase write = db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MessageDB.TableEntry.COLUMN_TO, ChatService.getUserName());
        cv.put(MessageDB.TableEntry.COLUMN_FROM, from);
        cv.put(MessageDB.TableEntry.COLUMN_MESSAGE, message);
        write.insert(MessageDB.TableEntry.TABLE_NAME, null, cv);
        db.close();
        mAdapter.swapCursor(getCursor());
        mAdapter.notifyDataSetChanged();
        Log.d("actChatPage/receiveMessage", "Message received: " + message);
        getListView().smoothScrollToPosition(mAdapter.getCount());
    }

    public void sendMessage(View v) {
        String msg = ((EditText) findViewById(R.id.edtMessage)).getText().toString();
        ((EditText) findViewById(R.id.edtMessage)).setText("");
        ContentValues cv = new ContentValues();
        cv.put(MessageDB.TableEntry.COLUMN_FROM, ChatService.getUserName());
        cv.put(MessageDB.TableEntry.COLUMN_TO, friendName);
        cv.put(MessageDB.TableEntry.COLUMN_MESSAGE, msg);
        db.getWritableDatabase().insert(MessageDB.TableEntry.TABLE_NAME, null, cv);
        db.close();
        Intent intent = new Intent(this, ChatService.class);
        intent.setAction("SEND_MESSAGE");
        intent.putExtra("friendName", friendName);
        intent.putExtra("message", msg);
        startService(intent);
        mAdapter.swapCursor(getCursor());
        mAdapter.notifyDataSetChanged();
        getListView().smoothScrollToPosition(mAdapter.getCount());
    }

    private class ChatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "friend_status_change" : {
                    //TODO: This. updateFriendsList();
                    break;
                }
                case "message_received" : {
                    receiveMessage(intent.getExtras().getString("arg0"), intent.getExtras().getString("arg1"));
                    break;
                }
                case "friend_request" : {
                    //TODO: This.
                }
            }
        }
    }
}
