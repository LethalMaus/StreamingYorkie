package com.lethalmaus.streaming_yorkie.file;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.adapter.UserAdapter;
import com.lethalmaus.streaming_yorkie.request.VolleySingleton;

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
    private boolean commonFolders;

    /**
     * Constructor is kept small to keep clarity.
     * setPaths & setDisplayPreferences need to be called afterwards.
     * @author LethalMaus
     * @param weakActivity weak reference of the activity which called this constructor
     * @param weakContext weak reference of the context which called this constructor
     * @param displayUsers bool if users need to be displayed or not
     * @param commonFolders bool if folders are for Followers/Following or F4F
     */
    public OrganizeFileHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext, WeakReference<RecyclerView> recyclerView, boolean displayUsers, boolean commonFolders) {
        this.weakActivity = weakActivity;
        this.weakContext = weakContext;
        this.recyclerView = recyclerView;
        this.appDirectory = weakContext.get().getFilesDir().toString();
        this.displayUsers = displayUsers;
        this.commonFolders = commonFolders;
    }

    /**
     * Sets the paths in which the files should be organized to. Keeps the constructor small.
     * Returns the class instance for method building
     * @param currentUsersPath path of current users
     * @param newUsersPath path of new users
     * @param unfollowedUsersPath path of unfollowed users
     * @param excludedUsersPath path of excluded users
     * @param requestPath path of previous users
     * @param usersPath path of user objects
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
    private void organizeFolders() {

        //each time it is called, the new folder is deleted
        boolean newUsersDirectoryNeedsRenewal = true;
        //List of the requested users
        ArrayList<String> requestedUsers = new ReadFileHandler(weakContext, requestPath).readFileNames();
        //List of the previously current users
        ArrayList<String> currentUsers = new ReadFileHandler(weakContext, currentUsersPath).readFileNames();

        if (!requestedUsers.isEmpty()) {
            //Iterate to find if a user has unfollowed
            for (int i = 0; i < currentUsers.size(); i++) {
                if (!requestedUsers.contains(currentUsers.get(i)) &&
                        !new File(appDirectory + File.separator + excludedUsersPath + File.separator + currentUsers.get(i)).exists()) {
                    new WriteFileHandler(weakContext, unfollowedUsersPath + File.separator + currentUsers.get(i), null, null, false).run();
                    new DeleteFileHandler(weakContext, currentUsersPath + File.separator + currentUsers.get(i)).run();
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
                    new WriteFileHandler(weakContext, newUsersPath + File.separator + requestedUsers.get(i), null, null, false).run();
                    new WriteFileHandler(weakContext, currentUsersPath + File.separator + requestedUsers.get(i), null, null, false).run();
                    if (new File(appDirectory + File.separator + unfollowedUsersPath + File.separator + requestedUsers.get(i)).exists()) {
                        new DeleteFileHandler(weakContext, unfollowedUsersPath + File.separator + requestedUsers.get(i)).run();
                    }
                }
            }
            //Deletes the requested users that are no longer needed
            new DeleteFileHandler(weakContext, requestPath).run();
        }
        if (!commonFolders) {
            organizeF4FFolders();
        }
    }

    /**
     * Organizes Folder specific to F4F
     * @author LethalMaus
     */
    private void organizeF4FFolders() {
        currentUsersPath = Globals.F4F_FOLLOW4FOLLOW_PATH;
        newUsersPath = Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH;
        unfollowedUsersPath = Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH;
        excludedUsersPath = Globals.F4F_EXCLUDED_PATH;
        //Previous users are not needed as they are combined from common folders
        new DeleteFileHandler(weakContext, null).deleteFileOrPath(newUsersPath);
        new DeleteFileHandler(weakContext, null).deleteFileOrPath(currentUsersPath);
        new DeleteFileHandler(weakContext, null).deleteFileOrPath(unfollowedUsersPath);

        //List of excluded users
        ArrayList<String> excluded = new ReadFileHandler(weakContext, excludedUsersPath).readFileNames();
        //List of current followers from common folders
        ArrayList<String> followers = new ReadFileHandler(weakContext, Globals.FOLLOWERS_CURRENT_PATH).readFileNames();
        //Adds all excluded from common folders to allow users to exclude again for preference
        followers.addAll(new ReadFileHandler(weakContext, Globals.FOLLOWERS_EXCLUDED_PATH + Globals.FOLLOWERS_CURRENT_PATH).readFileNames());
        //Removes F4F excluded
        followers.removeAll(excluded);
        //List of current following from common folders
        ArrayList<String> following = new ReadFileHandler(weakContext, Globals.FOLLOWING_CURRENT_PATH).readFileNames();
        //Adds all excluded from common folders to allow users to exclude again for preference
        following.addAll(new ReadFileHandler(weakContext, Globals.FOLLOWING_EXCLUDED_PATH + Globals.FOLLOWING_CURRENT_PATH).readFileNames());
        //Removes F4F excluded
        following.removeAll(excluded);

        for (String user : following) {
            //if user is in both follower & following, they are added to F4F(current), else they are added to Followers, Not-Following-Back(unfollowed)
            if (followers.contains(user)) {
                new WriteFileHandler(weakContext, currentUsersPath + File.separator + user, null, null, false).run();
            } else {
                new WriteFileHandler(weakContext, unfollowedUsersPath + File.separator + user, null, null, false).run();
            }
        }
        for (String user : followers) {
            //if user is in followers but not following, then they are added to Followers, Not-Being-Followed-Back(new)
            if (!following.contains(user)) {
                new WriteFileHandler(weakContext, newUsersPath + File.separator + user, null, null, false).run();
            }
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        if (displayUsers && recyclerView != null) {
            recyclerView.get().setAdapter(new UserAdapter(weakActivity, weakContext)
                    .setPaths(newUsersPath, currentUsersPath, unfollowedUsersPath, excludedUsersPath, usersPath)
                    .setDisplayPreferences(usersToDisplay, actionButtonType1, actionButtonType2, actionButtonType3));
        }
    }
}
