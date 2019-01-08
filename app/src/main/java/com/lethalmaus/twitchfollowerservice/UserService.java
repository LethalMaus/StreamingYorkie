package com.lethalmaus.twitchfollowerservice;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Map;

public class UserService extends AppCompatActivity {

    protected Globals globals;
    protected String status;
    protected String game;
    protected String memberSince;
    protected int views;
    protected int followerAmount;
    protected String broadcasterType;
    protected String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globals = new Globals(UserService.this, getApplicationContext());
    }
    @Override
    protected void onPause() {
        super.onPause();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("USER");
    }
    @Override
    protected void onStop() {
        super.onStop();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("USER");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("USER");
    }


    protected void getUser() {
        if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/channel", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            getUserResponseHandler(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getUserErrorHandler(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return globals.getHeaders();
                }
            };
            jsObjRequest.setTag("USER");
            VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            getUserOfflineHandler();
        }
    }

    protected void getUserResponseHandler(JSONObject response) {
        setUser(response);
        saveUser();
        showUser();
    }

    protected void getUserErrorHandler(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Twitch API error on User retrieval", Toast.LENGTH_SHORT).show();
        if(error.networkResponse.data != null) {
            String body = "";
            try {
                body = new String(error.networkResponse.data,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //TODO
            //log failure
            //use error.xml
            System.out.println(body);
        }
    }

    protected void getUserOfflineHandler() {
        Toast.makeText(getApplicationContext(), "OFFLINE: Showing saved User", Toast.LENGTH_SHORT).show();
        readUser();
        showUser();
    }

    protected void setUser(JSONObject response) {
        try {
            globals.username = response.getString("display_name");
            globals.userID = response.getString("_id");
            globals.userLogo = response.getString("logo");
            status = response.getString("status");
            game = response.getString("game");
            memberSince  = response.getString("created_at").replace("T", " ").replace("Z", " ");
            views = response.getInt("views");
            followerAmount = response.getInt("followers");
            description = response.getString("description");
            if (response.getString("broadcaster_type").equals("")) {
                broadcasterType = "streamer";
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
            user.put("display_name", globals.username);
            user.put("_id", globals.userID);
            user.put("logo", globals.userLogo);
            user.put("game", game);
            user.put("created_at", memberSince);
            user.put("views", views);
            user.put("follow_service", followerAmount);
            user.put("broadcaster_type", broadcasterType);
            user.put("status", status);
            user.put("description", description);
            globals.writeToFile("USER", user.toString());
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "User can't be saved", Toast.LENGTH_SHORT).show();
        }
    }

    protected void readUser() {
        try {
            JSONObject user = new JSONObject(globals.readFromFile(getApplicationContext().getFilesDir() + File.separator + "USER"));
            setUser(user);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "User can't be read", Toast.LENGTH_SHORT).show();
        }
    }

    protected void showUser() {
        ImageView user_Logo = findViewById(R.id.user_Logo);
        Glide.with(getApplicationContext()).load(globals.userLogo).into(user_Logo);

        TextView user_Username = findViewById(R.id.user_Username);
        user_Username.setText(globals.username);

        TextView user_ID = findViewById(R.id.user_ID);
        user_ID.setText(globals.userID);

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
