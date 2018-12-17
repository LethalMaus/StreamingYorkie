package com.lethalmaus.twitchfollowerservice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

//TODO
//add put & delete for followers
//notifications for followers
//arrange followers
//update files (or for each follower a file maybe)
//exclusions get just id
//auto follow & unfollow
//f4f
//following
//logout
//settings
//ui styling

public class MainActivity extends AppCompatActivity {

    protected String username;
    protected String userID;
    protected String userLogo;
    protected String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!userLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, Authorization.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        token = readFromFile(getApplicationContext().getFilesDir() + File.separator + "TOKEN", getApplicationContext());
        try {
            JSONObject user = new JSONObject(readFromFile(getApplicationContext().getFilesDir() + File.separator + "USER", getApplicationContext()));
            username = user.getString("display_name");
            userID = user.getString("_id");
            userLogo = user.getString("logo");
            //showUser();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "User can't be read", Toast.LENGTH_SHORT).show();
        }

        Button user = findViewById(R.id.user);
        user.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, User.class);
                        startActivity(intent);
                    }
                });

        Button followers = findViewById(R.id.followers);
        followers.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Followers.class);
                        startActivity(intent);
                    }
                });

        Button authorization = findViewById(R.id.authorization);
        authorization.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Authorization.class);
                        startActivity(intent);
                    }
                });

        Button following = findViewById(R.id.following);
        following.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Following.class);
                        startActivity(intent);
                    }
                });
    }

    protected void showUser() {
        ImageView user_Logo = findViewById(R.id.user_Logo);
        Glide.with(getApplicationContext()).load(userLogo).into(user_Logo);
        TextView user_Username = findViewById(R.id.user_Username);
        user_Username.setText(username);
    }

    protected boolean userLoggedIn() {
        File file = new File(getApplicationContext().getFilesDir(), "TOKEN");
        return file.exists();
    }

    protected boolean isNetworkAvailable (ConnectivityManager systemService) {
        NetworkInfo activeNetwork = systemService.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    protected String readFromFile(String filename, Context appContext) {
        try {
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp);
                }
                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();
                return stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(appContext.getApplicationContext(), "Errors opening file '" + filename + "'", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(appContext.getApplicationContext(), "Errors reading from file '" + filename + "'", Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}