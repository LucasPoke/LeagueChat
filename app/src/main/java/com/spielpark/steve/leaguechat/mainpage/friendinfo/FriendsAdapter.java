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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.ChatMode;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.github.theholywaffle.lolchatapi.wrapper.FriendGroup;
import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.chatpage.actChatPage;
import com.spielpark.steve.leaguechat.service.ChatService;
import com.spielpark.steve.leaguechat.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Steve on 1/31/2015.
 */
public class FriendsAdapter extends BaseAdapter {
    public static List<FriendInfo> infos;
    public static int mSelected = -1;
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
        final FriendInfo curFriend = infos.get(position);
        if (curFriend.getName().equals("!--GROUP--!")) {
            v = inflater.inflate(R.layout.listview_friends_section_header, null);
            v.setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.txt_header_name)).setText(curFriend.getGroupName() + Util.getFriendCount(curFriend.getProfIconID()));
            return v;
        }
        if (v == null || v.getTag() == null) {
            v = inflater.inflate(R.layout.listview_friends_list, null);
            ViewHolder vh = new ViewHolder();
            vh.profIcon = (ImageView)v.findViewById(R.id.fr_img_profile);
            vh.chatIcon = (ImageView)v.findViewById(R.id.fr_img_chatico);
            vh.name = (TextView)v.findViewById(R.id.fr_txt_name);
            vh.status = (TextView)v.findViewById(R.id.fr_txt_status);
            vh.gameStatus = (TextView)v.findViewById(R.id.fr_txt_ingame);
            v.setTag(vh);
        }
        if (position == mSelected) {
            v.setBackgroundColor(Color.argb(80, 222, 222, 222));
        } else {
            v.setBackgroundColor(Color.argb(47, 0, 124, 114));
        }
        ViewHolder vh = (ViewHolder) v.getTag();
        vh.profIcon.setImageResource(Util.getProfileIconId(curFriend.getProfIconID()));
        vh.chatIcon.setImageResource(curFriend.isPendingMessage() ? R.drawable.chatico_pending : R.drawable.chatico);
        vh.chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(context, actChatPage.class);
                Intent i2 = new Intent(context, ChatService.class);
                i2.setAction("REMOVE_NOTIFICATION");
                i2.putExtra("name", curFriend.getName());
                context.startService(i2);
                intent.putExtra("friendName", curFriend.getName());
                curFriend.setPendingMessage(false);
                notifyDataSetChanged();
                context.startActivity(intent);
            }
        });
        vh.name.setText(curFriend.getName());
        vh.status.setText(curFriend.getStatus());
        vh.gameStatus.setText(curFriend.getInGame().realTalk());
        if (vh.gameStatus.getText().toString().contains("In") || vh.gameStatus.getText().toString().equals("Spectating")) {
            vh.gameStatus.setTextColor(Color.rgb(240, 213, 38));
        } else if (vh.gameStatus.getText().toString().contains("Away")){
            vh.gameStatus.setTextColor(Color.rgb(239, 51, 42));
        } else {
            vh.gameStatus.setTextColor(Color.rgb(81, 240, 71));
        }
        return v;
    }

    static class ViewHolder {
        TextView name;
        TextView status;
        TextView gameStatus;
        ImageView profIcon;
        ImageView chatIcon;
    }
    public void refreshInfos() {
        List<FriendInfo> newList = new ArrayList<>();
        List<String> pendingMessages = new ArrayList(15);
        int groupVal;
        for (FriendInfo f : infos) {
            if (f.isPendingMessage()) {
                pendingMessages.add(f.getName());
            }
        }
        for (FriendGroup fg : ChatService.getFriendGroups()) {
            int numOnline = 0;
            int total = 0;groupVal = Util.getGroupVal(fg.getName());
            FriendInfo group = new FriendInfo("!--GROUP--!", fg.getName());
            group.setInGame(LolStatus.GameStatus.GROUP);
            group.setGroupPos(Util.getGroupVal(fg.getName()));
            for (Friend f : fg.getFriends()) {
                total++;
                if (!(f.isOnline())) continue;
                numOnline++;                String name = f.getName();
                String status = f.getStatus().getStatusMessage();
                LolStatus.GameStatus gameStatus = f.getChatMode() == ChatMode.AWAY ? LolStatus.GameStatus.AWAY : f.getStatus().getGameStatus();
                int iconId = f.getStatus().getProfileIconId();
                newList.add(new FriendInfo(name, status, fg.getName(), gameStatus, iconId, groupVal, pendingMessages.contains(name)));
            }
            group.setProfIconID(total + (1000*numOnline));
            newList.add(group);
        }
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
        new AsyncTask<Void, Void, FriendInfo>() {
            @Override
            protected FriendInfo doInBackground(Void... params) {
                return FriendInfoReader.getNewFriend(friend);
            }

            @Override
            protected void onPostExecute(FriendInfo info) {
                infos.add(info) ;
                Util.changeFriendGroupCount(infos, friend, true);
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
        Util.changeFriendGroupCount(infos, friend, false);
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
                    return 0;
                }
                int x = lhs.getInGame().order();
                x += lhs.getGroupPos();
                int y = rhs.getInGame().order();
                y += rhs.getGroupPos();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
