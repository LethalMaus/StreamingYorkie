package com.lethalmaus.streaming_yorkie.worker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.activity.Follow4Follow;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.F4FEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;
import com.lethalmaus.streaming_yorkie.entity.FollowingEntity;
import com.lethalmaus.streaming_yorkie.entity.UserEntity;
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
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Worker for automating FollowingEntity & Unfollowing
 * @author LethalMaus
 */
public class AutoFollowWorker extends Worker {

    private WeakReference<Context> weakContext;
    private StreamingYorkieDB streamingYorkieDB;
    private String autoFollow;
    private boolean autoFollowNotifications;

    /**
     * Constructor for AutoFollowWorker for automating FollowingEntity & Unfollowing
     * @author LethalMaus
     * @param context app context
     * @param params parameters for worker.super()
     */
    public AutoFollowWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.weakContext = new WeakReference<>(context);
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
        try {
            JSONObject settings = new JSONObject(new ReadFileHandler(null, weakContext, "SETTINGS_F4F").readFile());
            autoFollow = settings.getString(Globals.SETTINGS_AUTOFOLLOW);
            autoFollowNotifications = settings.getBoolean(Globals.SETTINGS_NOTIFICATIONS);
        } catch(JSONException e) {
            new WriteFileHandler(null, weakContext, "ERROR", null, "AutoFollow: Error reading settings | " + e.toString(), true).run();
        }
    }

    @Override
    public @NonNull Result doWork() {
        /*FIXME
        Delete when 1.3.1-a (13) is not in use
        This transfers local files to DB
        TODO this method will be found in MainActivity as well
         */
        if (weakContext != null && weakContext.get() != null && (
                new File(weakContext.get().getFilesDir().toString() + File.separator + "FOLLOWING_EXCLUDED").exists()
                || new File(weakContext.get().getFilesDir().toString() + File.separator + "FOLLOWERS_EXCLUDED").exists()
                || new File(weakContext.get().getFilesDir().toString() + File.separator + "F4F_EXCLUDED").exists())) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ArrayList<String> excluded = new ReadFileHandler(null, weakContext, "FOLLOWING_EXCLUDED").readFileNames();
                        for (int i = 0; i < excluded.size(); i++) {
                            JSONObject user = new JSONObject(new ReadFileHandler(null, weakContext, "FOLLOWING" + File.separator + excluded.get(i)).readFile());
                            FollowingEntity followingEntity = new FollowingEntity(Integer.parseInt(user.getString("_id")),
                                    user.getString("display_name"),
                                    user.getString("logo"),
                                    user.getString("created_at"),
                                    user.getBoolean("notifications"),
                                    0);
                            streamingYorkieDB.followingDAO().insertUser(followingEntity);
                        }
                        new DeleteFileHandler(null, weakContext, "FOLLOWING").run();
                        new DeleteFileHandler(null, weakContext, "FOLLOWING_EXCLUDED").run();
                        excluded = new ReadFileHandler(null, weakContext, "FOLLOWERS_EXCLUDED").readFileNames();
                        for (int i = 0; i < excluded.size(); i++) {
                            JSONObject user = new JSONObject(new ReadFileHandler(null, weakContext, "FOLLOWERS" + File.separator + excluded.get(i)).readFile());
                            FollowerEntity followerEntity = new FollowerEntity(Integer.parseInt(user.getString("_id")),
                                    user.getString("display_name"),
                                    user.getString("logo"),
                                    user.getString("created_at"),
                                    user.getBoolean("notifications"),
                                    0);
                            streamingYorkieDB.followerDAO().insertUser(followerEntity);
                        }
                        new DeleteFileHandler(null, weakContext, "FOLLOWERS").run();
                        new DeleteFileHandler(null, weakContext, "FOLLOWERS_EXCLUDED").run();
                        excluded = new ReadFileHandler(null, weakContext, "F4F_EXCLUDED").readFileNames();
                        for (int i = 0; i < excluded.size(); i++) {
                            JSONObject user;
                            if (new File(weakContext.get().getFilesDir().toString() + File.separator + "FOLLOWING" + File.separator + excluded.get(i)).exists()) {
                                user = new JSONObject(new ReadFileHandler(null, weakContext, "FOLLOWING" + File.separator + excluded.get(i)).readFile());
                            } else {
                                user = new JSONObject(new ReadFileHandler(null, weakContext, "FOLLOWERS" + File.separator + excluded.get(i)).readFile());
                            }
                            F4FEntity f4FEntity = new F4FEntity(Integer.parseInt(user.getString("_id")),
                                    user.getString("display_name"),
                                    user.getString("logo"),
                                    user.getString("created_at"),
                                    user.getBoolean("notifications"),
                                    0);
                            streamingYorkieDB.f4fDAO().insertUser(f4FEntity);
                        }
                        new DeleteFileHandler(null, weakContext, "F4F_EXCLUDED").run();
                    } catch (JSONException e) {
                        new WriteFileHandler(null, weakContext, "ERROR", null, "Error migrating local files to DB: " + e.toString(), true).run();
                    }
                }
            }).start();
        }
        final FollowingUpdateRequestHandler followingUpdateRequestHandler = new FollowingUpdateRequestHandler(null, weakContext, null) {
            @Override
            public void onCompletion() {
                super.onCompletion();
                if (autoFollow != null && (autoFollow.equals("FOLLOW") || autoFollow.equals("FOLLOW_UNFOLLOW"))) {
                    UserEntity userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserForAutoFollow();
                    if (userEntity != null) {
                        new WriteFileHandler(null, weakContext, Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE, null, null, false).writeToFileOrPath();
                        new WriteFileHandler(null, weakContext, Globals.NOTIFICATION_FOLLOW + File.separator + userEntity.getId(), null, null, false).writeToFileOrPath();
                        new FollowRequestHandler(null, weakContext) {
                            @Override
                            public void onCompletion() {
                                super.onCompletion();
                                UserEntity userEntity = streamingYorkieDB.f4fDAO().getFollowedNotFollowingUserForAutoFollow();
                                if (userEntity != null) {
                                    new WriteFileHandler(null, weakContext, Globals.NOTIFICATION_FOLLOW + File.separator + userEntity.getId(), null, null, false).writeToFileOrPath();
                                    setRequestParameters(Request.Method.PUT, userEntity.getId(), autoFollowNotifications)
                                            .sendRequest();
                                } else {
                                    notifyUser(weakContext);
                                }
                            }
                        }.setRequestParameters(Request.Method.PUT, userEntity.getId(), false)
                                .sendRequest();
                    }
                }
                if (autoFollow != null && (autoFollow.equals("UNFOLLOW") || autoFollow.equals("FOLLOW_UNFOLLOW"))) {
                    UserEntity userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserForAutoFollow();
                    if (userEntity != null) {
                        new WriteFileHandler(null, weakContext, Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE, null, null, false).writeToFileOrPath();
                        new WriteFileHandler(null, weakContext, Globals.NOTIFICATION_UNFOLLOW + File.separator + userEntity.getId(), null, null, false).writeToFileOrPath();
                        new FollowRequestHandler(null, weakContext) {
                            @Override
                            public void onCompletion() {
                                super.onCompletion();
                                UserEntity userEntity = streamingYorkieDB.f4fDAO().getNotFollowedFollowingUserForAutoFollow();
                                if (userEntity != null) {
                                    new WriteFileHandler(null, weakContext, Globals.NOTIFICATION_UNFOLLOW + File.separator + userEntity.getId(), null, null, false).writeToFileOrPath();
                                    setRequestParameters(Request.Method.DELETE, userEntity.getId(), autoFollowNotifications)
                                            .sendRequest();
                                } else {
                                    notifyUser(weakContext);
                                }
                            }
                        }.setRequestParameters(Request.Method.DELETE, userEntity.getId(), autoFollowNotifications)
                                .sendRequest();
                    }
                }
            }
        };
        final FollowersUpdateRequestHandler followersUpdateRequestHandler = new FollowersUpdateRequestHandler(null, weakContext, null) {
            @Override
            public void onCompletion() {
                super.onCompletion();
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
        int autoFollowCount = new ReadFileHandler(null, weakContext, Globals.NOTIFICATION_FOLLOW).countFiles();
        int autoUnfollowCount = new ReadFileHandler(null, weakContext, Globals.NOTIFICATION_UNFOLLOW).countFiles();
        if (weakContext != null && weakContext.get() != null
                && new File(weakContext.get().getFilesDir() + File.separator + Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE).exists()
                && (autoFollowCount > 0 || autoUnfollowCount > 0)) {
            new DeleteFileHandler(null, weakContext, null).deleteFileOrPath(Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE);
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