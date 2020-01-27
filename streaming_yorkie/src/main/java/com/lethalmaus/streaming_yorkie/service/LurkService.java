package com.lethalmaus.streaming_yorkie.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.activity.Lurk;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.ChannelEntity;
import com.lethalmaus.streaming_yorkie.entity.LurkEntity;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.LurkRequestHandler;
import com.lethalmaus.streaming_yorkie.util.NetworkUsageMonitor;

import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for creating a Background WebView Lurk
 * @author LethalMaus
 */
public class LurkService extends Service {

    private WeakReference<Context> weakContext;
    private NotificationManagerCompat notificationManager;
    private WindowManager windowManager;
    private WebView webView;
    private StreamingYorkieDB streamingYorkieDB;
    private List<String> channels = new ArrayList<>();
    private StringBuilder channelNames;
    private WifiManager wifiMgr;
    private Handler networkUsageHandler;
    private Runnable networkUsageRunnable;
    private boolean networkUsageMonitorRunning;
    private int noNetworkUsageCount;
    private String token = "";
    private JSONObject settings;
    private Map<String, PircBotX> botManager;
    private boolean serviceRestart;
    private String previousIntentAction;

    private boolean wifiOnly;
    private boolean informChannel;
    private String message;
    private int lurkResponseCount;
    private String channelName;

    @Override
    public void onCreate() {
        weakContext = new WeakReference<>(getApplicationContext());
        notificationManager =  NotificationManagerCompat.from(getApplicationContext());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        networkUsageMonitorRunning = false;
        noNetworkUsageCount = 10;
        serviceRestart = false;
        streamingYorkieDB = StreamingYorkieDB.getInstance(getApplicationContext());
        wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (new File(getFilesDir().toString() + File.separator + "TOKEN").exists()) {
            token = new ReadFileHandler(null, new WeakReference<>(getApplicationContext()), "TOKEN").readFile();
        }
        botManager = new HashMap<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    //This is needed for the Lurk WebView, even though its not recommended & considered dangerous. Hence the Lint suppression
    @SuppressLint("SetJavaScriptEnabled")
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*TODO
        if manuallurk, create html from db
        if autolurk send request then build
         */
        if (intent != null && intent.getAction() != null) {
            previousIntentAction = intent.getAction();
        }
        if (intent != null && intent.getAction() != null && intent.getAction().contentEquals("STOP_LURK")) {
            stopSelf();
        } else if (intent != null && intent.getAction() != null && intent.getAction().contentEquals("PAUSE_LURK")) {
            networkUsageHandler.removeCallbacks(networkUsageRunnable);
            networkUsageMonitorRunning = false;
            windowManager.removeView(webView);
            webView.destroy();
            webView = null;
            showNotification(true);
        } else {
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
            if (!wifiOnly || checkIfWifiIsOnAndConnected()) {

                int lurkCount = streamingYorkieDB.lurkDAO().getChannelsToBeLurkedCount();
                if (lurkCount > 0) {
                    final StringBuilder htmlInjection = new StringBuilder();
                    lurkResponseCount = 0;
                    for (int i = 0; i < lurkCount; i++) {
                        final LurkEntity lurk = streamingYorkieDB.lurkDAO().getChannelsToBeLurkedByPosition(i);
                        new LurkRequestHandler(null, weakContext, null) {
                            @Override
                            public void onCompletion() {
                                if (weakContext.get() != null) {
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
                                    }
                                }
                            }
                        }.newRequest(lurk.getChannelName()).initiate().sendRequest();
                    }
                }
/*
if (!htmlInjection.toString().isEmpty()) {
                                    new WriteFileHandler(weakActivity, weakContext, "LURK.HTML", null, htmlInjection.toString(), false).writeToFileOrPath();
 */
            }
            List<String> previousChannels = new ArrayList<>(channels);
            channels = Arrays.asList(streamingYorkieDB.lurkDAO().getChannelsToBeLurked());
            if (channels.size() > 0) {
                channelNames = new StringBuilder();
                for (int i = 0; i < channels.size(); i++) {
                    activateChatBot(channels.get(i));
                    channelNames.append(channels.get(i));
                    if (i < (channels.size() - 1)) {
                        channelNames.append(", ");
                    }
                }
                for (int i = 0; i < previousChannels.size(); i++) {
                    if (!channels.contains(previousChannels.get(i))) {
                        PircBotX bot = botManager.get(previousChannels.get(i));
                        if (bot != null) {
                            bot.stopBotReconnect();
                            bot.close();
                        }
                    }
                }
            }
            if (intent != null && intent.getAction() != null && intent.getAction().contentEquals("AUTO_LURK")) {
                try {
                    settings = new JSONObject(new ReadFileHandler(null, new WeakReference<>(getApplicationContext()), "SETTINGS_LURK").readFile());
                } catch(JSONException e) {
                    new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "LurkService: Error reading settings | " + e.toString(), true).run();
                }
            } else {
                settings = null;
            }

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = 0;
            params.y = 0;
            params.width = 0;
            params.height = 0;

            webView = new WebView(LurkService.this);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

            webView.loadUrl("file:///" + getFilesDir() + File.separator + "LURK.HTML");
            windowManager.addView(webView, params);
            showNotification(false);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            windowManager.removeView(webView);
            webView.destroy();
            webView = null;
            windowManager = null;
        }
        if (networkUsageHandler != null) {
            networkUsageHandler.removeCallbacks(networkUsageRunnable);
        }
        if (notificationManager != null) {
            notificationManager.cancel(3);
        }
        if (botManager != null && !botManager.isEmpty()) {
            for (Map.Entry<String, PircBotX> entry : botManager.entrySet()) {
                PircBotX bot = entry.getValue();
                bot.stopBotReconnect();
                bot.close();
            }
        }
        if (serviceRestart) {
            Intent intent = new Intent(getApplicationContext(), LurkService.class).setAction(previousIntentAction);
            if (Build.VERSION.SDK_INT < 28) {
                startService(intent);
            } else {
                startForegroundService(intent);
            }
        } else {
            Toast.makeText(this, "Stopped lurking", Toast.LENGTH_SHORT).show();
            new DeleteFileHandler(null, weakContext, "LURK.HTML").run();
        }
    }

    /**
     * Show a notification while this service is running.
     * @author LethalMaus
     * @param paused boolean to avoid NetworkUsageMonitor from running
     */
    private void showNotification(boolean paused) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Lurk.class), 0);

        PendingIntent stopLurk = null;
        Intent startPauseLurkIntent = new Intent(this, LurkService.class);
        int startPauseLurkDrawable;
        String startPauseLurkTitle;
        String contentText;
        if (webView == null) {
            startPauseLurkIntent.setAction("START_LURK");
            startPauseLurkDrawable = android.R.drawable.ic_media_play;
            startPauseLurkTitle = "Start";
            contentText = "Service paused for '" + channels.size() + "' streamers...";

            Intent stopLurkIntent = new Intent(this, LurkService.class);
            stopLurkIntent.setAction("STOP_LURK");
            stopLurk = PendingIntent.getService(this, 0, stopLurkIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            startPauseLurkIntent.setAction("PAUSE_LURK");
            startPauseLurkDrawable = android.R.drawable.ic_media_pause;
            startPauseLurkTitle = "Pause";
            contentText = "Service starting for '" + channels.size() + "' streamers...";
        }
        PendingIntent startPauseLurk = PendingIntent.getService(this, 0, startPauseLurkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.streaming_yorkie)
                .setContentTitle("Lurk")
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(startPauseLurkDrawable,startPauseLurkTitle, startPauseLurk);
        if (stopLurk != null) {
            mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"Stop", stopLurk);
        }

        if (!paused && !networkUsageMonitorRunning) {
            networkUsageMonitorRunning = true;
            final NetworkUsageMonitor networkUsageMonitor = new NetworkUsageMonitor(new WeakReference<>(getApplicationContext()));
            networkUsageHandler = new Handler();
            networkUsageRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (channels.isEmpty() || (settings != null && settings.getBoolean(Globals.SETTINGS_WIFI_ONLY) && !checkIfWifiIsOnAndConnected())) {
                            stopSelf();
                        }
                        long networkUsage = (networkUsageMonitor.getNetworkUsageDifference() / 3);
                        if (noNetworkUsageCount == 0) {
                            serviceRestart = true;
                            stopSelf();
                        } else if (networkUsage == 0) {
                            noNetworkUsageCount--;
                        } else {
                            noNetworkUsageCount = 10;
                        }
                        mBuilder.setContentText("@" + networkUsage + "kbit/s | " + channels.size() + " lurked: " + channelNames );
                        notificationManager.notify(3, mBuilder.build());
                        if (networkUsageHandler != null) {
                            networkUsageHandler.postDelayed(this, 3000);
                        }
                    } catch (JSONException e) {
                        new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "Could not get read settings for Lurk Service | " + e.toString(), true).run();
                    } catch (Exception e) {
                        new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "Could not get Lurk Service network usage | " + e.toString(), true).run();
                    }
                }
            };
            networkUsageHandler.postDelayed(networkUsageRunnable, 3000);
        }
        if (Build.VERSION.SDK_INT < 28) {
            notificationManager.notify(3, mBuilder.build());
        } else {
            startForeground(3, mBuilder.build());
        }
    }

    /**
     * Activates the a chat bot in the users name
     * @author LethalMaus
     * @param channel String
     */
    private void activateChatBot(String channel, boolean sendMessage) {
        if (!botManager.containsKey(channel)) {
            new Thread() {
                public void run() {
                    ChannelEntity channelEntity = streamingYorkieDB.channelDAO().getChannel();
                    if (channelEntity != null) {
                        Configuration configuration = new Configuration.Builder()
                                .setAutoReconnect(true)
                                .setAutoNickChange(false)
                                .setOnJoinWhoEnabled(false)
                                .setCapEnabled(true)
                                .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                                .addServer("irc.twitch.tv")
                                .setName(channelEntity.getDisplay_name())
                                .setServerPassword("oauth:" + token)
                                .addAutoJoinChannel("#" + channel.toLowerCase())
                                .addListener(new ListenerAdapter() {
                                })
                                .buildConfiguration();
                        PircBotX bot = new PircBotX(configuration);
                        new Thread() {
                            public void run() {
                                try {
                                    botManager.put(channel, bot);
                                    bot.startBot();
                                } catch (Exception e) {
                                    new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "Error starting chat: " + e.toString(), true).run();
                                }
                            }
                        }.start();
                        if (sendMessage) {
                            try {
                                //Wait for bot to start as the above method blocks the thread
                                if (bot.isConnected()) {
                                    Thread.sleep(1000);
                                    bot.sendIRC().message("#" + channel, message);
                                }
                            } catch (Exception e) {
                                new WriteFileHandler(null, weakContext, "ERROR", null, "Error sending to chat: " + e.toString(), true).run();
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * Method for checking if on WiFi network
     * @return boolean isOnAndConnected
     */
    private boolean checkIfWifiIsOnAndConnected() {
        if (wifiMgr != null && wifiMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            return wifiInfo.getNetworkId() > 0;
        } else {
            return false;
        }
    }
}