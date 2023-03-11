package com.example.gps_location

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gps_location.data.mdc_Library

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.gps_location.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.*

import org.json.JSONArray
import org.w3c.dom.NameList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val PERM_FLAG = 99


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERM_FLAG)
        }

    }

    fun isPermitted(): Boolean {

        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }

    fun startProcess() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        /*val addlist= mutableListOf<String>()
        val numlist=mutableListOf<String>()
        val namelist=mutableListOf<String>()
        val timelist=mutableListOf<String>()*/
        val card_view = binding.cardView
        card_view.visibility = View.GONE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //loadLibraries()
        showLibraries(googleMap)
        setUpdateLocationListener()

        googleMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng) {
                card_view.visibility = View.GONE
            }
        })

        googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {

                if (marker.tag == null) {
                    card_view.visibility = View.GONE
                    return false

                }

                var arr = marker.tag.toString().split("/")
                card_view.visibility = View.VISIBLE
                var centeradd = findViewById<TextView>(R.id.park_add_lot)
                var centername = findViewById<TextView>(R.id.park_name)
                var centerphone = findViewById<TextView>(R.id.phone_num)
                var centeropen = findViewById<TextView>(R.id.open_time)
                centername.text = marker.title
                centeradd.text = arr[0]
                centerphone.text = arr[1]
                centeropen.text = arr[2]
                return false
            }
        })
    }

    private fun showLibraries(googleMap: GoogleMap) {

        val latlngbounds = LatLngBounds.Builder()

        val jsonString = assets.open("clinic.json").reader().readText()
        val jsonArray = JSONArray(jsonString)
        for (index in 0..7) {

            val jsonObject = jsonArray.getJSONObject(index)
            val Latitude = jsonObject.getDouble("Latitude")
            val Longitude = jsonObject.getDouble("Longitude")
            val title = jsonObject.getString("Name")
            val position =
                LatLng(Latitude.toDouble(), Longitude.toDouble())
            val marker = MarkerOptions()
            marker.position(position)
            marker.title(title)
            val address = jsonObject.getString("Address")
            val number = jsonObject.getString("phoneNumber")
            val time = jsonObject.getString("Opentime")
            val markertag: Marker? = googleMap.addMarker(marker)
            markertag?.tag = address as String + "/" +
                    number as String + "/" +
                    time as String

            mMap.addMarker(marker)


            latlngbounds.include(position)
        }

        val bounds = latlngbounds.build()
        val padding = 0
    }

    private fun GoogleMap.setOnMapClickListener(onMarkerClickListener: OnMarkerClickListener) {

    }

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 100000

        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let {
                    for ((i, location) in it.locations.withIndex()) {
                        Log.d("로케이션", "$i ${location.latitude},${location.longitude}}")
                        setLastLocation(location)
                    }
                }
            }

        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()

        )
    }

    fun setLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)

        val cameraOption = CameraPosition.Builder()
            .target(myLocation)
            .zoom(13.0f)
            .build()
        val camera = CameraUpdateFactory.newCameraPosition(cameraOption)

        //mMap.clear()
        val descriptor = getDescriptorFromDrawable(R.drawable.my_location)
        val loc_marker = MarkerOptions()
            .position(myLocation)
            .title("현위치")
            .icon(descriptor)
        mMap.addMarker(loc_marker)
        val dis_marker: Marker
        mMap.moveCamera(camera)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERM_FLAG -> {
                var check = true
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        check = false
                        break
                    }
                }
                if (check) {
                    startProcess()
                } else {
                    Toast.makeText(this, "권한을 승인해야지만 앱을 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getDescriptorFromDrawable(drawableId: Int): BitmapDescriptor {
        var bitmapDrawable: BitmapDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(drawableId) as BitmapDrawable
        } else {
            bitmapDrawable = resources.getDrawable(drawableId) as BitmapDrawable
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 100, 100, false)

        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)

    }


}



