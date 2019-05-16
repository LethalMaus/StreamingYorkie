package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.MockContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertTrue;

public class VODFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    @Test
    public void shouldWriteVODFile() throws JSONException {
        //Setup
        JSONObject response = new JSONObject();
        JSONArray videos = new JSONArray();
        JSONObject vod = new JSONObject();
        vod.put("_id", "v12345");
        vod.put("title", "TITLE");
        vod.put("description", "DESCRIPTION");
        vod.put("tag_list", "TAG_LIST");
        vod.put("url", "URL");
        vod.put("created_at", "1970-01-04T17:03:55Z");
        vod.put("game", "TEST");
        vod.put("length", 3600);
        JSONObject preview = new JSONObject();
        preview.put("medium", "URL");
        vod.put("preview", preview);
        videos.put(0, vod);
        response.put("videos", videos);

        //Test
        VODFileHandler vodFileHandler = new VODFileHandler(new WeakReference<>(context));
        vodFileHandler.setResponse(response);
        vodFileHandler.writeVOD();
        assertTrue(new File(Globals.VOD_PATH + File.separator + "12345").exists());
        JSONObject video = new JSONObject(new ReadFileHandler(new WeakReference<>(context), Globals.VOD_PATH + File.separator + "12345").readFile());
        assertTrue(video.getString("title").contentEquals(vod.getString("title")));
        assertTrue(video.getString("description").contentEquals(vod.getString("description")));
        assertTrue(video.getString("tag_list").contentEquals(vod.getString("tag_list")));
        assertTrue(video.getString("url").contentEquals(vod.getString("url")));
        assertTrue(video.getString("created_at").contentEquals(vod.getString("created_at").replace("T", " ").replace("Z", "")));
        assertTrue(video.getString("game").contentEquals(vod.getString("game")));
        assertTrue(video.getString("length").contentEquals("1h 0m 0s"));
        assertTrue(video.getString("preview").contentEquals(vod.getJSONObject("preview").getString("medium")));

        //Cleanup
        assertTrue(new File(Globals.VOD_PATH + File.separator + "12345").delete());
    }
}
