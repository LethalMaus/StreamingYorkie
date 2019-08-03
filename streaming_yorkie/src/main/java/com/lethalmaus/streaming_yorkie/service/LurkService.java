package com.lethalmaus.streaming_yorkie.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.activity.Lurk;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.util.NetworkUsageMonitor;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for creating a Background WebView Lurk
 * @author LethalMaus
 */
public class LurkService extends Service {

    private NotificationManagerCompat notificationManager;
    private WindowManager windowManager;
    private WebView webView;
    private List<String> videos;
    private Handler networkUsageHandler;
    private Runnable networkUsageRunnable;
    private boolean networkUsageMonitorRunning;

    @Override
    public void onCreate() {
        notificationManager =  NotificationManagerCompat.from(getApplicationContext());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        networkUsageMonitorRunning = false;
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
            videos = new ArrayList<>();
            if (new ReadFileHandler(new WeakReference<Context>(this), Globals.LURK_PATH).countFiles() > 0) {
                List<String> lurkDataset = new ReadFileHandler(new WeakReference<Context>(this), Globals.LURK_PATH).readFileNames();
                for (int i = 0; i < lurkDataset.size(); i++) {
                    String video = new ReadFileHandler(new WeakReference<Context>(this), Globals.LURK_PATH + File.separator + lurkDataset.get(i)).readFile();
                    videos.add(video);
                }
            }
            if (videos.size() > 0) {
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
                params.gravity = Gravity.TOP | Gravity.START;
                params.x = 0;
                params.y = 0;
                params.width = 0;
                params.height = 0;

                webView = new WebView(this);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

                StringBuilder htmlInjection = new StringBuilder();
                for (int i = 0; i < videos.size(); i++) {
                    htmlInjection.append(videos.get(i));
                }
                new WriteFileHandler(new WeakReference<>(getApplicationContext()), "LURK.HTML", null, htmlInjection.toString(), false).writeToFileOrPath();
                webView.loadUrl("file:///" + getFilesDir() + File.separator + "LURK.HTML");
                windowManager.addView(webView, params);
            }
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
        networkUsageHandler.removeCallbacks(networkUsageRunnable);
        notificationManager.cancel(3);
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
            contentText = "Service paused for '" + videos.size() + "' streamers...";

            Intent stopLurkIntent = new Intent(this, LurkService.class);
            stopLurkIntent.setAction("STOP_LURK");
            stopLurk = PendingIntent.getService(this, 0, stopLurkIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } else {
            startPauseLurkIntent.setAction("PAUSE_LURK");
            startPauseLurkDrawable = android.R.drawable.ic_media_pause;
            startPauseLurkTitle = "Pause";
            contentText = "Service starting for '" + videos.size() + "' streamers...";
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
                        mBuilder.setContentText("Currently lurking " + videos.size() + " streamers @" + (networkUsage.getNetworkUsageDifference() / 3) + "kbit/s");
                        notificationManager.notify(3, mBuilder.build());
                        if (networkUsageHandler != null) {
                            networkUsageHandler.postDelayed(this, 3000);
                        }
                    } catch (Exception e) {
                        new WriteFileHandler(new WeakReference<>(getApplicationContext()), "ERROR", null, "Could not get Lurk Service network usage | " + e.toString(), true).run();
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
}
