package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;

import java.lang.ref.WeakReference;

/**
 * Activity for showing settings categories.
 * @author LethalMaus
 */
public class SettingsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageButton f4f = findViewById(R.id.menu_settings_f4f);
        f4f.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SettingsMenu.this, SettingsF4F.class);
                        startActivity(intent);
                    }
                });

        ImageButton vod = findViewById(R.id.menu_settings_vod);
        vod.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SettingsMenu.this, SettingsVOD.class);
                        startActivity(intent);
                    }
                });

        ImageButton logout = findViewById(R.id.menu_settings_logout);
        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SettingsMenu.this, Authorization.class);
                        startActivity(intent);
                    }
                });
        new UserRequestHandler(new WeakReference<Activity>(this), new WeakReference<Context>(this),true, false).sendRequest(0);
    }

    //Back to Settings Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
