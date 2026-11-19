package com.example.cappucareapp.network

import com.example.cappucareapp.model.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CappucareApiService {

    // GET: Obtener la lista de productos desde el servidor
    @GET("productos")
    suspend fun obtenerProductosRemotos(): Response<List<Producto>>

    // POST: Crear / registrar un nuevo producto en el servidor
    @POST("productos")
    suspend fun crearProductoRemoto(@Body producto: Producto): Response<Producto>

    // PUT: Actualizar un producto existente por su identificador en el servidor
    @PUT("productos/{id}")
    suspend fun actualizarProductoRemoto(
        @Path("id") id: String,
        @Body producto: Producto
    ): Response<Producto>

    // DELETE: Eliminar / dar de baja un producto por su identificador en el servidor
    @DELETE("productos/{id}")
    suspend fun eliminarProductoRemoto(@Path("id") id: String): Response<Unit>
}