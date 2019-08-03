package com.lethalmaus.streaming_yorkie.file;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Async Task to organize Followers/Following/F4F to their correct path (eg. new, current, ...)
 * @author LethalMaus
 */
public class OrganizeFileHandler extends AsyncTask<Void, Void, Void> {

    //All activities & context are weak referenced to avoid memory leaks
    private WeakReference<Activity> weakActivity;
    protected WeakReference<Context> weakContext;
    private WeakReference<RecyclerView> recyclerView;
    protected String appDirectory;

    //Paths required to organize files
    private String currentUsersPath;
    private String newUsersPath;
    private String unfollowedUsersPath;
    private String excludedUsersPath;
    private String requestPath;
    private String usersPath;

    //Display preferences
    private String usersToDisplay;
    private String actionButtonType1;
    private String actionButtonType2;
    private String actionButtonType3;
    private boolean displayUsers;

    /**
     * Constructor is kept small to keep clarity.
     * setPaths & setDisplayPreferences need to be called afterwards.
     * @author LethalMaus
     * @param weakActivity weak reference of the activity
     * @param weakContext weak reference of the context
     * @param recyclerView weak reference of the recycler view
     * @param displayUsers bool if users need to be displayed or not
     */
    public OrganizeFileHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView, boolean displayUsers) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.recyclerView = recyclerView;
        if (weakContext != null && weakContext.get() != null) {
            this.appDirectory = weakContext.get().getFilesDir().toString();
        }
        this.displayUsers = displayUsers;
    }

    /**
     * Sets the paths in which the files should be organized to. Keeps the constructor small.
     * Returns the class instance for method building
     * @param currentUsersPath path of current users
     * @param newUsersPath path of new users
     * @param unfollowedUsersPath path of unfollowed users
     * @param excludedUsersPath path of excluded users
     * @param requestPath path of previous users
     * @param usersPath path of channel objects
     * @return class instance for method building
     */
    public OrganizeFileHandler setPaths(String currentUsersPath, String newUsersPath, String unfollowedUsersPath, String excludedUsersPath, String requestPath, String usersPath) {
        this.currentUsersPath = currentUsersPath;
        this.newUsersPath = newUsersPath;
        this.unfollowedUsersPath = unfollowedUsersPath;
        this.excludedUsersPath = excludedUsersPath;
        this.requestPath = requestPath;
        this.usersPath = usersPath;
        return this;
    }

    /**
     * Sets the display preferences such as which users to display & its action buttons
     * @param usersToDisplay users to display
     * @param actionButtonType1 action button for each individual Follower/Following, can be null
     * @param actionButtonType2 action button for each individual Follower/Following, can be null
     * @param actionButtonType3 action button for each individual Follower/Following, can be null
     * @return class instance for method building
     */
    public OrganizeFileHandler setDisplayPreferences(String usersToDisplay, String actionButtonType1, String actionButtonType2, String actionButtonType3) {
        this.usersToDisplay = usersToDisplay;
        this.actionButtonType1 = actionButtonType1;
        this.actionButtonType2 = actionButtonType2;
        this.actionButtonType3 = actionButtonType3;
        return this;
    }

    @Override
    protected Void doInBackground(Void... params) {
        organizeFolders();
        return null;
    }

    /**
     * Organizes the folders specific to Follower & Following
     * @author LethalMaus
     */
    void organizeFolders() {

        //each time it is called, the new folder is deleted
        boolean newUsersDirectoryNeedsRenewal = true;
        //List of the requested users
        ArrayList<String> requestedUsers = new ReadFileHandler(weakContext, requestPath).readFileNames();
        //List of the previously current users
        ArrayList<String> currentUsers = new ReadFileHandler(weakContext, currentUsersPath).readFileNames();

        //FIXME flag files need a different solution
        //if (!requestedUsers.isEmpty() && !new File(appDirectory + File.separator + Globals.FLAG_FOLLOWERS_REQUEST_RUNNING).exists() && !new File(appDirectory + File.separator + Globals.FLAG_FOLLOWING_REQUEST_RUNNING).exists()) {
        if (!requestedUsers.isEmpty()) {
            //Iterate to find if a channel has unfollowed
            for (int i = 0; i < currentUsers.size(); i++) {
                if (!requestedUsers.contains(currentUsers.get(i)) &&
                        !new File(appDirectory + File.separator + excludedUsersPath + File.separator + currentUsers.get(i)).exists()) {
                    new WriteFileHandler(weakContext, unfollowedUsersPath + File.separator + currentUsers.get(i), null, null, false).writeToFileOrPath();
                    new DeleteFileHandler(weakContext, null).deleteFileOrPath(currentUsersPath + File.separator + currentUsers.get(i));
                }
            }
            //Iterate to find new users and add them
            for (int i = 0; i < requestedUsers.size(); i++) {
                if (!currentUsers.contains(requestedUsers.get(i)) &&
                        !new File(appDirectory + File.separator + excludedUsersPath + File.separator + requestedUsers.get(i)).exists()) {
                    if (newUsersDirectoryNeedsRenewal) {
                        newUsersDirectoryNeedsRenewal = false;
                        new DeleteFileHandler(weakContext, null).deleteFileOrPath(newUsersPath);
                    }
                    new WriteFileHandler(weakContext, newUsersPath + File.separator + requestedUsers.get(i), null, null, false).writeToFileOrPath();
                    new WriteFileHandler(weakContext, currentUsersPath + File.separator + requestedUsers.get(i), null, null, false).writeToFileOrPath();
                    if (new File(appDirectory + File.separator + unfollowedUsersPath + File.separator + requestedUsers.get(i)).exists()) {
                        new DeleteFileHandler(weakContext, null).deleteFileOrPath(unfollowedUsersPath + File.separator + requestedUsers.get(i));
                    }
                }
            }
            //Deletes the requested users that are no longer needed
            new DeleteFileHandler(weakContext, requestPath).run();
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        if (displayUsers && recyclerView != null && recyclerView.get() != null) {
            recyclerView.get().setAdapter(new UserAdapter(weakActivity, weakContext)
                    .setPaths(newUsersPath, currentUsersPath, unfollowedUsersPath, excludedUsersPath, usersPath)
                    .setDisplayPreferences(usersToDisplay, actionButtonType1, actionButtonType2, actionButtonType3));
        }
    }
}
