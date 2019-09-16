package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.entity.VOD;

/**
 * VODDAO Interface
 * @author LethalMaus
 */
@Dao
public interface VODDAO {

    /**
     * Gets a VOD by id
     * @author LethalMaus
     * @param id int
     * @return VOD
     */
    @Query("SELECT * FROM vod WHERE id = :id")
    VOD getVODById(int id);

    /**
     * Get expired VODs based on timestamp
     * @author LethalMaus
     * @param last_updated long
     * @return int[]
     */
    @Query("SELECT id FROM vod WHERE last_updated <> :last_updated AND exported = 0")
    int[] getExpiredVODs(long last_updated);

    /**
     * Get current VODs by position
     * @param offset position
     * @return VOD
     */
    @Query("SELECT * FROM vod WHERE excluded = 0 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    VOD getCurrentVODByPosition(int offset);

    /**
     * Get current VODs by position
     * @param offset position
     * @return VOD
     */
    @Query("SELECT * FROM vod WHERE excluded = 0 AND exported = 1 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    VOD getExportedVODByPosition(int offset);

    /**
     * Get current VODs by position
     * @param offset position
     * @return VOD
     */
    @Query("SELECT * FROM vod WHERE excluded = 1 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    VOD getExcludedVODByPosition(int offset);

    /**
     * Sees if a VOD is already exported
     * @author LethalMaus
     * @param id int
     * @return VOD
     */
    @Query("SELECT exported FROM vod WHERE id = :id")
    boolean isVODExported(int id);

    /**
     * Get 'Current' VOD Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod WHERE excluded = 0")
    int getCurrentVODsCount();

    /**
     * Get 'Exported' VOD Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod WHERE excluded = 0 AND exported = 1")
    int getExportedVODsCount();

    /**
     * Get 'Excluded' VOD Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod WHERE excluded = 1")
    int getExcludedVODsCount();

    /**
     * Delete VOD by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM vod WHERE id = :id")
    void deleteVODById(int id);

    /**
     * Inserts a VOD and replaces on conflict
     * @author LethalMaus
     * @param vod VOD
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVOD(VOD vod);

    /**
     * Updates existing VOD
     * @author LethalMaus
     * @param vod VOD
     */
    @Update
    void updateVOD(VOD vod);

    /**
     * Updates exported status of VOD
     * @author LethalMaus
     * @param exported boolean
     * @param id int
     */
    @Query("UPDATE vod SET exported = :exported WHERE id = :id")
    void updateVODExportStatusById(boolean exported, int id);

    /**
     * Updates excluded status of VOD
     * @author LethalMaus
     * @param excluded boolean
     * @param id int
     */
    @Query("UPDATE vod SET excluded = :excluded WHERE id = :id")
    void updateVODExclusionStatusById(boolean excluded, int id);

    /**
     * Removes exported status of all VODs
     * @author LethalMaus
     */
    @Query("UPDATE vod SET exported = 0 WHERE excluded = 0")
    void removeExportedStatus();

    /**
     * Removes excluded status of all VODs
     * @author LethalMaus
     */
    @Query("UPDATE vod SET excluded = 0")
    void removeExcludedStatus();
}
