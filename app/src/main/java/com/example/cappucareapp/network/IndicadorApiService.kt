package com.example.cappucareapp.network

import retrofit2.Response
import retrofit2.http.GET

// Modelo para mapear la respuesta JSON de ExchangeRate-API
data class ExchangeResponse(
    val result: String,
    val rates: Map<String, Double>
)

interface IndicadorApiService {
    @GET("v6/latest/USD")
    suspend fun obtenerIndicadores(): Response<ExchangeResponse>
}