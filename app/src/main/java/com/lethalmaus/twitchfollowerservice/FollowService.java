package com.lethalmaus.twitchfollowerservice;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class FollowService extends AppCompatActivity {

    protected Globals globals;
    protected ViewDefinitions viewDefinitions;

    protected int offset;
    protected int twitchTotal;

    protected boolean requestRunning;
    protected ProgressBar progressBar;

    protected ArrayList<String> users = new ArrayList<>();
    protected ArrayList<String> previousUsers = new ArrayList<>();
    protected ArrayList<String> currentUsers = new ArrayList<>();
    protected ArrayList<String> newUsers = new ArrayList<>();
    protected ArrayList<String> unfollowedUsers = new ArrayList<>();
    protected ArrayList<String> excludedUsers = new ArrayList<>();

    String usersPath;
    String currentUsersPath;
    String newUsersPath;
    String unfollowedUsersPath;
    String excludedUsersPath;
    private long mLastClickTime = 0;

    protected Method deleteButtonMethod;
    protected Method excludeButtonMethod;
    protected Method includeButtonMethod;
    protected Method followButtonMethod;
    protected Method notificationsButtonMethod;

    protected Boolean userNotifications;
    protected Boolean userFollows;

    protected ArrayList<String> usersToDisplay;
    protected Method actionButtonMethod1;
    protected Method actionButtonMethod2;
    protected Method actionButtonMethod3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_service);

        globals = new Globals(FollowService.this, getApplicationContext());
        viewDefinitions = new ViewDefinitions();
        requestRunning = false;
        progressBar = findViewById(R.id.requestRunning);
        progressBar.setVisibility(View.INVISIBLE);
        globals.showUser();

        ImageButton refreshPage = findViewById(R.id.refresh);
        refreshPage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime > 5000){
                            mLastClickTime = SystemClock.elapsedRealtime();
                            initialPreparation();
                            initialExecution();
                        }
                    }
                });

        try {
            excludeButtonMethod = FollowService.class.getDeclaredMethod("excludeButton");
            includeButtonMethod = FollowService.class.getDeclaredMethod("includeButton");
            deleteButtonMethod = FollowService.class.getDeclaredMethod("deleteButton");
            followButtonMethod = FollowService.class.getDeclaredMethod("followButton");
            notificationsButtonMethod = FollowService.class.getDeclaredMethod("notificationsButton");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error occurred loading button methods", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        cancelRequests();
    }
    @Override
    protected void onStop() {
        super.onStop();
        cancelRequests();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelRequests();
    }
    protected void cancelRequests() {
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("FOLLOWERS");
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("FOLLOWING");
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("FOLLOW");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return globals.onOptionsItemsSelected(item);
    }

    protected void setSubtitle(String subtitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    protected void initialPreparation() {
        twitchTotal = 0;
        offset = 0;
        previousUsers.removeAll(previousUsers);
        previousUsers.addAll(getUserIDs(usersPath));
        excludedUsers.removeAll(excludedUsers);
        excludedUsers.addAll(getUserIDs(excludedUsersPath));
        users.removeAll(users);
    }
    protected void initialExecution() {}

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
            globals.writeToFile(path + File.separator + usersToSave.get(i), null);
        }
    }

    protected void organizeUsers() {
        unfollowedUsers.removeAll(unfollowedUsers);
        unfollowedUsers.addAll(getUserIDs(unfollowedUsersPath));
        currentUsers.removeAll(currentUsers);
        newUsers.removeAll(newUsers);

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
            globals.deleteFileOrPath(newUsersPath);
            saveUserIDs(newUsersPath, newUsers);
        } else {
            newUsers.addAll(getUserIDs(newUsersPath));
        }

        if (currentUsers.size() > 0) {
            globals.deleteFileOrPath(currentUsersPath);
            saveUserIDs(currentUsersPath, currentUsers);
        }

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

    protected void requestFollowers(final boolean recursiveRequest) {
        if (!requestRunning || recursiveRequest) {
            requestRunning = true;
            mLastClickTime = SystemClock.elapsedRealtime();
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channels/" + globals.userID + "/follows" + "?limit=" + globals.REQUEST_LIMIT + "&direction=asc&offset=" + offset, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                requestFollowersResponseHandler(response, recursiveRequest);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestErrorHandler();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        return globals.getHeaders();
                    }
                };
                jsObjRequest.setTag("FOLLOWERS");
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                requestOfflineHandler();
            }
        }
    }
    protected void requestFollowersResponseHandler(JSONObject response, boolean recursiveRequest) {}

    protected void requestFollowing(final boolean recursiveRequest) {
        if (!requestRunning || recursiveRequest) {
            requestRunning = true;
            mLastClickTime = SystemClock.elapsedRealtime();
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/users/" + globals.userID + "/follows/channels" + "?limit=" + globals.REQUEST_LIMIT + "&direction=asc&offset=" + offset, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                requestFollowingResponseHandler(response, recursiveRequest);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestErrorHandler();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        return globals.getHeaders();
                    }
                };
                jsObjRequest.setTag("FOLLOWING");
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                requestOfflineHandler();
            }
        }
    }
    protected void requestFollowingResponseHandler(JSONObject response, boolean recursiveRequest) {}

    protected void requestErrorHandler() {
        Toast.makeText(getApplicationContext(), "Error requesting Users", Toast.LENGTH_SHORT).show();
        requestOfflineHandler();
    }

    protected void requestOfflineHandler() {
        Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved Users", Toast.LENGTH_SHORT).show();
        users.addAll(getUserIDs(usersPath));
        organizeUsers();
        displayUsers(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
        requestRunning = false;
    }

    protected void requestFollow(final int requestMethod, final String followingID, Boolean notifications) {
        if (!requestRunning) {
            requestRunning = true;
            mLastClickTime = SystemClock.elapsedRealtime();
            progressBar.setVisibility(View.VISIBLE);
            if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(requestMethod, "https://api.twitch.tv/kraken/users/" + globals.userID + "/follows/channels/" + followingID + "?notifications=" + notifications, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response != null && !response.toString().equals("") && requestMethod != Request.Method.DELETE) {
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
                                                userObject.toString());
                                        globals.writeToFile(
                                                globals.FOLLOWING_CURRENT_PATH
                                                        + File.separator
                                                        + userObject.getString("_id"),
                                                null);
                                        organizeUsers();
                                        displayUsers(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), "Twitch has changed its API, please contact the developer.", Toast.LENGTH_SHORT).show();
                                    } finally {
                                        requestRunning = false;
                                    }
                                } else {
                                    globals.deleteFileOrPath(globals.FOLLOWING_CURRENT_PATH
                                            + File.separator
                                            + followingID);
                                    organizeUsers();
                                    displayUsers(usersToDisplay, actionButtonMethod1, actionButtonMethod2, actionButtonMethod3);
                                    requestRunning = false;
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error changing Following preference", Toast.LENGTH_SHORT).show();
                        requestRunning = false;
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        return globals.getHeaders();
                    }

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        if (response.data != null && response.data.length > 0) {
                            try {
                                String jsonString =
                                        new String(
                                                response.data,
                                                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                                return Response.success(
                                        new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            } catch (JSONException je) {
                                return Response.error(new ParseError(je));
                            }
                        } else {
                            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    }
                };
                jsObjRequest.setTag("FOLLOW");
                VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
            } else {
                Toast.makeText(getApplicationContext(), "Cannot change Following preference when offline", Toast.LENGTH_SHORT).show();
                requestRunning = false;
            }
        }
    }

    protected void displayUsers(ArrayList<String> users, Method actionButtonMethod1, Method actionButtonMethod2, Method actionButtonMethod3) {
        usersToDisplay = users;
        this.actionButtonMethod1 = actionButtonMethod1;
        this.actionButtonMethod2 = actionButtonMethod2;
        this.actionButtonMethod3 = actionButtonMethod3;
        ImageButton actionButton1 = null;
        ImageButton actionButton2 = null;
        ImageButton actionButton3 = null;
        final LinearLayout layout = findViewById(R.id.table);
        layout.removeAllViews();
        if (users.size() > 0) {
            for (int i = users.size() - 1; i >= 0; i--) {
                try {
                    final JSONObject user = new JSONObject(globals.readFromFile(
                            getApplicationContext().getFilesDir() + usersPath + File.separator + users.get(i)));
                    if (user.has("notifications")) {
                        userNotifications = user.getBoolean("notifications");
                    }
                    userFollows = new File(getApplication().getFilesDir()
                            + File.separator
                            + globals.FOLLOWING_CURRENT_PATH
                            + File.separator
                            + user.getString("_id")
                    ).exists();
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
                case "FOLLOW_BUTTON":
                    imageButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    followButtonAction(user);
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

    protected ImageButton followButton() {
        ImageButton imageButton = new ImageButton(getApplicationContext());
        if (userFollows) {
            imageButton.setImageResource(R.drawable.unfollow);
        } else {
            imageButton.setImageResource(R.drawable.follow);
        }
        imageButton.setTag("FOLLOW_BUTTON");
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

    protected void deleteButtonAction(LinearLayout layout, View v, JSONObject user) {
        try{
            globals.deleteFileOrPath(unfollowedUsersPath + File.separator + user.getString("_id"));
            globals.deleteFileOrPath(usersPath + File.separator + user.getString("_id"));
            unfollowedUsers.remove(user.getString("_id"));
            organizeUsers();
            layout.removeView((View) v.getParent());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred deleting User", Toast.LENGTH_SHORT).show();
        }
    }

    protected void excludeButtonAction(LinearLayout layout, View v, JSONObject user) {
        try {
            globals.writeToFile(
                    excludedUsersPath
                            + File.separator
                            + user.getString("_id"),
                    null);
            excludedUsers.add(user.getString("_id"));
            organizeUsers();
            layout.removeView((View) v.getParent());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred excluding User", Toast.LENGTH_SHORT).show();
        }
    }

    protected void includeButtonAction(LinearLayout layout, View v, JSONObject user) {
        try {
            globals.deleteFileOrPath(
                    excludedUsersPath
                            + File.separator
                            + user.getString("_id"));
            excludedUsers.remove(user.getString("_id"));
            organizeUsers();
            layout.removeView((View) v.getParent());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred including User", Toast.LENGTH_SHORT).show();
        }
    }

    protected void followButtonAction(JSONObject user) {
        try {
            if (new File(getApplication().getFilesDir()
                    + File.separator
                    + globals.FOLLOWING_CURRENT_PATH
                    + File.separator
                    + user.getString("_id")
            ).exists()) {
                requestFollow(Request.Method.DELETE, user.getString("_id"), false);
            } else {
                requestFollow(Request.Method.PUT, user.getString("_id"), false);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred following/unfollowing User", Toast.LENGTH_SHORT).show();
        }
    }

    protected void notificationsButtonAction(JSONObject user) {
        try {
            if (user.getBoolean("notifications")) {
                requestFollow(Request.Method.PUT, user.getString("_id"), false);
            } else {
                requestFollow(Request.Method.PUT, user.getString("_id"), true);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "An error occurred changing Users notifications preference", Toast.LENGTH_SHORT).show();
        }
    }
}
