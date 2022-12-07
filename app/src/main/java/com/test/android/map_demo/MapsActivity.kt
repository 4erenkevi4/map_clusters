package com.test.android.map_demo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.test.android.map_demo.data.Pos
import com.test.android.map_demo.databinding.ActivityMapsBinding
import com.test.android.map_demo.repository.MapRepository
import com.test.android.map_demo.repository.MapRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private lateinit var binding: ActivityMapsBinding
    private lateinit var clusterManager: ClusterManager<MyItem>
    private var dialog: Dialog? = null
    private val posFlow = MutableSharedFlow<Pos>()
    private var isMapReady: Boolean = false

    private lateinit var testRepository: MapRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testRepository = MapRepositoryImpl(this)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        binding.root.setBackgroundColor(Color.TRANSPARENT)
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(R.layout.progressbar)
        builder.setCancelable(true)
        dialog = builder.create()
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lifecycleScope.launch() {
            posFlow.collect() {
                addItems(it)
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setUpClusterer() {
        clusterManager = ClusterManager(this, map)
        clusterManager.algorithm = GridBasedAlgorithm()
        map?.setOnCameraIdleListener(clusterManager)
        map?.setOnMarkerClickListener(clusterManager)

    }


    private fun getCurentposes(zoom: Int) {
        val map = map ?: return
        val currentLatitude = map.cameraPosition.target.latitude.toInt()
        val currentLongitude = map.cameraPosition.target.longitude.toInt()
      val level = when {
            zoom > 7 -> 0
            zoom < 4 -> 2
            else -> 1
        }
        lifecycleScope.launch(Dispatchers.IO) {
            setProgressDialog(true)
            try {
                val listlat = testRepository.filteredByLat(
                    currentLongitude - level,
                    currentLongitude + level,
                    currentLatitude - level,
                    currentLatitude + level
                )
                listlat.forEach { s ->
                    posFlow.emit(s)
                }
            } catch (e: Exception) {
                Toast.makeText(this@MapsActivity, e.message, Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }.invokeOnCompletion {
            setProgressDialog(false)
        }
    }

    private fun addItems(pos: Pos) {
        try {
            val offsetItem =
                MyItem(
                    pos.longitude.toDouble(),
                    pos.latitude.toDouble(),
                    "Title ${pos.longitude}",
                    "Snippet ${pos.latitude}"
                )
            clusterManager.addItem(offsetItem)
            clusterManager.cluster()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(50.4501, 30.5234), 7f))
        setUpClusterer()
        map?.setOnCameraIdleListener {
            clusterManager.clearItems()
            getCurentposes(map!!.cameraPosition.zoom.toDouble().toInt())
        }
    }


    inner class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) : ClusterItem {

        private val position: LatLng
        private val title: String
        private val snippet: String

        override fun getPosition(): LatLng {
            return position
        }

        override fun getTitle(): String {
            return title
        }

        override fun getSnippet(): String {
            return snippet
        }

        init {
            position = LatLng(lat, lng)
            this.title = title
            this.snippet = snippet
        }
    }

    private fun setProgressDialog(show: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (show) dialog!!.show() else dialog!!.dismiss()
        }
    }
}