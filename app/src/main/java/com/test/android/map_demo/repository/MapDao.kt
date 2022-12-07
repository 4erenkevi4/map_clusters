package com.test.android.map_demo.repository

import androidx.room.*
import com.test.android.map_demo.data.Pos

@Dao
interface MapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPos(pos: Pos)

    @Query("SELECT * FROM poses WHERE _lat BETWEEN :lat1 AND :lat2 AND _lon BETWEEN :lon1 AND :lon2")
    suspend fun filteredByLat(lat1: Int, lat2: Int, lon1: Int, lon2: Int): List<Pos>

    @Query("SELECT COUNT(*) FROM poses")
    suspend fun getCountPoses(): Int

    @Query("SELECT * FROM poses")
    suspend fun getAllPoses(): List<Pos>

}