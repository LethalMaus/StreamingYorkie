package com.lethalmaus.streaming_yorkie.worker;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.LurkRequestHandler;
import com.lethalmaus.streaming_yorkie.service.LurkService;

import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Worker for automating Lurking
 * @author LethalMaus
 */
public class AutoLurkWorker extends Worker {

    private WeakReference<Context> weakContext;
    private StreamingYorkieDB streamingYorkieDB;
    private boolean wifiOnly;
    private boolean informChannel;
    private String message;
    private int lurkResponseCount;
    private String channelName;

    /**
     * Constructor for AutoLurkWorker for automating Lurking
     * @author LethalMaus
     * @param context app context
     * @param params parameters for worker.super()
     */
    public AutoLurkWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.weakContext = new WeakReference<>(context);
        streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
        try {
            JSONObject settings = new JSONObject(new ReadFileHandler(null, weakContext, "SETTINGS_LURK").readFile());
            wifiOnly = settings.getBoolean(Globals.SETTINGS_WIFI_ONLY);
            informChannel = settings.getBoolean(Globals.SETTINGS_LURK_INFORM);
            if (informChannel) {
                new Thread() {
                    public void run() {
                        channelName = streamingYorkieDB.channelDAO().getChannel().getDisplay_name();
                    }
                }.start();
            }
            message = settings.getString(Globals.SETTINGS_LURK_MESSAGE);
            if (message.isEmpty()) {
                message = "!lurk";
            }
        } catch(JSONException e) {
            new WriteFileHandler(null, weakContext, "ERROR", null, "AutoLurk: Error reading settings | " + e.toString(), true).run();
        }
    }

    @Override
    public @NonNull
    Result doWork() {
        if (!wifiOnly || checkIfWifiIsOnAndConnected()) {
            new Thread() {
                public void run() {
                    int lurkCount = streamingYorkieDB.lurkDAO().getChannelsToBeLurkedCount();
                    if (lurkCount > 0) {
                        final StringBuilder htmlInjection = new StringBuilder();
                        lurkResponseCount = 0;
                        for (int i = 0; i < lurkCount; i++) {
                            final LurkEntity lurk = streamingYorkieDB.lurkDAO().getChannelsToBeLurkedByPosition(i);
                            new LurkRequestHandler(null, weakContext, null) {
                                @Override
                                public void onCompletion() {
                                    if (weakContext != null && weakContext.get() != null) {
                                        lurkResponseCount++;
                                        if (lurk.getHtml() != null && !lurk.getHtml().isEmpty() && lurk.isChannelIsToBeLurked()) {
                                            htmlInjection.append(lurk.getHtml());
                                        }
                                        if (informChannel && !lurk.isChannelInformedOfLurk()) {
                                            sendLurkMessage(lurk.getChannelName());
                                            lurk.setChannelInformedOfLurk(true);
                                            new Thread() {
                                                public void run() {
                                                    streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                                }
                                            }.start();
                                        }
                                        if (lurkResponseCount == lurkCount && !htmlInjection.toString().isEmpty()) {
                                            new WriteFileHandler(null, weakContext, "LURK.HTML", null, htmlInjection.toString(), false).writeToFileOrPath();
                                            new Thread() {
                                                public void run() {
                                                    //TODO delete me
                                                    SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.GERMAN);
                                                    new WriteFileHandler(null, weakContext, "WORKER.LOG", null, timestamp.format(new Date()), true).run();
                                                    Intent intent = new Intent(weakContext.get(), LurkService.class);
                                                    intent.setAction("AUTO_LURK");
                                                    if (Build.VERSION.SDK_INT < 28) {
                                                        weakContext.get().startService(intent);
                                                    } else {
                                                        weakContext.get().startForegroundService(intent);
                                                    }
                                                }
                                            }.start();
                                        }
                                    }
                                }
                            }.newRequest(lurk.getChannelName()).initiate().sendRequest();
                        }
                    }
                }
            }.start();
        }
       return Result.success();
    }

    /**
     * Method for checking if on WiFi network
     * @return boolean isOnAndConnected
     */
    private boolean checkIfWifiIsOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) weakContext.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr != null && wifiMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getNetworkId() > 0;
        } else {
            return false;
        }
    }

    /**
     * Method for sending a message to the lurked channel chat
     * @author LethalMaus
     * @param channel String channel name
     */
    private void sendLurkMessage(String channel) {
        new Thread() {
            public void run() {
                if (new File(weakContext.get().getFilesDir().toString() + File.separator + "TOKEN").exists()) {
                    String token = new ReadFileHandler(null, new WeakReference<>(weakContext.get()), "TOKEN").readFile();
                    Configuration configuration = new Configuration.Builder()
                            .setAutoReconnect(false)
                            .setAutoNickChange(false)
                            .setOnJoinWhoEnabled(false)
                            .setCapEnabled(true)
                            .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                            .addServer("irc.twitch.tv")
                            .setName(channelName)
                            .setServerPassword("oauth:" + token)
                            .addAutoJoinChannel("#" + channel)
                            .addListener(new ListenerAdapter() {})
                            .buildConfiguration();
                    PircBotX bot = new PircBotX(configuration);
                    new Thread() {
                        public void run() {
                            try {
                                bot.startBot();
                            } catch (Exception e) {
                                new WriteFileHandler(null, weakContext, "ERROR", null, "Error starting chat: " + e.toString(), true).run();
                            }
                        }
                    }.start();
                    try {
                        //Wait for bot to start as the above method blocks the thread
                        Thread.sleep(1000);
                        if (bot.isConnected()) {
                            bot.sendIRC().message("#" + channel, message);
                        }
                    } catch (Exception e) {
                        new WriteFileHandler(null, weakContext, "ERROR", null, "Error sending to chat: " + e.toString(), true).run();
                    } finally {
                        bot.stopBotReconnect();
                        bot.close();
                    }
                }
            }
        }.start();
    }
}
