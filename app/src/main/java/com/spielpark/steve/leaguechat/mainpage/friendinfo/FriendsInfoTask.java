package com.spielpark.steve.leaguechat.mainpage.friendinfo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.github.theholywaffle.lolchatapi.LoLChat;
import com.spielpark.steve.leaguechat.mainpage.actMainPage;
import com.spielpark.steve.leaguechat.service.ChatService;
import com.spielpark.steve.leaguechat.usersettings.Settings;

import java.util.List;

/**
 * Created by Steve on 1/31/2015.
 */
public class FriendsInfoTask extends AsyncTask {
    private ListView view;
    private static FriendInfoReader reader;
    Context ctx;
    LayoutInflater inflater;
    public FriendsInfoTask(ListView view, Context ctx, LayoutInflater inflater) {
        this.view = view;
        this.inflater = inflater;
        this.ctx = ctx;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Settings.updateStatus();
        reader = new FriendInfoReader(ChatService.getFriendGroups());
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        List<FriendInfo> infos = reader.getFriendInfo();
        reader.finish();
        FriendsAdapter adapter = new FriendsAdapter(ctx, infos, inflater);
        view.setAdapter(adapter);
        actMainPage.mAdapter = adapter;
    }
}
