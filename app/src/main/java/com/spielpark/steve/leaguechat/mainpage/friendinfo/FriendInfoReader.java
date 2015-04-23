package com.spielpark.steve.leaguechat.mainpage.friendinfo;

import android.content.Context;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.ChatMode;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.spielpark.steve.leaguechat.service.ChatService;

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
    public FriendInfoReader(List<Friend> friends) {
        friendInfo = new ArrayList<FriendInfo>();
        for (Friend f : friends) {
            String name = f.getName();
            String status = f.getStatus().getStatusMessage();
            LolStatus.GameStatus gameStatus = f.getChatMode() == ChatMode.AWAY ? LolStatus.GameStatus.AWAY : f.getStatus().getGameStatus();
            int iconId = f.getStatus().getProfileIconId();
            friendInfo.add(new FriendInfo(name, status, gameStatus, iconId));
        }
    }

    public static FriendInfo getNewFriend(Friend friend) {
        int profIcon = friend.getStatus().getProfileIconId();
        return new FriendInfo(friend.getName(), friend.getStatus().getStatusMessage(), friend.getStatus().getGameStatus(), profIcon);
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
                int y = rhs.getInGame().order();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
        return friendInfo;
    }
}
