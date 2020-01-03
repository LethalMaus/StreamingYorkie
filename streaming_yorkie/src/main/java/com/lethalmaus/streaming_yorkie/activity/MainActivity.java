package com.lethalmaus.streaming_yorkie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.worker.AutoFollowWorker;
import com.lethalmaus.streaming_yorkie.worker.AutoLurkWorker;
import com.lethalmaus.streaming_yorkie.worker.AutoVODExportWorker;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main Activity. If the channel isn't logged in then the activity changes to Authorization.
 * Otherwise it shows the menu.
 * @author LethalMaus
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.streaming_yorkie);
        }

        Globals.createNotificationChannel(new WeakReference<>(getApplicationContext()), Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_NAME, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION);

        findViewById(R.id.menu_followers).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Followers.class))
        );

        findViewById(R.id.menu_following).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Following.class))
        );

        findViewById(R.id.menu_f4f).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Follow4Follow.class))
        );

        findViewById(R.id.menu_vod).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, VODs.class))
        );

        findViewById(R.id.menu_multi).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, MultiView.class))
        );

        findViewById(R.id.menu_lurk).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Lurk.class))
        );

        findViewById(R.id.menu_userinfo).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Channel.class))
        );

        findViewById(R.id.menu_info).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, Info.class))
        );

        findViewById(R.id.menu_settings).setOnClickListener((View v) ->
            startActivity(new Intent(MainActivity.this, SettingsMenu.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLoggedIn();
        Globals.activateWorker(new WeakReference<>(getApplicationContext()), Globals.FILE_SETTINGS_F4F, Globals.SETTINGS_AUTOFOLLOW, AutoFollowWorker.class, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION);
        Globals.activateWorker(new WeakReference<>(getApplicationContext()), Globals.FILE_SETTINGS_VOD, Globals.SETTINGS_AUTOVODEXPORT, AutoVODExportWorker.class, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION);
        Globals.activateWorker(new WeakReference<>(getApplicationContext()), Globals.FILE_SETTINGS_LURK, Globals.SETTINGS_AUTOLURK, AutoLurkWorker.class, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_NAME, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION);
    }

    /**
     * Checks if a ChannelEntity has logged in before then it updates & displays the Username & Logo. Otherwise it starts a login process.
     * @author LethalMaus
     */
    private void userLoggedIn() {
        if (!new File(getFilesDir().toString() + File.separator + Globals.FILE_TOKEN).exists()) {
            startActivity(new Intent(MainActivity.this, Authorization.class));
        } else if ((new Date().getTime() - new File(getFilesDir().toString() + File.separator + Globals.FILE_TOKEN).lastModified()) > 5184000000L) {
            new DeleteFileHandler(new WeakReference<>(this), new WeakReference<>(getApplicationContext()), null).deleteFileOrPath(Globals.FILE_TOKEN);
            startActivity(new Intent(MainActivity.this, Authorization.class));
        } else {
            new UserRequestHandler(new WeakReference<>(this), new WeakReference<>(getApplicationContext())).sendRequest();
        }
    }
}