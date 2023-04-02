package com.example.gps_location

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.model.*


import org.json.JSONArray
import java.lang.Math.abs
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val PERM_FLAG = 99


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

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
        val card_view = binding.plCardView
        val mf=binding.scFrame


        card_view.visibility = View.GONE
        mf.panelHeight=220

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //setUpdateLocationListener()

        showLibraries(googleMap)

        googleMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng) {
                card_view.visibility = View.GONE
                mf.panelHeight=220

                softkeyboardHide()

            }
        })

        googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {

                if (marker.tag == null) {
                    card_view.visibility = View.GONE
                    mf.panelHeight=220

                    return false

                }

                var arr = marker.tag.toString().split("/")
                card_view.visibility = View.VISIBLE
                mf.panelHeight=0


                var centeradd=binding.parkAddLot
                var centername=binding.parkName
                var centerphone=binding.phoneNum
                var centeropen=binding.openTime

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
        else{
           setUpdateLocationListener()

        }
        Sc_adapter()
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
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval=1000
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

            }

        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()

        )

        mMap!!.setOnMapClickListener {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            val card_view = binding.plCardView
            val mf=binding.scFrame
            card_view.visibility = View.GONE
            mf.panelHeight=220
        }

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

        mMap.clear()
        mMap.addMarker(loc_marker)
        showLibraries(mMap)
        mMap.moveCamera(camera)
        nearby()

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

    fun nearby() {

        val jsonString = assets.open("clinic.json").reader().readText()
        val jsonArray = JSONArray(jsonString)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d("여기냐?",location.toString())
                for (index in 0..7) {

                    val jsonObject = jsonArray.getJSONObject(index)
                    val title = jsonObject.getString("Name")
                    val Latitude = jsonObject.getDouble("Latitude")
                    val Longitude = jsonObject.getDouble("Longitude")

                    val Lat_dif= location?.latitude?.minus(Latitude)
                    val Lng_dif= location?.longitude?.minus(Longitude)
                    if (Lat_dif?.let { abs(it) }!! <0.0091&& Lng_dif?.let { abs(it) }!! <0.0113)
                    {
                        Log.d("1키로보다 작대",title)
                    }

                }
        }
    }

    fun Sc_adapter() {
        var UserList = arrayListOf<place>(
            place("용산 아트홀","02-2199-7260","서울 용산구 녹사평대로 150 용산구종합행정타운","dragon_art_hall"),
            place("필로웨일 정신분석 심리상담센터","02-790-4260","서울 용산구 녹사평대로32길 10 해피하우스 302호","phlo"),
            place("마인드카페 정신건강의학과 의원","010-7445-9811","서울 용산구 장문로 23 몬드리안 서울 이태원 지하1층(아크앤 북 서점 내)","mind_cafe"),
            place("엔엑스 피트니스","02-792-4375","서울 용산구 녹사평대로 132 명보2빌딩 3층, 4층","nx_fit"),
            place("용산구 보건소","02-2199-8012","서울 용산구 녹사평대로 150 용산구종합행정타운","yongsan"),

            )
        val Adapter = Data(this, UserList)
        binding.listView.adapter = Adapter
    }
}









