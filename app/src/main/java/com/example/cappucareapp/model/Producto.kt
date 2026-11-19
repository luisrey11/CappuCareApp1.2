package com.example.cappucareapp.model

data class Producto(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int,
    val unidadMedida: String,
    val ubicacion: String
)