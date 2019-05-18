package com.lethalmaus.streaming_yorkie.worker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.activity.VODs;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.VODExportRequestHandler;
import com.lethalmaus.streaming_yorkie.request.VODRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Worker for automating VOD Exports
 * @author LethalMaus
 */
public class AutoVODExportWorker extends Worker {

    private WeakReference<Context> weakContext;
    private boolean publicize;
    private boolean split;

    /**
     * Constructor for AutoFollowWorker for automating Following & Unfollowing
     * @author LethalMaus
     * @param context app context
     * @param params parameters for worker.super()
     */
    public AutoVODExportWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.weakContext = new WeakReference<>(context);
        try {
            if (new File(weakContext.get().getFilesDir() + File.separator + "SETTINGS_VOD").exists()) {
                JSONObject settings = new JSONObject(new ReadFileHandler(weakContext, "SETTINGS_VOD").readFile());
                publicize = settings.getBoolean(Globals.SETTINGS_VISIBILITY);
                split = settings.getBoolean(Globals.SETTINGS_SPLIT);
            } else {
                publicize = false;
                split = false;
            }
        } catch(JSONException e) {
            new WriteFileHandler(weakContext, "ERROR", null, e.toString()+"\n", true).run();
        }
    }

    @Override
    public @NonNull Result doWork() {
        new VODRequestHandler(null, weakContext, null, false) {
            @Override
            public void responseAction() {
                ArrayList<String> vods = new ReadFileHandler(weakContext, Globals.VOD_PATH).readFileNames();
                vods.removeAll(new ReadFileHandler(weakContext, Globals.VOD_EXCLUDED_PATH).readFileNames());
                for (int i = 0; i < vods.size(); i++) {
                    if (!new File(weakContext.get().getFilesDir() + File.separator + Globals.VOD_EXPORTED_PATH + File.separator + vods.get(i)).exists()) {
                        try {
                            final JSONObject vodObject = new JSONObject(new ReadFileHandler(weakContext, Globals.VOD_PATH + File.separator + vods.get(i)).readFile());
                            new VODExportRequestHandler(null, weakContext){
                                @Override
                                public void responseHandler(JSONObject response) {
                                    try {
                                        new WriteFileHandler(weakContext, Globals.VOD_EXPORTED_PATH + File.separator + vodObject.getString("_id"), null, new ReadFileHandler(weakContext, Globals.VOD_PATH + File.separator + vodObject.getString("_id")).readFile(), false).run();
                                        new WriteFileHandler(weakContext, Globals.NOTIFICATION_VODEXPORT + File.separator + vodObject.getString("_id"), null, null, false).writeToFileOrPath();
                                    } catch (JSONException e) {
                                        new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true);
                                    }
                                }
                            }.export(vods.get(i), vodObject.getString("title"), vodObject.getString("description"), vodObject.getString("tag_list"), publicize, split);
                        } catch (JSONException e) {
                            new WriteFileHandler(weakContext, "ERROR", null, e.toString() + "\n", true);
                        }
                    }
                }
                notifyUser(weakContext);
            }
        }.newRequest().sendRequest(0);
        return Result.success();
    }

    /**
     * Method for pushing notifications to inform user if VOD has been exported
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    private static void notifyUser(WeakReference<Context> weakContext) {
        int autoVODExportCount = new ReadFileHandler(weakContext, Globals.NOTIFICATION_VODEXPORT).countFiles();
        if (autoVODExportCount > 0 && weakContext != null && weakContext.get() != null) {
            Intent intent = new Intent(weakContext.get(), VODs.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(weakContext.get(), 0, intent, 0);

            String content = autoVODExportCount + " VODs have been exported.";

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(weakContext.get(), Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.streaming_yorkie)
                    .setContentTitle("AutoVODExport")
                    .setContentText(content)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(weakContext.get());
            notificationManager.notify(2, mBuilder.build());
        }
    }
}
