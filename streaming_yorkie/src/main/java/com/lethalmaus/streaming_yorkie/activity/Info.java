package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.BuildConfig;
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
    private int showDevLogs;

    private RequestHandler requestHandler;

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
        requestHandler = new DevInfoRequestHandler(weakActivity, weakContext);
        requestHandler.sendRequest();

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
                        openLink("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#guide");
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
                        openLink("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#updates");
                    }
                });

        //Link to Source Code on Github
        ImageButton source_code = findViewById(R.id.info_source_code);
        source_code.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLink("https://github.com/LethalMaus/StreamingYorkie?files=1");
                    }
                });

        //Link to Contact options on Github for donations
        ImageButton contact = findViewById(R.id.info_contact);
        contact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLink("https://github.com/LethalMaus/StreamingYorkie/blob/master/README.md#contact");
                    }
                });

        //Link to Twitch for entertainment
        ImageButton twitch = findViewById(R.id.info_twitch);
        twitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLink("https://twitch.tv/LethalMaus");
                    }
                });

        //Link to Patreon for membership
        ImageButton patreon = findViewById(R.id.info_patreon);
        patreon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLink("https://patreon.com/LethalMaus");
                    }
                });

        //Link to Github for Open Source Projects
        ImageButton github = findViewById(R.id.info_github);
        github.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLink("https://github.com/LethalMaus/");
                    }
                });

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

    /**
     * Method for opening a link in relation to the developer
     * @author LethalMaus
     * @param url a link to eg. Github
     */
    private void openLink(String url) {
        if (RequestHandler.networkIsAvailable(weakContext)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(weakActivity.get(), "OFFLINE: Can't open link", Toast.LENGTH_SHORT).show();
        }
    }
}
