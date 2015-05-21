package com.spielpark.steve.leaguechat.chatpage;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.service.ChatService;
import com.spielpark.steve.leaguechat.util.Util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Steve on 2/6/2015.
 */
public class ChatAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public ChatAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String from = cursor.getString(cursor.getColumnIndex(MessageDB.TableEntry.COLUMN_FROM));
        String message = cursor.getString(cursor.getColumnIndex(MessageDB.TableEntry.COLUMN_MESSAGE));
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MessageDB.TableEntry.COLUMN_TIME)));
        int curDay = cal.get(Calendar.DAY_OF_YEAR);
        TextView datetext = ((TextView) view.findViewById(R.id.date));
        if (cursor.getPosition() == 0 || diffDays(cursor)) {
            datetext.setVisibility(View.VISIBLE);
            datetext.setText(Util.getTimeChatFormat(cal));
            Log.d("CA/BV", "New Date View: " + Util.getTimeChatFormat(cal));
        } else {
            datetext.setVisibility(View.GONE);
        }
        TextView chat;
        if (from.equalsIgnoreCase(ChatService.getUserName())) {
            chat = (TextView) view.findViewById(R.id.rightChat);
            view.findViewById(R.id.leftChat).setVisibility(View.GONE);
        } else {
            chat = (TextView) view.findViewById(R.id.leftChat);
            view.findViewById(R.id.rightChat).setVisibility(View.GONE);
        }
        chat.setVisibility(View.VISIBLE);
        chat.setText(message);
    }

    private boolean diffDays(Cursor cursor) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MessageDB.TableEntry.COLUMN_TIME)));
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        cursor.moveToPrevious();
        cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MessageDB.TableEntry.COLUMN_TIME)));
        int day2 = cal.get(Calendar.DAY_OF_YEAR);
        return day2 != day1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.listview_friends_chat, parent, false);
    }

}
