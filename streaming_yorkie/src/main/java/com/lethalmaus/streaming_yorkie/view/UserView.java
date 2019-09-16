package com.lethalmaus.streaming_yorkie.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.Channel;

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

    //User attributes
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
        Channel channel = streamingYorkieDB.channelDAO().getChannel();
        displayName = channel.getDisplay_name();
        logo = channel.getLogo();
        if (logo.contains("/")
                && new File(weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/")+1)).exists()) {
            logo = logo.substring(logo.lastIndexOf("/")+1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (weakContext != null && weakContext.get() != null && weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            Activity activity = weakActivity.get();
                ImageView user_Logo = activity.findViewById(R.id.user_Logo);
                if (!logo.contains("/")
                        && new File(weakContext.get().getFilesDir() + File.separator + logo).exists()) {
                    user_Logo.setImageBitmap(BitmapFactory.decodeFile(new File(weakContext.get().getFilesDir() + File.separator + logo).getAbsolutePath()));
                } else {
                    Glide.with(activity).load(logo).into(user_Logo);
                }
                TextView user_Username = activity.findViewById(R.id.user_Username);
                user_Username.setText(displayName);
        }
    }
}