package com.spielpark.steve.leaguechat.mainpage.friendinfo;

import com.github.theholywaffle.lolchatapi.LolStatus;

import dto.Game.Game;

/**
 * Created by Steve on 2/1/2015.
 */
public class FriendInfo {

    private String status;
    private String name;
    private String groupName;
    private LolStatus.GameStatus gameStatus;
    private int profIconID;
    private int groupPos;
    private boolean pendingMessage;

    public FriendInfo(String name, String groupName) {
        this.name = name;
        this.groupName = groupName;
    }

    public FriendInfo(String name, String status, String groupName, LolStatus.GameStatus inGame, int profIconID, int groupPos) {
        this.status = status;
        this.name = name;
        this.groupName = groupName;
        this.gameStatus = inGame;
        this.profIconID = profIconID;
        this.pendingMessage = false;
        this.groupPos = groupPos;
    }

    public FriendInfo(String name, String status, String groupName, LolStatus.GameStatus inGame, int profIconID, int groupPos, boolean pendingMessage) {
        this.status = status;
        this.name = name;
        this.groupName = groupName;
        this.gameStatus = inGame;
        this.profIconID = profIconID;
        this.pendingMessage = false;
        this.groupPos = groupPos;
        this.pendingMessage = pendingMessage;
    }

    public String getGroupName() {
        return groupName;
    }
    public int getProfIconID() {
        return profIconID;
    }

    public int getGroupPos() { return groupPos; }
    public void setGroupPos(int groupPos) { this.groupPos = groupPos; }
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

    public boolean isPendingMessage() {
        return pendingMessage;
    }

    public void setPendingMessage(boolean b) {
        this.pendingMessage = b;
    }
}
