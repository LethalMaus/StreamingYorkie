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
import android.widget.TextView;

import com.lethalmaus.streaming_yorkie.BuildConfig;
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

        ImageView developer_Logo = findViewById(R.id.info_developer);
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
        ImageButton readme = findViewById(R.id.info_readme);
        readme.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#guide");
                    }
                });

        //Offline help/guide
        ImageView help = findViewById(R.id.info_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Info.this, InfoGuide.class);
                startActivity(intent);
            }
        });

        //Link to Updates done available on Github
        ImageButton updates = findViewById(R.id.info_updates);
        updates.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#updates");
                    }
                });

        //Link to Source Code on Github
        ImageButton source_code = findViewById(R.id.info_source_code);
        source_code.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/StreamingYorkie?files=1");
                    }
                });

        //Link to Contact options on Github for donations
        ImageButton contact = findViewById(R.id.info_contact);
        contact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#contact");
                    }
                });

        //Link to Twitch for entertainment
        ImageButton twitch = findViewById(R.id.info_twitch);
        twitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://twitch.tv/LethalMaus");
                    }
                });

        //Link to Patreon for membership
        ImageButton patreon = findViewById(R.id.info_patreon);
        patreon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://patreon.com/LethalMaus");
                    }
                });

        //Link to Github for Open Source Projects
        ImageButton github = findViewById(R.id.info_github);
        github.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        devInfoRequestHandler.requestDevLink("https://github.com/LethalMaus/");
                    }
                });

        TextView appVersion = findViewById(R.id.info_app_version);
        appVersion.setText(BuildConfig.VERSION_NAME);
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
