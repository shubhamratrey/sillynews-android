package com.sillylife.sillynews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sillylife.sillynews.constants.Constants
import com.sillylife.sillynews.database.dao.ScheduleDao
import com.sillylife.sillynews.database.dao.TaskDao
import com.sillylife.sillynews.database.entities.ScheduleEntity
import com.sillylife.sillynews.database.entities.TaskEntity


@Database(entities = [TaskEntity::class, ScheduleEntity::class], version = 1, exportSchema = false)
abstract class SillyNewsDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao

    companion object {

        private var INSTANCE: SillyNewsDatabase? = null

        fun getInstance(context: Context): SillyNewsDatabase? {
            if (INSTANCE == null) {
                synchronized(SillyNewsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            SillyNewsDatabase::class.java,
                            Constants.SILLYNEWS_DATABASE)
                            .addMigrations(MIGRATION_1_TO_2)
                            .allowMainThreadQueries().build()
                }
            }
            return INSTANCE
        }


        private val MIGRATION_1_TO_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `schedule`(" +
                                    "`id` INTEGER NOT NULL," +
                                    "`timestamp` INTEGER NOT NULL DEFAULT(0)," +
                                    "`slug` Text," +
                                    "`title` Text," +
                                    "`status` Text," +
                                    "`start_time` Text," +
                                    "`end_time` Text," +
                                    "`icon_url` Text," +
                                    "`day` Text," +
                                    "`raw` Text," +
                                    "PRIMARY KEY(`id`))"
                    )
                    database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `task`(" +
                                    "`id` INTEGER NOT NULL," +
                                    "`timestamp` INTEGER NOT NULL DEFAULT(0)," +
                                    "`title` Text," +
                                    "`rank` INTEGER NOT NULL DEFAULT(0)," +
                                    "`status` Text," +
                                    "`schedule_id` Text," +
                                    "`added_on` Text," +
                                    "`raw` Text," +
                                    "PRIMARY KEY(`id`))"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
