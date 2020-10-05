package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.BuildConfig;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.DevInfoRequestHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;

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
    private int showDevLogs = 7;

    private RequestHandler requestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakActivity = new WeakReference<>(this);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.info);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requestHandler = new DevInfoRequestHandler(weakActivity, weakContext);
        requestHandler.sendRequest(false);

        findViewById(R.id.info_developer).setOnClickListener((View v) -> {
            if (showDevLogs > 0) {
                showDevLogs--;
            } else {
                startActivity(new Intent(Info.this, Logs.class));
            }
        });

        //Link to StreamingYorkie Guide on Github
        findViewById(R.id.info_readme).setOnClickListener(
                (View v) -> Globals.openLink(weakActivity, weakContext, "https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#guide")
        );

        //Offline help/guide
        findViewById(R.id.info_help).setOnClickListener((View v) -> {
            Intent intent = new Intent(Info.this, InfoGuide.class);
            startActivity(intent);
        });

        //Link to Updates done available on Github
        findViewById(R.id.info_updates).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#updates")
        );

        //Link to Source Code on Github
        findViewById(R.id.info_source_code).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://github.com/LethalMaus/StreamingYorkie?files=1")
        );

        //Link to Contact options on Github for donations
        findViewById(R.id.info_contact).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#contact")
        );

        //Link to Shout-outs on Github
        findViewById(R.id.info_shoutout).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#shout-outs")
        );

        //Link to Twitch for entertainment
        findViewById(R.id.info_twitch).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://twitch.tv/LethalMaus")
        );

        //Link to Patreon for membership
        findViewById(R.id.info_patreon).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://patreon.com/LethalMaus")
        );

        //Link to Github for Open Source Projects
        findViewById(R.id.info_github).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://github.com/LethalMaus/")
        );

        findViewById(R.id.info_discord).setOnClickListener(
                (View v) ->
                        Globals.openLink(weakActivity, weakContext, "https://discord.gg/66EpTMj")
        );

        TextView appVersion = findViewById(R.id.info_app_version);
        appVersion.setText(BuildConfig.VERSION_NAME);
    }
    //Cancels the Dev Logo request when it is not needed
    @Override
    protected void onPause() {
        super.onPause();
        requestHandler.cancelRequest();
    }
    //The only option is the back button for finishing the activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}