package com.example.cappucareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cappucareapp.ui.screens.PantallaInicio
import com.example.cappucareapp.ui.screens.PantallaListaProductos
import com.example.cappucareapp.ui.screens.PantallaMovimientos
import com.example.cappucareapp.ui.screens.PantallaRecursoNativo
import com.example.cappucareapp.ui.screens.PantallaRegistroProducto
import com.example.cappucareapp.ui.theme.CappucareTheme
import com.example.cappucareapp.utils.VibratorUtil
import com.example.cappucareapp.viewmodel.InventarioViewModel

class MainActivity : ComponentActivity() {

    private val inventarioViewModel: InventarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CappucareTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "pantalla_inicio"
                ) {
                    // 1. Menú Principal
                    composable("pantalla_inicio") {
                        PantallaInicio(
                            viewModel = inventarioViewModel,
                            onNavegarALista = { navController.navigate("lista_productos") },
                            onNavegarARegistro = { navController.navigate("registro_producto") },
                            onNavegarAMovimientos = { navController.navigate("pantalla_movimientos") },
                            onNavegarACamara = { navController.navigate("recurso_nativo") }
                        )
                    }

                    // 2. Lista de Inventario (CRUD: leer, filtrar, modificar stock y eliminar)
                    composable("lista_productos") {
                        val textoBusqueda by inventarioViewModel.textoBusqueda.collectAsState()
                        val soloStockCritico by inventarioViewModel.soloStockCritico.collectAsState()
                        val productosFiltrados by inventarioViewModel.productosFiltrados.collectAsState()

                        PantallaListaProductos(
                            productos = productosFiltrados,
                            textoBusqueda = textoBusqueda,
                            soloStockCritico = soloStockCritico,
                            onCambiarBusqueda = { inventarioViewModel.actualizarBusqueda(it) },
                            onAlternarFiltroCritico = { inventarioViewModel.alternarFiltroCritico() },
                            onGuardarNuevoStock = { codigo, nuevoStock ->
                                inventarioViewModel.modificarStockProducto(codigo, nuevoStock)
                            },
                            onEliminarProducto = { codigo ->
                                inventarioViewModel.eliminarProducto(codigo)
                            },
                            onNavegarARegistro = { navController.navigate("registro_producto") },
                            onNavegarACamara = { navController.navigate("recurso_nativo") },
                            onVolver = { navController.popBackStack() }
                        )
                    }

                    // 3. Registro de Producto con vibración háptica
                    composable("registro_producto") {
                        val context = LocalContext.current
                        PantallaRegistroProducto(
                            onGuardarProducto = { codigo: String, nombre: String, categoria: String, stock: Int, stockMin: Int, unidadMedida: String, ubicacion: String ->
                                inventarioViewModel.agregarProducto(
                                    codigo = codigo,
                                    nombre = nombre,
                                    categoria = categoria,
                                    stock = stock,
                                    stockMin = stockMin,
                                    unidadMedida = unidadMedida,
                                    ubicacion = ubicacion
                                )

                                VibratorUtil.dispararVibracion(context, 250)
                                navController.popBackStack()
                            },
                            onVolver = { navController.popBackStack() }
                        )
                    }

                    // 4. Historial de Movimientos
                    composable("pantalla_movimientos") {
                        val movimientos by inventarioViewModel.listaMovimientos.collectAsState()
                        PantallaMovimientos(
                            movimientos = movimientos,
                            onVolver = { navController.popBackStack() }
                        )
                    }

                    // 5. Cámara Nativa
                    composable("recurso_nativo") {
                        PantallaRecursoNativo(
                            onVolver = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}