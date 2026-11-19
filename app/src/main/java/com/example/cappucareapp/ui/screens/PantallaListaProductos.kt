package com.example.cappucareapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cappucareapp.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaProductos(
    productos: List<Producto>,
    textoBusqueda: String,
    soloStockCritico: Boolean,
    onCambiarBusqueda: (String) -> Unit,
    onAlternarFiltroCritico: () -> Unit,
    onGuardarNuevoStock: (String, Int) -> Unit,
    onEliminarProducto: (String) -> Unit,
    onNavegarARegistro: () -> Unit,
    onNavegarACamara: () -> Unit,
    onVolver: () -> Unit
) {
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var productoAEditarStock by remember { mutableStateOf<Producto?>(null) }
    var nuevoStockTexto by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cappucare - Inventario") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavegarACamara) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavegarARegistro) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = onCambiarBusqueda,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre, código o categoría...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            FilterChip(
                selected = soloStockCritico,
                onClick = onAlternarFiltroCritico,
                label = { Text("Ver solo Stock Crítico ⚠️") },
                leadingIcon = {
                    if (soloStockCritico) {
                        Icon(Icons.Default.Warning, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (soloStockCritico) "No hay insumos en stock crítico." else "No se encontraron productos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(productos, key = { it.codigo }) { producto ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(durationMillis = 400)) +
                                    slideInVertically(animationSpec = tween(durationMillis = 400)) { it / 2 }
                        ) {
                            TarjetaProducto(
                                producto = producto,
                                onVerMasClick = { productoSeleccionado = producto },
                                onEditarStockClick = {
                                    nuevoStockTexto = producto.stock.toString()
                                    productoAEditarStock = producto
                                },
                                onEliminarClick = { onEliminarProducto(producto.codigo) }
                            )
                        }
                    }
                }
            }
        }

        productoSeleccionado?.let { producto ->
            AlertDialog(
                onDismissRequest = { productoSeleccionado = null },
                title = { Text(text = producto.nombre) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Código: ${producto.codigo}")
                        Text(text = "Categoría: ${producto.categoria}")
                        Text(text = "Stock Actual: ${producto.stock} ${producto.unidadMedida}")
                        Text(text = "Stock Mínimo Permitido: ${producto.stockMinimo}")
                        Text(text = "Ubicación en Bodega: ${producto.ubicacion}")

                        if (producto.stock <= producto.stockMinimo) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚠️ Stock crítico - Requiere reposición inmediata",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { productoSeleccionado = null }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        productoAEditarStock?.let { prod ->
            AlertDialog(
                onDismissRequest = { productoAEditarStock = null },
                title = { Text(text = "Modificar stock - ${prod.nombre}") },
                text = {
                    OutlinedTextField(
                        value = nuevoStockTexto,
                        onValueChange = { nuevoStockTexto = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Nuevo stock (${prod.unidadMedida})") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val valor = nuevoStockTexto.toIntOrNull()
                            if (valor != null) {
                                onGuardarNuevoStock(prod.codigo, valor)
                            }
                            productoAEditarStock = null
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productoAEditarStock = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun TarjetaProducto(
    producto: Producto,
    onVerMasClick: () -> Unit,
    onEditarStockClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Código: ${producto.codigo} | Categoría: ${producto.categoria}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Disponibles: ${producto.stock} ${producto.unidadMedida}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Ubicación: ${producto.ubicacion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (producto.stock <= producto.stockMinimo) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ Stock crítico - Requiere reposición",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVerMasClick) {
                    Text(text = "Ver más")
                }
                IconButton(onClick = onEditarStockClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modificar stock"
                    )
                }
                IconButton(onClick = onEliminarClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar producto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}