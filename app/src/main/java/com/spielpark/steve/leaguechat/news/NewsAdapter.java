package com.spielpark.steve.leaguechat.news;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spielpark.steve.leaguechat.R;

/**
 * Created by Steve on 1/30/2015.
 */
public class NewsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] titles;
    private final String[] links;
    private final Typeface font;
    public NewsAdapter(Context ctx, String[] titles, Typeface font, String[] links) {
        super(ctx, R.layout.listview_reddit_posts, R.id.lstTextView, titles);
        this.context = ctx;
        this.titles = titles;
        this.font = font;
        this.links = links;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_reddit_posts, null);
        }
        TextView post = (TextView) v.findViewById(R.id.lstTextView);
        post.setTypeface(font);
        post.setText(Html.fromHtml("<a href=\"" + links[position] + "\">" + (titles[position].length() > 50 ? titles[position].substring(0, 50) + "..." : titles[position]) + "</a>"));
        post.setMovementMethod(LinkMovementMethod.getInstance());
        post.setClickable(true);
        return v;
    }
}
