package com.spielpark.steve.leaguechat.chatpage;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.service.ChatService;

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
        TextView chat;
        if (from.equalsIgnoreCase(ChatService.getUserName())) {
            chat = (TextView) view.findViewById(R.id.rightChat);
            view.findViewById(R.id.leftChat).setVisibility(view.GONE);
        } else {
            chat = (TextView) view.findViewById(R.id.leftChat);
            view.findViewById(R.id.rightChat).setVisibility(view.GONE);
        }
        chat.setVisibility(view.VISIBLE);
        chat.setText(message);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.listview_friends_chat, parent, false);
    }

}
