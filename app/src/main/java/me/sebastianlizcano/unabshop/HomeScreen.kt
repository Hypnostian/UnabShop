package me.sebastianlizcano.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onClickLogout: () -> Unit = {}) {

    val auth = Firebase.auth
    val user = auth.currentUser
    val vm: HomeViewModel = viewModel()
    val ui = vm.ui.collectAsState().value

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text("Unab Shop", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Notifications, "Notificaciones")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.ShoppingCart, "Carrito")
                    }
                    IconButton(onClick = {
                        auth.signOut()
                        onClickLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFFF9900),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (user != null) {
                Text("Bienvenido: ${user.email}", fontSize = 18.sp)
            } else {
                Text("No hay usuario", fontSize = 18.sp)
            }

            Divider(color = Color.Gray, thickness = 1.dp)

            // --- FORMULARIO DE PRODUCTO ---
            OutlinedTextField(
                value = ui.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.descripcion,
                onValueChange = vm::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ui.precio,
                onValueChange = vm::onPrecioChange,
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { vm.guardarProducto() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
            ) {
                Text("Guardar producto")
            }

            if (ui.error != null) {
                Text(ui.error ?: "", color = Color.Red)
            }

            Divider(color = Color.Gray, thickness = 1.dp)

            // --- LISTADO DE PRODUCTOS ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ui.productos) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(p.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(p.descripcion, fontSize = 14.sp)
                            Text("Precio: ${p.precio}", color = Color(0xFF555555))
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { p.id?.let { vm.eliminar(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}
