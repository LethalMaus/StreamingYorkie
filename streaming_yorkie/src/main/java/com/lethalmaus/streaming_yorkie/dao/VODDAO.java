package com.lethalmaus.streaming_yorkie.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.lethalmaus.streaming_yorkie.Globals;
import com.lethalmaus.streaming_yorkie.entity.VODEntity;

/**
 * VODDAO Interface
 * @author LethalMaus
 */
@Dao
public interface VODDAO {

    /**
     * Gets a VODEntity by id
     * @author LethalMaus
     * @param id int
     * @return VODEntity
     */
    @Query("SELECT * FROM vod WHERE id = :id")
    VODEntity getVODById(int id);

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
     * @return VODEntity
     */
    @Query("SELECT * FROM vod WHERE excluded = 0 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    VODEntity getCurrentVODByPosition(int offset);

    /**
     * Get current VODs by position
     * @param offset position
     * @return VODEntity
     */
    @Query("SELECT * FROM vod WHERE excluded = 0 AND exported = 1 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    VODEntity getExportedVODByPosition(int offset);

    /**
     * Get current VODs by position
     * @param offset position
     * @return VODEntity
     */
    @Query("SELECT * FROM vod WHERE excluded = 1 ORDER BY created_at DESC LIMIT 1 OFFSET :offset")
    VODEntity getExcludedVODByPosition(int offset);

    /**
     * Sees if a VODEntity is already exported
     * @author LethalMaus
     * @param id int
     * @return VODEntity
     */
    @Query("SELECT exported FROM vod WHERE id = :id")
    boolean isVODExported(int id);

    /**
     * Get 'Current' VODEntity Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod WHERE excluded = 0")
    int getCurrentVODsCount();

    /**
     * Get 'Exported' VODEntity Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod WHERE excluded = 0 AND exported = 1")
    int getExportedVODsCount();

    /**
     * Get 'Excluded' VODEntity Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod WHERE excluded = 1")
    int getExcludedVODsCount();

    /**
     * Get total VODEntity Count
     * @author LethalMaus
     * @return int count
     */
    @Query("SELECT COUNT(id) FROM vod")
    int getVODsCount();

    /**
     * Gets the last updated VODs to check if a full update is needed
     * @author LethalMaus
     * @param last_updated Long of when it was last updated
     * @return Array of VODEntity Ids
     */
    @Query("SELECT id FROM vod WHERE last_updated <> :last_updated ORDER BY created_at DESC LIMIT " + Globals.USER_UPDATE_REQUEST_LIMIT)
    int[] getLastVODs(Long last_updated);

    /**
     * Delete VODEntity by Id
     * @author LethalMaus
     * @param id int
     */
    @Query("DELETE FROM vod WHERE id = :id")
    void deleteVODById(int id);

    /**
     * Inserts a VODEntity and replaces on conflict
     * @author LethalMaus
     * @param vodEntity VODEntity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVOD(VODEntity vodEntity);

    /**
     * Updates existing VODEntity
     * @author LethalMaus
     * @param vodEntity VODEntity
     */
    @Update
    void updateVOD(VODEntity vodEntity);

    /**
     * Updates exported status of VODEntity
     * @author LethalMaus
     * @param exported boolean
     * @param id int
     */
    @Query("UPDATE vod SET exported = :exported WHERE id = :id")
    void updateVODExportStatusById(boolean exported, int id);

    /**
     * Updates excluded status of VODEntity
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
