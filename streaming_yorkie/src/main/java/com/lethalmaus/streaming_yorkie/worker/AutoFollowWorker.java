package com.lethalmaus.streaming_yorkie.worker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.activity.Follow4Follow;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.User;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.FollowRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowersUpdateRequestHandler;
import com.lethalmaus.streaming_yorkie.request.FollowingUpdateRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
            new WriteFileHandler(weakContext, "ERROR", null, "AutoFollow: Error reading settings | " + e.toString(), true).run();
        }
    }

    @Override
    public @NonNull Result doWork() {
        final FollowingUpdateRequestHandler followingUpdateRequestHandler = new FollowingUpdateRequestHandler(null, weakContext, null) {
            @Override
            public void onCompletion() {
                final StreamingYorkieDB streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
                if (autoFollow != null && (autoFollow.equals("FOLLOW") || autoFollow.equals("FOLLOW_UNFOLLOW"))) {
                    FollowRequestHandler followRequestHandler =  new FollowRequestHandler(null, weakContext) {
                        @Override
                        public void onCompletion() {
                            User user = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserForAutoFollow();
                            if (user != null) {
                            new WriteFileHandler(weakContext, Globals.NOTIFICATION_FOLLOW + File.separator + user.getId(), null, null, false).writeToFileOrPath();
                                setRequestParameters(Request.Method.PUT, user.getId(), autoFollowNotifications)
                                        .sendRequest();
                            } else {
                                notifyUser(weakContext);
                            }
                        }
                    };
                    User user = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserForAutoFollow();
                    if (user != null) {
                        new WriteFileHandler(weakContext, Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE, null, null, false).writeToFileOrPath();
                        followRequestHandler.setRequestParameters(Request.Method.PUT, user.getId(), false)
                                .sendRequest();
                    }
                }
                if (autoFollow != null && (autoFollow.equals("UNFOLLOW") || autoFollow.equals("FOLLOW_UNFOLLOW"))) {
                    FollowRequestHandler followRequestHandler =  new FollowRequestHandler(null, weakContext) {
                        @Override
                        public void onCompletion() {
                            User user = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserForAutoFollow();
                            if (user != null) {
                            new WriteFileHandler(weakContext, Globals.NOTIFICATION_UNFOLLOW + File.separator + user.getId(), null, null, false).writeToFileOrPath();
                                setRequestParameters(Request.Method.DELETE, user.getId(), autoFollowNotifications)
                                        .sendRequest();
                            } else {
                                notifyUser(weakContext);
                            }
                        }
                    };
                    User user = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserForAutoFollow();
                    if (user != null) {
                        new WriteFileHandler(weakContext, Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE, null, null, false).writeToFileOrPath();
                        followRequestHandler.setRequestParameters(Request.Method.DELETE, user.getId(), false)
                                .sendRequest();
                    }
                }
            }
        };
        final FollowersUpdateRequestHandler followersUpdateRequestHandler = new FollowersUpdateRequestHandler(null, weakContext, null) {
            @Override
            public void onCompletion() {
                followingUpdateRequestHandler.initiate().sendRequest();
            }
        };
        followersUpdateRequestHandler.initiate().sendRequest();
        return Result.success();
    }

    /**
     * Method for pushing notifications to inform channel if someone has followed, unfollowed or both
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    private static void notifyUser(WeakReference<Context> weakContext) {
        int autoFollowCount = new ReadFileHandler(weakContext, Globals.NOTIFICATION_FOLLOW).countFiles();
        int autoUnfollowCount = new ReadFileHandler(weakContext, Globals.NOTIFICATION_UNFOLLOW).countFiles();
        if (weakContext != null && weakContext.get() != null
                && new File(weakContext.get().getFilesDir() + File.separator + Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE).exists()
                && (autoFollowCount > 0 || autoUnfollowCount > 0)) {
            new DeleteFileHandler(weakContext, null).deleteFileOrPath(Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE);
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