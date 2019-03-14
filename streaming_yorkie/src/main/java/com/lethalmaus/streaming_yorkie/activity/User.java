package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.request.VolleySingleton;

import java.lang.ref.WeakReference;

/**
 * Activity for User Info view that displays the info from the Users Twitch account
 * @author LethalMaus
 */
public class User extends AppCompatActivity {

    //All contexts are weak referenced to avoid memory leaks
    protected WeakReference<Context> weakContext;
    private UserRequestHandler userRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.weakContext = new WeakReference<>(getApplicationContext());
        setContentView(R.layout.user);
        userRequestHandler = new UserRequestHandler(new WeakReference<Activity>(this), weakContext, true, true, true);
        userRequestHandler.sendRequest(0);

        ImageButton refreshPage = findViewById(R.id.user_refresh);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userRequestHandler.sendRequest(0);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Globals.onOptionsItemsSelected(this, item);
    }

    //Cancels User requests as they are not needed without this activity
    @Override
    protected void onPause() {
        super.onPause();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("USER");
    }
    @Override
    protected void onStop() {
        super.onStop();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("USER");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleySingleton.getInstance(weakContext).getRequestQueue().cancelAll("USER");
    }
}