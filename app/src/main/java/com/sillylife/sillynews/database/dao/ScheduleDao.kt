package com.sillylife.sillynews.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.sillylife.sillynews.database.entities.ScheduleEntity

@Dao
interface ScheduleDao : BaseDao<ScheduleEntity> {

    @Query("SELECT * FROM schedule WHERE id = :id")
    fun getContentUnitById(id: Int): ScheduleEntity

//    @Query("SELECT * FROM content_unit WHERE parts_downloaded > 0 or is_downloaded_all = 1")
//    fun getCUWithMoreThanZeroPartDownloaded(): LiveData<List<ContentUnitEntity>>
//
//    @Query("SELECT * FROM content_unit WHERE parts_downloaded = 0")
//    fun getCUWithZeroPartsDownloaded(): LiveData<List<ContentUnitEntity>>
//
//    @Query("SELECT * FROM content_unit WHERE slug = :slug AND parts_downloaded > 0 or is_downloaded_all = 1")
//    fun getSpecificCUWithMoreThanZeroPartDownloaded(slug: String): LiveData<ContentUnitEntity>
//
//    @Query("SELECT * FROM content_unit WHERE title LIKE :name AND parts_downloaded > 0 ORDER BY timestamp ASC")
//    fun searchContentUnits(name: String): List<ContentUnitEntity>?
//
//    @Query("SELECT * FROM content_unit WHERE slug = :slug AND parts_downloaded = 0 or is_downloaded_all = 0")
//    fun getSpecificCUWithZeroPartsDownloaded(slug: String): LiveData<ContentUnitEntity>
//
//    @Query("SELECT * FROM content_unit WHERE slug = :slug")
//    fun getCUUpdate(slug: String): LiveData<ContentUnitEntity>
//
//    @Query("SELECT * FROM content_unit WHERE slug = :slug")
//    fun getContentUnit(slug: String): ContentUnitEntity
//
//    @Query("SELECT COUNT(*) FROM content_unit WHERE show_slug = :showSlug and parts_downloaded > 0")
//    fun getContentUnitByShowForDownloads(showSlug: String): Int
//
//    @Query("SELECT * FROM content_unit order by id desc limit 1")
//    fun getLastInserted(): ContentUnitEntity
}
