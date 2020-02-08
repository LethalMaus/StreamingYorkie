package com.lethalmaus.streaming_yorkie.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.service.LurkService;

import java.lang.ref.WeakReference;

/**
 * Receiver for automating Lurking
 * @author LethalMaus
 */
public class AutoLurkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Globals.activateAlarm(new WeakReference<>(context), Globals.FILE_SETTINGS_LURK, Globals.SETTINGS_AUTOLURK, Globals.LURK_SERVICE_ALARM_ID, AutoLurkReceiver.class, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_ID, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_NAME, Globals.LURKSERVICE_NOTIFICATION_CHANNEL_DESCRIPTION, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
        }

        Intent lurkIntent = new Intent(context, LurkService.class);
        lurkIntent.setAction("AUTO_LURK");
        if (Build.VERSION.SDK_INT < 28) {
            context.startService(lurkIntent);
        } else {
            context.startForegroundService(lurkIntent);
        }
    }
}
