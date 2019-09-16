package com.lethalmaus.streaming_yorkie.request;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.lethalmaus.streaming_yorkie.entity.Channel;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.view.UserView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Class for requesting current user info
 * @author LethalMaus
 */
public class UserRequestHandler extends RequestHandler {

    @Override
    public String url() {
        return "https://api.twitch.tv/kraken/user";
    }

    @Override
    public int method() {
        return Request.Method.GET;
    }

    /**
     * Constructor for UserRequestHandler for requesting current user info
     * @author LethalMaus
     * @param weakActivity weak referenced activity
     * @param weakContext weak referenced context
     */
    public UserRequestHandler(WeakReference<Activity> weakActivity, WeakReference<Context> weakContext) {
        super(weakActivity, weakContext, null);
        this.requestType = "USER";
    }

    @Override
    public void responseHandler(final JSONObject response) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    try {
                        URL url = new URL(response.getString("logo"));
                        InputStream inputStream = new BufferedInputStream(url.openStream());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int byteNumber;
                        while (-1 != (byteNumber = inputStream.read(buffer))) {
                            byteArrayOutputStream.write(buffer, 0, byteNumber);
                        }
                        byteArrayOutputStream.close();
                        inputStream.close();
                        FileOutputStream fos = new FileOutputStream(weakContext.get().getFilesDir() + File.separator + response.getString("logo").substring(response.getString("logo").lastIndexOf("/") + 1));
                        fos.write(byteArrayOutputStream.toByteArray());
                        fos.close();
                    } catch (Exception e) {
                        new WriteFileHandler(weakContext, "ERROR", null, "Cannot download channel logo | " + e.toString(), true).run();
                    }
                    Channel existingChannel = streamingYorkieDB.channelDAO().getChanneById(Integer.parseInt(response.getString("_id")));
                    if (existingChannel != null) {
                        existingChannel.setDisplay_name(response.getString("display_name"));
                        existingChannel.setLogo(response.getString("logo"));
                        existingChannel.setCreated_at(response.getString("created_at").replace("T", " ").replace("Z", ""));
                        existingChannel.setDescription(response.getString("bio"));
                        streamingYorkieDB.channelDAO().updateChannel(existingChannel);
                    } else {
                        Channel channel = new Channel(Integer.parseInt(response.getString("_id")), response.getString("display_name"), response.getString("logo"), "", response.getString("created_at").replace("T", " ").replace("Z", ""), 0, 0, "", response.getString("bio"), "");
                        streamingYorkieDB.channelDAO().insertChannel(channel);
                    }
                    new UserView(weakActivity, weakContext).execute();
                } catch (JSONException e) {
                    if (weakActivity != null && weakActivity.get() != null) {
                        weakActivity.get().runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        Toast.makeText(weakContext.get(), "User can't be saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                    new WriteFileHandler(weakContext, "ERROR", null, "Error saving User | " + e.toString(), true).run();
                }
            }
        }).start();
    }

}
