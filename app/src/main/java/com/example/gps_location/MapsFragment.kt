//
//package com.example.gps_location
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import androidx.fragment.app.Fragment
//
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.inputmethod.InputMethodManager
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getSystemService
//import com.example.gps_location.databinding.ActivityMapsBinding
//import com.example.gps_location.databinding.FragmentMapsBinding
//import com.google.android.gms.location.*
//
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.*
//import org.json.JSONArray
//
//class MapsFragment : Fragment() {
//    val permissions = arrayOf(
//        android.Manifest.permission.ACCESS_FINE_LOCATION,
//        android.Manifest.permission.ACCESS_COARSE_LOCATION
//    )
//    val PERM_FLAG = 99
//    lateinit var mapsActivity: MapsActivity
//    private lateinit var mMap: GoogleMap
//    private lateinit var binding: FragmentMapsBinding
//
//    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
//    private val callback = OnMapReadyCallback { googleMap ->}
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {         // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        if (isPermitted()) {
//            startProcess()
//        } else {
//            ActivityCompat.requestPermissions(mapsActivity, permissions, PERM_FLAG)
//        }
//        return inflater.inflate(R.layout.fragment_maps, container, false)
//    }
//    fun isPermitted(): Boolean {
//
//        for (perm in permissions) {
//            if (ContextCompat.checkSelfPermission(
//                    mapsActivity,
//                    perm
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            }
//        }
//
//        return true
//    }
//
//    fun startProcess() {
//        val mapFragment = childFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(mapsActivity)
//    }
//
//    override fun onMapReadyCallback(googleMap: GoogleMap) {
//        mMap = googleMap
//        var imm: InputMethodManager?=null
//        imm=mapsActivity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//        val card_view = binding.plCardView
//        val mf=binding.scFrame
//
//
//        card_view.visibility = View.GONE
//        mf.panelHeight=220
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mapsActivity)
//
//        showLibraries(googleMap)
//
//        googleMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
//            override fun onMapClick(p0: LatLng) {
//                card_view.visibility = View.GONE
//                mf.panelHeight=220
//
//                softkeyboardHide()
//
//            }
//        })
//
//        googleMap!!.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
//            override fun onMarkerClick(marker: Marker): Boolean {
//
//                if (marker.tag == null) {
//                    card_view.visibility = View.GONE
//                    mf.panelHeight=220
//
//                    return false
//
//                }
//
//                var arr = marker.tag.toString().split("/")
//                card_view.visibility = View.VISIBLE
//                mf.panelHeight=0
//
//
//                var centeradd=binding.parkAddLot
//                var centername=binding.parkName
//                var centerphone=binding.phoneNum
//                var centeropen=binding.openTime
//                centername.text = marker.title
//                centeradd.text = arr[0]
//                centerphone.text = arr[1]
//                centeropen.text = arr[2]
//                var index= arr[3].toInt()
//                var drawableTypedArray=resources.obtainTypedArray(R.array.images)
//
//                binding.imageView.setImageResource(drawableTypedArray.getResourceId(index,-1))
//                return false
//            }
//        })
//
//
//        val loc_btn=binding.myLoc
//        val bar=binding.searchBar
//
//        bar.setOnClickListener{
//                val intent = Intent(mapsActivity, SearchActivity::class.java)
//                startActivity(intent)
//
//        }
//
//        loc_btn.setOnClickListener{
//            setUpdateLocationListener()
//
//        }
//
//
//        val S_Lng=arguments.getDouble("Searched_Lng",0.0)
//        val S_Lat=arguments.getDouble("Searched_Lat",0.0)
//
//
//
//        if (S_Lng!=0.0) {
//
//            val S_LatLng=LatLng(S_Lat,S_Lng)
//            val cameraOption = CameraPosition.Builder()
//                .target(S_LatLng)
//                .zoom(13.0f)
//                .build()
//            val camera = CameraUpdateFactory.newCameraPosition(cameraOption)
//            mMap.moveCamera(camera)
//        }
//        else{
//            setUpdateLocationListener()
//
//        }
//        Sc_adapter()
//    }
//
//    private fun showLibraries(googleMap: GoogleMap) {
//
//        val latlngbounds = LatLngBounds.Builder()
//
//        val jsonString = mapsActivity.assets.open("clinic.json").reader().readText()
//        val jsonArray = JSONArray(jsonString)
//        for (index in 0..7) {
//
//            val jsonObject = jsonArray.getJSONObject(index)
//            val Latitude = jsonObject.getDouble("Latitude")
//            val Longitude = jsonObject.getDouble("Longitude")
//            val title = jsonObject.getString("Name")
//            val position =
//                LatLng(Latitude.toDouble(), Longitude.toDouble())
//            val marker = MarkerOptions()
//            marker.position(position)
//            marker.title(title)
//            val address = jsonObject.getString("Address")
//            val number = jsonObject.getString("phoneNumber")
//            val time = jsonObject.getString("Opentime")
//            val markertag: Marker? = googleMap.addMarker(marker)
//            val counter = index.toString()
//
//            markertag?.tag = address as String + "/" +
//                    number as String + "/" +
//                    time as String + "/" +
//                    counter as String
//            mMap.addMarker(marker)
//
//
//            latlngbounds.include(position)
//        }
//
//
//        val bounds = latlngbounds.build()
//        val padding = 0
//    }
//
//    fun softkeyboardHide() {
//        val imm = mapsActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(mapsActivity.currentFocus?.windowToken, 0)
//    }
//    lateinit var fusedLocationClient: FusedLocationProviderClient
//    fun setUpdateLocationListener() {
//        val locationRequest = LocationRequest.create()
//        locationRequest.run {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval=1000
//        }
//
//
//
//        val locationCallback: LocationCallback
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult?.let {
//                    for ((i, location) in it.locations.withIndex()) {
//                        Log.d("로케이션", "$i ${location.latitude},${location.longitude}}")
//                            setLastLocation(location)
//                    }
//
//                }
//
//            }
//
//        }
//
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            Looper.myLooper()
//        )
//
//        mMap!!.setOnMapClickListener {
//            fusedLocationClient.removeLocationUpdates(locationCallback)
//            val card_view = binding.plCardView
//            val mf=binding.scFrame
//            card_view.visibility = View.GONE
//            mf.panelHeight=220
//        }
//
//    }
//
//    fun setLastLocation(location: Location) {
//        val myLocation = LatLng(location.latitude, location.longitude)
//
//        val cameraOption = CameraPosition.Builder()
//            .target(myLocation)
//            .zoom(13.0f)
//            .build()
//        val camera = CameraUpdateFactory.newCameraPosition(cameraOption)
//
//        val descriptor = getDescriptorFromDrawable(R.drawable.person)
//        val loc_marker = MarkerOptions()
//            .position(myLocation)
//            .title("현위치")
//            .icon(descriptor)
//
//        mMap.clear()
//        mMap.addMarker(loc_marker)
//        showLibraries(mMap)
//        mMap.moveCamera(camera)
//        nearby()
//
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        // 2. Context를 액티비티로 형변환해서 할당
//        mapsActivity = context as MapsActivity
//    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(callback)
//    }
//}*/
