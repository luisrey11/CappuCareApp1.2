package com.example.cappucareapp

import com.example.cappucareapp.viewmodel.InventarioViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: InventarioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = InventarioViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun agregarProducto_incrementaListaYRegistraMovimiento() {
        val cantidadInicial = viewModel.listaProductos.value.size

        viewModel.agregarProducto(
            codigo = "P999",
            nombre = "Gasas Estériles",
            categoria = "Curación",
            stock = 50,
            stockMin = 10,
            unidadMedida = "Unidades",
            ubicacion = "Estante C1"
        )

        val productosActuales = viewModel.listaProductos.value
        assertEquals(cantidadInicial + 1, productosActuales.size)
        assertTrue(productosActuales.any { it.codigo == "P999" && it.stock == 50 })
        assertTrue(viewModel.listaMovimientos.value.any { it.codigoProducto == "P999" && it.tipo == "ENTRADA" })
    }

    @Test
    fun modificarStockProducto_actualizaStockYEvitaNegativos() {
        viewModel.agregarProducto(
            codigo = "P500",
            nombre = "Guantes Quirúrgicos",
            categoria = "Protección",
            stock = 20,
            stockMin = 5,
            unidadMedida = "Cajas",
            ubicacion = "Estante A1"
        )

        viewModel.modificarStockProducto("P500", 35)
        val actualizado = viewModel.listaProductos.value.find { it.codigo == "P500" }
        assertEquals(35, actualizado?.stock)

        viewModel.modificarStockProducto("P500", -10)
        val enCero = viewModel.listaProductos.value.find { it.codigo == "P500" }
        assertEquals(0, enCero?.stock)
    }

    @Test
    fun eliminarProducto_quitaElProductoDeLaLista() {
        viewModel.agregarProducto(
            codigo = "P777",
            nombre = "Alcohol Gel",
            categoria = "Desinfección",
            stock = 15,
            stockMin = 5,
            unidadMedida = "Litros",
            ubicacion = "Bodega Central"
        )

        viewModel.eliminarProducto("P777")

        assertFalse(viewModel.listaProductos.value.any { it.codigo == "P777" })
    }

    @Test
    fun actualizarBusqueda_actualizaElEstadoDeTexto() {
        viewModel.actualizarBusqueda("Guantes")
        assertEquals("Guantes", viewModel.textoBusqueda.value)
    }

    @Test
    fun alternarFiltroCritico_cambiaEstadoBooleano() {
        assertFalse(viewModel.soloStockCritico.value)
        viewModel.alternarFiltroCritico()
        assertTrue(viewModel.soloStockCritico.value)
    }
}