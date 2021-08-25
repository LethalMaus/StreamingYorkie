package com.lethalmaus.streaming_yorkie.database.dao

import androidx.room.*
import com.lethalmaus.streaming_yorkie.database.entity.FollowingEntity

@Dao
interface FollowingDAO {

    @Query("SELECT * FROM following WHERE id = :id")
    fun getUserById(id: Int): FollowingEntity?

    @Query("SELECT * FROM following WHERE status LIKE :status ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getUserByStatusAndPosition(status: String?, offset: Int): FollowingEntity?

    @Query("SELECT * FROM following WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getCurrentUserByPosition(offset: Int): FollowingEntity?

    @Query("SELECT * FROM following WHERE last_updated <> :lastUpdated AND status NOT LIKE 'EXCLUDED'")
    fun getUnfollowedUsers(lastUpdated: Long?): List<FollowingEntity?>?

    @Query("SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND last_updated >= :lastUpdated ORDER BY created_at DESC LIMIT 3")
    fun getLastUsers(lastUpdated: Long?): IntArray?

    @Query("SELECT COUNT(id) FROM following WHERE status LIKE :status")
    fun getUsersByStatusCount(status: String?): Int

    @Query("DELETE FROM following WHERE id = :id")
    fun deleteUserById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(followingEntity: FollowingEntity?)

    @Update
    fun updateUser(followingEntity: FollowingEntity?)

    @Query("UPDATE following SET status = :status WHERE id = :id")
    fun updateUserStatusById(status: String?, id: Int)
}