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
        friendName = getIntent().getExtras().getString("friendName");
        cursor = ChatService.queryDB(friendName, this);
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

    private void receiveMessage() {
        mAdapter.swapCursor(ChatService.queryDB(friendName, this));
        mAdapter.notifyDataSetChanged();
        getListView().smoothScrollToPosition(mAdapter.getCount());
    }

    public void sendMessage(View v) {
        String msg = ((EditText) findViewById(R.id.edtMessage)).getText().toString();
        if (msg.length() == 0) return;
        ((EditText) findViewById(R.id.edtMessage)).setText("");

        Intent intent = new Intent(this, ChatService.class);
        intent.setAction("SEND_MESSAGE");
        intent.putExtra("friendName", friendName);
        intent.putExtra("message", msg);
        startService(intent);
        mAdapter.swapCursor(ChatService.queryDB(friendName, this));
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
                    receiveMessage();
                    break;
                }
                case "friend_request" : {
                    //TODO: This.
                }
            }
        }
    }
}
