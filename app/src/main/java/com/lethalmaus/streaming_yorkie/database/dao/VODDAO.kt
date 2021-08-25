package com.lethalmaus.streaming_yorkie.database.dao

import androidx.room.*
import com.lethalmaus.streaming_yorkie.database.entity.VODEntity

@Dao
interface VODDAO {

    @Query("SELECT * FROM vod WHERE id = :id")
    fun getVODById(id: Int): VODEntity?

    @Query("SELECT id FROM vod WHERE last_updated <> :lastUpdated AND exported = 0")
    fun getExpiredVODs(lastUpdated: Long): IntArray?

    @Query("SELECT * FROM vod WHERE excluded = 0 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getCurrentVODByPosition(offset: Int): VODEntity?

    @Query("SELECT * FROM vod WHERE excluded = 0 AND exported = 1 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getExportedVODByPosition(offset: Int): VODEntity?

    @Query("SELECT * FROM vod WHERE excluded = 1 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    fun getExcludedVODByPosition(offset: Int): VODEntity?

    @Query("SELECT exported FROM vod WHERE id = :id")
    fun isVODExported(id: Int): Boolean

    @get:Query("SELECT COUNT(id) FROM vod WHERE excluded = 0")
    val getCurrentVODsCount: Int

    @get:Query("SELECT COUNT(id) FROM vod WHERE excluded = 0 AND exported = 1")
    val getExportedVODsCount: Int

    @get:Query("SELECT COUNT(id) FROM vod WHERE excluded = 1")
    val getExcludedVODsCount: Int

    @get:Query("SELECT COUNT(id) FROM vod")
    val getVODsCount: Int

    @Query("SELECT id FROM vod WHERE last_updated <> :lastUpdated ORDER BY created_at DESC LIMIT 1")
    fun getLastVOD(lastUpdated: Long?): IntArray?

    @Query("DELETE FROM vod WHERE id = :id")
    fun deleteVODById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVOD(vodEntity: VODEntity?)

    @Update
    fun updateVOD(vodEntity: VODEntity?)

    @Query("UPDATE vod SET exported = :exported WHERE id = :id")
    fun updateVODExportStatusById(exported: Boolean, id: Int)

    @Query("UPDATE vod SET excluded = :excluded WHERE id = :id")
    fun updateVODExclusionStatusById(excluded: Boolean, id: Int)

    @Query("UPDATE vod SET exported = 0 WHERE excluded = 0")
    fun removeExportedStatus()

    @Query("UPDATE vod SET excluded = 0")
    fun removeExcludedStatus()
}