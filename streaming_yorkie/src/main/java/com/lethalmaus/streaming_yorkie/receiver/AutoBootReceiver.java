package com.lethalmaus.streaming_yorkie.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.worker.AutoFollowWorker;
import com.lethalmaus.streaming_yorkie.worker.AutoVODExportWorker;

import java.lang.ref.WeakReference;

/**
 * Receiver for activating alarms & workers on reboot
 * @author LethalMaus
 */
public class AutoBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            WeakReference<Context> weakContext = new WeakReference<>(context);
            Globals.activateWorker(weakContext, Globals.FILE_SETTINGS_F4F, Globals.SETTINGS_AUTOFOLLOW, AutoFollowWorker.class, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_ID, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME, Globals.AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION);
            Globals.activateWorker(weakContext, Globals.FILE_SETTINGS_VOD, Globals.SETTINGS_AUTOVODEXPORT, AutoVODExportWorker.class, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME, Globals.AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION);
            Globals.activateAlarm(weakContext, Globals.SETTINGS_AUTOLURK, 10000);
        }
    }
}
