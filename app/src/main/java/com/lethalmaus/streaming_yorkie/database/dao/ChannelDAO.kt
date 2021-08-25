package com.lethalmaus.streaming_yorkie.database.dao

import androidx.room.*
import com.lethalmaus.streaming_yorkie.database.entity.ChannelEntity

@Dao
interface ChannelDAO {

    @get:Query("SELECT * FROM channel LIMIT 1")
    val channel: ChannelEntity?

    @Query("SELECT * FROM channel WHERE id = :id")
    fun getChannelById(id: Int): ChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChannel(channelEntity: ChannelEntity?)

    @Update
    fun updateChannel(channelEntity: ChannelEntity?)
}