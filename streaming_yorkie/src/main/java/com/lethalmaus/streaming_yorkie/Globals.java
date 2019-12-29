package com.lethalmaus.streaming_yorkie;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.lethalmaus.streaming_yorkie.activity.Authorization;
import com.lethalmaus.streaming_yorkie.activity.Info;
import com.lethalmaus.streaming_yorkie.activity.InfoGuide;
import com.lethalmaus.streaming_yorkie.activity.SettingsMenu;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Globals contains centralized constants & variables that are used throughout the whole app.
 * @author LethalMaus
 */
public class Globals {

    //ID of the app registered by Twitch
    public static final String CLIENTID = "tjots3mhxunw0sj2a20ka3wz39p7bp";
    //ID of Twitch
    public static final String TWITCHID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
    //Object limit per request. Gets added to offset
    public static final int USER_REQUEST_LIMIT = 25;
    public static final int USER_UPDATE_REQUEST_LIMIT = 3;
    public static final int VOD_REQUEST_LIMIT = 10;
    public static final int VOD_UPDATE_REQUEST_LIMIT = 1;

    //Request Types
    public static final String CHANNEL = "CHANNEL";

    //Settings object keys
    public static final String SETTINGS_AUTOFOLLOW = "AutoFollow";
    public static final String SETTINGS_AUTOVODEXPORT = "AutoVODExport";
    public static final String SETTINGS_INTERVAL = "Interval";
    public static final String SETTINGS_INTERVAL_UNIT = "IntervalUnit";
    public static final String SETTINGS_NOTIFICATIONS = "Notifications";
    public static final String SETTINGS_VISIBILITY = "Visibility";
    public static final String SETTINGS_SPLIT = "Split";

    //Settings
    public static final String SETTINGS_OFF = "OFF";
    public static final String SETTINGS_FOLLOW = "FOLLOW";
    public static final String SETTINGS_UNFOLLOW = "UNFOLLOW";
    public static final String SETTINGS_FOLLOWUNFOLLOW = "FOLLOW_UNFOLLOW";
    public static final String SETTINGS_SHARE_F4F_STATUS = "SHARE_F4F_STATUS";
    public static final String SETTINGS_EXPORT = "EXPORT";

    //Settings interval unit
    public static final String SETTINGS_INTERVAL_UNIT_MINUTES = "MINUTES";
    public static final String SETTINGS_INTERVAL_UNIT_HOURS = "HOURS";
    public static final String SETTINGS_INTERVAL_UNIT_DAYS = "DAYS";

    //Notification Folders
    public static final String NOTIFICATION_FOLLOW = "NOTIFICATION_FOLLOW";
    public static final String NOTIFICATION_UNFOLLOW = "NOTIFICATION_UNFOLLOW";
    public static final String NOTIFICATION_VODEXPORT = "NOTIFICATION_VODEXPORT";

    //Notifications ID, Name & Description
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_ID = "AUTOFOLLOW_NOTIFICATION";
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME = "AutoFollow";
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify channel if new followers are followed and unfollowers that are unfollowed";

    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID = "AUTOVODEXPORT_NOTIFICATION";
    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME = "AutoVODExport";
    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify channel if new VODs have been exported";

    public static final String LURKSERVICE_NOTIFICATION_CHANNEL_ID = "LURKSERVICE_NOTIFICATION";
    public static final String LURKSERVICE_NOTIFICATION_CHANNEL_NAME = "LurkService";
    public static final String LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify channel if lurking is activated";

    //Flag Files
    public static final String FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE = "FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE";
    public static final String FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE = "FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE";

    /**
     * Options menu to be available throughout app
     * @author LethalMaus
     * @param activity activity requiring options
     * @param item MenuItem that is selected eg. Info or Settings
     * @return boolean whether an option was successfully selected
     */
    public static boolean onOptionsItemsSelected(Activity activity, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_info_guide:
                intent = new Intent(activity, InfoGuide.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_info:
                intent = new Intent(activity, Info.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(activity, SettingsMenu.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_logout:
                intent = new Intent(activity, Authorization.class);
                activity.startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    /**
     * Opens a notification channel for the AutoFollower
     * @author LethalMaus
     * @param weakContext weak reference to Context
     * @param channelID notification channel ID
     * @param channelName notification channel name
     * @param channelDescription notification channel description
     */
    public static void createNotificationChannel(WeakReference<Context> weakContext, String channelID, String channelName, String channelDescription) {
        if (weakContext != null && weakContext.get() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = weakContext.get().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Checks if settings were ever made for the Worker type, then checks if the Worker has been activated & with which preferences.
     * A PeriodicWorkRequest is activated to perform the Worker process
     * @author LethalMaus
     * @param weakContext weak reference to Context
     * @param settingsFileName name of settings files
     * @param workerName name of worker process
     * @param workerClass name of worker class
     * @param channelID notification channel ID
     * @param channelName notification channel name
     * @param channelDescription notification channel description
     */
    public static void activateWorker(WeakReference<Context> weakContext, String settingsFileName, String workerName, Class<? extends Worker> workerClass, String channelID, String channelName, String channelDescription) {
        if (weakContext != null && weakContext.get() != null && new File(weakContext.get().getFilesDir().toString() + File.separator + settingsFileName).exists()) {
            try {
                JSONObject settings = new JSONObject(new ReadFileHandler(null, weakContext, settingsFileName).readFile());
                if (!settings.getString(workerName).equals(Globals.SETTINGS_OFF) && !isWorkerActive(weakContext, workerName)) {
                    createNotificationChannel(weakContext, channelID, channelName, channelDescription);
                    TimeUnit intervalUnit;
                    switch (settings.getString(Globals.SETTINGS_INTERVAL_UNIT)) {
                        case Globals.SETTINGS_INTERVAL_UNIT_MINUTES:
                            intervalUnit = TimeUnit.MINUTES;
                            break;
                        case Globals.SETTINGS_INTERVAL_UNIT_HOURS:
                            intervalUnit = TimeUnit.HOURS;
                            break;
                        case Globals.SETTINGS_INTERVAL_UNIT_DAYS:
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
                } else if (settings.getString(workerName).equals(Globals.SETTINGS_OFF) && isWorkerActive(weakContext, workerName)) {
                    WorkManager.getInstance().cancelAllWorkByTag(workerName);
                }
            } catch (JSONException e) {
                Toast.makeText(weakContext.get(), "Error activating " + workerName, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(null, weakContext, "ERROR", null, "Main: error activating '" + workerName + "'. " + e.toString(), true).run();
            }
        }
    }

    /**
     * Method for checking if worker is running or enqueued to avoid restarting
     * @author LethalMaus
     * @param weakContext weak reference to Context
     * @param workerName name of worker process
     * @return Boolean if running or enqueued
     */
    private static Boolean isWorkerActive(WeakReference<Context> weakContext, String workerName) {
        if (weakContext != null && weakContext.get() != null) {
            try {
                if (WorkManager.getInstance().getWorkInfosForUniqueWork(workerName).get().size() > 0) {
                    WorkInfo.State state = WorkManager.getInstance().getWorkInfosForUniqueWork(workerName)
                            .get().get(0).getState();
                    return (state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.RUNNING);
                } else {
                    return false;
                }
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(weakContext.get(), "Error activating " + workerName, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(null, weakContext, "ERROR", null, "Main: error activating '" + workerName + "'. " + e.toString(), true).run();
            }
        }
        return false;
    }
}
