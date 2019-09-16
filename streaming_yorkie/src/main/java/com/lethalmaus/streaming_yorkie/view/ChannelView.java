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
public class ChannelView extends AsyncTask<Void, View, Void> {

    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private StreamingYorkieDB streamingYorkieDB;

    //Channel attributes
    private String displayName;
    private int userID;
    private String logo;
    private String game;
    private String createdAt;
    private int views;
    private int followers;
    private String status;
    private String description;
    private String broadcasterType;

    /**
     * Constructor for ChannelView for viewing current channel info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public ChannelView(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
    }

    @Override
    protected Void doInBackground(Void... params) {
        Channel channel = streamingYorkieDB.channelDAO().getChannel();
        displayName = channel.getDisplay_name();
        userID = channel.getId();
        logo = channel.getLogo();
        if (logo.contains("/")
                && new File(weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/") + 1)).exists()) {
            logo = logo.substring(logo.lastIndexOf("/") + 1);
        }
        createdAt = channel.getCreated_at();
        if (channel.getGame() != null && !channel.getGame().contentEquals("")) {
            game = channel.getGame();
        } else {
            game = "-";
        }
        views = channel.getViews();
        followers = channel.getFollowers();
        if (channel.getStatus() != null && !channel.getStatus().contentEquals("")) {
            status = channel.getStatus();
        } else {
            status = "-";
        }
        if (channel.getDescription() != null && !channel.getDescription().contentEquals("")) {
            description = channel.getDescription();
        } else {
            description = "-";
        }
        if (channel.getBroadcasterType() != null && !channel.getBroadcasterType().contentEquals("")) {
            broadcasterType = channel.getBroadcasterType();
        } else {
            broadcasterType = "-";
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
                    Glide.with(activity).load(new File(logo)).into(user_Logo);
                }

                TextView user_Username = activity.findViewById(R.id.user_Username);
                user_Username.setText(displayName);

                TextView user_ID = activity.findViewById(R.id.user_ID);
                user_ID.setText(String.valueOf(userID));

                TextView user_Game = activity.findViewById(R.id.user_Game);
                user_Game.setText(game);

                TextView user_MemberSince = activity.findViewById(R.id.user_MemberSince);
                user_MemberSince.setText(createdAt);

                TextView user_Views = activity.findViewById(R.id.user_Views);
                user_Views.setText(String.valueOf(views));

                TextView user_Follows = activity.findViewById(R.id.user_Follows);
                user_Follows.setText(String.valueOf(followers));

                TextView user_BroadcasterType = activity.findViewById(R.id.user_BroadcasterType);
                user_BroadcasterType.setText(broadcasterType);

                TextView user_Status = activity.findViewById(R.id.user_Status);
                user_Status.setText(status);

                TextView user_Description = activity.findViewById(R.id.user_Description);
                user_Description.setText(description);
        }
    }
}