package com.example.gps_location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.gps_location.data.mdc_Library

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gps_location.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.LatLngBounds
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadLibraries()

        // Add a marker in Sydney and move the camera

    }
    private fun loadLibraries() {
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

                    val result = response.body() //as mdc_Library

                    //Toast.makeText(this@MapsActivity, "안넘어감?", Toast.LENGTH_SHORT)
                    showLibraries(result)

                }

                override fun onFailure(call: Call<mdc_Library>, t: Throwable) {
                    //Log.e("라이브러리", "error=${t.localizedMessage}")
                    Toast.makeText(this@MapsActivity, "데이터를 가져올 수 없습니다", Toast.LENGTH_SHORT)
                        .show()
                }

            })
        service.getLibraries(SeoulmdcApi.API_KEY, 0,1)
            .enqueue(object : Callback<mdc_Library> {

                override fun onResponse(call: Call<mdc_Library>,
                                        response: Response<mdc_Library>
                ) {

                    val result = response.body() //as mdc_Library

                    //Toast.makeText(this@MapsActivity, "안넘어감?", Toast.LENGTH_SHORT)
                    showLibraries(result)

                }

                override fun onFailure(call: Call<mdc_Library>, t: Throwable) {
                    //Log.e("라이브러리", "error=${t.localizedMessage}")
                    Toast.makeText(this@MapsActivity, "데이터를 가져올 수 없습니다", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }


    fun showLibraries(result: mdc_Library?) {


        result?.let {
            val latlngbounds = LatLngBounds.Builder()
            for(mdc_library in it.tbEntranceItem.row) {

                val position =
                    LatLng(mdc_library.LATITUDE.toDouble(), mdc_library.LONGITUDE.toDouble())
                val marker = MarkerOptions().position(position).title(mdc_library.SISUL_NM)
                mMap.addMarker(marker)

                latlngbounds.include(position)
            }
            val bounds = latlngbounds.build()
            val padding = 0
            val camera = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.moveCamera(camera)
        }
    }

}