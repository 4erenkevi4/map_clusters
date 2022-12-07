package com.test.android.map_demo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "poses")
data class Pos(
    val latitude: String,
    val longitude: String,
    val _lat: Int,
    val _lon: Int,
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
