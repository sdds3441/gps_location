package com.example.gps_location

import com.example.gps_location.data.mdc_Library
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

class SeoulmdcApi {
    companion object {
        const val DOMAIN = "http://openapi.seoul.go.kr:8088/"
        const val API_KEY = "5743776b436b6863313238734a745a43"

    }
}

interface SeoulmdcService{
    @GET("{api_key}/json/tbEntranceItem/{start}/{end}")
    fun getLibraries(@Path("api_key")key:String,@Path("start")start:Int,@Path("end")end:Int) : Call<mdc_Library>
}