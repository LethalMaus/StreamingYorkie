package com.lethalmaus.streaming_yorkie.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.ChannelRequestHandler;
import com.lethalmaus.streaming_yorkie.request.VolleySingleton;

import java.lang.ref.WeakReference;

/**
 * Activity for ChannelEntity Info view that displays the info from the Users Twitch account
 * @author LethalMaus
 */
public class Channel extends AppCompatActivity {

    //All contexts are weak referenced to avoid memory leaks
    protected WeakReference<Context> weakContext;
    private ChannelRequestHandler channelRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.channel);
        channelRequestHandler = new ChannelRequestHandler(new WeakReference<>(this), weakContext);
        channelRequestHandler.sendRequest();

        ImageButton refreshPage = findViewById(R.id.user_refresh);
        refreshPage.setOnClickListener((View v) ->
            channelRequestHandler.sendRequest()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return Globals.onOptionsItemsSelected(this, item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll(Globals.CHANNEL);
    }
}