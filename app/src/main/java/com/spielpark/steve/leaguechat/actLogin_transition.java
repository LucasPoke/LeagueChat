package com.spielpark.steve.leaguechat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.digitale.spzio.GIFView;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.spielpark.steve.leaguechat.mainpage.actMainPage;
import com.spielpark.steve.leaguechat.service.ChatService;


public class actLogin_transition extends Activity {
    private TextView status;
    private LoginReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_transition);
        status = (TextView) findViewById(R.id.txtStatusUpdate);
        if (receiver == null) {
            receiver = new LoginReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("login_status_update");
            filter.addAction("login_transition");
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
        }
        ((GIFView) findViewById(R.id.garen)).loadGIFResource(this, R.drawable.garen_spin);
        beginLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        receiver = null;
    }

    private void beginLogin() {
        Bundle extras = getIntent().getExtras();
        Intent intent = new Intent(getApplicationContext(), ChatService.class);
        intent.putExtra("username", extras.getString("un"));
        intent.putExtra("password", extras.getCharArray("pw"));
        intent.putExtra("region", extras.getString("region"));
        extras.remove("password");
        intent.setAction("DO_LOGIN");
        startService(intent);
    }

    private class LoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            switch (intent.getAction()) {
                case "login_status_update" : {
                    status.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setText(intent.getExtras().getString("arg0"));
                        }
                    });
                    break;
                }
                case "login_transition" : {
                    Intent trans = new Intent(actLogin_transition.this, actMainPage.class);
                    actLogin_transition.this.startActivity(trans);
                }
            }
        }
    }
}
