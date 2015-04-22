package com.spielpark.steve.leaguechat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.spielpark.steve.leaguechat.news.NewsReader;
import com.spielpark.steve.leaguechat.news.NewsTask;
import com.spielpark.steve.leaguechat.service.ChatService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class actLogin extends Activity {
    private final int NUM_ARTICLES = 8;
    private final ArrayList<String> regions = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        setLoginText();
        getNews();
        for (ChatServer s : ChatServer.values()) {
            regions.add(s.name());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.region_item, R.id.spnRegion, regions);
        adapter.setDropDownViewResource(R.layout.region_item);
        ((Spinner)findViewById(R.id.region_select)).setAdapter(adapter);
        ((Spinner)findViewById(R.id.region_select)).setSelection(regions.indexOf("NA"));
    }

    public void beginLogin(View v) {
        String user = ((EditText) findViewById(R.id.et_enter_username)).getText().toString();
        char[] password = ((EditText) findViewById(R.id.et_enter_password)).getText().toString().toCharArray();
        Intent intent = new Intent(this, actLogin_transition.class);
        //intent.putExtra("un", NotMyPasswords.notMyUserName);
        //intent.putExtra("pw", NotMyPasswords.notMyPassword.toCharArray());
        intent.putExtra("region", ((Spinner)findViewById(R.id.region_select)).getSelectedItem().toString());
        intent.putExtra("un", user);
        intent.putExtra("pw", password);
        startActivity(intent);
    }

    private void setLoginText() {
        final Typeface LOGIN_TITLE_FONT = Typeface.createFromAsset(getAssets(), "fonts/FrizQuadrataRegular.ttf");
        final Typeface ACCOUNT_INF_FONT = Typeface.createFromAsset(getAssets(), "fonts/hurtmold.ttf");
        final Shader textShader = new LinearGradient(20, 0, 220, 40,
                new int[]{Color.rgb(170, 134, 79), Color.rgb(218, 164, 79), Color.rgb(230, 247, 123)},
                new float[]{0, 0, 1}, Shader.TileMode.MIRROR);
        TextView loginTitle = (TextView) findViewById(R.id.txtLoginTitle);
        TextView postsTitle = (TextView) findViewById(R.id.txtPostsTitle);
        ((TextView) findViewById(R.id.acc_info)).setTypeface(ACCOUNT_INF_FONT);
        ((TextView) findViewById(R.id.username)).setTypeface(ACCOUNT_INF_FONT);
        ((TextView) findViewById(R.id.password)).setTypeface(ACCOUNT_INF_FONT);
        ((EditText) findViewById(R.id.et_enter_password)).setTypeface(ACCOUNT_INF_FONT);
        ((EditText) findViewById(R.id.et_enter_username)).setTypeface(ACCOUNT_INF_FONT);
        loginTitle.setTypeface(LOGIN_TITLE_FONT);
        postsTitle.setTypeface(LOGIN_TITLE_FONT);
        loginTitle.getPaint().setShader(textShader);
        postsTitle.getPaint().setShader(textShader);
    }

    private void getNews() {
        final Typeface REDDIT_POSTS = Typeface.createFromAsset(getAssets(), "fonts/Sansation.ttf");
        ListView txtPosts = (ListView) findViewById(R.id.txtStories);
        txtPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)view.findViewById(R.id.lstTextView)).setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView)view.findViewById(R.id.lstTextView)).setLinksClickable(true);
                ((TextView)view.findViewById(R.id.lstTextView)).setAutoLinkMask(Linkify.WEB_URLS);
                ((TextView)view.findViewById(R.id.lstTextView)).performClick();
                System.out.println(((TextView) view.findViewById(R.id.lstTextView)).getText());
            }
        });
        AsyncTask getTopPosts = new NewsTask(txtPosts, (RelativeLayout) findViewById(R.id.loadingPanel), REDDIT_POSTS, NUM_ARTICLES).execute();
    }

    private class RegionAdapter extends ArrayAdapter {

        public RegionAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
        }
    }
}
