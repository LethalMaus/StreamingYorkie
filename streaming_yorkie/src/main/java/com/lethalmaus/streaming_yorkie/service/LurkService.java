package com.lethalmaus.streaming_yorkie.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
import com.lethalmaus.streaming_yorkie.request.StreamStatusRequestHandler;
import com.lethalmaus.streaming_yorkie.util.NetworkUsageMonitor;

import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
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
    private LurkEntity[] lurks;
    private ConnectivityManager connectivityManager;
    private Handler networkUsageHandler;
    private Runnable networkUsageRunnable;
    private boolean networkUsageMonitorRunning;
    private int noNetworkUsageCount;
    private String token = "";
    private Map<String, PircBotX> botManager;
    private boolean serviceRestart;
    private StringBuilder channelNames;

    private boolean wifiOnly = false;
    private boolean informChannel = false;
    private String message = "";

    @Override
    public void onCreate() {
        notificationManager =  NotificationManagerCompat.from(getApplicationContext());
        networkUsageMonitorRunning = false;
        showNotification(false);
        weakContext = new WeakReference<>(getApplicationContext());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        noNetworkUsageCount = 10;
        serviceRestart = false;
        streamingYorkieDB = StreamingYorkieDB.getInstance(getApplicationContext());
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (new File(getFilesDir().toString() + File.separator + "TOKEN").exists()) {
            token = new ReadFileHandler(null, new WeakReference<>(getApplicationContext()), "TOKEN").readFile();
        }
        botManager = new HashMap<>();
        if (new File(getFilesDir().toString() + File.separator + "SETTINGS_LURK").exists()) {
            try {
                JSONObject settings = new JSONObject(new ReadFileHandler(null, weakContext, "SETTINGS_LURK").readFile());
                wifiOnly = settings.getBoolean(Globals.SETTINGS_WIFI_ONLY);
                informChannel = settings.getBoolean(Globals.SETTINGS_LURK_INFORM);
                message = settings.getString(Globals.SETTINGS_LURK_MESSAGE);
                if (message.isEmpty()) {
                    message = "!lurk";
                }
            } catch (JSONException e) {
                new WriteFileHandler(null, weakContext, Globals.FILE_ERROR, null, "AutoLurk: Error reading settings | " + e.toString(), true).run();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    //This is needed for the Lurk WebView, even though its not recommended & considered dangerous. Hence the Lint suppression
    @SuppressLint("SetJavaScriptEnabled")
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "STOP_LURK":
                    stopSelf();
                    break;
                case "PAUSE_LURK":
                    networkUsageHandler.removeCallbacks(networkUsageRunnable);
                    networkUsageMonitorRunning = false;
                    webView.destroy();
                    webView = null;
                    showNotification(true);
                    break;
                default:
                    if (!wifiOnly || checkIfWifiIsOnAndConnected()) {
                        Handler serviceHandler = new Handler();
                        Runnable serviceRunnable = () -> {
                            WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                    : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                            params.gravity = Gravity.TOP | Gravity.START;
                            params.x = 0;
                            params.y = 0;
                            params.width = 1;
                            params.height = 1;
                            webView = new WebView(LurkService.this);
                            webView.setWebViewClient(new WebViewClient());
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.getSettings().setDomStorageEnabled(true);
                            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                            webView.loadUrl("file:///" + getFilesDir() + File.separator + "LURK.HTML");
                            if (windowManager != null) {
                                windowManager.addView(webView, params);
                                showNotification(false);
                            }
                        };
                        new Thread() {
                            @Override
                            public void run() {
                                if (streamingYorkieDB.lurkDAO().getChannelsToBeLurkedCount() > 0) {
                                    final StringBuilder htmlInjection = new StringBuilder();
                                    new StreamStatusRequestHandler(null, weakContext, null) {
                                        @Override
                                        public void onCompletion() {
                                            lurks = streamingYorkieDB.lurkDAO().getOnlineLurks();
                                            if (lurks.length <= 0) {
                                                stopSelf();
                                                return;
                                            }
                                            String[] offline = streamingYorkieDB.lurkDAO().getOfflineLurks();
                                            for (String offlineStreamer : offline) {
                                                PircBotX bot = botManager.get(offlineStreamer);
                                                if (bot != null) {
                                                    bot.stopBotReconnect();
                                                    bot.close();
                                                }
                                            }
                                            channelNames = new StringBuilder();
                                            String prefix = "";
                                            for (LurkEntity lurk : lurks) {
                                                htmlInjection.append(lurk.getHtml());
                                                channelNames.append(prefix);
                                                prefix = ", ";
                                                channelNames.append(lurk.getChannelName());
                                                activateChatBot(lurk.getChannelName(), informChannel && !lurk.isChannelInformedOfLurk());
                                                lurk.setChannelInformedOfLurk(true);
                                                streamingYorkieDB.lurkDAO().updateLurk(lurk);
                                            }
                                            new WriteFileHandler(null, weakContext, "LURK.HTML", null, htmlInjection.toString(), false).writeToFileOrPath();
                                            serviceHandler.post(serviceRunnable);
                                        }
                                    }.newRequest(streamingYorkieDB.lurkDAO().getChannelIdsToBeLurked()).initiate().sendRequest();
                                }
                            }
                        }.start();
                    } else {
                        stopSelf();
                    }
                    break;
            }
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
            Intent intent = new Intent(getApplicationContext(), LurkService.class).setAction("RESTART_LURK");
            if (Build.VERSION.SDK_INT < 28) {
                startService(intent);
            } else {
                startForegroundService(intent);
            }
        } else {
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
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.streaming_yorkie)
                .setContentTitle("Lurk")
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setOngoing(true);
        if (paused) {
            mBuilder.setContentText("Service paused for '" + lurks.length + "' streamers...");
            mBuilder.addAction(android.R.drawable.ic_media_play, "Start", PendingIntent.getService(this, 0,  new Intent(this, LurkService.class).setAction("START_LURK"), PendingIntent.FLAG_UPDATE_CURRENT));
            mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"Stop", PendingIntent.getService(this, 0, new Intent(this, LurkService.class).setAction("STOP_LURK"), PendingIntent.FLAG_CANCEL_CURRENT));
        } else if (!networkUsageMonitorRunning && lurks != null) {
            mBuilder.setContentText("Service starting for '" + lurks.length + "' streamers...");
            mBuilder.addAction(android.R.drawable.ic_media_pause, "Pause", PendingIntent.getService(this, 0, new Intent(this, LurkService.class).setAction("PAUSE_LURK"), PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            mBuilder.setContentText("Lurk Service is starting...");
            mBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel,"Stop", PendingIntent.getService(this, 0, new Intent(this, LurkService.class).setAction("STOP_LURK"), PendingIntent.FLAG_CANCEL_CURRENT));
        }

        if (Build.VERSION.SDK_INT < 28) {
            notificationManager.notify(3, mBuilder.build());
        } else {
            startForeground(3, mBuilder.build());
        }

        if (!paused && !networkUsageMonitorRunning && lurks != null) {
            networkUsageMonitorRunning = true;
            final NetworkUsageMonitor networkUsageMonitor = new NetworkUsageMonitor(new WeakReference<>(getApplicationContext()));
            networkUsageHandler = new Handler();
            networkUsageRunnable = () -> {
                try {
                    if (lurks.length <= 0 || wifiOnly && !checkIfWifiIsOnAndConnected()) {
                        stopSelf();
                    }
                    long networkUsage = (networkUsageMonitor.getNetworkUsageDifference() / 5);
                    if (noNetworkUsageCount == 0) {
                        serviceRestart = true;
                        stopSelf();
                    } else if (networkUsage == 0) {
                        noNetworkUsageCount--;
                    } else {
                        noNetworkUsageCount = 10;
                    }
                    mBuilder.setContentText("@" + networkUsage + "kbit/s | " + lurks.length + " lurked: " + channelNames.toString());
                    notificationManager.notify(3, mBuilder.build());
                    if (networkUsageHandler != null) {
                        networkUsageHandler.postDelayed(networkUsageRunnable, 3000);
                    }
                } catch (Exception e) {
                    new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "Could not get Lurk Service network usage | " + e.toString(), true).run();
                }
            };
            networkUsageHandler.postDelayed(networkUsageRunnable, 5000);
        }
    }

    /**
     * Activates a chat bot in the users name and sends a message
     * @author LethalMaus
     * @param channel String
     * @param sendMessage boolean
     */
    private void activateChatBot(String channel, boolean sendMessage) {
        if (!botManager.containsKey(channel)) {
            new Thread() {
                @Override
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
                        try (PircBotX bot = new PircBotX(configuration)) {
                            new Thread() {
                                @Override
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
                                //Wait for bot to start as the above method blocks the thread
                                if (bot.isConnected()) {
                                    Thread.sleep(1000);
                                    bot.sendIRC().message("#" + channel, message);
                                }
                            }
                        } catch (Exception e) {
                            new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "Error initializing & sending to chat: " + e.toString(), true).run();
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
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            boolean isMetered = connectivityManager.isActiveNetworkMetered();
            return isConnected && !isMetered;
        } else {
            return false;
        }
    }
}