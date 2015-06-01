package com.spielpark.steve.leaguechat.mainpage.friendinfo;

import android.content.Context;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.ChatMode;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.github.theholywaffle.lolchatapi.wrapper.FriendGroup;
import com.spielpark.steve.leaguechat.service.ChatService;
import com.spielpark.steve.leaguechat.util.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import constant.Region;
import constant.Season;
import dto.Summoner.Summoner;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

/**
 * Created by Steve on 1/31/2015.
 */
public class FriendInfoReader {
    private static List<FriendInfo> friendInfo;

    public FriendInfoReader(List<FriendGroup> groups) {
        friendInfo = new ArrayList<>();
        int groupVal;
        for (FriendGroup fg : groups) {
            int numOnline = 0;
            int total = 0;
            groupVal = Util.getGroupVal(fg.getName());
            FriendInfo group = new FriendInfo("!--GROUP--!", fg.getName());
            group.setInGame(LolStatus.GameStatus.GROUP);
            group.setGroupPos(Util.getGroupVal(fg.getName()));
            for (Friend f : fg.getFriends()) {
                total++;
                if (!(f.isOnline())) continue;
                numOnline++;
                String name = f.getName();
                String status = f.getStatus().getStatusMessage();
                LolStatus.GameStatus gameStatus = f.getChatMode() == ChatMode.AWAY ? LolStatus.GameStatus.AWAY : f.getStatus().getGameStatus();
                if (gameStatus == null) gameStatus = LolStatus.GameStatus.OUT_OF_GAME;
                int iconId = f.getStatus().getProfileIconId();
                friendInfo.add(new FriendInfo(name, status, fg.getName(), gameStatus, iconId, groupVal));
            }
            group.setProfIconID(total + (1000*numOnline));
            friendInfo.add(group);
        }
    }

    public static FriendInfo getNewFriend(Friend friend) {
        int profIcon = friend.getStatus().getProfileIconId();
        return new FriendInfo(friend.getName(), friend.getStatus().getStatusMessage(), friend.getGroup().getName(), friend.getStatus().getGameStatus(), profIcon, Util.getGroupVal(friend.getGroup().getName()));
    }

    public List<FriendInfo> getFriendInfo() {
        Collections.sort(friendInfo, new Comparator<FriendInfo>() {

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
        return friendInfo;
    }

    public void finish() {
        friendInfo = null;
    }
}
