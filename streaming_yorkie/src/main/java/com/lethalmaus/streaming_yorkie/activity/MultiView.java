package com.lethalmaus.streaming_yorkie.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for viewing several Streams at once
 * @author LethalMaus
 */
//This is needed for the Player, even though its not recommended & considered dangerous. Hence the Lint suppression
@SuppressLint("SetJavaScriptEnabled")
public class MultiView extends AppCompatActivity {

    private String channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_view);

        ImageView multi_start = findViewById(R.id.multi_start);
        multi_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestHandler.networkIsAvailable(new WeakReference<>(getApplicationContext()))) {
                    getChannels();
                    if (!channels.isEmpty()) {
                        findViewById(R.id.multi_input).setVisibility(View.GONE);
                        WebView multi_view = findViewById(R.id.multi_view);
                        multi_view.setVisibility(View.VISIBLE);
                        multi_view.getSettings().setJavaScriptEnabled(true);
                        multi_view.loadUrl("https://lethalmaus.github.io/TwitchMultiView/?channels=" + channels.replaceAll("\\s", ""));
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().hide();
                        }
                        hideSystemUI();
                    }
                } else {
                    Toast.makeText(MultiView.this, "OFFLINE: Cannot MultiView when offline", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cast) {
            try {
                startActivity(new Intent("android.settings.CAST_SETTINGS"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MultiView.this, "Your phone does not have casting capabilities with this app", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        WebView multi_view = findViewById(R.id.multi_view);
        if (multi_view.getVisibility() == View.VISIBLE) {
            multi_view.setVisibility(View.GONE);
            multi_view.loadUrl("about:blank");
            findViewById(R.id.multi_input).setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            showSystemUI();
        } else {
            finish();
        }
    }

    /**
     * Method for getting channel names from inputs
     * @author LethalMaus
     */
    private void getChannels() {
        channels = "";
        EditText channelInput1 = findViewById(R.id.multi_channel1_input);
        EditText channelInput2 = findViewById(R.id.multi_channel2_input);
        EditText channelInput3 = findViewById(R.id.multi_channel3_input);
        EditText channelInput4 = findViewById(R.id.multi_channel4_input);
        if (channelInput1 != null && channelInput1.getText().toString().length() > 0) {
            channels += channelInput1.getText().toString();
        }
        if (channelInput2 != null && channelInput2 .getText().toString().length() > 0) {
            if (!channels.isEmpty()) {
                channels += ",";
            }
            channels += channelInput2.getText().toString();
        }
        if (channelInput3 != null && channelInput3.getText().toString().length() > 0) {
            if (!channels.isEmpty()) {
                channels += ",";
            }
            channels += channelInput3.getText().toString();
        }
        if (channelInput4 != null && channelInput4.getText().toString().length() > 0) {
            if (!channels.isEmpty()) {
                channels += ",";
            }
            channels += channelInput4.getText().toString();
        }
    }

    /**
     * Hides the softkeys for better viewing
     * @author LethalMaus
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     * Shows the softkeys
     * @author LethalMaus
     */
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);
    }
}
