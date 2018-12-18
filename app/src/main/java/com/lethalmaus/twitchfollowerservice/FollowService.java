package com.lethalmaus.twitchfollowerservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class FollowService extends AppCompatActivity {

    protected Globals globals;
    protected ViewDefinitions viewDefinitions = new ViewDefinitions();

    protected int limit = 25;
    protected int offset;
    protected int twitchTotal;

    protected boolean requestRunning;
    protected ProgressBar progressBar;

    protected Method deleteButtonMethod;
    protected Method excludeButtonMethod;
    protected Method includeButtonMethod;
    protected Method notificationsButtonMethod;
    protected Boolean userNotifications;

    protected ArrayList<String> usersToDisplay;
    protected Method actionButtonMethod1;
    protected Method actionButtonMethod2;
    protected Method actionButtonMethod3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers);

        globals = new Globals(getApplicationContext());

        requestRunning = false;
        progressBar = findViewById(R.id.requestRunning);

        showUser();

        try {
            excludeButtonMethod = FollowService.class.getDeclaredMethod("excludeButton");
            includeButtonMethod = FollowService.class.getDeclaredMethod("includeButton");
            deleteButtonMethod = FollowService.class.getDeclaredMethod("deleteButton");
            notificationsButtonMethod = FollowService.class.getDeclaredMethod("notificationsButton");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error occurred loading button methods", Toast.LENGTH_SHORT).show();
        }
    }

    protected ArrayList<String> getUserIDs(String path) {
        File pathToUsers = new File(getApplicationContext().getFilesDir() + path);
        String[] pathToUsersArray = pathToUsers.list();
        if (pathToUsersArray == null || pathToUsersArray.length <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(pathToUsersArray));
    }

    protected void saveUserIDs(String path, ArrayList<String> usersToSave) {
        for (int i = 0; i < usersToSave.size(); i++) {
            globals.writeToFile(path + File.separator + usersToSave.get(i), null, getApplicationContext());
        }
    }

    protected void showUser() {
        ImageView user_Logo = findViewById(R.id.user_Logo);
        Glide.with(getApplicationContext()).load(globals.userLogo).into(user_Logo);
        TextView user_Username = findViewById(R.id.user_Username);
        user_Username.setText(globals.username);
    }

    protected void organizeUsers(ArrayList<String> users, ArrayList<String> previousUsers, ArrayList<String> newUsers, String newUsersPath, ArrayList<String> currentUsers, String currentUsersPath, ArrayList<String> unfollowedUsers, String unfollowedUsersPath, ArrayList<String> excludedUsers) {
        unfollowedUsers.clear();
        unfollowedUsers.addAll(getUserIDs(unfollowedUsersPath));
        newUsers.clear();

        for (int i = 0; i < previousUsers.size(); i++) {
            if (!users.contains(previousUsers.get(i)) && !excludedUsers.contains(previousUsers.get(i)) && !unfollowedUsers.contains(previousUsers.get(i))) {
                unfollowedUsers.add(previousUsers.get(i));
            }
        }
        for (int i = 0; i < users.size(); i++) {
            if (!previousUsers.contains(users.get(i)) && !excludedUsers.contains(users.get(i)) && !unfollowedUsers.contains(users.get(i))) {
                newUsers.add(users.get(i));
                currentUsers.add(users.get(i));
            } else if (!excludedUsers.contains(users.get(i)) && !unfollowedUsers.contains(users.get(i))) {
                currentUsers.add(users.get(i));
            }
        }

        if (newUsers.size() > 0) {
            globals.deleteFileOrPath(newUsersPath, getApplicationContext());
            saveUserIDs(newUsersPath, newUsers);
        } else {
            newUsers.clear();
            newUsers.addAll(getUserIDs(newUsersPath));
        }

        if (currentUsers.size() > 0) {
            globals.deleteFileOrPath(currentUsersPath, getApplicationContext());
            saveUserIDs(currentUsersPath, currentUsers);
        }
        currentUsers.clear();
        currentUsers.addAll(getUserIDs(currentUsersPath));

        if (unfollowedUsers.size() > 0) {
            saveUserIDs(unfollowedUsersPath, unfollowedUsers);
        }

        newUsers.removeAll(excludedUsers);
        unfollowedUsers.removeAll(excludedUsers);
        currentUsers.removeAll(excludedUsers);

        setPageCounts(
                newUsers.size(),
                currentUsers.size(),
                unfollowedUsers.size(),
                excludedUsers.size()
        );
    }

    protected void highlightButton(ImageButton imageButton) {
        findViewById(R.id.page1).setBackgroundResource(0);
        findViewById(R.id.page2).setBackgroundResource(0);
        findViewById(R.id.page3).setBackgroundResource(0);
        findViewById(R.id.page4).setBackgroundResource(0);
        imageButton.setBackgroundResource(R.drawable.highlight_page_button);
    }

    protected void setPageCounts(int count1, int count2, int count3, int count4) {
        TextView page1Count = findViewById(R.id.count1);
        page1Count.setText(String.valueOf(count1));
        TextView page2Count = findViewById(R.id.count2);
        page2Count.setText(String.valueOf(count2));
        TextView page3Count = findViewById(R.id.count3);
        page3Count.setText(String.valueOf(count3));
        TextView page4Count = findViewById(R.id.count4);
        page4Count.setText(String.valueOf(count4));
    }

    protected ImageButton deleteButton() {
        ImageButton imageButton = new ImageButton(getApplicationContext());
        imageButton.setImageResource(R.drawable.delete);
        imageButton.setTag("DELETE_BUTTON");
        return imageButton;
    }

    protected ImageButton excludeButton() {
        ImageButton imageButton = new ImageButton(getApplicationContext());
        imageButton.setImageResource(R.drawable.excluded);
        imageButton.setTag("EXCLUDE_BUTTON");
        return imageButton;
    }

    protected ImageButton includeButton() {
        ImageButton imageButton = new ImageButton(getApplicationContext());
        imageButton.setImageResource(R.drawable.include);
        imageButton.setTag("INCLUDE_BUTTON");
        return imageButton;
    }

    protected ImageButton notificationsButton() {
        ImageButton imageButton = new ImageButton(getApplicationContext());
        if (userNotifications) {
            imageButton.setImageResource(R.drawable.deactivate_notifications);
        } else {
            imageButton.setImageResource(R.drawable.notifications);
        }
        imageButton.setTag("NOTIFICATIONS_BUTTON");
        return imageButton;
    }
}
