package com.lethalmaus.streaming_yorkie.file;

import android.content.Context;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.MockContext;

import org.junit.Test;

import java.io.File;
import java.lang.ref.WeakReference;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrganizeFileHandlerTest {

    private Context context = new MockContext().getMockContext();

    private OrganizeFileHandler organizeFileHandler;

    @Test
    public void shouldOrganizeFolders() {
        //Setup
        String currentUsersPath = "CURRENT_PATH_TEST";
        String newUsersPath = "NEW_PATH_TEST";
        String unfollowedUsersPath = "UNFOLLOWED_PATH_TEST";
        String excludedUsersPath = "EXCLUDED_PATH_TEST";
        String requestPath = "REQUEST_PATH_TEST";
        String usersPath = "USER_PATH_TEST";
        new WriteFileHandler(new WeakReference<>(context), newUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), currentUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), excludedUsersPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), requestPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), requestPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), requestPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST", null, null, false).writeToFileOrPath();
        //Test
        organizeFileHandler = new OrganizeFileHandler(null, new WeakReference<>(context), null, false, true);
        organizeFileHandler.setPaths(currentUsersPath, newUsersPath, unfollowedUsersPath, excludedUsersPath, requestPath, usersPath);
        organizeFileHandler.setDisplayPreferences(null, null, null, null);
        organizeFileHandler.organizeFolders();
        assertTrue(new File(unfollowedUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST").exists());
        assertFalse(new File(newUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST").exists());
        assertFalse(new File(currentUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST").exists());
        assertFalse(new File(excludedUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST").exists());
        assertTrue(new File(excludedUsersPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST").exists());
        assertFalse(new File(newUsersPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST").exists());
        assertFalse(new File(currentUsersPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST").exists());
        assertFalse(new File(unfollowedUsersPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST").exists());
        assertTrue(new File(newUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST").exists());
        assertTrue(new File(currentUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST").exists());
        assertFalse(new File(unfollowedUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST").exists());
        assertFalse(new File(excludedUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST").exists());
        assertTrue(new File(newUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST").exists());
        assertTrue(new File(currentUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST").exists());
        assertFalse(new File(unfollowedUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST").exists());
        assertFalse(new File(excludedUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST").exists());
        //Cleanup
        assertTrue(new File(unfollowedUsersPath + File.separator + "CURRENT_TO_UNFOLLOWED_AND_FROM_NEW_DELETED_USER_TEST").delete());
        assertTrue(new File(excludedUsersPath + File.separator + "REQUESTED_BUT_EXCLUDED_USER_TEST").delete());
        assertTrue(new File(newUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST").delete());
        assertTrue(new File(currentUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT1_USER_TEST").delete());
        assertTrue(new File(newUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST").delete());
        assertTrue(new File(currentUsersPath + File.separator + "REQUESTED_TO_NEW_AND_CURRENT2_USER_TEST").delete());
        assertTrue(new File(currentUsersPath).delete());
        assertTrue(new File(newUsersPath).delete());
        assertTrue(new File(excludedUsersPath).delete());
        assertTrue(new File(unfollowedUsersPath).delete());
    }

    @Test
    public void shouldOrganizeF4FFolders() {
        //Setup
        new WriteFileHandler(new WeakReference<>(context), Globals.FOLLOWERS_CURRENT_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), Globals.FOLLOWERS_CURRENT_PATH + File.separator + "F4F_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), Globals.FOLLOWING_CURRENT_PATH + File.separator + "F4F_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), Globals.FOLLOWING_CURRENT_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), Globals.FOLLOWERS_EXCLUDED_PATH + "_" + Globals.FOLLOWERS_CURRENT_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), Globals.FOLLOWING_EXCLUDED_PATH + "_" + Globals.FOLLOWING_CURRENT_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST", null, null, false).writeToFileOrPath();
        new WriteFileHandler(new WeakReference<>(context), Globals.F4F_EXCLUDED_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST", null, null, false).writeToFileOrPath();
        //Test
        organizeFileHandler = new OrganizeFileHandler(null, new WeakReference<>(context), null, false, false);
        organizeFileHandler.setPaths(Globals.F4F_FOLLOW4FOLLOW_PATH, Globals.F4F_FOLLOW4FOLLOW_PATH, Globals.F4F_FOLLOW4FOLLOW_PATH, Globals.F4F_FOLLOW4FOLLOW_PATH, Globals.F4F_FOLLOW4FOLLOW_PATH, Globals.F4F_FOLLOW4FOLLOW_PATH);
        organizeFileHandler.setDisplayPreferences(null, null, null, null);
        organizeFileHandler.organizeFolders();
        assertTrue(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST").exists());
        assertFalse(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST").exists());
        assertFalse(new File(Globals.F4F_EXCLUDED_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST").exists());
        assertTrue(new File(Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + "F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + "F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_EXCLUDED_PATH + File.separator + "F4F_TEST").exists());
        assertTrue(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST").exists());
        assertFalse(new File(Globals.F4F_EXCLUDED_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST").exists());
        assertTrue(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_EXCLUDED_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST").exists());
        assertTrue(new File(Globals.F4F_EXCLUDED_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST").exists());
        assertFalse(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST").exists());
        //Cleanup
        assertTrue(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST").delete());
        assertTrue(new File(Globals.F4F_FOLLOW4FOLLOW_PATH + File.separator + "F4F_TEST").delete());
        assertTrue(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST").delete());
        assertTrue(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST").delete());
        assertTrue(new File(Globals.F4F_EXCLUDED_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST").delete());
        assertTrue(new File(Globals.FOLLOWERS_CURRENT_PATH + File.separator + "FOLLOWER_NOT_IN_FOLLOWING_TEST").delete());
        assertTrue(new File(Globals.FOLLOWERS_CURRENT_PATH + File.separator + "F4F_TEST").delete());
        assertTrue(new File(Globals.FOLLOWING_CURRENT_PATH + File.separator + "F4F_TEST").delete());
        assertTrue(new File(Globals.FOLLOWING_CURRENT_PATH + File.separator + "FOLLOWING_NOT_IN_FOLLOWER_TEST").delete());
        assertTrue(new File(Globals.FOLLOWERS_EXCLUDED_PATH + "_" + Globals.FOLLOWERS_CURRENT_PATH + File.separator + "FOLLOWER_NOT_EXCLUDED_IN_F4F_TEST").delete());
        assertTrue(new File(Globals.FOLLOWING_EXCLUDED_PATH + "_" + Globals.FOLLOWING_CURRENT_PATH + File.separator + "FOLLOWING_EXCLUDED_IN_F4F_TEST").delete());
        assertTrue(new File(Globals.F4F_NOTFOLLOWED_FOLLOWING_PATH).delete());
        assertTrue(new File(Globals.F4F_FOLLOW4FOLLOW_PATH).delete());
        assertTrue(new File(Globals.F4F_FOLLOWED_NOTFOLLOWING_PATH).delete());
        assertTrue(new File(Globals.F4F_EXCLUDED_PATH).delete());
        assertTrue(new File(Globals.FOLLOWERS_CURRENT_PATH).delete());
        assertTrue(new File(Globals.FOLLOWING_CURRENT_PATH).delete());
        assertTrue(new File(Globals.FOLLOWERS_EXCLUDED_PATH + "_" + Globals.FOLLOWERS_CURRENT_PATH).delete());
        assertTrue(new File(Globals.FOLLOWING_EXCLUDED_PATH + "_" + Globals.FOLLOWING_CURRENT_PATH).delete());
    }


}
