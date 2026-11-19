package com.example.cappucareapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroProducto(
    onGuardarProducto: (String, String, String, Int, Int, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var unidadMedida by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    var errorValidacion by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Insumo Médico") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código (ej. P005)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Insumo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock Inicial") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = stockMinimo,
                onValueChange = { stockMinimo = it },
                label = { Text("Stock Mínimo de Alerta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = unidadMedida,
                onValueChange = { unidadMedida = it },
                label = { Text("Unidad de Medida (ej. Cajas, Unidades)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación en Bodega") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorValidacion) {
                Text(
                    text = "⚠️ Por favor, complete todos los campos correctamente.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val stockInt = stock.toIntOrNull() ?: 0
                    val stockMinInt = stockMinimo.toIntOrNull() ?: 0

                    if (codigo.isNotBlank() && nombre.isNotBlank() && categoria.isNotBlank() &&
                        unidadMedida.isNotBlank() && ubicacion.isNotBlank()
                    ) {
                        errorValidacion = false
                        onGuardarProducto(codigo, nombre, categoria, stockInt, stockMinInt, unidadMedida, ubicacion)
                    } else {
                        errorValidacion = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar e Ingresar Insumo")
            }
        }
    }
}