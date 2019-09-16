package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.entity.Follower;
import com.lethalmaus.streaming_yorkie.entity.Following;

import java.util.List;

/**
 * FollowingDAO Interface
 * @author LethalMaus
 */
@Dao
public interface FollowingDAO {

    /**
     * Gets all Users
     * @author LethalMaus
     * @return List of Users
     */
    @Query("SELECT * FROM following")
    List<Following> getAll();

    /**
     * Gets a User by id
     * @author LethalMaus
     * @param id Twitch id
     * @return User
     */
    @Query("SELECT * FROM following WHERE id = :id")
    Following getUserById(int id);

    /**
     * Gets a User based on status and position
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @param offset Position relevant to status
     * @return User
     */
    @Query("SELECT * FROM following WHERE status LIKE :status ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    Following getUserByStatusAndPosition(String status, int offset);

    /**
     * Gets a current User based on position
     * @author LethalMaus
     * @param offset Position relevant to status
     * @return User
     */
    @Query("SELECT * FROM following WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    Follower getCurrentUserByPosition(int offset);

    /**
     * Gets all Users who haven't changed last_updated and don't contain the status 'EXCLUDED'
     * @author LethalMaus
     * @param last_updated Long of when it was last updated
     * @return List of Users
     */
    @Query("SELECT * FROM following WHERE last_updated <> :last_updated AND status NOT LIKE 'EXCLUDED'")
    List<Following> getUnfollowedUsers(Long last_updated);

    /**
     * Gets the last 3 updated Users to check if a full update is needed
     * @author LethalMaus
     * @return Array of User Ids
     */
    @Query("SELECT id FROM following ORDER BY created_at DESC LIMIT 3")
    int[] getLastUsers();

    /**
     * Counts Users by status
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @return int Count
     */
    @Query("SELECT COUNT(id) FROM following WHERE status LIKE :status")
    int countUsersByStatus(String status);

    /**
     * Delete User by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM following WHERE id = :id")
    void deleteUserById(int id);

    /**
     * Inserts a User and replaces on conflict
     * @author LethalMaus
     * @param following User
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(Following following);

    /**
     * Updates existing User
     * @author LethalMaus
     * @param following User
     */
    @Update
    void updateUser(Following following);

    /**
     * Update User status by Id
     * @author LethalMaus
     * @param status String status eg. NEW, CURRENT, ...
     * @param id int
     */
    @Query("UPDATE following SET status = :status WHERE id = :id")
    void updateUserStatusById(String status, int id);

}
