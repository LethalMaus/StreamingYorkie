package com.lethalmaus.twitchfollowerservice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Followers extends FollowService {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usersPath = globals.FOLLOWERS_PATH;
        currentUsersPath = globals.FOLLOWERS_CURRENT_PATH;
        newUsersPath = globals.FOLLOWERS_NEW_PATH;
        unfollowedUsersPath = globals.FOLLOWERS_UNFOLLOWED_PATH;
        excludedUsersPath = globals.FOLLOWERS_EXCLUDED_PATH;

        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(newButton);
                            setSubtitle("New");
                            displayUsers(newUsers, excludeButtonMethod, followButtonMethod,  null);
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
                            setSubtitle("Current");
                            displayUsers(currentUsers, excludeButtonMethod, followButtonMethod, null);
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
                                setSubtitle("Unfollowed");
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
                            setSubtitle("Excluded");
                            displayUsers(excludedUsers, includeButtonMethod, followButtonMethod, null);
                        }
                    }
                });
        highlightButton(newButton);
        setSubtitle("New");
        usersToDisplay = newUsers;
        actionButtonMethod1 = excludeButtonMethod;
        actionButtonMethod2 = followButtonMethod;
        actionButtonMethod3 = null;
        initialExecution();
    }

    @Override
    protected void initialExecution() {
        initialPreparation();
        requestFollowers(false);
    }

    @Override
    protected void requestFollowersResponseHandler(JSONObject response, boolean recursiveRequest) {
        try {
            offset += globals.REQUEST_LIMIT;
            if (!recursiveRequest) {
                twitchTotal = response.getInt("_total");
            }
            for (int i = 0; i < response.getJSONArray("follows").length(); i++) {
                JSONObject userObject = new JSONObject();
                userObject.put("display_name", response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("display_name"));
                userObject.put("_id", response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("_id"));
                userObject.put("logo", response.getJSONArray("follows").getJSONObject(i).getJSONObject("user").getString("logo"));
                userObject.put("created_at", response.getJSONArray("follows").getJSONObject(i).getString("created_at"));
                globals.writeToFile(
                        globals.FOLLOWERS_PATH
                                + File.separator
                                + userObject.getString("_id"),
                        userObject.toString());
                users.add(userObject.getString("_id"));
            }
            if (response.getJSONArray("follows").length() > 0) {
                requestFollowers(true);
            } else {
                if (twitchTotal != users.size()) {
                    Toast.makeText(getApplicationContext(), "Twitch Data for 'Followers' is out of sync. Total should be '" + twitchTotal
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