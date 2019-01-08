package com.lethalmaus.twitchfollowerservice;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Info extends AppCompatActivity {

    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        globals = new Globals(Info.this, getApplicationContext());

        getCurrentDeveloperLogo();

        ImageButton github = findViewById(R.id.github_Logo);
        github.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO change to repo when not private
                        requestUrl("https://github.com/LethalMaus");
                    }
                });

        ImageButton discord = findViewById(R.id.discord_Logo);
        discord.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUrl("https://discord.gg/uG97jTj");
                    }
                });

        ImageButton twitch = findViewById(R.id.twitch_Logo);
        twitch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUrl("https://twitch.tv/LethalMaus");
                    }
                });

        ImageButton patreon = findViewById(R.id.patreon_Logo);
        patreon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUrl("https://patreon.com/LethalMaus");
                    }
                });

        ImageButton paypal = findViewById(R.id.paypal_Logo);
        paypal.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestUrl("https://paypal.me/JamesCullimore/2,50");
                    }
                });
    }
    @Override
    protected void onPause() {
        super.onPause();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("DEV");
    }
    @Override
    protected void onStop() {
        super.onStop();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("DEV");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleySingleton.getInstance(this).getRequestQueue().cancelAll("DEV");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    protected void getCurrentDeveloperLogo() {
        if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, "https://api.twitch.tv/kraken/users/188850000", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                ImageView developer_Logo = findViewById(R.id.developer_Logo);
                                Glide.with(getApplicationContext()).load(response.getString("logo")).into(developer_Logo);
                            } catch (JSONException e) {
                                //TODO log error
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO log error
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return globals.getHeaders();
                }
            };
            jsObjRequest.setTag("DEV");
            VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        }
    }

    protected void requestUrl(String url) {
        if (globals.isNetworkAvailable((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch(ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No browser can be found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "OFFLINE: Can't open link", Toast.LENGTH_SHORT).show();
        }
    }
}
