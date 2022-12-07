package com.test.android.map_demo.repository

import android.content.Context
import com.test.android.map_demo.data.Pos


class MapRepositoryImpl(context: Context) :
    MapRepository {
    private val MApDao: MapDao by lazy {
        val db = MapRoomDatabase.getInstance(context)
        db.mapDao()
    }

    override suspend fun addPos(pos: Pos) {
        MApDao.addPos(pos)
    }

    override suspend fun filteredByCoordinate(
        lat1: Int,
        lat2: Int,
        lon1: Int,
        lon2: Int
    ): List<Pos> {
        return MApDao.filteredByCoordinate(lat1, lat2, lon1, lon2)
    }

    override suspend fun getCountPoses(): Int {
        return MApDao.getCountPoses()
    }

    override suspend fun getAllPoses(): List<Pos> {
        return getAllPoses()
    }
}
