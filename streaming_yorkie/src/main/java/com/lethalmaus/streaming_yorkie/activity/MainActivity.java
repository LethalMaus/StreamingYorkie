package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.F4FEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.worker.AutoFollowWorker;
import com.lethalmaus.streaming_yorkie.worker.AutoVODExportWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

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

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.streaming_yorkie);
        }

        /*FIXME
        Delete when 1.3.1-a (13) is not in use
        This transfers local files to DB
        TODO this method will be found in AutoFollowWorker as well
         */
        if (new File(getFilesDir().toString() + File.separator + "FOLLOWING_EXCLUDED").exists()
                || new File(getFilesDir().toString() + File.separator + "FOLLOWERS_EXCLUDED").exists()
                || new File(getFilesDir().toString() + File.separator + "F4F_EXCLUDED").exists()) {
            new Thread(new Runnable() {
                public void run() {
                    WeakReference<Activity> weakActivity = new WeakReference<>(MainActivity.this);
                    WeakReference<Context> weakContext = new WeakReference<>(getApplicationContext());
                    try {
                        StreamingYorkieDB streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
                        ArrayList<String> excluded = new ReadFileHandler(weakActivity, weakContext, "FOLLOWING_EXCLUDED").readFileNames();
                        for (int i = 0; i < excluded.size(); i++) {
                            JSONObject user = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "FOLLOWING" + File.separator + excluded.get(i)).readFile());
                            FollowingEntity followingEntity = new FollowingEntity(Integer.parseInt(user.getString("_id")),
                                    user.getString("display_name"),
                                    user.getString("logo"),
                                    user.getString("created_at"),
                                    user.getBoolean("notifications"),
                                    0);
                            streamingYorkieDB.followingDAO().insertUser(followingEntity);
                        }
                        new DeleteFileHandler(weakActivity, weakContext, "FOLLOWING").run();
                        new DeleteFileHandler(weakActivity, weakContext, "FOLLOWING_EXCLUDED").run();
                        excluded = new ReadFileHandler(weakActivity, weakContext, "FOLLOWERS_EXCLUDED").readFileNames();
                        for (int i = 0; i < excluded.size(); i++) {
                            JSONObject user = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "FOLLOWERS" + File.separator + excluded.get(i)).readFile());
                            FollowerEntity follower = new FollowerEntity(Integer.parseInt(user.getString("_id")),
                                    user.getString("display_name"),
                                    user.getString("logo"),
                                    user.getString("created_at"),
                                    user.getBoolean("notifications"),
                                    0);
                            streamingYorkieDB.followerDAO().insertUser(follower);
                        }
                        new DeleteFileHandler(weakActivity, weakContext, "FOLLOWERS").run();
                        new DeleteFileHandler(weakActivity, weakContext, "FOLLOWERS_EXCLUDED").run();
                        excluded = new ReadFileHandler(weakActivity, weakContext, "F4F_EXCLUDED").readFileNames();
                        for (int i = 0; i < excluded.size(); i++) {
                            JSONObject user;
                            if (new File(getFilesDir().toString() + File.separator + "FOLLOWING" + File.separator + excluded.get(i)).exists()) {
                                user = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "FOLLOWING" + File.separator + excluded.get(i)).readFile());
                            } else {
                                user = new JSONObject(new ReadFileHandler(weakActivity, weakContext, "FOLLOWERS" + File.separator + excluded.get(i)).readFile());
                            }
                            F4FEntity f4f = new F4FEntity(Integer.parseInt(user.getString("_id")),
                                    user.getString("display_name"),
                                    user.getString("logo"),
                                    user.getString("created_at"),
                                    user.getBoolean("notifications"),
                                    0);
                            streamingYorkieDB.f4fDAO().insertUser(f4f);
                        }
                        new DeleteFileHandler(weakActivity, weakContext, "F4F_EXCLUDED").run();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Error migrating local files to DB", Toast.LENGTH_SHORT).show();
                        new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "Error migrating local files to DB: " + e.toString(), true).run();
                    }
                }
            }).start();
        }

        Globals.createNotificationChannel(new WeakReference<>(getApplicationContext()), Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_NAME, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION);

        ImageButton followers = findViewById(R.id.menu_followers);
        followers.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Followers.class);
                        startActivity(intent);
                    }
                });

        ImageButton following = findViewById(R.id.menu_following);
        following.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Following.class);
                        startActivity(intent);
                    }
                });

        ImageButton f4f = findViewById(R.id.menu_f4f);
        f4f.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Follow4Follow.class);
                        startActivity(intent);
                    }
                });

        ImageButton vods = findViewById(R.id.menu_vod);
        vods.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, VODs.class);
                        startActivity(intent);
                    }
                });

        ImageButton multi = findViewById(R.id.menu_multi);
        multi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MultiView.class);
                        startActivity(intent);
                    }
                });

        ImageButton lurk = findViewById(R.id.menu_lurk);
        lurk.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Lurk.class);
                        startActivity(intent);
                    }
                });

        ImageButton user = findViewById(R.id.menu_userinfo);
        user.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Channel.class);
                        startActivity(intent);
                    }
                });

        ImageButton info = findViewById(R.id.menu_info);
        info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Info.class);
                        startActivity(intent);
                    }
                });

        ImageButton settings = findViewById(R.id.menu_settings);
        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SettingsMenu.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLoggedIn();
        Globals.activateWorker(new WeakReference<>(getApplicationContext()), "SETTINGS_F4F", Globals.SETTINGS_AUTOFOLLOW, AutoFollowWorker.class, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION);
        Globals.activateWorker(new WeakReference<>(getApplicationContext()), "SETTINGS_VOD", Globals.SETTINGS_AUTOVODEXPORT, AutoVODExportWorker.class, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION);
    }

    /**
     * Checks if a ChannelEntity has logged in before then it updates & displays the Username & Logo. Otherwise it starts a login process.
     * @author LethalMaus
     */
    private void userLoggedIn() {
        if (!new File(getFilesDir().toString() + File.separator + "TOKEN").exists()) {
            Intent intent = new Intent(MainActivity.this, Authorization.class);
            startActivity(intent);
        } else if ((new Date().getTime() - new File(getFilesDir().toString() + File.separator + "TOKEN").lastModified()) > 5184000000L) {
            new DeleteFileHandler(new WeakReference<>(this), new WeakReference<>(getApplicationContext()), null).deleteFileOrPath("TOKEN");
            Intent intent = new Intent(MainActivity.this, Authorization.class);
            startActivity(intent);
        } else {
            new UserRequestHandler(new WeakReference<>(this), new WeakReference<>(getApplicationContext())).sendRequest();
        }
    }
}