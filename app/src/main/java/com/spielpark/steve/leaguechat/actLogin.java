package com.spielpark.steve.leaguechat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.spielpark.steve.leaguechat.news.NewsTask;
import com.spielpark.steve.leaguechat.usersettings.Settings;

import java.util.ArrayList;


public class actLogin extends Activity {
    @SuppressWarnings("FieldCanBeLocal")
    private final int NUM_ARTICLES = 8;
    private final ArrayList<String> regions = new ArrayList<>();

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

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
        Settings.init(getApplicationContext().getSharedPreferences(Settings.USER_SETTINGS, MODE_PRIVATE));
    }

    public void beginLogin(View v) {
        String user = ((EditText) findViewById(R.id.et_enter_username)).getText().toString();
        char[] password = ((EditText) findViewById(R.id.et_enter_password)).getText().toString().toCharArray();
        ((EditText)findViewById(R.id.et_enter_password)).setText("");
        Intent intent = new Intent(this, actLogin_transition.class);
        intent.putExtra("un", NotMyPasswords.notMyUserName);
        intent.putExtra("pw", NotMyPasswords.notMyPassword.toCharArray());
        intent.putExtra("region", ((Spinner)findViewById(R.id.region_select)).getSelectedItem().toString());
        //intent.putExtra("un", user);
        //intent.putExtra("pw", password);
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
                view.findViewById(R.id.lstTextView).performClick();
            }
        });
        new NewsTask(txtPosts, (RelativeLayout) findViewById(R.id.loadingPanel), REDDIT_POSTS, NUM_ARTICLES).execute();
    }
}
