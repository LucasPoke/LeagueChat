package com.spielpark.steve.leaguechat.mainpage;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.actLogin_transition;
import com.spielpark.steve.leaguechat.chatpage.MessageDB;
import com.spielpark.steve.leaguechat.chatpage.actChatPage;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendInfo;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendsAdapter;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendsInfoTask;
import com.spielpark.steve.leaguechat.service.ChatService;
import com.spielpark.steve.leaguechat.util.Util;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class actMainPage extends ActionBarActivity {
    public static FriendsAdapter mAdapter;
    private static ListView friendsList;
    private ChatReceiver receiver;
    private ScheduledExecutorService refresher;
    private MessageDB db;
    private boolean mRefreshActive = false;
    private boolean mReceiverActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_page);
        if (savedInstanceState == null) {
            setUpFriendsList();
            setUpRefresh();
            db = MessageDB.getInstance(this);
        }
        findViewById(R.id.info_view).setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onResume();
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
        if(!mRefreshActive) setUpRefresh();
        if(!mReceiverActive) setUpReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("actMainPage/onStop", "Main Page activity stopped.");
        if (mReceiverActive) LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiver = null;
        if (mRefreshActive) refresher.shutdown();
        mReceiverActive = false;
        mRefreshActive = false;
    }

    private void setUpRefresh() {
        Log.d("actMainPage/setupRefresh", "activating refresher.");
        refresher = Executors.newScheduledThreadPool(1);
        refresher.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    mAdapter.refreshInfos();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10, 30, TimeUnit.SECONDS);
        mRefreshActive = true;
    }

    private void setUpReceiver() {
        Log.d("actMainPage", "Setting up receiver.");
        receiver = new ChatReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("friend_status_change");
        filter.addAction("friend_request");
        filter.addAction("message_received");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        mReceiverActive = true;
    }

    private void setUpFriendsList() {
        friendsList = (ListView) findViewById(R.id.friends_list);
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("actMainPage/setUpFriendsList", "Friend Object: " + ChatService.getFriendByName(mAdapter.getItem(position).getName()).getStatus());
                Log.d("Gametime", "Game Time: " + new SimpleDateFormat("mmm:ss").format(ChatService.getFriendByName(mAdapter.getItem(position).getName()).getStatus().getGameTime()));
                Log.d("Skin", "Skin: " + ChatService.getFriendByName(mAdapter.getItem(position).getName()).getStatus().getSkin());
                actMainPage.this.displayExtendedInfo(ChatService.getFriendByName(mAdapter.getInfo().get(position).getName()));
                FriendsAdapter.mSelected = (position == FriendsAdapter.mSelected ? -1 : position);
                mAdapter.notifyDataSetChanged();
            }
        });
        FriendsInfoTask loadFriendsTask = new FriendsInfoTask(friendsList, this, LayoutInflater.from(this));
        loadFriendsTask.execute();
    }

    private void displayExtendedInfo(Friend f) {
        LolStatus status = f.getStatus();
        TextView fName = (TextView)findViewById(R.id.info_Name);
        if (f.getName().equals(fName.getText().toString())) {
            findViewById(R.id.info_view).setVisibility(View.GONE);
            fName.setText("");
            return;
        }
        TextView divName = (TextView)findViewById(R.id.info_division);
        TextView tier = (TextView)findViewById(R.id.info_league_tier);
        TextView time = (TextView)findViewById(R.id.info_game_time);
        TextView gameType = (TextView)findViewById(R.id.info_game_type);
        TextView lastPlayed = (TextView)findViewById(R.id.info_last_played);
        TextView rankedWins = (TextView)findViewById(R.id.info_ranked_wins);
        TextView normalWins = (TextView)findViewById(R.id.info_normal_wins);
        TextView playingAs = (TextView)findViewById(R.id.info_last_played_header);
        ImageView championPic = (ImageView)findViewById(R.id.info_champion_pic);
        ImageView leaguePic = (ImageView)findViewById(R.id.info_league_image);
        ImageView profPic = (ImageView)findViewById(R.id.info_profile_pic);
        String championPicSrc = status.getSkin().equals("") ? "N/A" : status.getSkin();
        String league = status.getRankedLeagueTier().name().equals("UNRANKED") ? "wood" : status.getRankedLeagueTier().name().toLowerCase();
        String division  = status.getRankedLeagueDivision().name().equals("NONE") ? "V" : status.getRankedLeagueDivision().name();
        boolean inGame = ! (status.getGameStatus().internal().equals("outOfGame"));
        fName.setText(f.getName());
        divName.setText(status.getRankedLeagueName());
        tier.setText(league.substring(0, 1).toUpperCase() + league.substring(1) + " " + division);
        tier.getPaint().setShader(Util.getTierGraphics(league));
        rankedWins.setText(Html.fromHtml("Ranked Wins: <b>" + String.valueOf(status.getRankedWins()) + "</b>"));
        normalWins.setText(Html.fromHtml("Normal Wins: <b>" + String.valueOf(status.getNormalWins()) +"</b>"));
        playingAs.setText(inGame ? "Playing as" : "Last played");
        championPic.setImageResource(getImageId(this, championPicSrc.toLowerCase()));
        time.setText(Html.fromHtml("Time: <b>" + (inGame ? new SimpleDateFormat("mm:ss").format(status.getGameTime()) + "</b>" : "00:00")));
        gameType.setText(Html.fromHtml("Type: <b>" + status.getGameQueueType().desc() + "</b>"));
        lastPlayed.setText(championPicSrc);
        leaguePic.setImageResource(getImageId(this, league));
        profPic.setImageResource(Util.getProfileIconId(status.getProfileIconId()));
        findViewById(R.id.info_view).setVisibility(View.VISIBLE);
    }

    private int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
    private void updateFriend(Friend f, String type) {
        if (f != null) {
            //Log.d("actMainPage/updateFriend", "Name: " + f.getName() + "..Type: " + type);
            switch (type) {
                case "away":
                case "busy":
                case "status":
                case "available": {
                    mAdapter.updateStatus(f);
                    //Log.d("actMainPage_updateFriend", "Friend status change: " + f.getName() + "...to: " + type);
                    break;
                }
                case "join": {
                    mAdapter.addFriend(f);
                    //Log.d("actMainPage_updateFriend", "Friend joined: " + f.getName());
                    break;
                }
                case "leave": {
                    //Log.d("actMainPage_updateFriend", "Friend left: " + f.getName());
                    mAdapter.removeFriend(f);
                }
            }
        }
    }

    private void receiveMessage(String from) {
        for (FriendInfo inf : mAdapter.getInfo()) {
            if (inf.getName().equals(from) && !(inf.isPendingMessage())) {
                inf.setPendingMessage(true);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private class ChatReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "friend_status_change" : {
                    actMainPage.this.updateFriend(ChatService.updated, intent.getExtras().getString("arg1"));
                    actMainPage.this.updateFriend(ChatService.updated2, intent.getExtras().getString("arg1"));
                    ChatService.updated = null;
                    ChatService.updated2 = null;
                    break;
                }
                case "message_received" : {
                    receiveMessage(intent.getExtras().getString("arg0"));
                    break;
                }
                case "friend_request" : {
                    //TODO: This.
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}
