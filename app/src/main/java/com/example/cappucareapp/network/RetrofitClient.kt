package com.example.cappucareapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val MICROSERVICE_URL = "https://6afe43923a0e70bc8d5337a0.mockapi.io/"
    private const val EXTERNAL_API_URL = "https://open.er-api.com/"

    val microservicioApi: CappucareApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MICROSERVICE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CappucareApiService::class.java)
    }

    val externaApi: IndicadorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(EXTERNAL_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IndicadorApiService::class.java)
    }
}