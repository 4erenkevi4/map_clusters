package com.test.android.map_demo.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.android.map_demo.data.Pos


@Database(entities = [Pos::class], version = 1, exportSchema = false)

abstract class MapRoomDatabase : RoomDatabase() {
    abstract fun testDao(): MapDao

    companion object {

        @Volatile
        private var INSTANCE: MapRoomDatabase? = null

        fun getInstance(context: Context): MapRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            val instance = Room.databaseBuilder(
                context.applicationContext,
                MapRoomDatabase::class.java,
                "poses"
            ).createFromAsset("poses.db").build()
            INSTANCE = instance
            return instance

        }
    }
}