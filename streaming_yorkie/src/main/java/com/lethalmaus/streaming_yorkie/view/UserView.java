package com.lethalmaus.streaming_yorkie.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * AsyncTask for viewing current channel info
 * @author LethalMaus
 */
public class UserView extends AsyncTask<Void, View, Void> {

    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private StreamingYorkieDB streamingYorkieDB;

    //UserEntity attributes
    private String displayName;
    private String logo;

    /**
     * Constructor for UserView for viewing current user info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public UserView (WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
    }

    @Override
    protected Void doInBackground(Void... params) {
        ChannelEntity channelEntity = streamingYorkieDB.channelDAO().getChannel();
        if (channelEntity != null) {
            displayName = channelEntity.getDisplay_name();
            logo = channelEntity.getLogo();
            if (logo.contains("/")
                    && new File(weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/") + 1)).exists()) {
                logo = logo.substring(logo.lastIndexOf("/") + 1);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        try {
            if (weakContext != null && weakContext.get() != null && weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
                if (!displayName.isEmpty() && !logo.isEmpty()) {
                    ImageView user_Logo = weakActivity.get().findViewById(R.id.user_Logo);
                    if (user_Logo != null) {
                        if (!logo.contains("/")
                                && new File(weakContext.get().getFilesDir() + File.separator + logo).exists()) {
                            user_Logo.setImageBitmap(BitmapFactory.decodeFile(new File(weakContext.get().getFilesDir() + File.separator + logo).getAbsolutePath()));
                        } else {
                            Glide.with(weakActivity.get()).load(logo).into(user_Logo);
                        }
                        TextView user_Username = weakActivity.get().findViewById(R.id.user_Username);
                        user_Username.setText(displayName);
                    }
                } else {
                    Toast.makeText(weakActivity.get(), "Error accessing User Info locally. Please contact the developer", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException e) {
            new WriteFileHandler(weakActivity, weakContext, "ERROR", null, "ANTI-CRASH: Not able to load logo due to: " + e.toString(), true).run();
        }
    }
}