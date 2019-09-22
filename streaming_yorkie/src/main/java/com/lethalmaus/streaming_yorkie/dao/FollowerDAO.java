package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.entity.Follower;

import java.util.List;

/**
 * FollowerDAO Interface
 * @author LethalMaus
 */
@Dao
public interface FollowerDAO {

    /**
     * Gets a User by id
     * @author LethalMaus
     * @param id Twitch id
     * @return User
     */
    @Query("SELECT * FROM follower WHERE id = :id")
    Follower getUserById(int id);

    /**
     * Gets a User based on status and position
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @param offset Position relevant to status
     * @return User
     */
    @Query("SELECT * FROM follower WHERE status LIKE :status ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    Follower getUserByStatusAndPosition(String status, int offset);

    /**
     * Gets a current User based on position
     * @author LethalMaus
     * @param offset Position relevant to status
     * @return User
     */
    @Query("SELECT * FROM follower WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    Follower getCurrentUserByPosition(int offset);

    /**
     * Gets all Users who haven't changed last_updated and don't contain the status 'EXCLUDED'
     * @author LethalMaus
     * @param last_updated Long of when it was last updated
     * @return List of Users
     */
    @Query("SELECT * FROM follower WHERE last_updated <> :last_updated AND status NOT LIKE 'EXCLUDED'")
    List<Follower> getUnfollowedUsers(Long last_updated);

    /**
     * Gets the last updated Users to check if a full update is needed
     * @author LethalMaus
     * @param last_updated Long of when it was last updated
     * @return Array of User Ids
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
     * Delete User by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM follower WHERE id = :id")
    void deleteUserById(int id);

    /**
     * Inserts a User and replaces on conflict
     * @author LethalMaus
     * @param follower User
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(Follower follower);

    /**
     * Updates existing User
     * @author LethalMaus
     * @param follower User
     */
    @Update
    void updateUser(Follower follower);

    /**
     * Update User status by Id
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @param id int
     */
    @Query("UPDATE follower SET status = :status WHERE id = :id")
    void updateUserStatusById(String status, int id);
}
