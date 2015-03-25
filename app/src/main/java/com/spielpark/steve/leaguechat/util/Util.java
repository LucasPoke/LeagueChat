package com.spielpark.steve.leaguechat.util;

import android.util.Log;

/**
 * Created by Steve on 2/1/2015.
 */
public class Util {

    public static int getProfileIconId(int num) {
        StringBuilder ret = new StringBuilder("7f020");
        int ritoID = num;
        if (ritoID <= 28) {
            ritoID++;
            if (ritoID < 16) {
                ret.append("0");
            }
            ret.append("0" + Integer.toHexString(ritoID));
        } else {
            ritoID -= 471;
            if (ritoID < 256) {
                ret.append("0");
            }
            ret.append(Integer.toHexString(ritoID));
        }
        return Integer.parseInt(ret.toString(), 16);
    }
}
