package com.spielpark.steve.leaguechat.mainpage.friendinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.ChatMode;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.chatpage.actChatPage;
import com.spielpark.steve.leaguechat.service.ChatService;
import com.spielpark.steve.leaguechat.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dto.Static.Info;

/**
 * Created by Steve on 1/31/2015.
 */
public class FriendsAdapter extends BaseAdapter {
    public static List<FriendInfo> infos;
    private Context context;
    private LayoutInflater inflater;
    public FriendsAdapter(Context ctx, List<FriendInfo> infos, LayoutInflater inflater) {
        this.context = ctx;
        this.infos = infos;
        this.inflater = inflater;
    }

    public static List<FriendInfo> getInfo() {
        return infos;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public FriendInfo getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.listview_friends_list, null);
        }
        final FriendInfo curFriend = infos.get(position);
        ((ImageView)v.findViewById(R.id.fr_img_profile)).setImageResource(Util.getProfileIconId(curFriend.getProfIconID()));
        (v.findViewById(R.id.fr_img_chatico)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setBackgroundColor(0x7030a0EE);
                Intent intent = new Intent(context, actChatPage.class);
                intent.putExtra("friendName", curFriend.getName());
                context.startActivity(intent);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setBackgroundColor(0x00000000);
                    }
                }, 200);
            }
        });
        ((TextView)v.findViewById(R.id.fr_txt_name)).setText(curFriend.getName());
        ((TextView)v.findViewById(R.id.fr_txt_status)).setText(curFriend.getStatus());
        TextView inGameStatus = ((TextView)v.findViewById(R.id.fr_txt_ingame));
        inGameStatus.setText(curFriend.getInGame().realTalk());
        if (inGameStatus.getText().toString().contains("In") || inGameStatus.getText().toString().equals("Spectating")) {
            inGameStatus.setTextColor(Color.rgb(240, 213, 38));
        } else if (inGameStatus.getText().toString().contains("Away")){
            inGameStatus.setTextColor(Color.rgb(239, 51, 42));
        } else {
            inGameStatus.setTextColor(Color.rgb(81, 240, 71));
        }
        return v;
    }

    public void refreshInfos() {
        List<FriendInfo> newList = new ArrayList<>();
        for (Friend f : ChatService.getOnlineFriends()) {
            newList.add(new FriendInfo(f.getName(), f.getStatus().getStatusMessage(), f.getChatMode() == ChatMode.AWAY ? LolStatus.GameStatus.AWAY : f.getStatus().getGameStatus(), f.getStatus().getProfileIconId()));
        }
        //Log.d("FriendsAdapter/refreshInfos", "Refreshing friends list~");
        infos = newList;
        this.sort();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FriendsAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public void addFriend(final Friend friend) {
        String name = friend.getName();
        for (FriendInfo f : infos) {
            if (f.getName().equalsIgnoreCase(name)) {
                return;
            }
        }
        AsyncTask addFriendTask = new AsyncTask<Void, Void, FriendInfo>() {
            @Override
            protected FriendInfo doInBackground(Void... params) {
                return FriendInfoReader.getNewFriend(friend);
            }

            @Override
            protected void onPostExecute(FriendInfo info) {
                infos.add(info) ;
                FriendsAdapter.this.notifyDataSetChanged();
                FriendsAdapter.this.sort();
            }

        }.execute();
    }
    public void removeFriend(Friend friend) {
        String name = friend.getName();
        FriendInfo toRemove = null;
        for (FriendInfo f : infos) {
            if (name.equalsIgnoreCase(f.getName())) {
                toRemove = f;
            }
        }
        infos.remove(toRemove);
        this.notifyDataSetChanged();
    }

    public void updateStatus(Friend friend) {
        String fName = friend.getName();
        for (FriendInfo f : infos) {
            if (f.getName().equals(fName)) {
                f.setInGame(friend.getChatMode() == ChatMode.AWAY ? LolStatus.GameStatus.AWAY : friend.getStatus().getGameStatus());
                f.setStatus(friend.getStatus().getStatusMessage());
            }
        }
        this.sort();
        this.notifyDataSetChanged();
    }

    private void sort() {
        Collections.sort(infos, new Comparator<FriendInfo>() {

            @Override
            public int compare(FriendInfo lhs, FriendInfo rhs) {
                if (lhs == null || rhs == null) {
                    return 0;
                }
                if (lhs.getInGame() == null || rhs.getInGame() == null) {
                    Friend r = ChatService.getFriendByName(lhs.getName());
                    Friend l = ChatService.getFriendByName(rhs.getName());
                    Log.d("FriendsAdapter/sort", "COMPARISON WAS NULL: " + r.getStatus());
                    Log.d("FriendsAdapter/sort", "COMPARISON WAS NULL: " + l.getStatus());
                    return 0;
                }
                int x = lhs.getInGame().order();
                int y = rhs.getInGame().order();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
