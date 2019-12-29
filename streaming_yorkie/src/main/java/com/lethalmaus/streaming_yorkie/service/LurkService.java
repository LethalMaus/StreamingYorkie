package com.lethalmaus.streaming_yorkie.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
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
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.util.NetworkUsageMonitor;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Service for creating a Background WebView Lurk
 * @author LethalMaus
 */
public class LurkService extends Service {

    private NotificationManagerCompat notificationManager;
    private WindowManager windowManager;
    private WebView webView;
    private StreamingYorkieDB streamingYorkieDB;
    private String[] channels;
    private StringBuilder channelNames;
    private Handler networkUsageHandler;
    private Runnable networkUsageRunnable;
    private boolean networkUsageMonitorRunning;
    private String token = "";

    @Override
    public void onCreate() {
        notificationManager =  NotificationManagerCompat.from(getApplicationContext());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        networkUsageMonitorRunning = false;
        streamingYorkieDB = StreamingYorkieDB.getInstance(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    //This is needed for the Lurk WebView, even though its not recommended & considered dangerous. Hence the Lint suppression
    @SuppressLint("SetJavaScriptEnabled")
    public int onStartCommand(Intent intent, int flags, int startId) {
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
            new Thread() {
                public void run() {
                    channels = streamingYorkieDB.lurkDAO().getChannelsToBeLurked();
                    if (channels.length > 0) {
                        channelNames = new StringBuilder();
                        for (int i = 0; i < channels.length; i++) {
                            activateChatBot(channels[i]);
                            channelNames.append(channels[i]);
                            if (i < (channels.length - 1)) {
                                channelNames.append(", ");
                            }
                        }
                    }
                }
            }.start();
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
/*
                if (new File(getFilesDir().toString() + File.separator + "TOKEN").exists()) {
                    token = new ReadFileHandler(null, new WeakReference<>(getApplicationContext()), "TOKEN").readFile();
                }
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "OAuth " + token);
                webView.loadUrl("file:///" + getFilesDir() + File.separator + "LURK.HTML", headers);
 */
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
        Toast.makeText(this,"Stopped lurking", Toast.LENGTH_SHORT).show();
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
            contentText = "Service paused for '" + channels.length + "' streamers...";

            Intent stopLurkIntent = new Intent(this, LurkService.class);
            stopLurkIntent.setAction("STOP_LURK");
            stopLurk = PendingIntent.getService(this, 0, stopLurkIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            startPauseLurkIntent.setAction("PAUSE_LURK");
            startPauseLurkDrawable = android.R.drawable.ic_media_pause;
            startPauseLurkTitle = "Pause";
            contentText = "Service starting for '" + channels.length + "' streamers...";
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
            final NetworkUsageMonitor networkUsage = new NetworkUsageMonitor(new WeakReference<>(getApplicationContext()));
            networkUsageHandler = new Handler();
            networkUsageRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        mBuilder.setContentText("@" + (networkUsage.getNetworkUsageDifference() / 3) + "kbit/s | " + channels.length + " lurked: " + channelNames );
                        notificationManager.notify(3, mBuilder.build());
                        if (networkUsageHandler != null) {
                            networkUsageHandler.postDelayed(this, 3000);
                        }
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
    private void activateChatBot(String channel) {
        new Thread() {
            public void run() {
                ChannelEntity channelEntity = StreamingYorkieDB.getInstance(getApplicationContext()).channelDAO().getChannel();
                if (channelEntity != null) {
                    Configuration configuration = new Configuration.Builder()
                            .setAutoNickChange(false)
                            .setOnJoinWhoEnabled(false)
                            .setCapEnabled(true)
                            .addCapHandler(new EnableCapHandler("twitch.tv/membership"))
                            .addServer("irc.twitch.tv")
                            .setName(Integer.toString(channelEntity.getId()).toLowerCase())
                            .setServerPassword("oauth:" + token)
                            .addAutoJoinChannel("#" + channel.toLowerCase())
                            .addListener(new ListenerAdapter(){})
                            .buildConfiguration();
                    try {
                        PircBotX bot = new PircBotX(configuration);
                        bot.startBot();
                    } catch (Exception e) {
                        new WriteFileHandler(null, new WeakReference<>(getApplicationContext()), "ERROR", null, "Error starting chat: " + e.toString(), true).run();
                    }
                }
            }
        }.start();
    }
}