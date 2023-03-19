package com.example.gps_location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.gps_location.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*


import org.json.JSONArray


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
        var imm:InputMethodManager?=null
        imm=getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        val card_view = binding.cardView
        card_view.visibility = View.GONE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        showLibraries(googleMap)
        //setUpdateLocationListener()

        googleMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng) {
                card_view.visibility = View.GONE

                softkeyboardHide()

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
                var index= arr[3].toInt()
                var drawableTypedArray=resources.obtainTypedArray(R.array.images)

                binding.imageView.setImageResource(drawableTypedArray.getResourceId(index,-1))
                return false
            }
        })


        val loc_btn=findViewById<ImageButton>(R.id.my_loc)

        val bar=findViewById<ImageView>(R.id.search_bar)


        bar.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            }

        loc_btn.setOnClickListener{
            setUpdateLocationListener()
        }

        val S_Lng=intent.getDoubleExtra("Searched_Lng", 0.0)
        val S_Lat=intent.getDoubleExtra("Searched_Lat", 0.0)

        if (S_Lng!=0.0) {

            val S_LatLng=LatLng(S_Lat,S_Lng)
            val cameraOption = CameraPosition.Builder()
                .target(S_LatLng)
                .zoom(13.0f)
                .build()
            val camera = CameraUpdateFactory.newCameraPosition(cameraOption)
            mMap.moveCamera(camera)
        }

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
            val counter = index.toString()

            markertag?.tag = address as String + "/" +
                    number as String + "/" +
                    time as String + "/" +
                    counter as String
            mMap.addMarker(marker)


            latlngbounds.include(position)
        }


        val bounds = latlngbounds.build()
        val padding = 0
    }

    lateinit var fusedLocationClient: FusedLocationProviderClient


    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create().run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            //interval = 10000
        }

        val locationCallback: LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        Log.d("로케이션", "$i ${location.latitude},${location.longitude}}")
                        setLastLocation(location)
                    }
                }
                //Log.d("여까지 오냐?","옴")
                /*let {
                    for ((i, location) in it.locations.withIndex()) {
                        Log.d("로케이션", "$i ${location.latitude},${location.longitude}}")
                        setLastLocation(location)
                    }
                }*/
            }
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest(),
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

        val descriptor = getDescriptorFromDrawable(R.drawable.person)
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

    fun softkeyboardHide() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}









