package com.lethalmaus.streaming_yorkie.activity;

import android.os.Bundle;

import android.view.View;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.request.VolleySingleton;

import java.io.File;

/**
 * Activity with common methods for sub-activities for following, followers, f4f
 * @author LethalMaus
 */
public class FollowParent extends UserParent {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelRequests();
        deleteNotifications();
    }

    @Override
    protected void onPause() {
        cancelRequests();
        super.onPause();
    }

    /**
     * Deletes any notification files when the activity is started
     * @author LethalMaus
     */
    private void deleteNotifications() {
        new Thread(() -> {
            if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_FOLLOW).exists()) {
                new DeleteFileHandler(weakActivity, weakContext, Globals.NOTIFICATION_FOLLOW).run();
            }
            if (new File(getFilesDir() + File.separator + Globals.NOTIFICATION_UNFOLLOW).exists()) {
                new DeleteFileHandler(weakActivity, weakContext, Globals.NOTIFICATION_UNFOLLOW).run();
            }
            if (new File(getFilesDir() + File.separator + Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE).exists()) {
                new DeleteFileHandler(weakActivity, weakContext, Globals.FLAG_AUTOFOLLOW_NOTIFICATION_UPDATE).run();
            }
        }).start();
    }

    @Override
    protected void cancelRequests() {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(weakContext);
        if (volleySingleton != null) {
            volleySingleton.getRequestQueue().cancelAll("FOLLOWING_UPDATE");
            volleySingleton.getRequestQueue().cancelAll("FOLLOWERS_UPDATE");
            volleySingleton.getRequestQueue().cancelAll("FOLLOWING");
            volleySingleton.getRequestQueue().cancelAll("FOLLOWERS");
            volleySingleton.getRequestQueue().cancelAll("FOLLOW");
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}