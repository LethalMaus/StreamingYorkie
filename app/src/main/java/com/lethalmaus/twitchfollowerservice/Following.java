package com.lethalmaus.twitchfollowerservice;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Following extends FollowService {

    /*TODO
    DRY principle
    load user portion wise if possible (this might need a big refactor)
    */
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> previousFollowing = new ArrayList<>();
    private ArrayList<String> currentFollowing = new ArrayList<>();
    private ArrayList<String> newFollowing = new ArrayList<>();
    private ArrayList<String> unfollowing = new ArrayList<>();
    private ArrayList<String> excludedFollowing = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Following");

        //TODO DRY @override
        ImageButton refreshPage = findViewById(R.id.refresh);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initialExecution();
                    }
                });

        //TODO DRY @override
        final ImageButton newButton = findViewById(R.id.page1);
        newButton.setImageResource(R.drawable.new_button);
        newButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(newButton);
                            showFollowing(newFollowing, excludeButtonMethod, notificationsButtonMethod,  null);
                        }
                    }
                });

        final ImageButton currentButton = findViewById(R.id.page2);
        currentButton.setImageResource(R.drawable.follow);
        currentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(currentButton);
                            showFollowing(currentFollowing, excludeButtonMethod, notificationsButtonMethod, null);
                        }
                    }
                });

        final ImageButton unfollowedButton = findViewById(R.id.page3);
        unfollowedButton.setImageResource(R.drawable.unfollow);
        unfollowedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(unfollowedButton);
                            showFollowing(unfollowing, deleteButtonMethod, excludeButtonMethod,null);
                        }
                    }
                });

        final ImageButton exclusionsButton = findViewById(R.id.page4);
        exclusionsButton.setImageResource(R.drawable.excluded);
        exclusionsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!requestRunning) {
                            highlightButton(exclusionsButton);
                            showFollowing(excludedFollowing, includeButtonMethod, notificationsButtonMethod, null);
                        }
                    }
                });
        highlightButton(newButton);
        usersToDisplay = newFollowing;
        actionButtonMethod1 = excludeButtonMethod;
        actionButtonMethod2 = notificationsButtonMethod;
        actionButtonMethod3 = null;
        initialExecution();
    }

    //TODO DRY
    private void initialExecution() {
        twitchTotal = 0;
        offset = 0;
        previousFollowing.clear();
        previousFollowing.addAll(getUserIDs(globals.FOLLOWING_PATH));
        excludedFollowing.clear();
        excludedFollowing.addAll(getUserIDs(globals.FOLLOWING_EXCLUDED_PATH));
        following.clear();
        requestFollowing();
    }

    private void requestNotifications(String followingID, Boolean notifications) {
        if (!requestRunning) {
            requestRunning = true;
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, "https://api.twitch.tv/kraken/users/" + globals.userID + "/follows/channels/" + followingID + "?notifications=" + notifications, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject userObject = new JSONObject();
                                    userObject.put("display_name", response.getJSONObject("channel").getString("display_name"));
                                    userObject.put("_id", response.getJSONObject("channel").getString("_id"));
                                    userObject.put("logo", response.getJSONObject("channel").getString("logo"));
                                    userObject.put("notifications", response.getBoolean("notifications"));
                                    userObject.put("created_at", response.getString("created_at"));
                                    globals.writeToFile(
                                            globals.FOLLOWING_PATH
                                                    + File.separator
                                                    + userObject.getString("_id"),
                                            userObject.toString(),
                                            getApplicationContext());
                                    organizeUsers(following,
                                            previousFollowing,
                                            newFollowing,
                                            globals.FOLLOWING_NEW_PATH,
                                            currentFollowing,
                                            globals.FOLLOWING_CURRENT_PATH,
                                            unfollowing,
                                            globals.FOLLOWING_UNFOLLOWED_PATH,
                                            excludedFollowing);
                                    showFollowing(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
                                } finally {
                                    requestRunning = false;
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not change notifications preference", Toast.LENGTH_SHORT).show();
                        requestRunning = false;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/vnd.twitchtv.v5+json");
                        headers.put("Client-ID", globals.CLIENTID);
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "OAuth " + globals.token);
                        return headers;
                    }
                };
                jsObjRequest.setTag("NOTIFICATIONS");
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                Toast.makeText(getApplicationContext(), "Cannot change notifications preference when offline", Toast.LENGTH_SHORT).show();
                requestRunning = false;
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    //TODO DRY
    private void requestFollowing() {
        if (!requestRunning) {
            requestRunning = true;
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/users/" + globals.userID + "/follows/channels" + "?limit=" + limit + "&direction=asc&offset=" + offset, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    offset += limit;
                                    if (twitchTotal == 0) {
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
                                                userObject.toString(),
                                                getApplicationContext());
                                        following.add(userObject.getString("_id"));
                                    }
                                    if (response.getJSONArray("follows").length() > 0) {
                                        requestRunning = false;
                                        requestFollowing();
                                    } else {
                                        if (twitchTotal != following.size()) {
                                            Toast.makeText(getApplicationContext(), "Twitch Data for 'Following' is out of sync. Total should be '" + twitchTotal
                                                    + "' but is only giving '" + following.size() + "'", Toast.LENGTH_SHORT).show();
                                        }
                                        organizeUsers(following,
                                                previousFollowing,
                                                newFollowing,
                                                globals.FOLLOWING_NEW_PATH,
                                                currentFollowing,
                                                globals.FOLLOWING_CURRENT_PATH,
                                                unfollowing,
                                                globals.FOLLOWING_UNFOLLOWED_PATH,
                                                excludedFollowing);
                                        showFollowing(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
                                } finally {
                                    requestRunning = false;
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error requesting Following", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved following", Toast.LENGTH_SHORT).show();
                        following.addAll(getUserIDs(globals.FOLLOWING_PATH));
                        organizeUsers(following,
                                previousFollowing,
                                newFollowing,
                                globals.FOLLOWING_NEW_PATH,
                                currentFollowing,
                                globals.FOLLOWING_CURRENT_PATH,
                                unfollowing,
                                globals.FOLLOWING_UNFOLLOWED_PATH,
                                excludedFollowing);
                        showFollowing(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
                        requestRunning = false;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/vnd.twitchtv.v5+json");
                        headers.put("Client-ID", globals.CLIENTID);
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                jsObjRequest.setTag("FOLLOWING");
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved following", Toast.LENGTH_SHORT).show();
                following.addAll(getUserIDs(globals.FOLLOWING_PATH));
                organizeUsers(following,
                        previousFollowing,
                        newFollowing,
                        globals.FOLLOWING_NEW_PATH,
                        currentFollowing,
                        globals.FOLLOWING_CURRENT_PATH,
                        unfollowing,
                        globals.FOLLOWING_UNFOLLOWED_PATH,
                        excludedFollowing);
                showFollowing(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
                requestRunning = false;
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    //TODO DRY
    public void showFollowing(ArrayList<String> users, Method actionButtonMethod1, Method actionButtonMethod2, Method actionButtonMethod3) {
        usersToDisplay = users;
        this.actionButtonMethod1 = actionButtonMethod1;
        this.actionButtonMethod2 = actionButtonMethod2;
        this.actionButtonMethod3 = actionButtonMethod3;
        ImageButton actionButton1 = null;
        ImageButton actionButton2 = null;
        ImageButton actionButton3 = null;
        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout layout = findViewById(R.id.table);
        layout.removeAllViews();
        if (users.size() > 0) {
            for (int i = users.size() - 1; i >= 0; i--) {
                try {
                    final JSONObject user = new JSONObject(globals.readFromFile(
                            getApplicationContext().getFilesDir() + globals.FOLLOWING_PATH + File.separator + users.get(i),
                            getApplicationContext()));
                    userNotifications = user.getBoolean("notifications");
                    try {
                        if (actionButtonMethod1 != null) {
                            actionButton1 = (ImageButton) actionButtonMethod1.invoke(this);
                        }
                        if (actionButtonMethod2 != null) {
                            actionButton2 = (ImageButton) actionButtonMethod2.invoke(this);
                        }
                        if (actionButtonMethod3 != null) {
                            actionButton3 = (ImageButton) actionButtonMethod3.invoke(this);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error calling button method", Toast.LENGTH_SHORT).show();
                    }
                    addButtonListener(actionButton1, layout, user);
                    addButtonListener(actionButton2, layout, user);
                    addButtonListener(actionButton3, layout, user);
                    ConstraintLayout tableRow = viewDefinitions.userTableRow(
                            getApplicationContext(),
                            user.getString("logo"),
                            user.getString("display_name"),
                            actionButton1,
                            actionButton2,
                            actionButton3);
                    layout.addView(tableRow);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "An error occurred displaying Users", Toast.LENGTH_SHORT).show();
                }
            }
        }  else {
            final TextView textView = new TextView(getApplicationContext());
            textView.setGravity(Gravity.CENTER);
            textView.setText("Nothing to show");
            layout.addView(textView);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    protected void addButtonListener(ImageButton imageButton, final LinearLayout layout, final JSONObject user) {
        if (imageButton != null) {
            switch (imageButton.getTag().toString()) {
                case "DELETE_BUTTON":
                    imageButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteButtonAction(layout, v, user);
                                }
                            });
                    break;
                case "EXCLUDE_BUTTON":
                    imageButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    excludeButtonAction(layout, v, user);
                                }
                            });
                    break;
                case "INCLUDE_BUTTON":
                    imageButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    includeButtonAction(layout, v, user);
                                }
                            });
                    break;
                case "NOTIFICATIONS_BUTTON":
                    imageButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    notificationsButtonAction(user);
                                }
                            });
                    break;
            }
        }
    }

    //TODO DRY
    protected void deleteButtonAction(LinearLayout layout, View v, JSONObject user) {
        try{
            globals.deleteFileOrPath(globals.FOLLOWING_UNFOLLOWED_PATH + File.separator + user.getString("_id"), getApplicationContext());
            globals.deleteFileOrPath(globals.FOLLOWING_PATH + File.separator + user.getString("_id"), getApplicationContext());
            unfollowing.remove(user.getString("_id"));
            organizeUsers(following,
                    previousFollowing,
                    newFollowing,
                    globals.FOLLOWING_NEW_PATH,
                    currentFollowing,
                    globals.FOLLOWING_CURRENT_PATH,
                    unfollowing,
                    globals.FOLLOWING_UNFOLLOWED_PATH,
                    excludedFollowing);
            layout.removeView((View) v.getParent());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred deleting User", Toast.LENGTH_SHORT).show();
        }
    }
    protected void excludeButtonAction(LinearLayout layout, View v, JSONObject user) {
        try {
            globals.writeToFile(
                    globals.FOLLOWING_EXCLUDED_PATH
                            + File.separator
                            + user.getString("_id"),
                    null,
                    getApplicationContext());
            excludedFollowing.add(user.getString("_id"));
            organizeUsers(following,
                    previousFollowing,
                    newFollowing,
                    globals.FOLLOWING_NEW_PATH,
                    currentFollowing,
                    globals.FOLLOWING_CURRENT_PATH,
                    unfollowing,
                    globals.FOLLOWING_UNFOLLOWED_PATH,
                    excludedFollowing);
            layout.removeView((View) v.getParent());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred excluding User", Toast.LENGTH_SHORT).show();
        }
    }

    protected void includeButtonAction(LinearLayout layout, View v, JSONObject user) {
        try {
            globals.deleteFileOrPath(
                    globals.FOLLOWING_EXCLUDED_PATH
                            + File.separator
                            + user.getString("_id"),
                    getApplicationContext());
            excludedFollowing.remove(user.getString("_id"));
            organizeUsers(following,
                    previousFollowing,
                    newFollowing,
                    globals.FOLLOWING_NEW_PATH,
                    currentFollowing,
                    globals.FOLLOWING_CURRENT_PATH,
                    unfollowing,
                    globals.FOLLOWING_UNFOLLOWED_PATH,
                    excludedFollowing);
            layout.removeView((View) v.getParent());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred including User", Toast.LENGTH_SHORT).show();
        }
    }

    protected void notificationsButtonAction(JSONObject user) {
        try {
            if (user.getBoolean("notifications")) {
                requestNotifications(user.getString("_id"), false);
            } else {
                requestNotifications(user.getString("_id"), true);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred changing Users notifications preference", Toast.LENGTH_SHORT).show();
        }
    }
}