package com.lethalmaus.twitchfollowerservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class Globals {

    protected Activity activity;
    protected Context context;

    protected String username;
    protected String userID;
    protected String userLogo;
    protected String token;

    protected final String CLIENTID = "tjots3mhxunw0sj2a20ka3wz39p7bp";
    protected final int REQUEST_LIMIT = 25;

    protected final String FOLLOWERS_PATH = File.separator + "FOLLOWERS";
    protected final String FOLLOWERS_NEW_PATH = File.separator + "FOLLOWERS_NEW";
    protected final String FOLLOWERS_CURRENT_PATH = File.separator + "FOLLOWERS_CURRENT";
    protected final String FOLLOWERS_UNFOLLOWED_PATH = File.separator + "FOLLOWERS_UNFOLLOWED";
    protected final String FOLLOWERS_EXCLUDED_PATH = File.separator + "FOLLOWERS_EXCLUDED";

    protected final String FOLLOWING_PATH = File.separator + "FOLLOWING";
    protected final String FOLLOWING_NEW_PATH = File.separator + "FOLLOWING_NEW";
    protected final String FOLLOWING_CURRENT_PATH = File.separator + "FOLLOWING_CURRENT";
    protected final String FOLLOWING_UNFOLLOWED_PATH = File.separator + "FOLLOWING_UNFOLLOWED";
    protected final String FOLLOWING_EXCLUDED_PATH = File.separator + "FOLLOWING_EXCLUDED";

    protected final String F4F_EXCLUDED_PATH = File.separator + "F4F_EXCLUDED";

    protected Globals(Activity activity, Context appContext) {
        this.activity = activity;
        this.context = appContext;
        if (new File(context.getFilesDir() + File.separator + "TOKEN").exists()) {
            token = readFromFile(context.getFilesDir() + File.separator + "TOKEN");
        }
        getUser();
    }

    protected void getUser() {
        if (new File(context.getFilesDir() + File.separator + "USER").exists()) {
            try {
                JSONObject user = new JSONObject(readFromFile(context.getFilesDir() + File.separator + "USER"));
                username = user.getString("display_name");
                userID = user.getString("_id");
                userLogo = user.getString("logo");
            } catch (JSONException e) {
                Toast.makeText(context, "User can't be read", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void showUser() {
        if (username == null || userLogo == null || username.isEmpty() || userLogo.isEmpty()) {
            getUser();
        }
        ImageView user_Logo = activity.findViewById(R.id.user_Logo);
        Glide.with(context).load(userLogo).into(user_Logo);
        TextView user_Username = activity.findViewById(R.id.user_Username);
        user_Username.setText(username);
    }

    protected boolean userLoggedIn() {
        File file = new File(context.getFilesDir(), "TOKEN");
        return file.exists();
    }

    protected boolean isNetworkAvailable(ConnectivityManager systemService) {
        NetworkInfo activeNetwork = systemService.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    protected void writeToFile(String filename, String data) {
        try {
            File file = new File(context.getFilesDir() + File.separator + filename);
            if (!file.exists()) {
                if (!new File(file.getParent()).exists() && !new File(file.getParent()).mkdirs()) {
                    Toast.makeText(context.getApplicationContext(), "Error creating directory '" + filename + "'", Toast.LENGTH_SHORT).show();
                }
                if(!file.createNewFile()) {
                    Toast.makeText(context.getApplicationContext(), "Error creating file '" + filename + "'", Toast.LENGTH_SHORT).show();
                }
            }
            if (data != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context.getApplicationContext(), "Error writing to file '" + filename + "'", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "Error writing to file '" + filename + "'", Toast.LENGTH_SHORT).show();
        }
    }

    protected void deleteFileOrPath(String pathOrFileName) {
        File pathOrFile = new File(context.getFilesDir() + File.separator + pathOrFileName);
        if (pathOrFile.exists()) {
            if (pathOrFile.isDirectory()) {
                for (String file : pathOrFile.list()) {
                    deleteFileOrPath(pathOrFileName + File.separator + file);
                }
            }
            if (!pathOrFile.delete()) {
                Toast.makeText(context.getApplicationContext(), "Error deleting file: " + pathOrFileName, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context.getApplicationContext(), "File does not exist: " + pathOrFileName, Toast.LENGTH_SHORT).show();
        }
    }

    protected String readFromFile(String filename) {
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
            Toast.makeText(context.getApplicationContext(), "Errors opening file '" + filename + "'", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "Errors reading from file '" + filename + "'", Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/vnd.twitchtv.v5+json");
        headers.put("Client-ID", CLIENTID);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "OAuth " + token);
        return headers;
    }

    protected void initialSetup() {
        if (
        !new File(context.getFilesDir() + File.separator + FOLLOWERS_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWERS_NEW_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWERS_CURRENT_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWERS_UNFOLLOWED_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWERS_EXCLUDED_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWING_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWING_NEW_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWING_CURRENT_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWING_UNFOLLOWED_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + FOLLOWING_EXCLUDED_PATH).mkdir() ||
        !new File(context.getFilesDir() + File.separator + F4F_EXCLUDED_PATH).mkdir()
                ) {
            Toast.makeText(context.getApplicationContext(), "Initial folder setup failed", Toast.LENGTH_SHORT).show();
        }
    }

    protected void removeFiles() {
        deleteFileOrPath("USER");
        deleteFileOrPath("TOKEN");
        deleteFileOrPath(FOLLOWERS_PATH);
        deleteFileOrPath(FOLLOWERS_NEW_PATH);
        deleteFileOrPath(FOLLOWERS_CURRENT_PATH);
        deleteFileOrPath(FOLLOWERS_UNFOLLOWED_PATH);
        deleteFileOrPath(FOLLOWERS_EXCLUDED_PATH);
        deleteFileOrPath(FOLLOWING_PATH);
        deleteFileOrPath(FOLLOWING_NEW_PATH);
        deleteFileOrPath(FOLLOWING_CURRENT_PATH);
        deleteFileOrPath(FOLLOWING_UNFOLLOWED_PATH);
        deleteFileOrPath(FOLLOWING_EXCLUDED_PATH);
        deleteFileOrPath(F4F_EXCLUDED_PATH);

    }

    protected boolean onOptionsItemsSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_info:
                intent = new Intent(activity, Info.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(activity, Settings.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_logout:
                intent = new Intent(activity, Authorization.class);
                activity.startActivity(intent);
                return true;
        }
        return false;
    }
}
