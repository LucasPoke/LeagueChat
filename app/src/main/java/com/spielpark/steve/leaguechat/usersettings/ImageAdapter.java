package com.spielpark.steve.leaguechat.usersettings;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.spielpark.steve.leaguechat.util.Util;

/**
 * Created by Steve on 5/24/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private int mSelected;
    private final int NUM_PICS = 376;
    private Context ctx;

    public ImageAdapter(Context ctx) {
        this.ctx = ctx;
        this.mSelected = getReducedPos(Settings.getUserPic());
    }

    @Override
    public int getCount() {
        return NUM_PICS;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView img;
        if (convertView == null) {
            img = new ImageView(ctx);
        } else {
            img = (ImageView) convertView;
        }
        if (position == mSelected) {
            img.setBackgroundColor(Color.argb(80, 120, 250, 120));
        } else {
            img.setBackgroundColor(Color.TRANSPARENT);
        }
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        img.setPadding(12, 6, 12, 6);
        img.setTag(Integer.valueOf(getCorrectPos(position)));
        img.setImageResource(Util.getProfileIconId(getCorrectPos(position)));
        return img;
    }

    public int getCorrectPos(int pos) {
        pos = (pos < 501 && pos > 28) ? pos + 472 : pos;
        pos = (pos < 808 && pos > 795) ? pos + 12 : pos;
        return pos;
    }

    private int getReducedPos(int pos) {
        pos = (pos > 795) ? pos - 12 : pos;
        pos = (pos > 500 ) ? pos - 472 : pos;
        return pos;
    }

    public void setSelected(int num) {
        this.mSelected = num;
    }

    public int getSelected() {
        return mSelected;
    }
}
