package com.lethalmaus.twitchfollowerservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Globals {

    protected String username;
    protected String userID;
    protected String userLogo;
    protected String token;

    protected final String CLIENTID = "tjots3mhxunw0sj2a20ka3wz39p7bp";

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

    protected Globals(Context appContext) {
        token = readFromFile(appContext.getFilesDir() + File.separator + "TOKEN", appContext);
        try {
            JSONObject user = new JSONObject(readFromFile(appContext.getFilesDir() + File.separator + "USER", appContext));
            username = user.getString("display_name");
            userID = user.getString("_id");
            userLogo = user.getString("logo");
        } catch (JSONException e) {
            Toast.makeText(appContext, "User can't be read", Toast.LENGTH_SHORT).show();
        }
    }

    protected boolean userLoggedIn(Context appContext) {
        File file = new File(appContext.getFilesDir(), "TOKEN");
        return file.exists();
    }

    protected boolean isNetworkAvailable (ConnectivityManager systemService) {
        NetworkInfo activeNetwork = systemService.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    protected void writeToFile(String filename, String data, Context appContext) {
        try {
            File file = new File(appContext.getFilesDir() + File.separator + filename);
            if (!file.exists()) {
                if (!new File(file.getParent()).exists() && !new File(file.getParent()).mkdirs()) {
                    Toast.makeText(appContext.getApplicationContext(), "Error creating directory '" + filename + "'", Toast.LENGTH_SHORT).show();
                }
                if(!file.createNewFile()) {
                    Toast.makeText(appContext.getApplicationContext(), "Error creating file '" + filename + "'", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(appContext.getApplicationContext(), "Error writing to file '" + filename + "'", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(appContext.getApplicationContext(), "Error writing to file '" + filename + "'", Toast.LENGTH_SHORT).show();
        }
    }

    protected void deleteFileOrPath(String pathOrFileName, Context appContext) {
        File pathOrFile = new File(appContext.getFilesDir() + File.separator + pathOrFileName);
        if (pathOrFile.exists()) {
            if (pathOrFile.isDirectory()) {
                for (String file : pathOrFile.list()) {
                    deleteFileOrPath(pathOrFileName + File.separator + file, appContext.getApplicationContext());
                }
            }
            if (!pathOrFile.delete()) {
                Toast.makeText(appContext.getApplicationContext(), "Error deleting file: " + pathOrFileName, Toast.LENGTH_SHORT).show();
            }
        }
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
