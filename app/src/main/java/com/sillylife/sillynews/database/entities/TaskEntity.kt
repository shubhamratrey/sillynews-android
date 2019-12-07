package com.sillylife.sillynews.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created on 24/09/18.
 */
@Entity(tableName = "task")
data class TaskEntity(@PrimaryKey var id: Int,
                      var timestamp: Long = 0L,
                      var title: String? = null,
                      var rank: Int? = null,
                      var status: String? = null,
                      @ColumnInfo(name = "schedule_id") var scheduleId: String? = null,
                      @ColumnInfo(name = "added_on") var addedOn: String? = null,
                      var raw: String? = null) {
    constructor() : this(-1, 0L, null, null, null, null, null, null)
}