package com.sillylife.sillynews.database

import com.sillylife.sillynews.database.entities.ScheduleEntity
import com.sillylife.sillynews.models.Schedule

object MapDbEntities {

    fun scheduleToEntity(schedule: Schedule): ScheduleEntity? {
        val entity = ScheduleEntity()
        if (schedule.slug != null) {
            entity.slug = schedule.slug
        }

        entity.id = if (schedule.id == null) 0 else schedule.id!!
        if (schedule.title != null) {
            entity.title = schedule.title
        }
        if (schedule.status != null) {
            entity.status = schedule.status
        }
        if (schedule.start_time != null) {
            entity.startTime = schedule.start_time
        }
        if (schedule.end_time != null) {
            entity.endTime = schedule.end_time
        }
        if (schedule.icon_url != null) {
            entity.iconUrl = schedule.icon_url
        }
        if (schedule.day != null) {
            entity.day = schedule.day
        }
        return entity
    }
}
