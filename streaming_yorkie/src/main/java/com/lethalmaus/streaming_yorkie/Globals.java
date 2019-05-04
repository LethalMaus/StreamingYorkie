package com.lethalmaus.streaming_yorkie;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.lethalmaus.streaming_yorkie.activity.Authorization;
import com.lethalmaus.streaming_yorkie.activity.Info;
import com.lethalmaus.streaming_yorkie.activity.Settings;

/**
 * Globals contains centralized constants & variables that are used throughout the whole app.
 * @author LethalMaus
 */
public class Globals {

    //ID of the app registered by Twitch
    public static final String CLIENTID = "tjots3mhxunw0sj2a20ka3wz39p7bp";
    //ID of Twitch
    public static final String TWITCHID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
    //User Object limit per request. Gets added to offset
    public static final int USER_REQUEST_LIMIT = 25;
    public static final int VOD_REQUEST_LIMIT = 10;

    //Directories for Followers
    public static final String FOLLOWERS_PATH = "FOLLOWERS";
    public static final String FOLLOWERS_REQUEST_PATH = "FOLLOWERS_REQUESTED";
    public static final String FOLLOWERS_NEW_PATH = "FOLLOWERS_NEW";
    public static final String FOLLOWERS_CURRENT_PATH = "FOLLOWERS_CURRENT";
    public static final String FOLLOWERS_UNFOLLOWED_PATH = "FOLLOWERS_UNFOLLOWED";
    public static final String FOLLOWERS_EXCLUDED_PATH = "FOLLOWERS_EXCLUDED";

    //Directories for Following
    public static final String FOLLOWING_PATH = "FOLLOWING";
    public static final String FOLLOWING_REQUEST_PATH = "FOLLOWING_REQUESTED";
    public static final String FOLLOWING_NEW_PATH = "FOLLOWING_NEW";
    public static final String FOLLOWING_CURRENT_PATH = "FOLLOWING_CURRENT";
    public static final String FOLLOWING_UNFOLLOWED_PATH = "FOLLOWING_UNFOLLOWED";
    public static final String FOLLOWING_EXCLUDED_PATH = "FOLLOWING_EXCLUDED";

    //Directories for F4F
    public static final String F4F_FOLLOWED_NOTFOLLOWING_PATH = "FOLLOWED_NOTFOLLOWING";
    public static final String F4F_FOLLOW4FOLLOW_PATH = "FOLLOW4FOLLOW";
    public static final String F4F_NOTFOLLOWED_FOLLOWING_PATH = "NOTFOLLOWED_FOLLOWING";
    public static final String F4F_EXCLUDED_PATH = "F4F_EXCLUDED";

    //Flags to prevent errors
    public static final String FLAG_PATH = "FLAGS";
    public static final String FOLLOW_REQUEST_RUNNING_FLAG = "FOLLOW_REQUEST_RUNNING";

    //Directories for VODs
    public static final String VOD_PATH = "VODS";
    public static final String VOD_EXPORTED_PATH = "VODS_EXPORTED";

    //AutoFollow settings object keys
    public static final String AUTOFOLLOW = "Auto-Follow";
    public static final String AUTOFOLLOW_INTERVAL = "Auto-FollowInterval";
    public static final String AUTOFOLLOW_INTERVAL_UNIT = "Auto-FollowIntervalUnit";
    public static final String AUTOFOLLOW_NOTIFICATIONS = "Auto-FollowNotifications";

    //AutoFollow settings AUTOFOLLOW values
    public static final String AUTOFOLLOW_OFF = "OFF";
    public static final String AUTOFOLLOW_FOLLOW = "FOLLOW";
    public static final String AUTOFOLLOW_UNFOLLOW = "UNFOLLOW";
    public static final String AUTOFOLLOW_FOLLOWUNFOLLOW = "FOLLOW_UNFOLLOW";

    //AutoFollow settings AUTOFOLLOW_INTERVAL_UNIT values
    public static final String AUTOFOLLOW_INTERVAL_UNIT_MINUTES = "MINUTES";
    public static final String AUTOFOLLOW_INTERVAL_UNIT_HOURS = "HOURS";
    public static final String AUTOFOLLOW_INTERVAL_UNIT_DAYS = "DAYS";

    //AutoFollow settings AUTOFOLLOW_NOTIFICATIONS values
    public static final String NOTIFICATION_FOLLOW = "NOTIFICATION_FOLLOW";
    public static final String NOTIFICATION_UNFOLLOW = "NOTIFICATION_UNFOLLOW";

    public static final String NOTIFICATION_VODEXPORT = "NOTIFICATION_VODEXPORT";

    //Notifications ID, Name & Description
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_ID = "AUTOFOLLOW_NOTIFICATION";
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_NAME = "AutoFollow";
    public static final String AUTOFOLLOW_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify user if new followers are followed and unfollowers that are unfollowed";

    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_ID = "AUTOVODEXPORT_NOTIFICATION";
    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_NAME = "AutoVODExport";
    public static final String AUTOVODEXPORT_NOTIFICATION_CHANNEL_DESCRIPTION = "Notify user if new VODs have been exported";

    /**
     * Options menu to be available throughout app
     * @author LethalMaus
     * @param activity activity requiring options
     * @param item MenuItem that is selected eg. Info or Settings
     * @return boolean whether an option was successfully selected
     */
    public static boolean onOptionsItemsSelected(Activity activity, MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_info:
                intent = new Intent(activity, Info.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_settings:
                intent = new Intent(activity, Settings.class);
                activity.startActivity(intent);
                return true;
            case R.id.menu_logout:
                intent = new Intent(activity, Authorization.class);
                activity.startActivity(intent);
                return true;
        }
        return false;
    }
}
