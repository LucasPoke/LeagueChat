package com.spielpark.steve.leaguechat.mainpage.friendinfo;

import com.github.theholywaffle.lolchatapi.LolStatus;

import dto.Game.Game;

/**
 * Created by Steve on 2/1/2015.
 */
public class FriendInfo {

    private String  status;
    private String name;
    private LolStatus.GameStatus gameStatus;
    private int profIconID;

    public FriendInfo(String name) {
        this.name = name;
    }

    public FriendInfo(String name, String status, LolStatus.GameStatus inGame, int profIconID) {
        this.status = status;
        this.name = name;
        this.gameStatus = inGame;

        this.profIconID = profIconID;
    }

    public int getProfIconID() {
        return profIconID;
    }

    public void setProfIconID(int profIconID) {
        this.profIconID = profIconID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LolStatus.GameStatus getInGame() {
        return gameStatus;
    }

    public void setInGame(LolStatus.GameStatus inGame) {
        this.gameStatus = inGame;
    }

}
