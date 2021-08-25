package com.lethalmaus.streaming_yorkie.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lethalmaus.streaming_yorkie.database.entity.F4FEntity
import com.lethalmaus.streaming_yorkie.database.entity.FollowerEntity
import com.lethalmaus.streaming_yorkie.database.entity.FollowingEntity

@Dao
interface F4FDAO {

    @Query("SELECT * FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getFollow4FollowUserByPosition(offset: Int): FollowerEntity?

    @get:Query("SELECT COUNT(id) FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded)")
    val follow4FollowUserCount: Int

    @Query("SELECT * FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getFollowedNotFollowingUserByPosition(offset: Int): FollowerEntity?

    @get:Query("SELECT * FROM follower WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1")
    val followedNotFollowingUserForAutoFollow: FollowerEntity?

    @get:Query("SELECT COUNT(id) FROM follower WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM following WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded)")
    val followedNotFollowingUserCount: Int

    @Query("SELECT * FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getNotFollowedFollowingUserByPosition(offset: Int): FollowingEntity?

    @get:Query("SELECT * FROM following WHERE (status LIKE 'NEW' OR status LIKE 'CURRENT') AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded) ORDER BY created_at DESC LIMIT 1")
    val notFollowedFollowingUserForAutoFollow: FollowingEntity?

    @get:Query("SELECT COUNT(id) FROM following WHERE status NOT LIKE 'UNFOLLOWED' AND id NOT IN (SELECT id FROM follower WHERE status NOT LIKE 'UNFOLLOWED') AND id NOT IN (SELECT id FROM f4f_excluded)")
    val notFollowedFollowingUserCount: Int

    @Query("SELECT * FROM f4f_excluded ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getExcludedFollow4FollowUserByPosition(offset: Int): F4FEntity?

    @get:Query("SELECT COUNT(id) FROM f4f_excluded")
    val excludedFollow4FollowUserCount: Int

    @Query("DELETE FROM f4f_excluded WHERE id = :id")
    fun deleteUserById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(f4FEntity: F4FEntity?)
}