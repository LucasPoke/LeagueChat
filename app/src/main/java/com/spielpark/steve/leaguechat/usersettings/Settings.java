package com.spielpark.steve.leaguechat.usersettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.theholywaffle.lolchatapi.LolStatus;
import com.spielpark.steve.leaguechat.service.ChatService;

/**
 * Created by Steve on 5/22/2015.
 */
public class Settings {

    public static final String USER_SETTINGS = "LeagueChat_Profile_Settings";
    private static final String USER_STATUS = "status";
    private static final String USER_PIC = "profile_picture";

    private static SharedPreferences preferences;

    public Settings() {

    }

    public static void init(SharedPreferences p) {
        preferences = p;
    }


    public static int getUserPic() {
        return preferences.getInt(USER_PIC, 8);
    }

    public static String getUserStatus() {
        return preferences.getString(USER_STATUS, "LeagueChat with Android");
    }

    public static void setUserStatus(String s) {
        SharedPreferences.Editor set = preferences.edit();
        set.putString(USER_STATUS, s);
        set.apply();
        updateStatus();
    }

    public static void setUserPic(int i) {
        SharedPreferences.Editor set = preferences.edit();
        set.putInt(USER_PIC, i);
        set.apply();
        updateStatus();
    }

    public static void updateStatus() {
        LolStatus status = new LolStatus();
        status.setLevel(30);
        status.setRankedLeagueName("Android's Cyborgs");
        status.setTier(LolStatus.Tier.GOLD);
        status.setNormalWins(9001);
        status.setRankedWins(9001);
        status.setProfileIconId(Settings.getUserPic());
        status.setStatusMessage(Settings.getUserStatus());
        status.setGameStatus(LolStatus.GameStatus.OUT_OF_GAME);
        ChatService.setStatus(status);
    }

}
