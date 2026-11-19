package com.example.cappucareapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cappucareapp.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    viewModel: InventarioViewModel, // Recibimos el ViewModel para observar los estados de red
    onNavegarALista: () -> Unit,
    onNavegarARegistro: () -> Unit,
    onNavegarAMovimientos: () -> Unit,
    onNavegarACamara: () -> Unit
) {
    // Observamos el estado del servidor Spring Boot y de la API externa
    val estadoServidor by viewModel.estadoServidor.collectAsState()
    val valorDolar by viewModel.valorDolar.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cappucare - Gestión Médica") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta Informativa de Red (Microservicio Spring Boot + API Externa)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Servidor: $estadoServidor",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "API Externa: $valorDolar",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Tarjeta 1: Registrar Insumo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarARegistro() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Registrar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Registrar Nuevo Insumo",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Añada nuevos productos mediante el formulario",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Tarjeta 2: Ver Inventario (Apunta a PantallaListaProducto)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarALista() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.List, contentDescription = "Lista")
                    Column {
                        Text(text = "Inventario y Stock", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Consulte existencias y alertas críticas", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Tarjeta 3: Movimientos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarAMovimientos() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = "Movimientos")
                    Column {
                        Text(text = "Historial de Movimientos", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Revise entradas y salidas de insumos", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Tarjeta 4: Cámara / Recurso Nativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavegarACamara() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Cámara")
                    Column {
                        Text(text = "Escáner de Cámara", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Utilice la cámara para lectura rápida", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}