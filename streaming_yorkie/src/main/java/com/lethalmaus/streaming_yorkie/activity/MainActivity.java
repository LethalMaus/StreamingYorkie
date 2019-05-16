package com.lethalmaus.streaming_yorkie.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
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
import com.lethalmaus.streaming_yorkie.worker.AutoVODExportWorker;

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
import androidx.work.Worker;

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

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.drawable.streaming_yorkie);
        }

        /*FIXME
        Delete after next update, this is to inform users of breaking changes
         */
        if (new File(getFilesDir().toString() + File.separator + "SETTINGS").exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    new DeleteFileHandler(new WeakReference<>(getApplicationContext()), "SETTINGS").run();
                    dialog.dismiss();
                }
            });
            builder.setMessage("Auto-Follow settings have been reset due to the introduction of VOD Exports. Sorry for the inconvenience");
            builder.setTitle("Breaking Changes");
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        userLoggedIn();
        activateWorker("SETTINGS_F4F", Globals.SETTINGS_AUTOFOLLOW, AutoFollowWorker.class, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION);
        activateWorker("SETTINGS_VOD", Globals.SETTINGS_AUTOVODEXPORT, AutoVODExportWorker.class, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION);

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

        ImageButton vods = findViewById(R.id.vod_menu);
        vods.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, VODs.class);
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
                        Intent intent = new Intent(MainActivity.this, SettingsMenu.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLoggedIn();
        activateWorker("SETTINGS_F4F", Globals.SETTINGS_AUTOFOLLOW, AutoFollowWorker.class, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION);
        activateWorker("SETTINGS_VOD", Globals.SETTINGS_AUTOVODEXPORT, AutoVODExportWorker.class, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION);
    }

    /**
     * Opens a notification channel for the AutoFollower
     * @author LethalMaus
     * @param channelID notification channel ID
     * @param channelName notification channel name
     * @param channelDescription notification channel description
     */
    private void createNotificationChannel(String channelID, String channelName, String channelDescription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
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
            new UserRequestHandler(new WeakReference<Activity>(this), new WeakReference<>(getApplicationContext()), true, false, false).sendRequest(0);
        }
    }

    /**
     * Checks if settings were ever made for the Worker type, then checks if the Worker has been activated & with which preferences.
     * A PeriodicWorkRequest is activated to perform the Worker process
     * @author LethalMaus
     * @param settingsFileName name of settings files
     * @param workerName name of worker process
     * @param workerClass name of worker class
     * @param channelID notification channel ID
     * @param channelName notification channel name
     * @param channelDescription notification channel description
     */
    public void activateWorker(String settingsFileName, String workerName, Class<? extends Worker> workerClass, String channelID, String channelName, String channelDescription) {
        if (new File(getFilesDir().toString() + File.separator + settingsFileName).exists()) {
            try {
                JSONObject settings = new JSONObject(new ReadFileHandler(new WeakReference<>(getApplicationContext()), settingsFileName).readFile());
                if (!settings.getString(workerName).equals(Globals.SETTINGS_OFF)) {
                    createNotificationChannel(channelID, channelName, channelDescription);
                    TimeUnit intervalUnit;
                    switch (settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) {
                        case Globals.SETTINGS_INTERVAL_UNIT_MINUTES:
                            intervalUnit = TimeUnit.MINUTES;
                            break;
                        case Globals.SETTINGS_INTERVAL_UNIT_HOURS:
                            intervalUnit = TimeUnit.HOURS;
                            break;
                        case Globals.SETTINGS_INTERVAL_UNIT_DAYS:
                            intervalUnit = TimeUnit.DAYS;
                            break;
                        default:
                            intervalUnit = TimeUnit.DAYS;
                            break;
                    }
                    PeriodicWorkRequest.Builder autoFollowBuilder = new PeriodicWorkRequest.Builder(workerClass, settings.getInt(Globals.SETTINGS_INTERVAL), intervalUnit);
                    Constraints constraints = new Constraints.Builder()
                            .setRequiresBatteryNotLow(true)
                            .setRequiresStorageNotLow(true)
                            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                            .build();
                    PeriodicWorkRequest workRequest = autoFollowBuilder.setConstraints(constraints).addTag(workerName).build();
                    WorkManager.getInstance().enqueueUniquePeriodicWork(workerName, ExistingPeriodicWorkPolicy.KEEP, workRequest);
                } else {
                    WorkManager.getInstance().cancelAllWorkByTag(workerName);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error activating " + workerName, Toast.LENGTH_SHORT).show();
            }
        }
    }
}