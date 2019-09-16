package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.lethalmaus.streaming_yorkie.entity.F4F;
import com.lethalmaus.streaming_yorkie.entity.User;

/**
 * F4FDAO Interface
 * @author LethalMaus
 */
@Dao
public interface F4FDAO {

    /**
     * Get Follow4Follow User by position
     * @author LethalMaus
     * @param offset int
     * @return User
     */
    @Query("SELECT * FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    User getFollow4FollowUserByPosition(int offset);

    /**
     * Get Follow4Follow User count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded)")
    int getFollow4FollowUserCount();

    /**
     * Get Followed_NotFollowing User by position
     * @author LethalMaus
     * @param offset int
     * @return User
     */
    @Query("SELECT * FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    User getFollowedNotFollowingUserByPosition(int offset);

    /**
     * Get Followed_NotFollowing User for AutoFollow
     * @author LethalMaus
     * @return User
     */
    @Query("SELECT * FROM follower WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded) ORDER BY created_at DESC LIMIT 1")
    User getFollowedNotFollowingUserForAutoFollow();

    /**
     * Get Followed_NotFollowing User count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded)")
    int getFollowedNotFollowingUserCount();

    /**
     * Get NotFollowed_Following User by position
     * @author LethalMaus
     * @param offset int
     * @return User
     */
    @Query("SELECT * FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    User getNotFollowedFollowingUserByPosition(int offset);

    /**
     * Get NotFollowed_Following User for AutoFollow
     * @author LethalMaus
     * @return User
     */
    @Query("SELECT * FROM following WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded) ORDER BY created_at DESC LIMIT 1")
    User getNotFollowedFollowingUserForAutoFollow();

    /**
     * Get NotFollowed_Following User count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id from f4f_excluded)")
    int getNotFollowedFollowingUserCount();

    /**
     * Get excluded Follow4Follow User by position
     * @author LethalMaus
     * @param offset int
     * @return User
     */
    @Query("SELECT * FROM f4f_excluded ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    F4F getExcludedFollow4FollowUserByPosition(int offset);

    /**
     * Get excluded Follow4Follow User count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM f4f_excluded")
    int getExcludedFollow4FollowUserCount();

    /**
     * Delete User by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM f4f_excluded WHERE id = :id")
    void deleteUserById(int id);

    /**
     * Inserts a User and replaces on conflict
     * @author LethalMaus
     * @param f4f User
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(F4F f4f);
}
