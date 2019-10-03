package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.lethalmaus.streaming_yorkie.entity.F4FEntity;
import com.lethalmaus.streaming_yorkie.entity.UserEntity;

/**
 * F4FDAO Interface
 * @author LethalMaus
 */
@Dao
public interface F4FDAO {

    /**
     * Get Follow4Follow UserEntity by position
     * @author LethalMaus
     * @param offset int
     * @return UserEntity
     */
    @Query("SELECT * FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    UserEntity getFollow4FollowUserByPosition(int offset);

    /**
     * Get Follow4Follow UserEntity count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded)")
    int getFollow4FollowUserCount();

    /**
     * Get Followed_NotFollowing UserEntity by position
     * @author LethalMaus
     * @param offset int
     * @return UserEntity
     */
    @Query("SELECT * FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    UserEntity getFollowedNotFollowingUserByPosition(int offset);

    /**
     * Get Followed_NotFollowing UserEntity for AutoFollow
     * @author LethalMaus
     * @return UserEntity
     */
    @Query("SELECT * FROM follower WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1")
    UserEntity getFollowedNotFollowingUserForAutoFollow();

    /**
     * Get Followed_NotFollowing UserEntity count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded)")
    int getFollowedNotFollowingUserCount();

    /**
     * Get NotFollowed_Following UserEntity by position
     * @author LethalMaus
     * @param offset int
     * @return UserEntity
     */
    @Query("SELECT * FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    UserEntity getNotFollowedFollowingUserByPosition(int offset);

    /**
     * Get NotFollowed_Following UserEntity for AutoFollow
     * @author LethalMaus
     * @return UserEntity
     */
    @Query("SELECT * FROM following WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1")
    UserEntity getNotFollowedFollowingUserForAutoFollow();

    /**
     * Get NotFollowed_Following UserEntity count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded)")
    int getNotFollowedFollowingUserCount();

    /**
     * Get excluded Follow4Follow UserEntity by position
     * @author LethalMaus
     * @param offset int
     * @return UserEntity
     */
    @Query("SELECT * FROM f4f_excluded ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    F4FEntity getExcludedFollow4FollowUserByPosition(int offset);

    /**
     * Get excluded Follow4Follow UserEntity count
     * @author LethalMaus
     * @return count int
     */
    @Query("SELECT COUNT(id) FROM f4f_excluded")
    int getExcludedFollow4FollowUserCount();

    /**
     * Delete UserEntity by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM f4f_excluded WHERE id = :id")
    void deleteUserById(int id);

    /**
     * Inserts a UserEntity and replaces on conflict
     * @author LethalMaus
     * @param f4FEntity UserEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(F4FEntity f4FEntity);
}
