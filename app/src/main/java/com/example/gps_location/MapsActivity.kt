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
import android.view.View
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
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    val PERM_FLAG=99

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (isPermitted()){
            startProcess()
        }else{
            ActivityCompat.requestPermissions(this,permissions,PERM_FLAG)
        }
    }

    fun isPermitted():Boolean{

        for(perm in permissions){
            if (ContextCompat.checkSelfPermission(this,perm)!= PackageManager.PERMISSION_GRANTED){
                return false
            }
        }

        return true
    }

    fun startProcess(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        //loadLibraries()
        showLibraries()
        setUpdateLocationListener()


    }

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    /*private fun loadLibraries() {
        val retrofit = Retrofit.Builder()
            .baseUrl(SeoulmdcApi.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(SeoulmdcService::class.java)
        service.getLibraries(SeoulmdcApi.API_KEY, 698,701)
            .enqueue(object : Callback<mdc_Library> {

                override fun onResponse(call: Call<mdc_Library>,
                    response: Response<mdc_Library>
                ) {



                    Toast.makeText(this@MapsActivity, "안넘어감?", Toast.LENGTH_SHORT)
                    showLibraries()

                }

                override fun onFailure(call: Call<mdc_Library>, t: Throwable) {
                    //Log.e("라이브러리", "error=${t.localizedMessage}")
                    Toast.makeText(this@MapsActivity, "데이터를 가져올 수 없습니다", Toast.LENGTH_SHORT)
                        .show()
                }

            })

    }*/


    private fun showLibraries() {

            val latlngbounds = LatLngBounds.Builder()

            val jsonString=assets.open("clinic.json").reader().readText()
            val jsonArray=JSONArray(jsonString)

            for(index in 0..7) {


                val jsonObject = jsonArray.getJSONObject(index)
                val Latitude = jsonObject.getDouble("Latitude")
                val Longitude = jsonObject.getDouble("Longitude")
                val title=jsonObject.getString("Name")
                val position =
                    LatLng(Latitude.toDouble(), Longitude.toDouble())
                val marker = MarkerOptions().position(position).title(title)
                mMap.addMarker(marker)

                latlngbounds.include(position)
            }
            val bounds = latlngbounds.build()
            val padding = 0
            //val camera = CameraUpdateFactory.newLatLngBounds(bounds, padding)

       // val camera=CameraUpdateFactory.newCameraPosition(cameraOption)
            //mMap.moveCamera(camera)

    }

    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority= LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
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
    fun setLastLocation(location: Location){
        val myLocation=LatLng(location.latitude,location.longitude)

        val cameraOption=CameraPosition.Builder()
            .target(myLocation)
            .zoom(13.0f)
            .build()
        val camera=CameraUpdateFactory.newCameraPosition(cameraOption)

        //mMap.clear()
        val descriptor=getDescriptorFromDrawable(R.drawable.my_location)
        val marker=MarkerOptions()
            .position(myLocation)
            .title("I am here!")
            .icon(descriptor)
            mMap.addMarker(marker)
        mMap.moveCamera(camera)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERM_FLAG->{
                var check=true
                for(grant in grantResults){
                    if (grant!= PackageManager.PERMISSION_GRANTED){
                        check = false
                        break
                    }
                }
                if (check){
                    startProcess()
                }else{
                    Toast.makeText(this,"권한을 승인해야지만 앱을 사용할 수 있습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun getDescriptorFromDrawable(drawableId:Int):BitmapDescriptor{
        var bitmapDrawable: BitmapDrawable
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(drawableId) as BitmapDrawable
        }
        else{
            bitmapDrawable=resources.getDrawable(drawableId) as BitmapDrawable
        }

        val scaledBitmap= Bitmap.createScaledBitmap(bitmapDrawable.bitmap,100,100,false)

        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)

    }
}

