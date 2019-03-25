package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.DevInfoRequestHandler;
import com.lethalmaus.streaming_yorkie.request.VolleySingleton;

import java.lang.ref.WeakReference;

/**
 * Activity for Info view that displays info about the app & developer
 * @author LethalMaus
 */
public class Info extends AppCompatActivity {

    //All activities & contexts are weak referenced to avoid memory leaks
    protected WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;
    //hidden count on Dev Logo to display all files in the same directory as the App
    private int showDevLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<Activity>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.info);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        showDevLogs = 7;
        final DevInfoRequestHandler devInfoRequestHandler = new DevInfoRequestHandler(weakActivity, weakContext);
        devInfoRequestHandler.requestDevLogo();

        ImageView developer_Logo = findViewById(R.id.developer_Logo);
        developer_Logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showDevLogs > 0) {
                    showDevLogs--;
                } else {
                    Intent intent = new Intent(Info.this, Logs.class);
                    startActivity(intent);
                }
            }
        });

        //Link to StreamingYorkie Guide on Github
        ImageButton streaming_yorkie = findViewById(R.id.streaming_yorkie_Logo);
        streaming_yorkie.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/StreamingYorkie#guide");
                    }
                });

        //Link to Paypal for donations
        ImageButton streaming_yorkie_contact = findViewById(R.id.streaming_yorkie_contact_Logo);
        streaming_yorkie_contact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/StreamingYorkie#contact");
                    }
                });

        //Link to Github for Open Source Projects
        ImageButton github = findViewById(R.id.github_Logo);
        github.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/");
                    }
                });

        //Link to Discord for contact & support
        ImageButton discord = findViewById(R.id.discord_Logo);
        discord.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://discord.gg/uG97jTj");
                    }
                });

        //Link to Twitch for entertainment
        ImageButton twitch = findViewById(R.id.twitch_Logo);
        twitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://twitch.tv/LethalMaus");
                    }
                });

        //Link to Patreon for donations
        ImageButton patreon = findViewById(R.id.patreon_Logo);
        patreon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://patreon.com/LethalMaus");
                    }
                });

    }
    //Cancels the Dev Logo request when it is not needed
    @Override
    protected void onPause() {
        super.onPause();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("DEV");
    }
    @Override
    protected void onStop() {
        super.onStop();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("DEV");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("DEV");
    }
    //The only option is the back button for finishing the activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
