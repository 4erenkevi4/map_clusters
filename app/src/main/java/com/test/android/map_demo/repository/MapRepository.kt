package com.test.android.map_demo.repository

import com.test.android.map_demo.data.Pos

interface MapRepository {
    suspend fun addPos(pos: Pos)
    suspend fun filteredByLat (lat1: Int,lat2: Int, lon1: Int, lon2: Int): List<Pos>
    suspend fun getCountPoses(): Int
    suspend fun getAllPoses():List<Pos>

}