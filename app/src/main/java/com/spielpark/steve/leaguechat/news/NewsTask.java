package com.spielpark.steve.leaguechat.news;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spielpark.steve.leaguechat.R;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by Steve on 1/30/2015.
 */
public class NewsTask extends AsyncTask {

    private final ListView view;
    private final RelativeLayout progressBar;
    private final Typeface font;
    private final int num;
    private static NewsReader reader;
    public NewsTask(View view, RelativeLayout progressPanel, Typeface font, int num) {
        this.view = (ListView) view;
        progressBar = progressPanel;
        this.font = font;
        this.num = num;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        reader = new NewsReader();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressBar.setVisibility(View.GONE);
        Map<String, String> titles_links = reader.getTitlesAndLink(num);
        String[] titles = new String[titles_links.size()];
        String[] links = new String[titles_links.size()];
        int pos = 0;
        for (String title : titles_links.keySet()) {
            titles[pos] = title;
            links[pos] = titles_links.get(title);
            pos++;
        }
        NewsAdapter adapter = new NewsAdapter(view.getContext(), titles, font, links);
        view.setClickable(true);
        view.setAdapter(adapter);
        view.setVisibility(View.VISIBLE);
    }
}
