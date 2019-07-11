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

public class ChannelFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    @Test
    public void shouldWriteAffiliatedUserFile() throws JSONException {
        shouldWriteChannelFile("affiliate");
    }

    @Test
    public void shouldWriteNonAffiliatedUserFile() throws JSONException {
        shouldWriteChannelFile("");
    }

    private void shouldWriteChannelFile(String affiliateOrNonAffiliate) throws JSONException {
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
        ChannelFileHandler channelFileHandler = new ChannelFileHandler(new WeakReference<>(context));
        channelFileHandler.setResponse(response);
        channelFileHandler.writeChannel();
        assertTrue(new File("CHANNEL" ).exists());
        JSONObject channel = new JSONObject(new ReadFileHandler(new WeakReference<>(context), "CHANNEL").readFile());
        assertTrue(channel.getString("display_name").contentEquals(response.getString("display_name")));
        assertTrue(channel.getString("_id").contentEquals(response.getString("_id")));
        assertTrue(channel.getString("logo").contentEquals(response.getString("logo")));
        assertTrue(channel.getString("game").contentEquals(response.getString("game")));
        assertTrue(channel.getString("created_at").contentEquals(response.getString("created_at").replace("T", " ").replace("Z", "")));
        assertEquals(channel.getInt("views"), response.getInt("views"));
        assertEquals(channel.getInt("followers"), response.getInt("followers"));
        assertTrue(channel.getString("status").contentEquals(response.getString("status")));
        assertTrue(channel.getString("description").contentEquals(response.getString("description")));
        assertTrue(channel.getString("broadcaster_type").contentEquals(response.getString("broadcaster_type")) || channel.getString("broadcaster_type").contentEquals("streamer"));
        //Cleanup
        assertTrue(new File("CHANNEL").delete());
    }
}
