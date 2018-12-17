package com.lethalmaus.twitchfollowerservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class FollowService extends AppCompatActivity {

    protected Globals globals;
    protected ViewDefinitions viewDefinitions = new ViewDefinitions();

    protected int limit = 25;
    protected int offset;
    protected int twitchTotal;

    protected boolean requestRunning;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers);

        globals = new Globals(getApplicationContext());

        requestRunning = false;
        progressBar = findViewById(R.id.requestRunning);

        showUser();
    }

    protected void showUser() {
        ImageView user_Logo = findViewById(R.id.user_Logo);
        Glide.with(getApplicationContext()).load(globals.userLogo).into(user_Logo);
        TextView user_Username = findViewById(R.id.user_Username);
        user_Username.setText(globals.username);
    }
}
