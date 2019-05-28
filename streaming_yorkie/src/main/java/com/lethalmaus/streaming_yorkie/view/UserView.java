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
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * AsyncTask for viewing current user info
 * @author LethalMaus
 */
public class UserView extends AsyncTask<Void, View, Void> {

    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    //Whether user is to be display or just updated
    private boolean displayUser;
    //Whether all user info is to be shown or just name & logo
    private boolean showAllInfo;

    //User attributes
    private String displayName;
    private String userID;
    private String logo;
    private String game;
    private String createdAt;
    private int views;
    private int followers;
    private String status;
    private String description;
    private String broadcasterType;

    /**
     * Constructor for UserView for viewing current user info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     * @param displayUser boolean whether user is to be display or just updated
     * @param showAllInfo boolean whether all user info is to be shown or just name & logo
     */
    public UserView (WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, boolean displayUser, boolean showAllInfo) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.displayUser = displayUser;
        this.showAllInfo = showAllInfo;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONObject user = new JSONObject(new ReadFileHandler(weakContext, "USER").readFile());
            displayName = user.getString("display_name");
            userID = user.getString("_id");
            logo = user.getString("logo");
            if (new File(weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/")+1)).exists()) {
                logo = weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/")+1);
            }
            game = user.getString("game");
            createdAt = user.getString("created_at");
            views = user.getInt("views");
            followers = user.getInt("followers");
            status = user.getString("status");
            description = user.getString("description");
            broadcasterType = user.getString("broadcaster_type");
        } catch (JSONException e) {
            new WriteFileHandler(weakContext, "ERROR", null, "Error reading USER file | " + e.toString(), true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            Activity activity = weakActivity.get();
            if (displayUser && showAllInfo) {
                ImageView user_Logo = activity.findViewById(R.id.user_Logo);
                if (new File(weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/")+1)).exists()) {
                    user_Logo.setImageBitmap(BitmapFactory.decodeFile(new File(logo).getAbsolutePath()));
                } else {
                    Glide.with(activity).load(new File(logo)).into(user_Logo);
                }

                TextView user_Username = activity.findViewById(R.id.user_Username);
                user_Username.setText(displayName);

                TextView user_ID = activity.findViewById(R.id.user_ID);
                user_ID.setText(userID);

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
            } else if (displayUser) {
                ImageView user_Logo = activity.findViewById(R.id.user_Logo);
                if (new File(weakContext.get().getFilesDir() + File.separator + logo.substring(logo.lastIndexOf("/")+1)).exists()) {
                    user_Logo.setImageBitmap(BitmapFactory.decodeFile(new File(logo).getAbsolutePath()));
                } else {
                    Glide.with(activity).load(new File(logo)).into(user_Logo);
                }

                TextView user_Username = activity.findViewById(R.id.user_Username);
                user_Username.setText(displayName);
            }
        }
    }
}