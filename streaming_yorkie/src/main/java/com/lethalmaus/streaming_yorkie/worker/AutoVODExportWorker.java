package com.lethalmaus.streaming_yorkie.worker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.R;
import com.lethalmaus.streaming_yorkie.activity.VODs;
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;
import com.lethalmaus.streaming_yorkie.entity.VOD;
import com.lethalmaus.streaming_yorkie.file.DeleteFileHandler;
import com.lethalmaus.streaming_yorkie.file.ReadFileHandler;
import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;
import com.lethalmaus.streaming_yorkie.request.VODExportRequestHandler;
import com.lethalmaus.streaming_yorkie.request.VODRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Worker for automating VOD Exports
 * @author LethalMaus
 */
public class AutoVODExportWorker extends Worker {

    private WeakReference<Context> weakContext;
    private boolean visibility;
    private boolean split;
    private int vodId;

    /**
     * Constructor for AutoVODExportWorker for automating VOD Exports
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
                visibility = settings.getBoolean(Globals.SETTINGS_VISIBILITY);
                split = settings.getBoolean(Globals.SETTINGS_SPLIT);
            } else {
                visibility = false;
                split = false;
            }
        } catch(JSONException e) {
            new WriteFileHandler(weakContext, "ERROR", null, "AutoVOD: Error reading settings | " + e.toString(), true).run();
        }
    }

    @Override
    public @NonNull Result doWork() {
        new VODRequestHandler(null, weakContext, null) {
            @Override
            public void onCompletion() {
                StreamingYorkieDB streamingYorkieDB = StreamingYorkieDB.getInstance(weakContext.get());
                VODExportRequestHandler vodExportRequestHandler = new VODExportRequestHandler(null, weakContext, null) {
                    @Override
                    public void onCompletion() {
                        new WriteFileHandler(weakContext, Globals.NOTIFICATION_VODEXPORT + File.separator + vodId, null, null, false).writeToFileOrPath();
                        notifyUser(weakContext);
                    }
                };
                int vodCount = streamingYorkieDB.vodDAO().getCurrentVODsCount();
                if (vodCount > 0) {
                    new WriteFileHandler(weakContext, Globals.FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE, null, null, false).writeToFileOrPath();
                }
                for (int i = 0; i < vodCount; i++) {
                    VOD vod = streamingYorkieDB.vodDAO().getCurrentVODByPosition(i);
                    vodId = vod.getId();
                    if (!vod.isExported()) {
                        JSONObject body = new JSONObject();
                        try {
                            body.put("title", vod.getTitle());
                            body.put("description", vod.getDescription());
                            body.put("tag_list", vod.getTag_list());
                            body.put("private", !visibility);
                            body.put("do_split", split);
                        } catch (JSONException e) {
                            new WriteFileHandler(weakContext, "ERROR", null, "AutoVOD: Twitch export content could not be set. | " + e.toString(), true).run();
                        }
                        vodExportRequestHandler.setVodId(vodId).setPostBody(body).sendRequest();
                    }
                }
            }
        }.initiate().sendRequest();
        return Result.success();
    }

    /**
     * Method for pushing notifications to inform channel if VOD has been exported
     * @author LethalMaus
     * @param weakContext weak reference context
     */
    private static void notifyUser(WeakReference<Context> weakContext) {
        int autoVODExportCount = new ReadFileHandler(weakContext, Globals.NOTIFICATION_VODEXPORT).countFiles();
        if (weakContext != null && weakContext.get() != null
                && new File(weakContext.get().getFilesDir() + File.separator + Globals.FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE).exists()
                && autoVODExportCount > 0) {
            new DeleteFileHandler(weakContext, null).deleteFileOrPath(Globals.FLAG_AUTOVODEXPORT_NOTIFICATION_UPDATE);
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
