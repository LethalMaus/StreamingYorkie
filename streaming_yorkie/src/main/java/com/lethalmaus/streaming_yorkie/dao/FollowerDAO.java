package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.entity.FollowerEntity;

import java.util.List;

/**
 * FollowerDAO Interface
 * @author LethalMaus
 */
@Dao
public interface FollowerDAO {

    /**
     * Gets a UserEntity by id
     * @author LethalMaus
     * @param id Twitch id
     * @return UserEntity
     */
    @Query("SELECT * FROM follower WHERE id = :id")
    FollowerEntity getUserById(int id);

    /**
     * Gets a UserEntity based on status and position
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @param offset Position relevant to status
     * @return UserEntity
     */
    @Query("SELECT * FROM follower WHERE status LIKE :status ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    FollowerEntity getUserByStatusAndPosition(String status, int offset);

    /**
     * Gets a current UserEntity based on position
     * @author LethalMaus
     * @param offset Position relevant to status
     * @return UserEntity
     */
    @Query("SELECT * FROM follower WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    FollowerEntity getCurrentUserByPosition(int offset);

    /**
     * Gets all Users who haven't changed last_updated and don't contain the status 'EXCLUDED'
     * @author LethalMaus
     * @param last_updated Long of when it was last updated
     * @return List of Users
     */
    @Query("SELECT * FROM follower WHERE last_updated <> :last_updated AND status NOT LIKE 'EXCLUDED'")
    List<FollowerEntity> getUnfollowedUsers(Long last_updated);

    /**
     * Gets the last updated Users to check if a full update is needed
     * @author LethalMaus
     * @param last_updated Long of when it was last updated
     * @return Array of UserEntity Ids
     */
    @Query("SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND last_updated == :last_updated ORDER BY created_at DESC LIMIT " + Globals.USER_UPDATE_REQUEST_LIMIT)
    int[] getLastUsers(Long last_updated);

    /**
     * Counts Users by status
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @return int Count
     */
    @Query("SELECT COUNT(id) FROM follower WHERE status LIKE :status")
    int countUsersByStatus(String status);

    /**
     * Delete UserEntity by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM follower WHERE id = :id")
    void deleteUserById(int id);

    /**
     * Inserts a UserEntity and replaces on conflict
     * @author LethalMaus
     * @param followerEntity UserEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(FollowerEntity followerEntity);

    /**
     * Updates existing UserEntity
     * @author LethalMaus
     * @param followerEntity UserEntity
     */
    @Update
    void updateUser(FollowerEntity followerEntity);

    /**
     * Update UserEntity status by Id
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @param id int
     */
    @Query("UPDATE follower SET status = :status WHERE id = :id")
    void updateUserStatusById(String status, int id);
}