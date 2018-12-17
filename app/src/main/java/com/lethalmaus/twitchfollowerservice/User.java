package com.lethalmaus.twitchfollowerservice;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class User extends MainActivity {

    protected String status;
    protected String game;
    protected String memberSince;
    protected int views;
    protected int followerAmount;
    protected String broadcasterType;
    protected String description;
    private Globals globals = new Globals(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        getSupportActionBar().setTitle("User");
        getUser();
    }

    protected void getUser() {
        if (isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channel", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            setUser(response);
                            saveUser();
                            showUser();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Twitch API error on User retrieval", Toast.LENGTH_SHORT).show();
                    if(error.networkResponse.data!=null) {
                        String body = "";
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.println(body);
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/vnd.twitchtv.v5+json");
                    headers.put("Client-ID", globals.CLIENTID);
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "OAuth " + token);
                    return headers;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved User", Toast.LENGTH_SHORT).show();
            readUser();
            showUser();
        }
    }

    protected void setUser(JSONObject response) {
        try {
            username = response.getString("display_name");
            userID = response.getString("_id");
            userLogo = response.getString("logo");
            status = response.getString("status");
            game = response.getString("game");
            memberSince  = response.getString("created_at").replace("T", " ").replace("Z", " ");
            views = response.getInt("views");
            followerAmount = response.getInt("followers");
            description = response.getString("description");
            if (response.getString("broadcaster_type").equals("")) {
                broadcasterType = "Normal";
            } else {
                broadcasterType  = response.getString("broadcaster_type");
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "User can't be set", Toast.LENGTH_SHORT).show();
        }
    }

    protected void saveUser() {
        try {
            JSONObject user = new JSONObject();
            user.put("display_name", username);
            user.put("_id", userID);
            user.put("logo", userLogo);
            user.put("game", game);
            user.put("created_at", memberSince);
            user.put("views", views);
            user.put("followers", followerAmount);
            user.put("broadcaster_type", broadcasterType);
            user.put("status", status);
            user.put("description", description);
            globals.writeToFile("USER", user.toString(), getApplicationContext());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "User can't be saved", Toast.LENGTH_SHORT).show();
        }
    }

    protected void readUser() {
        try {
            JSONObject user = new JSONObject(readFromFile(getApplicationContext().getFilesDir() + File.separator + "USER", getApplicationContext()));
            setUser(user);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "User can't be read", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void showUser() {
        ImageView user_Logo = findViewById(R.id.user_Logo);
        Glide.with(getApplicationContext()).load(userLogo).into(user_Logo);

        TextView user_Username = findViewById(R.id.user_Username);
        user_Username.setText(username);

        TextView user_ID = findViewById(R.id.user_ID);
        user_ID.setText(userID);

        TextView user_Game = findViewById(R.id.user_Game);
        user_Game.setText(game);

        TextView user_MemberSince = findViewById(R.id.user_MemberSince);
        user_MemberSince.setText(memberSince);

        TextView user_Views = findViewById(R.id.user_Views);
        user_Views.setText(String.valueOf(views));

        TextView user_Follows = findViewById(R.id.user_Follows);
        user_Follows.setText(String.valueOf(followerAmount));

        TextView user_BroadcasterType = findViewById(R.id.user_BroadcasterType);
        user_BroadcasterType.setText(broadcasterType);

        TextView user_Status = findViewById(R.id.user_Status);
        user_Status.setText(status);

        TextView user_Description = findViewById(R.id.user_Description);
        user_Description.setText(description);
    }
}