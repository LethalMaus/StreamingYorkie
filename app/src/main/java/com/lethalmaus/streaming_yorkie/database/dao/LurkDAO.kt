package com.lethalmaus.streaming_yorkie.database.dao

import androidx.room.*
import com.lethalmaus.streaming_yorkie.database.entity.LurkEntity

@Dao
interface LurkDAO {

    @Query("SELECT * FROM lurk WHERE channelName LIKE :channelName")
    fun getLurkByChannelName(channelName: String?): LurkEntity?

    @Query("SELECT * FROM lurk WHERE channelId = :channelId")
    fun getLurkByChannelId(channelId: String?): LurkEntity?

    @Query("SELECT * FROM lurk ORDER BY channelName ASC LIMIT 1 OFFSET :offset")
    fun getLurkByPosition(offset: Int): LurkEntity?

    @Query("SELECT * FROM lurk WHERE channelIsToBeLurked = 1 ORDER BY channelName ASC LIMIT 1 OFFSET :offset")
    fun getChannelsToBeLurkedByPosition(offset: Int): LurkEntity?

    @get:Query("SELECT channelId FROM lurk WHERE channelIsToBeLurked = 1")
    val getChannelIdsToBeLurked: IntArray?

    @get:Query("SELECT * FROM lurk WHERE channelIsToBeLurked = 1 AND html IS NOT NULL")
    val getOnlineLurks: Array<LurkEntity?>?

    @get:Query("SELECT channelName FROM lurk WHERE html IS NULL")
    val getOfflineLurks: Array<String?>?

    @get:Query("SELECT COUNT() FROM lurk")
    val getLurkCount: Int

    @get:Query("SELECT COUNT() FROM lurk WHERE channelIsToBeLurked = 1")
    val getChannelsToBeLurkedCount: Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLurk(lurkEntity: LurkEntity?)

    @Update
    fun updateLurk(lurkEntity: LurkEntity?)

    @Query("DELETE FROM lurk WHERE channelName LIKE :channelName")
    fun deleteLurkByChannelName(channelName: String?)
}