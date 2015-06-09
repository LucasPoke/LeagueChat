package com.spielpark.steve.leaguechat.usersettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.LolStatus;
import com.spielpark.steve.leaguechat.service.ChatService;

import org.jdom2.JDOMException;

import java.io.IOException;

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

    public static void setUserInfo(String s, int i) {
        SharedPreferences.Editor set = preferences.edit();
        set.putString(USER_STATUS, s);
        set.putInt(USER_PIC, i);
        set.apply();
        updateStatus();
    }

    public static void updateStatus() {
        LolStatus status = new LolStatus();
        try {
            status = new LolStatus("<body><profileIcon>539</profileIcon><level>30</level><wins>9001</wins><leaves>69</leaves><odinWins>106</odinWins><odinLeaves>3</odinLeaves><queueType>NONE</queueType><rankedLosses>0</rankedLosses><rankedRating>0</rankedRating><tier>GOLD</tier><rankedSoloRestricted>false</rankedSoloRestricted><championMasteryScore>255</championMasteryScore><statusMsg>LeagueChat with Android!</statusMsg><skinname>Tryndamere</skinname><gameQueueType>CAP_5x5</gameQueueType><timeStamp>1432520450830</timeStamp><gameStatus>outOfGame</gameStatus><rankedLeagueName>Cyborg's Spawned</rankedLeagueName><rankedLeagueDivision>1</rankedLeagueDivision><rankedLeagueTier>GOLD</rankedLeagueTier><rankedLeagueQueue>RANKED_SOLO_5x5</rankedLeagueQueue><rankedWins>9001</rankedWins></body>");
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        } finally {
            status.setProfileIconId(Settings.getUserPic());
            status.setStatusMessage(Settings.getUserStatus());
            status.setGameStatus(LolStatus.GameStatus.OUT_OF_GAME);
        }
        ChatService.setStatus(status);
    }
}
