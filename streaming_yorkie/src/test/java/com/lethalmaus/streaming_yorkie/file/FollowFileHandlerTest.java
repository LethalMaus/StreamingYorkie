package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.MockContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FollowFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    private FollowFileHandler followFileHandler;

    @Test
    public void shouldWriteSingleFollowingFile() throws JSONException {
        //Setup
        JSONObject response = new JSONObject();
        JSONObject channel = new JSONObject();
        channel.put("display_name", "TEST");
        channel.put("_id", "12345");
        channel.put("logo", "localhost");
        response.put("notifications", true);
        response.put("channel", channel);
        response.put("created_at", "01.01.1970");
        //Test
        followFileHandler = new FollowFileHandler(new WeakReference<>(context), "FOLLOW_FILE_USER_PATH_TEST", "FOLLOW_FILE_REQUEST_PATH_TEST", null);
        followFileHandler.setResponse(response);
        followFileHandler.writeFollowerFile();
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST" + File.separator + "12345").exists());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST" + File.separator + "12345").exists());
        JSONObject following = new JSONObject(new ReadFileHandler(new WeakReference<>(context), "FOLLOW_FILE_USER_PATH_TEST" + File.separator + "12345").readFile());
        assertTrue(following.getString("display_name").contentEquals(channel.getString("display_name")));
        assertTrue(following.getString("_id").contentEquals(channel.getString("_id")));
        assertTrue(following.getString("logo").contentEquals(channel.getString("logo")));
        assertEquals(following.getBoolean("notifications"), response.getBoolean("notifications"));
        assertTrue(following.getString("created_at").contentEquals(response.getString("created_at")));
        //Cleanup
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST" + File.separator + "12345").delete());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST" + File.separator + "12345").delete());
    }

    @Test
    public void shouldWriteMultipleFollowingFile() throws JSONException {
       shouldWriteMultipleFollowFiles("channel");
    }

    @Test
    public void shouldWriteMultipleFollowersFile() throws JSONException {
        shouldWriteMultipleFollowFiles("channel");
    }

    private void shouldWriteMultipleFollowFiles(String channelOrUser) throws JSONException {
        //Setup
        JSONObject response = new JSONObject();
        JSONArray follows = new JSONArray();
        JSONObject follow = new JSONObject();
        JSONObject channel1 = new JSONObject();
        channel1.put("display_name", "TEST1");
        channel1.put("_id", "12345");
        channel1.put("logo", "localhost");
        follow.put("notifications", true);
        follow.put("created_at", "01.01.1970");
        follow.put(channelOrUser, channel1);
        follows.put(follows.length(), follow);
        JSONObject channel2 = new JSONObject();
        follow = new JSONObject();
        channel2.put("display_name", "TEST2");
        channel2.put("_id", "23456");
        channel2.put("logo", "localhost");
        follow.put("notifications", true);
        follow.put("created_at", "01.01.1970");
        follow.put(channelOrUser, channel2);
        follows.put(follows.length(), follow);
        response.put("follows", follows);
        //Test
        followFileHandler = new FollowFileHandler(new WeakReference<>(context), "FOLLOW_FILE_USER_PATH_TEST", "FOLLOW_FILE_REQUEST_PATH_TEST", null);
        followFileHandler.setResponse(response);
        followFileHandler.writeFollowerFile();
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST" + File.separator + "12345").exists());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST" + File.separator + "12345").exists());
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST" + File.separator + "23456").exists());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST" + File.separator + "23456").exists());
        JSONObject following = new JSONObject(new ReadFileHandler(new WeakReference<>(context), "FOLLOW_FILE_USER_PATH_TEST" + File.separator + "12345").readFile());
        assertTrue(following.getString("display_name").contentEquals(channel1.getString("display_name")));
        assertTrue(following.getString("_id").contentEquals(channel1.getString("_id")));
        assertTrue(following.getString("logo").contentEquals(channel1.getString("logo")));
        assertEquals(following.getBoolean("notifications"), response.getJSONArray("follows").getJSONObject(0).getBoolean("notifications"));
        assertTrue(following.getString("created_at").contentEquals(response.getJSONArray("follows").getJSONObject(0).getString("created_at")));
        following = new JSONObject(new ReadFileHandler(new WeakReference<>(context), "FOLLOW_FILE_USER_PATH_TEST" + File.separator + "23456").readFile());
        assertTrue(following.getString("display_name").contentEquals(channel2.getString("display_name")));
        assertTrue(following.getString("_id").contentEquals(channel2.getString("_id")));
        assertTrue(following.getString("logo").contentEquals(channel2.getString("logo")));
        assertEquals(following.getBoolean("notifications"), response.getJSONArray("follows").getJSONObject(1).getBoolean("notifications"));
        assertTrue(following.getString("created_at").contentEquals(response.getJSONArray("follows").getJSONObject(1).getString("created_at")));
        //Cleanup
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST" + File.separator + "12345").delete());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST" + File.separator + "12345").delete());
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST" + File.separator + "23456").delete());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST" + File.separator + "23456").delete());
        assertTrue(new File("FOLLOW_FILE_REQUEST_PATH_TEST").delete());
        assertTrue(new File("FOLLOW_FILE_USER_PATH_TEST").delete());
    }
}
