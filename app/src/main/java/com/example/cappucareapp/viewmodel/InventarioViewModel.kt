package com.example.cappucareapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cappucareapp.model.Movimiento
import com.example.cappucareapp.model.Producto
import com.example.cappucareapp.network.RetrofitClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventarioViewModel : ViewModel() {

    private val _listaProductos = MutableStateFlow<List<Producto>>(emptyList())
    val listaProductos: StateFlow<List<Producto>> = _listaProductos.asStateFlow()

    private val _listaMovimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val listaMovimientos: StateFlow<List<Movimiento>> = _listaMovimientos.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _soloStockCritico = MutableStateFlow(false)
    val soloStockCritico: StateFlow<Boolean> = _soloStockCritico.asStateFlow()

    // Estado para la API Externa (Valor Dólar para importación de insumos)
    private val _valorDolar = MutableStateFlow("Cargando indicador...")
    val valorDolar: StateFlow<String> = _valorDolar.asStateFlow()

    // Estado de conexión con el servidor / MockAPI
    private val _estadoServidor = MutableStateFlow("Conectando...")
    val estadoServidor: StateFlow<String> = _estadoServidor.asStateFlow()

    val productosFiltrados: StateFlow<List<Producto>> = combine(
        _listaProductos,
        _textoBusqueda,
        _soloStockCritico
    ) { lista, query, critico ->
        var resultado = lista
        if (critico) {
            resultado = resultado.filter { it.stock <= it.stockMinimo }
        }
        if (query.isNotBlank()) {
            resultado = resultado.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.codigo.contains(query, ignoreCase = true) ||
                        it.categoria.contains(query, ignoreCase = true)
            }
        }
        resultado
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        cargarDesdeMicroservicio()
        cargarApiExterna()
    }

    // 1. Conexión con servidor / MockAPI (GET)
    fun cargarDesdeMicroservicio() {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.microservicioApi.obtenerProductosRemotos()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    _listaProductos.value = respuesta.body()!!
                    _estadoServidor.value = "Conectado a Spring Boot (BD Activa)"
                } else {
                    _estadoServidor.value = "Error servidor: ${respuesta.code()}"
                }
            } catch (_: Exception) {
                _estadoServidor.value = "Sin conexión al microservicio local"
            }
        }
    }

    // 2. Conexión con API Externa (open.er-api.com)
    fun cargarApiExterna() {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitClient.externaApi.obtenerIndicadores()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val valorClp = respuesta.body()!!.rates["CLP"] ?: 950.0
                    _valorDolar.value = "USD Ref. Insumos: $$valorClp CLP"
                } else {
                    _valorDolar.value = "USD Ref. Insumos: $950.0 CLP (Est.)"
                }
            } catch (_: Exception) {
                _valorDolar.value = "USD Ref. Insumos: $950.0 CLP (Est.)"
            }
        }
    }

    // CREATE (POST)
    fun agregarProducto(
        codigo: String,
        nombre: String,
        categoria: String,
        stock: Int,
        stockMin: Int,
        unidadMedida: String,
        ubicacion: String
    ) {
        val nuevoProducto = Producto(
            codigo = codigo,
            nombre = nombre,
            categoria = categoria,
            stock = stock,
            stockMinimo = stockMin,
            unidadMedida = unidadMedida,
            ubicacion = ubicacion
        )

        _listaProductos.value = _listaProductos.value + nuevoProducto
        registrarMovimientoInterno(
            codigo = codigo,
            nombre = nombre,
            tipo = "ENTRADA",
            cantidad = stock,
            motivo = "Ingreso por registro inicial"
        )

        viewModelScope.launch {
            try {
                RetrofitClient.microservicioApi.crearProductoRemoto(nuevoProducto)
            } catch (_: Exception) { }
        }
    }

    // UPDATE (PUT) - Modificar stock por código
    fun modificarStockProducto(codigo: String, nuevoStock: Int) {
        val stockValidado = if (nuevoStock >= 0) nuevoStock else 0
        var productoActualizado: Producto? = null

        _listaProductos.value = _listaProductos.value.map { producto ->
            if (producto.codigo == codigo) {
                val editado = producto.copy(stock = stockValidado)
                productoActualizado = editado
                editado
            } else {
                producto
            }
        }

        productoActualizado?.let { prod ->
            viewModelScope.launch {
                try {
                    RetrofitClient.microservicioApi.actualizarProductoRemoto(prod.codigo, prod)
                } catch (_: Exception) { }
            }
        }
    }

    // DELETE (DELETE) - Eliminar por código
    fun eliminarProducto(codigo: String) {
        _listaProductos.value = _listaProductos.value.filterNot { it.codigo == codigo }

        viewModelScope.launch {
            try {
                RetrofitClient.microservicioApi.eliminarProductoRemoto(codigo)
            } catch (_: Exception) { }
        }
    }

    private fun registrarMovimientoInterno(
        codigo: String,
        nombre: String,
        tipo: String,
        cantidad: Int,
        motivo: String
    ) {
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val nuevoMovimiento = Movimiento(
            id = "M${System.currentTimeMillis()}",
            codigoProducto = codigo,
            nombreProducto = nombre,
            tipo = tipo,
            cantidad = cantidad,
            motivo = motivo,
            fecha = fechaActual
        )
        _listaMovimientos.value = listOf(nuevoMovimiento) + _listaMovimientos.value
    }

    fun actualizarBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }

    fun alternarFiltroCritico() {
        _soloStockCritico.value = !_soloStockCritico.value
    }
}