package com.spielpark.steve.leaguechat.util;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.spielpark.steve.leaguechat.mainpage.friendinfo.FriendInfo;

import java.util.List;

/**
 * Created by Steve on 2/1/2015.
 */
public class Util {

    public static void changeFriendGroupCount(List<FriendInfo> list, Friend toChange, boolean add) {
        for (int i = 0; i < list.size(); i++) {
            FriendInfo f = list.get(i);
            if (f.getName().equals("!--GROUP--!") && f.getGroupName().equals(toChange.getGroup())) {
                int rep = f.getProfIconID();
                int numOnline = rep / 1000;
                int total = rep % 1000;
                numOnline += (add ? 1 : -1);
                rep = total + (numOnline * 1000);
                f.setProfIconID(rep);
                return;
            }
        }
    }

    public static String getFriendCount(int rep) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(" (");
        bldr.append(rep / 1000);
        bldr.append("/");
        bldr.append(rep % 1000);
        bldr.append(")");
        return bldr.toString();
    }

    public static int getProfileIconId(int num) {
        StringBuilder ret = new StringBuilder("7f020");
        int ritoID = ((num == 29) ? 0 : num);
        if (ritoID <= 28) {
            ritoID++;
            if (ritoID < 16) {
                ret.append("0");
            }
            ret.append("0").append(Integer.toHexString(ritoID));
        } else {
            if (ritoID < 795) {
                ritoID -= 13;
            }
            ritoID -= 471;
            if (ritoID < 256) {
                ret.append("0");
            }
            ret.append(Integer.toHexString(ritoID));
        }
        return Integer.parseInt(ret.toString(), 16);
    }

    public static int getGroupVal(String name) {
        int ret = 0;
        for (Character c : name.toCharArray()) {
            ret += c;
        }
        Log.d("Util/GGV", "Name: " + name + "...Val: " + ret);
        return name.contains("**") ? 0 : ret;
    }

    public static Shader getTierGraphics(String tier) {
        Shader shader;
        switch(tier) {
            case "wood": {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(100, 65, 32), Color.rgb(168, 119, 13)}, null, Shader.TileMode.MIRROR);
                break;
            }
            case "bronze": {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(106, 73, 27), Color.rgb(117, 51, 1)}, null, Shader.TileMode.MIRROR);
                break;
            }
            case "silver" : {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(170, 188, 177), Color.rgb(85, 100, 82)}, null, Shader.TileMode.MIRROR);
                break;
            }
            case "gold" : {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(238, 213, 104), Color.rgb(168, 120, 73)}, null, Shader.TileMode.MIRROR);
                break;
            }
            case "platinum" : {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(131, 223, 189), Color.rgb(49, 148, 125)}, null, Shader.TileMode.MIRROR);
                break;
            }
            case "diamond" : {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(130, 208, 249), Color.rgb(235, 208, 169), Color.rgb(39, 89, 167), Color.rgb(191, 209, 186)},
                        null, Shader.TileMode.MIRROR);
                break;
            }
            case "master" : {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(113, 130, 124), Color.rgb(160, 236, 225), Color.rgb(249, 226, 115), Color.rgb(17, 166, 156)},
                        null, Shader.TileMode.MIRROR);
                break;
            }
            case "challenger" : {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(191, 150, 68), Color.rgb(255, 231, 126), Color.rgb(128, 238, 238), Color.rgb(255, 231, 117)},
                        null, Shader.TileMode.MIRROR);
                break;
            }
            default: {
                shader = new LinearGradient(20, 0, 220, 40,
                        new int[]{Color.rgb(100, 65, 32), Color.rgb(168, 119, 13)},
                        new float[]{0, 1}, Shader.TileMode.MIRROR);
                break;
            }
        }
        return shader;
    }
}
