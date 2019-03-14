package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.request.UserRequestHandler;
import com.lethalmaus.streaming_yorkie.worker.AutoFollowWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * Main Activity. If the user isn't logged in then the activity changes to Authorization.
 * Otherwise it shows the menu.
 * @author LethalMaus
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        userLoggedIn();
        activateAutoFollow();
        createNotificationChannel();

        ImageButton followers = findViewById(R.id.followers_menu);
        followers.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Followers.class);
                        startActivity(intent);
                    }
                });

        ImageButton following = findViewById(R.id.following_menu);
        following.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Following.class);
                        startActivity(intent);
                    }
                });

        ImageButton f4f = findViewById(R.id.f4f_menu);
        f4f.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Follow4Follow.class);
                        startActivity(intent);
                    }
                });

        ImageButton user = findViewById(R.id.userinfo_menu);
        user.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, User.class);
                        startActivity(intent);
                    }
                });

        ImageButton info = findViewById(R.id.info_menu);
        info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Info.class);
                        startActivity(intent);
                    }
                });

        ImageButton settings = findViewById(R.id.settings_menu);
        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLoggedIn();
        activateAutoFollow();
    }

    /**
     * Opens a notification channel for the AutoFollower
     * @author LethalMaus
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Globals.NOTIFICATION_CHANNEL_ID, Globals.NOTIFICATION_CHANNEL_NAME, importance);
            channel.setDescription(Globals.NOTIFICATION_CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Checks if a User has logged in before then it updates & displays the Username & Logo. Otherwise it starts a login process.
     * @author LethalMaus
     */
    private void userLoggedIn() {
        if (!new File(getFilesDir().toString() + File.separator + "TOKEN").exists()) {
            Intent intent = new Intent(MainActivity.this, Authorization.class);
            startActivity(intent);
        } else {
            new UserRequestHandler(new WeakReference<Activity>(this), new WeakReference<>(getApplicationContext()), true, false, true).sendRequest(0);
        }
    }

    /**
     * Checks if settings were ever made, then checks if AutoFollower has been activated & with which preferences.
     * A PeriodicWorkRequest is activated to perform the AutoFollower
     * @author LethalMaus
     */
    public void activateAutoFollow() {
        if (new File(getFilesDir().toString() + File.separator + "SETTINGS").exists()) {
            try {
                JSONObject settings = new JSONObject(new ReadFileHandler(new WeakReference<>(getApplicationContext()), "SETTINGS").readFile());
                if (!settings.getString(Globals.AUTOFOLLOW).equals(Globals.AUTOFOLLOW_OFF)) {
                    TimeUnit intervalUnit;
                    switch (settings.getString(Globals.AUTOFOLLOW_INTERVAL_UNIT)) {
                        case Globals.AUTOFOLLOW_INTERVAL_UNIT_MINUTES:
                            intervalUnit = TimeUnit.MINUTES;
                            break;
                        case Globals.AUTOFOLLOW_INTERVAL_UNIT_HOURS:
                            intervalUnit = TimeUnit.HOURS;
                            break;
                        case Globals.AUTOFOLLOW_INTERVAL_UNIT_DAYS:
                            intervalUnit = TimeUnit.DAYS;
                            break;
                        default:
                            intervalUnit = TimeUnit.DAYS;
                            break;
                    }
                    PeriodicWorkRequest.Builder autoFollowBuilder = new PeriodicWorkRequest.Builder(AutoFollowWorker.class, settings.getInt(Globals.AUTOFOLLOW_INTERVAL), intervalUnit);
                    Constraints constraints = new Constraints.Builder()
                            .setRequiresBatteryNotLow(true)
                            .setRequiresStorageNotLow(true)
                            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                            .build();
                    PeriodicWorkRequest autoFollow = autoFollowBuilder.setConstraints(constraints).addTag("AUTOFOLLOW_WORKER").build();
                    WorkManager.getInstance().enqueueUniquePeriodicWork("AUTOFOLLOW_WORKER", ExistingPeriodicWorkPolicy.KEEP, autoFollow);
                    if (new File(getFilesDir().toString() + File.separator + Globals.FOLLOWERS_NEW_PATH).exists()) {
                        new DeleteFileHandler(new WeakReference<>(getApplicationContext()), Globals.FOLLOWERS_NEW_PATH).run();
                    }
                } else {
                    WorkManager.getInstance().cancelAllWorkByTag("AUTOFOLLOW_WORKER");
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error activating Auto-Follow", Toast.LENGTH_SHORT).show();
            }
        }
    }
}