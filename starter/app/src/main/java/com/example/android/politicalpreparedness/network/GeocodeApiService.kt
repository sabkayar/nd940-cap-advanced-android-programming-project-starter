package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.models.GeoCoding
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"


private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(GeocodeHttpClient.getClient())
        .baseUrl(BASE_URL)
        .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface GeocodeApiService {
    @GET("json")
    suspend fun getGeoCodes(@Query("latlng") latLong: String): GeoCoding
}

object GeocodeApi {
    val retrofitService: GeocodeApiService by lazy {
        retrofit.create(GeocodeApiService::class.java)
    }
}