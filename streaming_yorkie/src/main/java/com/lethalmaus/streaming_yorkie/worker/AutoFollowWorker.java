package com.lethalmaus.streaming_yorkie.worker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.activity.Follow4Follow;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.OrganizeFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.FollowRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowersRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Worker for automating Following & Unfollowing
 * @author LethalMaus
 */
public class AutoFollowWorker extends Worker {

    private WeakReference<Context> weakContext;
    private String autoFollow;
    private boolean autoFollowNotifications;

    /**
     * Constructor for AutoFollowWorker for automating Following & Unfollowing
     * @author LethalMaus
     * @param context app context
     * @param params parameters for worker.super()
     */
    public AutoFollowWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.weakContext = new WeakReference<>(context);
        try {
            JSONObject settings = new JSONObject(new ReadFileHandler(weakContext, "SETTINGS_F4F").readFile());
            autoFollow = settings.getString(Globals.SETTINGS_AUTOFOLLOW);
            autoFollowNotifications = settings.getBoolean(Globals.SETTINGS_NOTIFICATIONS);
        } catch(JSONException e) {
            new WriteFileHandler(weakContext, "ERROR", null, e.toString()+"\n", true).run();
        }
    }

    @Override
    public @NonNull Result doWork() {
        new FollowersRequestHandler(null, weakContext, null, false, true) {
            @Override
            protected void responseAction() {
                new autoFollowOrganizeFileHandler(null, weakContext, false, true)
                        .setPreferences(autoFollow, autoFollowNotifications)
                        .setPaths(Globals.FOLLOWERS_CURRENT_PATH, Globals.FOLLOWERS_NEW_PATH, Globals.FOLLOWERS_UNFOLLOWED_PATH, Globals.FOLLOWERS_EXCLUDED_PATH, Globals.FOLLOWERS_REQUEST_PATH, Globals.FOLLOWERS_PATH)
                        .execute();
            }
        }.newRequest().sendRequest(0);
        return Result.success();
    }

    /**
     * Extends OrganizeFileHandler to organize new followers
     * @author LethalMaus
     */
    static class autoFollowOrganizeFileHandler extends OrganizeFileHandler {

        //Constant - FOLLOW, UNFOLLOW & FOLLOW_UNFOLLOW (both)
        private String autoFollow;
        //Whether the AutoFollow should activate/deactivate notifications
        private boolean autoFollowNotifications;

        /**
         * Constructor for autoFollowOrganizeFileHandler that extends OrganizeFileHandler to organize new followers
         * @author LethalMaus
         * @param weakActivity weak referenced activity
         * @param weakContext weak referenced context
         * @param displayUsers boolean whether user is to be displayed
         * @param commonFolders constant for F4F folders or not
         */
        autoFollowOrganizeFileHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, boolean displayUsers, boolean commonFolders) {
            super(weakActivity, weakContext, null, displayUsers, commonFolders);
        }

        /**
         * Sets the AutoFollow preferences
         * @author LethalMaus
         * @param autoFollow constant - type of action to be taken eg. FOLLOW, UNFOLLOW & FOLLOW_UNFOLLOW
         * @param autoFollowNotifications boolean whether the AutoFollow should activate/deactivate notifications
         * @return instance of itself for method building
         */
        autoFollowOrganizeFileHandler setPreferences(String autoFollow, boolean autoFollowNotifications) {
            this.autoFollow = autoFollow;
            this.autoFollowNotifications = autoFollowNotifications;
            return this;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (autoFollow.equals("FOLLOW") || autoFollow.equals("FOLLOW_UNFOLLOW")) {
                ArrayList<String> newUsers = new ReadFileHandler(weakContext, Globals.FOLLOWERS_NEW_PATH).readFileNames();
                for (final String user : newUsers) {
                    if (!new File(appDirectory + File.separator + Globals.FOLLOWING_CURRENT_PATH + File.separator + user).exists() &&
                            !new File(appDirectory + File.separator + Globals.FOLLOWING_EXCLUDED_PATH + File.separator + user).exists() &&
                            !new File(appDirectory + File.separator + Globals.F4F_EXCLUDED_PATH + File.separator + user).exists()) {
                        new FollowRequestHandler(null, weakContext) {
                            @Override
                            public void responseHandler(JSONObject response) {
                                super.responseHandler(response);
                                new WriteFileHandler(weakContext, Globals.NOTIFICATION_FOLLOW + File.separator + user, null, null, false).run();
                            }
                        }.setRequestParameters(Request.Method.PUT, user, autoFollowNotifications).requestFollow();
                    }
                }
            }
            if (autoFollow.equals("UNFOLLOW") || autoFollow.equals("FOLLOW_UNFOLLOW")) {
                ArrayList<String> unfollowedUsers = new ReadFileHandler(weakContext, Globals.FOLLOWERS_UNFOLLOWED_PATH).readFileNames();
                for (final String user : unfollowedUsers) {
                    if (!new File(appDirectory + File.separator + Globals.FOLLOWING_CURRENT_PATH + File.separator + user).exists() &&
                            !new File(appDirectory + File.separator + Globals.FOLLOWING_EXCLUDED_PATH + File.separator + user).exists() &&
                            !new File(appDirectory + File.separator + Globals.F4F_EXCLUDED_PATH + File.separator + user).exists()) {
                        new FollowRequestHandler(null, weakContext) {
                            @Override
                            public void responseHandler(JSONObject response) {
                                super.responseHandler(response);
                                new WriteFileHandler(weakContext, Globals.NOTIFICATION_UNFOLLOW + File.separator + user, null, null, false).run();
                            }
                        }.setRequestParameters(Request.Method.DELETE, user, autoFollowNotifications).requestFollow();
                    }
                }
            }
            notifyUser(weakContext);
        }
    }

    /**
     * Method for pushing notifications to inform user if someone has followed, unfollowed or both
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    private static void notifyUser(WeakReference<Context> weakContext) {
        int autoFollowCount = new ReadFileHandler(weakContext, Globals.NOTIFICATION_FOLLOW).countFiles();
        int autoUnfollowCount = new ReadFileHandler(weakContext, Globals.NOTIFICATION_UNFOLLOW).countFiles();
        if (autoFollowCount > 0 || autoUnfollowCount > 0 && weakContext != null && weakContext.get() != null) {
            Intent intent = new Intent(weakContext.get(), Follow4Follow.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(weakContext.get(), 0, intent, 0);

            String content = "";
            if (autoFollowCount > 0) {
                content += "You follow '" + autoFollowCount + "' new Followers. ";
            }
            if (autoUnfollowCount > 0) {
                content += "You unfollowed '" + autoUnfollowCount + "' Unfollowers.";
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(weakContext.get(), Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.streaming_yorkie)
                    .setContentTitle("AutoFollow")
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(weakContext.get());
            notificationManager.notify(1, mBuilder.build());
        }
    }
}