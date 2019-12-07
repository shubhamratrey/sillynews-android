package com.sillylife.sillynews.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created on 24/09/18.
 */
@Entity(tableName = "schedule")
data class ScheduleEntity(@PrimaryKey var id: Int,
                          var timestamp: Long = 0L,
                          var slug: String? = null,
                          var title: String? = null,
                          var status: String? = null,
                          @ColumnInfo(name = "start_time") var startTime: String? = null,
                          @ColumnInfo(name = "end_time") var endTime: String? = null,
                          @ColumnInfo(name = "icon_url") var iconUrl: String? = null,
                          @ColumnInfo(name = "day") var day: String? = null,
                          var raw: String? = null) {

    constructor() : this(-1, 0L, null, null, null, null, null, null, null)
}