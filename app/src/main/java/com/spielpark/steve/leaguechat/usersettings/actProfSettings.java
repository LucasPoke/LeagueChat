package com.spielpark.steve.leaguechat.usersettings;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.spielpark.steve.leaguechat.R;
import com.spielpark.steve.leaguechat.mainpage.actMainPage;
import com.spielpark.steve.leaguechat.util.Util;

public class actProfSettings extends ActionBarActivity {

    private ImageAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profsettings);
        setupGrid();
    }

    private void setupGrid() {
        final GridView grid = (GridView) findViewById(R.id.grid_profpictures);
        mAdapter = new ImageAdapter(this);
        grid.setAdapter(mAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelected(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        ((ImageView) findViewById(R.id.img_current)).setImageResource(Util.getProfileIconId(Settings.getUserPic()));
    }

    public void onCancel(View v) {
        Intent i = new Intent(this, actMainPage.class);
        startActivity(i);
    }

    public void onSave(View v) {
        Settings.setUserStatus(((EditText) findViewById(R.id.et_settings_status)).getText().toString());
        Settings.setUserPic(mAdapter.getCorrectPos(mAdapter.getSelected()));
        Intent i = new Intent(this, actMainPage.class);
        startActivity(i);}

}
