package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.MockContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    @Test
    public void shouldWriteAffiliatedUserFile() throws JSONException {
        shouldWriteUserFile("affiliate");
    }

    @Test
    public void shouldWriteNonAffiliatedUserFile() throws JSONException {
        shouldWriteUserFile("");
    }

    private void shouldWriteUserFile(String affiliateOrNonAffiliate) throws JSONException {
        //Setup
        JSONObject response = new JSONObject();
        response.put("display_name", "TEST1");
        response.put("_id", "12345");
        response.put("logo", "/localhost");
        response.put("game", "TEST1");
        response.put("created_at", "1970-01-04T17:03:55Z");
        response.put("views", 1);
        response.put("followers", 1);
        response.put("status", "TEST1");
        response.put("description", "TEST1");
        response.put("broadcaster_type", affiliateOrNonAffiliate);
        //Test
        UserFileHandler userFileHandler = new UserFileHandler(new WeakReference<>(context), false);
        userFileHandler.setResponse(response);
        userFileHandler.writeUser();
        assertTrue(new File("USER" ).exists());
        JSONObject user = new JSONObject(new ReadFileHandler(new WeakReference<>(context), "USER").readFile());
        assertTrue(user.getString("display_name").contentEquals(response.getString("display_name")));
        assertTrue(user.getString("_id").contentEquals(response.getString("_id")));
        assertTrue(user.getString("logo").contentEquals(response.getString("logo")));
        assertTrue(user.getString("game").contentEquals(response.getString("game")));
        assertTrue(user.getString("created_at").contentEquals(response.getString("created_at").replace("T", " ").replace("Z", "")));
        assertEquals(user.getInt("views"), response.getInt("views"));
        assertEquals(user.getInt("followers"), response.getInt("followers"));
        assertTrue(user.getString("status").contentEquals(response.getString("status")));
        assertTrue(user.getString("description").contentEquals(response.getString("description")));
        assertTrue(user.getString("broadcaster_type").contentEquals(response.getString("broadcaster_type")) || user.getString("broadcaster_type").contentEquals("streamer"));
        //Cleanup
        assertTrue(new File("USER").delete());
    }
}
