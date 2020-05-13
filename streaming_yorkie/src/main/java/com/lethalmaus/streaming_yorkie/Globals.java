package com.lethalmaus.streaming_yorkie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.lethalmaus.streaming_yorkie.activity.Authorization;
import com.lethalmaus.streaming_yorkie.activity.Info;
import com.lethalmaus.streaming_yorkie.activity.InfoGuide;
import com.lethalmaus.streaming_yorkie.activity.SettingsMenu;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.receiver.AutoLurkReceiver;
import com.lethalmaus.streaming_yorkie.request.PurchaseMadeRequestHandler;
import com.lethalmaus.streaming_yorkie.request.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Globals contains centralized constants & variables that are used throughout the whole app.
 * @author LethalMaus
 */
public class Globals {

    /**
     * Utility class should not be constructed
     * @author LethalMaus
     */
    private Globals() {
        throw new IllegalStateException("Utility class");
    }

    //ID of the app registered by Twitch
    public static final String CLIENTID = "tjots3mhxunw0sj2a20ka3wz39p7bp";
    //ID of Twitch
    public static final String TWITCHID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
    //Object limit per request. Gets added to offset
    public static final int USER_REQUEST_LIMIT = 25;
    public static final int USER_UPDATE_REQUEST_LIMIT = 3;
    public static final int VOD_REQUEST_LIMIT = 10;
    public static final int VOD_UPDATE_REQUEST_LIMIT = 1;

    //Twitch properties
    public static final String TWITCH_URL = "twitch.tv";
    public static final String ACCESS_TOKEN = "access_token";

    //Request Types
    public static final String CHANNEL = "CHANNEL";

    //Files
    public static final String FILE_TOKEN = "TOKEN";
    public static final String FILE_TWITCH_TOKEN = "TWITCH_TOKEN";
    public static final String FILE_ERROR = "ERROR";
    public static final String FILE_SETTINGS_VOD = "SETTINGS_VOD";
    public static final String FILE_SETTINGS_F4F = "SETTINGS_F4F";
    public static final String FILE_SETTINGS_LURK = "SETTINGS_LURK";
    public static final String FILE_SUPPORTER = "SUPPORTER";
    public static final String FILE_SUBSCRIBER = "SUBSCRIBER";
    public static final String FILE_SHARE_F4F = "SHARE_F4F";
    public static final String FILE_LURK_HTML = "LURK.HTML";

    //Settings object keys
    public static final String SETTINGS_AUTOFOLLOW = "AutoFollow";
    public static final String SETTINGS_AUTOVODEXPORT = "AutoVODExport";
    public static final String SETTINGS_AUTOLURK = "AutoLurk";
    public static final String SETTINGS_INTERVAL = "Interval";
    public static final String SETTINGS_INTERVAL_UNIT = "IntervalUnit";
    public static final String SETTINGS_NOTIFICATIONS = "Notifications";
    public static final String SETTINGS_VISIBILITY = "Visibility";
    public static final String SETTINGS_SPLIT = "Split";
    public static final String SETTINGS_WIFI_ONLY = "WIFI_ONLY";
    public static final String SETTINGS_LURK_INFORM = "LURK_INFORM";
    public static final String SETTINGS_LURK_MESSAGE = "LURK_MESSAGE";

    //Settings
    public static final String SETTINGS_OFF = "OFF";
    public static final String SETTINGS_FOLLOW = "FOLLOW";
    public static final String SETTINGS_UNFOLLOW = "UNFOLLOW";
    public static final String SETTINGS_FOLLOWUNFOLLOW = "FOLLOW_UNFOLLOW";
    public static final String SETTINGS_SHARE_F4F_STATUS = "SHARE_F4F_STATUS";
    public static final String SETTINGS_EXPORT = "EXPORT";
    public static final String SETTINGS_LURK = "LURK";

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
    private static final int LURK_SERVICE_ALARM_ID = 1;

    //Flag Files
    public static final String FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE = "FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE";
    public static final String FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE = "FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE";

    //Lurk Service Commands
    public static final String AUTO_LURK = "AUTO_LURK";
    public static final String STOP_LURK = "STOP_LURK";
    public static final String PAUSE_LURK = "PAUSE_LURK";
    public static final String RESTART_LURK = "RESTART_LURK";
    public static final String START_LURK = "START_LURK";

    //Action buttons
    public static final String EXCLUDE_BUTTON = "EXCLUDE_BUTTON";
    public static final String NOTIFICATIONS_BUTTON = "NOTIFICATIONS_BUTTON";
    public static final String INCLUDE_BUTTON = "INCLUDE_BUTTON";
    public static final String DELETE_BUTTON = "DELETE_BUTTON";


    /**
     * Options menu to be available throughout app
     * @author LethalMaus
     * @param activity activity requiring options
     * @param item MenuItem that is selected eg. Info or Settings
     * @return boolean whether an option was successfully selected
     */
    public static boolean onOptionsItemsSelected(Activity activity, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info_guide:
                activity.startActivity(new Intent(activity, InfoGuide.class));
                return true;
            case R.id.menu_info:
                activity.startActivity(new Intent(activity, Info.class));
                return true;
            case R.id.menu_settings:
                activity.startActivity(new Intent(activity, SettingsMenu.class));
                return true;
            case R.id.menu_logout:
                activity.startActivity(new Intent(activity, Authorization.class));
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
            try {
                String settingsString = new ReadFileHandler(null, weakContext, settingsFileName).readFile();
                if (weakContext != null && weakContext.get() != null && !settingsString.isEmpty()) {
                    JSONObject settings = new JSONObject(settingsString);
                    if (!settings.getString(workerName).equals(Globals.SETTINGS_OFF) && !isWorkerActive(weakContext, workerName)) {
                        createNotificationChannel(weakContext, channelID, channelName, channelDescription);
                        TimeUnit intervalUnit = TimeUnit.MINUTES;
                        int interval = 15;
                        if (settings.has(Globals.SETTINGS_INTERVAL_UNIT) && settings.has(Globals.SETTINGS_INTERVAL)) {
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
                            interval = settings.getInt(Globals.SETTINGS_INTERVAL);
                        }
                        PeriodicWorkRequest.Builder autoFollowBuilder = new PeriodicWorkRequest.Builder(workerClass, interval, intervalUnit);
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
                }
            } catch (JSONException e) {
                Toast.makeText(weakContext.get(), "Error reading settings for " + workerName, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(null, weakContext, Globals.FILE_ERROR, null, "Main: reading settings for '" + workerName + "'. " + e.toString(), true).run();
            }
    }

    /**
     * Checks if settings were ever made for the Alarm type, then checks if the Alarm has been activated & with which preferences.
     * AlarmManager is used to perform the Receiver process
     * @author LethalMaus
     * @param weakContext weak reference to context
     * @param alarmName name of alarm process
     * @param triggerIn time in milliseconds until alarm is triggered
     */
    public static void activateAlarm(WeakReference<Context> weakContext, String alarmName, long triggerIn) {
        try {
            String settingsString = new ReadFileHandler(null, weakContext, Globals.FILE_SETTINGS_LURK).readFile();
            if (checkWeakReference(weakContext) && !settingsString.isEmpty()) {
                JSONObject settings = new JSONObject(settingsString);
                if (!settings.getString(alarmName).equals(Globals.SETTINGS_OFF) && (PendingIntent.getBroadcast(weakContext.get(), Globals.LURK_SERVICE_ALARM_ID, new Intent(weakContext.get(), AutoLurkReceiver.class), PendingIntent.FLAG_NO_CREATE) == null)) {
                    Intent intent = new Intent(weakContext.get(), AutoLurkReceiver.class);
                    intent.setAction("LURK_ALARM");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(weakContext.get(), Globals.LURK_SERVICE_ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) weakContext.get().getSystemService(Context.ALARM_SERVICE);
                    createNotificationChannel(weakContext, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_NAME, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION);
                    if (alarmManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + triggerIn, pendingIntent);
                        } else {
                            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                        }
                    }
                } else if (settings.getString(alarmName).equals(Globals.SETTINGS_OFF) && (PendingIntent.getBroadcast(weakContext.get(), Globals.LURK_SERVICE_ALARM_ID, new Intent(weakContext.get(), AutoLurkReceiver.class), PendingIntent.FLAG_NO_CREATE) != null)) {
                    AlarmManager alarmManager = (AlarmManager) weakContext.get().getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.cancel(PendingIntent.getBroadcast(weakContext.get(), Globals.LURK_SERVICE_ALARM_ID, new Intent(weakContext.get(), AutoLurkReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(weakContext.get(), "Error reading settings for " + alarmName, Toast.LENGTH_SHORT).show();
            new WriteFileHandler(null, weakContext, Globals.FILE_ERROR, null, "Main: reading settings for '" + alarmName + "'. " + e.toString(), true).run();
        }
    }

    /**
     * Method for checking if worker is running or enqueued to avoid restarting
     * @author LethalMaus
     * @param weakContext weak reference to Context
     * @param workerName name of worker process
     * @return boolean if running or enqueued
     */
    private static boolean isWorkerActive(WeakReference<Context> weakContext, String workerName) {
        if (weakContext != null && weakContext.get() != null) {
            try {
                if (!WorkManager.getInstance().getWorkInfosForUniqueWork(workerName).get().isEmpty()) {
                    WorkInfo.State state = WorkManager.getInstance().getWorkInfosForUniqueWork(workerName)
                            .get().get(0).getState();
                    return (state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.RUNNING);
                } else {
                    return false;
                }
            } catch (ExecutionException e) {
                Toast.makeText(weakContext.get(), "Error getting " + workerName, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(null, weakContext, Globals.FILE_ERROR, null, "Main: error getting '" + workerName + "'. " + e.toString(), true).run();
            } catch (InterruptedException e) {
                Toast.makeText(weakContext.get(), "Error activating " + workerName, Toast.LENGTH_SHORT).show();
                new WriteFileHandler(null, weakContext, Globals.FILE_ERROR, null, "Main: error activating '" + workerName + "'. " + e.toString(), true).run();
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    /**
     *
     * Method for opening a link externally from the app
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param url a link to eg. Github
     */
    public static void openLink(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, String url) {
        if (RequestHandler.networkIsAvailable(weakContext)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            weakActivity.get().startActivity(intent);
        } else {
            Toast.makeText(weakActivity.get(), "OFFLINE: Can't open link", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method for handling google purchase objects from the play store
     * @author LethalMaus
     * @param weakActivity weak reference activity
     * @param weakContext weak reference context
     * @param billingClient billing client used
     * @param purchase google purchase object
     */
    public static void handlePurchase(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, BillingClient billingClient, Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (purchase.isAutoRenewing()) {
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_SUBSCRIBER, null, purchase.getPurchaseToken(), true).run();
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, (BillingResult billingResult) -> {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            handlePurchaseSuccess(weakActivity, weakContext, purchase);
                        }
                    });
                }
            } else {
                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.consumeAsync(consumeParams, (BillingResult billingResult, String s) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        handlePurchaseSuccess(weakActivity, weakContext, purchase);
                    }
                });
            }
            new WriteFileHandler(weakActivity, weakContext, Globals.FILE_SUPPORTER, null, purchase.getPurchaseToken(), true).run();
        }
    }

    /**
     * Method for informing developer of purchase made
     * @author LethalMaus
     * @param weakActivity weak reference activity
     * @param weakContext weak reference context
     * @param purchase google purchase object
     */
    private static void handlePurchaseSuccess(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, Purchase purchase) {
        new Thread(() -> {
            try {
                JSONObject postBody = new JSONObject();
                String username = StreamingYorkieDB.getInstance(weakContext.get()).channelDAO().getChannel().getDisplay_name();
                postBody.put("content", username + " | " + purchase.getSku() + " | " + purchase.getOrderId());
                new PurchaseMadeRequestHandler(weakActivity, weakContext).setPostBody(postBody).sendRequest(false);
            } catch (JSONException e) {
                new WriteFileHandler(weakActivity, weakContext, Globals.FILE_ERROR, null, "Error informing developer of subscription status" + " | " + e.toString(), true).run();
            }
        }).start();
    }

    /**
     * Checks if a weak activity is still usable
     * @author LethalMaus
     * @param weakReference weak activity
     * @return boolean if usable
     */
    public static boolean checkWeakActivity(WeakReference<Activity> weakReference) {
        return checkWeakReference(weakReference) && !weakReference.get().isDestroyed() && !weakReference.get().isFinishing();
    }

    /**
     * Checks if a weak reference is still usable
     * @author LethalMaus
     * @param weakReference weak reference
     * @return boolean if usable
     */
    public static boolean checkWeakReference(WeakReference weakReference) {
        return weakReference != null && weakReference.get() != null;
    }
}
