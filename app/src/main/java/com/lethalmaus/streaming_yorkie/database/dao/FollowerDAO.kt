package com.lethalmaus.streaming_yorkie.database.dao

import androidx.room.*
import com.lethalmaus.streaming_yorkie.database.entity.FollowerEntity

@Dao
interface FollowerDAO {

    @Query("SELECT * FROM follower WHERE id = :id")
    fun getUserById(id: Int): FollowerEntity?

    @Query("SELECT * FROM follower WHERE status LIKE :status ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getUserByStatusAndPosition(status: String?, offset: Int): FollowerEntity?

    @Query("SELECT * FROM follower WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getCurrentUserByPosition(offset: Int): FollowerEntity?

    @Query("SELECT * FROM follower WHERE last_updated <> :lastUpdated AND status NOT LIKE 'EXCLUDED'")
    fun getUnfollowedUsers(lastUpdated: Long?): List<FollowerEntity>?

    @Query("SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED' ORDER BY created_at DESC LIMIT 3")
    fun getLastUsers(): IntArray?

    @Query("SELECT COUNT(id) FROM follower WHERE status LIKE :status")
    fun countUsersByStatus(status: String?): Int

    @Query("DELETE FROM follower WHERE id = :id")
    fun deleteUserById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(followerEntity: FollowerEntity?)

    @Update
    fun updateUser(followerEntity: FollowerEntity?)

    @Query("UPDATE follower SET status = :status WHERE id = :id")
    fun updateUserStatusById(status: String?, id: Int)
}