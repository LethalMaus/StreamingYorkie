package com.lethalmaus.streaming_yorkie.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.FollowRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Recycler View Adapter for Followers/Following/F4F
 * @author LethalMaus
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    //All activities & contexts are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    private WeakReference<Context> weakContext;
    private String appDirectory;

    //Paths for Dataset files
    private String newUsersPath;
    private String currentUsersPath;
    private String unfollowedUsersPath;
    private String excludedUsersPath;

    //Display preferences
    private String usersPath;
    private String usersToDisplay;
    private ArrayList<String> userDataset;
    private String actionButtonType1;
    private String actionButtonType2;
    private String actionButtonType3;

    //Page counts
    private int pageCount1;
    private int pageCount2;
    private int pageCount3;
    private int pageCount4;

    private FollowRequestHandler followRequestHandler;

    /**
     * Simple View Holder for loading the View with a Dataset Row
     * @author LethalMaus
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {

        View userRow;

        /**
         * Holder for User View
         * @param userRow View for User Row
         */
        UserViewHolder(View userRow) {
            super(userRow);
            this.userRow = userRow;
        }
    }

    /**
     * Adapter for displaying a User Dataset
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public UserAdapter(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
        followRequestHandler = new FollowRequestHandler(weakActivity, weakContext);
    }

    /**
     * Sets the paths where the data files can be found
     * @author LethalMaus
     * @param newUsersPath directory of new user ids
     * @param currentUsersPath directory of current user ids
     * @param unfollowedUsersPath directory of unfollowed user ids
     * @param excludedUsersPath directory of excluded user ids
     * @param usersPath directory of user objects
     * @return an instance of itself for method building
     */
    public UserAdapter setPaths(String newUsersPath, String currentUsersPath, String unfollowedUsersPath, String excludedUsersPath, String usersPath) {
        this.newUsersPath = newUsersPath;
        this.currentUsersPath = currentUsersPath;
        this.unfollowedUsersPath = unfollowedUsersPath;
        this.excludedUsersPath = excludedUsersPath;
        this.usersPath = usersPath;
        //As soon as we know the paths, the counts can be set
        setPageCounts();
        setPageCountViews(weakActivity);
        return this;
    }

    /**
     * Sets Display preferences
     * @author LethalMaus
     * @param usersToDisplay constant of which users are to be displayed
     * @param actionButtonType1 constant of which button is required in relation to the itemsToDisplay
     * @param actionButtonType2 constant of which button is required in relation to the itemsToDisplay
     * @param actionButtonType3 constant of which button is required in relation to the itemsToDisplay
     * @return an instance of itself for method building
     */
    public UserAdapter setDisplayPreferences(String usersToDisplay, String actionButtonType1, String actionButtonType2, String actionButtonType3) {
        this.usersToDisplay = usersToDisplay;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        this.actionButtonType3 = actionButtonType3;
        //As soon as we know how to display the users, we get the user Dataset
        getUsers();
        //An empty row or table can be displayed based on if the dataset is empty or not
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            if (userDataset.size() > 0) {
                weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
                weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
            } else {
                weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
                weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
                weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            }
        }
        return this;
    }

    @Override
    @NonNull
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_parent_row, parent, false);
        return new UserViewHolder(userRow);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing() && weakContext != null && weakContext.get() != null) {
            try {
                JSONObject userObject;
                if (new File(weakContext.get().getFilesDir() + File.separator + Globals.FOLLOWING_PATH + File.separator + userDataset.get(position)).exists()) {
                    userObject = new JSONObject(new ReadFileHandler(weakContext, Globals.FOLLOWING_PATH + File.separator + userDataset.get(position)).readFile());
                } else {
                    userObject = new JSONObject(new ReadFileHandler(weakContext, Globals.FOLLOWERS_PATH + File.separator + userDataset.get(position)).readFile());
                }

                TextView textView = userViewHolder.userRow.findViewById(R.id.userrow_username);
                textView.setText(userObject.getString("display_name"));

                ImageView imageView = userViewHolder.userRow.findViewById(R.id.userrow_logo);
                Glide.with(weakContext.get()).load(userObject.getString("logo")).into(imageView);

                ImageButton button1 = userViewHolder.userRow.findViewById(R.id.userrow_button1);
                editButton(button1, actionButtonType1, userObject.getString("_id"));
                ImageButton button2 = userViewHolder.userRow.findViewById(R.id.userrow_button2);
                editButton(button2, actionButtonType2, userObject.getString("_id"));
                ImageButton button3 = userViewHolder.userRow.findViewById(R.id.userrow_button3);
                editButton(button3, actionButtonType3, userObject.getString("_id"));

                ProgressBar progressBar = weakActivity.get().findViewById(R.id.progressbar);
                progressBar.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true);
            }
        }
    }

    /**
     * Takes action once a dataset has been changed then notifies UI
     */
    private void datasetChanged() {
        if (userDataset != null && userDataset.size() > 0) {
            if (userDataset.size() > 1 && (usersToDisplay.contentEquals("FOLLOWED_NOTFOLLOWING") || usersToDisplay.contentEquals("NOTFOLLOWED_FOLLOWING"))) {
                weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.VISIBLE);
            } else {
                weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
            }
            weakActivity.get().findViewById(R.id.table).setVisibility(View.VISIBLE);
            weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.GONE);
        } else {
            weakActivity.get().findViewById(R.id.follow_unfollow_all).setVisibility(View.GONE);
            weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
            weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userDataset != null ? userDataset.size() : 0;
    }

    /**
     * Gets the Dataset (list of user ids) based on itemsToDisplay
     * @author LethalMaus
     */
    private void getUsers() {
        switch (usersToDisplay) {
            case "NEW":
                userDataset = new ReadFileHandler(weakContext, newUsersPath).readFileNames();
                break;
            case "CURRENT":
                userDataset = new ReadFileHandler(weakContext, currentUsersPath).readFileNames();
                break;
            case "UNFOLLOWED":
                userDataset = new ReadFileHandler(weakContext, unfollowedUsersPath).readFileNames();
                break;
            case "EXCLUDED":
                userDataset = new ReadFileHandler(weakContext, excludedUsersPath).readFileNames();
                break;
            case "FOLLOWED_NOTFOLLOWING":
                usersPath = Globals.FOLLOWERS_PATH;
                userDataset = new ReadFileHandler(weakContext, Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH).readFileNames();
                actionAllButton(true);
                break;
            case "FOLLOW4FOLLOW":
                usersPath = Globals.FOLLOWERS_PATH;
                userDataset = new ReadFileHandler(weakContext, Globals.F4F_FOLLOW4FOLLOW_PATH).readFileNames();
                break;
            case "NOTFOLLOWED_FOLLOWING":
                usersPath = Globals.FOLLOWING_PATH;
                userDataset = new ReadFileHandler(weakContext, Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH).readFileNames();
                actionAllButton(false);
                break;
            case "F4F_EXCLUDED":
                usersPath = Globals.FOLLOWING_PATH;
                userDataset = new ReadFileHandler(weakContext, Globals.F4F_EXCLUDED_PATH).readFileNames();
                break;
        }
        //To show the newest first
        Collections.reverse(userDataset);
    }

    /**
     * Edits the button based on the actionButtonType
     * @author LethalMaus
     * @param button which button is to be changed
     * @param actionButtonType a constant of the action button type
     * @param userID the user id which is related to the button
     */
    private void editButton(ImageButton button, String actionButtonType, String userID) {
        if (actionButtonType != null) {
            switch (actionButtonType) {
                case "DELETE_BUTTON":
                    deleteButton(button, userID);
                    break;
                case "EXCLUDE_BUTTON":
                    excludeButton(button, userID);
                    break;
                case "INCLUDE_BUTTON":
                    includeButton(button, userID);
                    break;
                case "FOLLOW_BUTTON":
                    followButton(button, userID);
                    break;
                case "NOTIFICATIONS_BUTTON":
                    notificationsButton(button, userID);
                    break;
            }
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Button for deleting a user
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be deleted
     */
    private void deleteButton(ImageButton imageButton, final String userID) {
        imageButton.setImageResource(R.drawable.delete);
        imageButton.setTag("DELETE_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFileHandler(weakContext, usersPath + File.separator + userID).run();
                new DeleteFileHandler(weakContext, unfollowedUsersPath + File.separator + userID).run();
                userDataset.remove(userID);
                datasetChanged();
                pageCount3--;
                setPageCountViews(weakActivity);
            }
        });
    }

    /**
     * Button for excluding a user from automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be excluded
     */
    private void excludeButton(ImageButton imageButton, final String userID) {
        imageButton.setImageResource(R.drawable.excluded);
        imageButton.setTag("EXCLUDE_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WriteFileHandler(weakContext, excludedUsersPath + File.separator + userID, null, null, false).run();
                userDataset.remove(userID);
                datasetChanged();
                if (new File(appDirectory + File.separator + newUsersPath + File.separator + userID).exists()) {
                    //When a user is excluded from new, it has not considered new anymore
                    if (!newUsersPath.contains("NEW")) {
                        new WriteFileHandler(weakContext, excludedUsersPath + "_" + newUsersPath + File.separator + userID, null, null, false).run();
                    }
                    new DeleteFileHandler(weakContext, newUsersPath + File.separator + userID).run();
                    pageCount1--;
                }
                if (new File(appDirectory + File.separator + currentUsersPath + File.separator + userID).exists()) {
                    new WriteFileHandler(weakContext, excludedUsersPath + "_" + currentUsersPath + File.separator + userID, null, null, false).run();
                    new DeleteFileHandler(weakContext, currentUsersPath + File.separator + userID).run();
                    pageCount2--;
                }
                if (new File(appDirectory + File.separator + unfollowedUsersPath + File.separator + userID).exists()) {
                    new WriteFileHandler(weakContext, excludedUsersPath + "_" + unfollowedUsersPath + File.separator + userID, null, null, false).run();
                    new DeleteFileHandler(weakContext, unfollowedUsersPath + File.separator + userID).run();
                    pageCount3--;
                }
                new WriteFileHandler(weakContext, excludedUsersPath + File.separator + userID, null, null, false).run();
                pageCount4++;
                setPageCountViews(weakActivity);
            }
        });
    }

    /**
     * Button for including a user to automation and other views
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be included
     */
    private void includeButton(ImageButton imageButton, final String userID) {
        imageButton.setImageResource(R.drawable.include);
        imageButton.setTag("INCLUDE_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFileHandler(weakContext, null).deleteFileOrPath(excludedUsersPath + File.separator + userID);
                userDataset.remove(userID);
                datasetChanged();
                //Only for F4F exclusions is the newUsersPath needed
                if (new File(appDirectory + File.separator + excludedUsersPath + "_" + newUsersPath + File.separator + userID).exists() && !newUsersPath.contains("NEW")) {
                    new WriteFileHandler(weakContext, newUsersPath + File.separator + userID, null, null, false).run();
                    new DeleteFileHandler(weakContext, excludedUsersPath + "_" + newUsersPath + File.separator + userID).run();
                    pageCount1++;
                }
                if (new File(appDirectory + File.separator + excludedUsersPath + "_" + currentUsersPath + File.separator + userID).exists()) {
                    new WriteFileHandler(weakContext, currentUsersPath + File.separator + userID, null, null, false).run();
                    new DeleteFileHandler(weakContext, excludedUsersPath + "_" + currentUsersPath + File.separator + userID).run();
                    pageCount2++;
                }
                if (new File(appDirectory + File.separator + excludedUsersPath + "_" + unfollowedUsersPath + File.separator + userID).exists()) {
                    new WriteFileHandler(weakContext, unfollowedUsersPath + File.separator + userID, null, null, false).run();
                    new DeleteFileHandler(weakContext, excludedUsersPath + "_" + unfollowedUsersPath + File.separator + userID).run();
                    pageCount3++;
                }
                new DeleteFileHandler(weakContext, excludedUsersPath + File.separator + userID).run();
                pageCount4--;
                setPageCountViews(weakActivity);
            }
        });
    }

    /**
     * Button for following/unfollowing a user
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to be followed/unfollowed
     */
    private void followButton(final ImageButton imageButton, final String userID) {
        if (new File(appDirectory + File.separator + Globals.FOLLOWING_CURRENT_PATH + File.separator + userID).exists() ||
                new File(appDirectory + File.separator + Globals.FOLLOWING_EXCLUDED_PATH  + "_" + Globals.FOLLOWING_CURRENT_PATH + File.separator + userID).exists()) {
            imageButton.setImageResource(R.drawable.unfollow);
        } else {
            imageButton.setImageResource(R.drawable.follow);
        }
        imageButton.setTag("FOLLOW_BUTTON");
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followRequestHandler.networkIsAvailable()) {
                    if (new File(appDirectory + File.separator + Globals.FOLLOWING_CURRENT_PATH + File.separator + userID).exists() ||
                            new File(appDirectory + File.separator + Globals.FOLLOWING_EXCLUDED_PATH + "_" + Globals.FOLLOWING_CURRENT_PATH + File.separator + userID).exists()) {
                        followRequestHandler.setRequestParameters(Request.Method.DELETE, userID, false)
                                .requestFollow();
                        if (usersToDisplay.contains(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH)) {
                            userDataset.remove(userID);
                            datasetChanged();
                            pageCount3--;
                            pageCount2++;
                            setPageCountViews(weakActivity);
                        } else if (usersToDisplay.contains(Globals.F4F_FOLLOW4FOLLOW_PATH)) {
                            userDataset.remove(userID);
                            datasetChanged();
                            pageCount2--;
                            pageCount1++;
                            setPageCountViews(weakActivity);
                        } else {
                            imageButton.setImageResource(R.drawable.follow);
                        }
                    } else {
                        followRequestHandler.setRequestParameters(Request.Method.PUT, userID, false)
                                .requestFollow();
                        if (usersToDisplay.contains(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH)) {
                            userDataset.remove(userID);
                            datasetChanged();
                            pageCount1--;
                            pageCount2++;
                            setPageCountViews(weakActivity);
                        } else {
                            imageButton.setImageResource(R.drawable.unfollow);
                        }
                    }
                } else if (weakContext != null && weakContext.get() != null) {
                    Toast.makeText(weakContext.get(), "Cannot change Following preferences when offline", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Button for activating/deactivating notifications of users
     * @author LethalMaus
     * @param imageButton button view
     * @param userID user to have notifications activated/deactivated
     */
    private void notificationsButton(final ImageButton imageButton, final String userID) {
        if (!new File(appDirectory + File.separator + Globals.FOLLOWING_CURRENT_PATH + File.separator + userID).exists() &&
                !new File(appDirectory + File.separator + Globals.FOLLOWING_EXCLUDED_PATH  + Globals.FOLLOWING_CURRENT_PATH + File.separator + userID).exists()) {
            imageButton.setVisibility(View.INVISIBLE);
        } else {
            try {
                JSONObject user = new JSONObject(new ReadFileHandler(weakContext, Globals.FOLLOWING_PATH + File.separator + userID).readFile());
                if (user.getBoolean("notifications")) {
                    imageButton.setImageResource(R.drawable.deactivate_notifications);
                } else {
                    imageButton.setImageResource(R.drawable.notifications);
                }
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setTag("NOTIFICATIONS_BUTTON");
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (followRequestHandler.networkIsAvailable()) {
                            try {
                                JSONObject user = new JSONObject(new ReadFileHandler(weakContext, Globals.FOLLOWING_PATH + File.separator + userID).readFile());
                                if (user.getBoolean("notifications")) {
                                    followRequestHandler.setRequestParameters(Request.Method.PUT, userID, false)
                                            .requestFollow();
                                    imageButton.setImageResource(R.drawable.notifications);
                                } else {
                                    followRequestHandler.setRequestParameters(Request.Method.PUT, userID, true)
                                            .requestFollow();
                                    imageButton.setImageResource(R.drawable.deactivate_notifications);
                                }
                            } catch (JSONException e) {
                                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
                            }
                        } else if (weakContext != null && weakContext.get() != null) {
                            Toast.makeText(weakContext.get(), "Cannot change Notification preferences when offline", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (JSONException e) {
                new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true).run();
            }
        }
    }

    /**
     * Button for following / unfollowing all users within menu.
     * @author LethalMaus
     * @param followAll if true followAll, else unfollowAll
     */
    private void actionAllButton(final boolean followAll) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            final ImageButton imageButton = weakActivity.get().findViewById(R.id.follow_unfollow_all);
            if (userDataset.size() > 1) {
                final int method;
                if (followAll) {
                    imageButton.setImageResource(R.drawable.follow);
                    method = Request.Method.PUT;
                } else {
                    imageButton.setImageResource(R.drawable.unfollow);
                    method = Request.Method.DELETE;
                }
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (followRequestHandler.networkIsAvailable()) {
                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                            for (String userID : userDataset) {
                                followRequestHandler.setRequestParameters(method, userID, false)
                                        .requestFollow();
                                if (followAll) {
                                    pageCount1--;
                                    pageCount2++;
                                    new DeleteFileHandler(weakContext, Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + userID).run();
                                    new WriteFileHandler(weakContext, Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + userID, null, null, false).run();
                                } else {
                                    pageCount3--;
                                    new DeleteFileHandler(weakContext, Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + userID).run();
                                }
                            }
                            userDataset.clear();
                            datasetChanged();
                            setPageCountViews(weakActivity);
                            imageButton.setVisibility(View.GONE);
                            weakActivity.get().findViewById(R.id.table).setVisibility(View.GONE);
                            weakActivity.get().findViewById(R.id.emptyuserrow).setVisibility(View.VISIBLE);
                            weakActivity.get().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                        }
                    }
                });
            } else {
                imageButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Method for counting files within user directories
     * @author LethalMaus
     */
    private void setPageCounts() {
        pageCount1 = new ReadFileHandler(weakContext, newUsersPath).countFiles();
        pageCount2 = new ReadFileHandler(weakContext, currentUsersPath).countFiles();
        pageCount3 = new ReadFileHandler(weakContext, unfollowedUsersPath).countFiles();
        pageCount4 = new ReadFileHandler(weakContext, excludedUsersPath).countFiles();
    }

    /**
     * Method for setting the page count views
     * @author LethalMaus
     * @param weakActivity weak reference of an activity which contains the views
     */
    private void setPageCountViews(WeakReference<Activity> weakActivity) {
        if (weakActivity != null && weakActivity.get() != null && !weakActivity.get().isDestroyed() && !weakActivity.get().isFinishing()) {
            TextView page1 = weakActivity.get().findViewById(R.id.count1);
            TextView page2 = weakActivity.get().findViewById(R.id.count2);
            TextView page3 = weakActivity.get().findViewById(R.id.count3);
            TextView page4 = weakActivity.get().findViewById(R.id.count4);
            page1.setText(String.valueOf(pageCount1));
            page2.setText(String.valueOf(pageCount2));
            page3.setText(String.valueOf(pageCount3));
            page4.setText(String.valueOf(pageCount4));
        }
    }
}
