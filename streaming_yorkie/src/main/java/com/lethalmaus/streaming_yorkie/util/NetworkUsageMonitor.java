package com.lethalmaus.streaming_yorkie.util;

import android.content.Context;
import android.net.TrafficStats;

import com.lethalmaus.streaming_yorkie.file.WriteFileHandler;

import java.lang.ref.WeakReference;

/**
 * Tool for tracking network usage
 * @author LethalMaus
 */
public class NetworkUsageMonitor {

    private WeakReference<Context> weakContext;
    private long dataUsageInKbits;

    /**
     * Constructor for NetworkUsageMonitor tool
     * @param weakContext weak context reference
     */
    public NetworkUsageMonitor(WeakReference<Context> weakContext) {
        dataUsageInKbits = 0;
        this.weakContext = weakContext;
        if (weakContext != null && weakContext.get() != null) {
            try {
                long bytesReceived = TrafficStats.getUidRxBytes(weakContext.get().getPackageManager().getApplicationInfo("com.lethalmaus.streaming_yorkie", 0).uid);
                long bytesTransmitted = TrafficStats.getUidTxBytes(weakContext.get().getPackageManager().getApplicationInfo("com.lethalmaus.streaming_yorkie", 0).uid);
                dataUsageInKbits = (bytesReceived + bytesTransmitted) / 125L;
            } catch (Exception e) {
                new WriteFileHandler(new WeakReference<>(weakContext.get()), "ERROR", null, "Could not get Lurk Service network usage | " + e.toString(), true).run();
            }
        }
    }

    /**
     * Gets the difference in network data usage since last call
     * @author LethalMaus
     * @return difference in network usage since last call
     */
    public long getNetworkUsageDifference() {
        if (weakContext != null && weakContext.get() != null && dataUsageInKbits > 0) {
            try {
                long bytesReceived = TrafficStats.getUidRxBytes(weakContext.get().getPackageManager().getApplicationInfo("com.lethalmaus.streaming_yorkie", 0).uid);
                long bytesTransmitted = TrafficStats.getUidTxBytes(weakContext.get().getPackageManager().getApplicationInfo("com.lethalmaus.streaming_yorkie", 0).uid);
                long difference = ((bytesReceived + bytesTransmitted) / 125L) - dataUsageInKbits;
                dataUsageInKbits += difference;
                return difference;
            } catch (Exception e) {
                new WriteFileHandler(new WeakReference<>(weakContext.get()), "ERROR", null, "Could not get Lurk Service network usage | " + e.toString(), true).run();
            }
        }
        return 0;
    }
}
