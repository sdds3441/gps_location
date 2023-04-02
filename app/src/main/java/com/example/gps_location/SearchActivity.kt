package com.example.gps_location

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mancj.materialsearchbar.MaterialSearchBar
import org.json.JSONArray


class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val lv = findViewById<ListView>(R.id.mListView)
        val searchBar = findViewById<MaterialSearchBar>(R.id.search_bar)
        searchBar.setHint("Search")
        //음성검색모드 끄기
        searchBar.setSpeechMode(false)
        //검색어 목록 넣기
        val jsonString = assets.open("clinic.json").reader().readText()
        val jsonArray = JSONArray(jsonString)

        var titles= mutableListOf<String>()
        var Lat=mutableListOf<Double>()
        var Lng=mutableListOf<Double>()

        for (index in 0..7) {

            val jsonObject = jsonArray.getJSONObject(index)
            val title = jsonObject.getString("Name")
            val Latitude = jsonObject.getDouble("Latitude")
            val Longitude = jsonObject.getDouble("Longitude")

            Lat.add(Latitude)
            Lng.add(Longitude)
            titles.add(title)
        }

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles)

        lv.setAdapter(adapter)
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {
                TODO("Not yet implemented")
            }
            //검색창 누른 상태 여부 확인
            override fun onSearchStateChanged(enabled: Boolean) {
                //맞으면 리스트뷰 보이게 설정
                if(enabled){
                    lv.visibility = View.VISIBLE
                }else{ //아니면 안 보이게
                    lv.visibility = View.INVISIBLE
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                TODO("Not yet implemented")
            }

        })

        searchBar.addTextChangeListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            //검색어 변경하면 ListView 내용 변경
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

        })

        //ListView 내의 아이템 누르면 Toast 발생
        lv.setOnItemClickListener { _, _, position, _ ->

            val intent = Intent(this, MapsActivity::class.java)
                .putExtra("Searched_Lat", Lat[position].toDouble())
                .putExtra("Searched_Lng", Lng[position])
            Log.d("됬나?",Lat[position].toString())
            startActivity(intent)

            val bundle=Bundle()
            bundle.putDouble("Searched_Lat",Lat[position].toDouble())
            bundle.putDouble("Searched_Lng",Lng[position].toDouble())


        /*Toast.makeText(
                this@SearchActivity,
                adapter.getItem(position)!!.toString(),
                Toast.LENGTH_SHORT
            ).show()*/
        }
    }
}