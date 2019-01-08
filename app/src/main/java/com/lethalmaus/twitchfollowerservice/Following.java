package com.lethalmaus.twitchfollowerservice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Following extends FollowService {

    /*TODO
    load user portion wise if possible (this might need a big refactor)
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Following");

        usersPath = globals.FOLLOWING_PATH;
        currentUsersPath = globals.FOLLOWING_CURRENT_PATH;
        newUsersPath = globals.FOLLOWING_NEW_PATH;
        unfollowedUsersPath = globals.FOLLOWING_UNFOLLOWED_PATH;
        excludedUsersPath = globals.FOLLOWING_EXCLUDED_PATH;

        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(newButton);
                            displayUsers(newUsers, excludeButtonMethod, notificationsButtonMethod,  followButtonMethod);
                        }
                    }
                });

        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(currentButton);
                            displayUsers(currentUsers, excludeButtonMethod, notificationsButtonMethod, followButtonMethod);
                        }
                    }
                });

        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(unfollowedButton);
                            displayUsers(unfollowedUsers, deleteButtonMethod, excludeButtonMethod, followButtonMethod);
                        }
                    }
                });

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(exclusionsButton);
                            displayUsers(excludedUsers, includeButtonMethod, notificationsButtonMethod, followButtonMethod);
                        }
                    }
                });
        highlightButton(newButton);
        usersToDisplay = newUsers;
        actionButtonMethod1 = excludeButtonMethod;
        actionButtonMethod2 = notificationsButtonMethod;
        actionButtonMethod3 = followButtonMethod;
        initialExecution();
    }

    @Override
    protected void initialExecution() {
        initialPreparation();
        requestFollowing(false);
    }

    @Override
    protected void requestFollowingResponseHandler(JSONObject response, boolean recursiveRequest) {
        try {
            offset += globals.REQUEST_LIMIT;
            if (!recursiveRequest) {
                twitchTotal = response.getInt("_total");
            }
            for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                JSONObject userObject = new JSONObject();
                userObject.put("display_name", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("display_name"));
                userObject.put("_id", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("_id"));
                userObject.put("logo", response.getJSONArray("follows").getJSONObject(i).getJSONObject("channel").getString("logo"));
                userObject.put("notifications", response.getJSONArray("follows").getJSONObject(i).getBoolean("notifications"));
                userObject.put("created_at", response.getJSONArray("follows").getJSONObject(i).getString("created_at"));
                globals.writeToFile(
                        globals.FOLLOWING_PATH
                                + File.separator
                                + userObject.getString("_id"),
                        userObject.toString());
                users.add(userObject.getString("_id"));
            }
            if (response.getJSONArray("follows").length() > 0) {
                requestFollowing(true);
            } else {
                if (twitchTotal != users.size()) {
                    Toast.makeText(getApplicationContext(), "Twitch Data for 'Following' is out of sync. Total should be '" + twitchTotal
                            + "' but is only giving '" + users.size() + "'", Toast.LENGTH_SHORT).show();
                }
                organizeUsers();
                displayUsers(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
        } finally {
            requestRunning = false;
        }
    }
}