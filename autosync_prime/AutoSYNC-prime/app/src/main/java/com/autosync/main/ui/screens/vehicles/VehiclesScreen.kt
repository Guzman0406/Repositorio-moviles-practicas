package com.autosync.main.ui.screens.vehicles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.autosync.main.R
import com.autosync.main.data.local.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToEditVehicle: (Int) -> Unit,
    onNavigateToVehicleHistory: (Int) -> Unit,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var vehicleToDelete by remember { mutableStateOf<Vehicle?>(null) }

    if (showDeleteDialog && vehicleToDelete != null) {
        DeleteConfirmationDialog(
            vehicleName = "${vehicleToDelete!!.make} ${vehicleToDelete!!.model}",
            onConfirm = {
                viewModel.deleteVehicle(vehicleToDelete!!)
                showDeleteDialog = false
                vehicleToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                vehicleToDelete = null
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Mis Vehículos", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = {
                    IconButton(onClick = onNavigateToAddVehicle) {
                        Icon(Icons.Default.Add, "Agregar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            if (vehicles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No tienes vehículos registrados", color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToAddVehicle,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Text("AGREGAR PRIMER AUTO")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(vehicles) { vehicle ->
                        VehicleListItem(
                            vehicle = vehicle,
                            onHistoryClick = { onNavigateToVehicleHistory(vehicle.id) },
                            onEditClick = { onNavigateToEditVehicle(vehicle.id) },
                            onDeleteClick = {
                                vehicleToDelete = vehicle
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleListItem(
    vehicle: Vehicle,
    onHistoryClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Updated Image Container
            Box(
                 modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background),
                 contentAlignment = Alignment.Center
            ) {
                 if(vehicle.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(vehicle.imageUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                 } else {
                    Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_car_placeholder), null, tint = MaterialTheme.colorScheme.onSurfaceVariant) // Fallback if placeholder unavailable
                 }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.make, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(vehicle.model.uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                Text("${vehicle.year} • ${vehicle.licensePlate}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Action Buttons (Icons only for cleaner look)
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.History, "Historial", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f))
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    vehicleName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = { Text("¿Eliminar vehículo?", color = MaterialTheme.colorScheme.onSurface) },
        text = { Text("Se eliminará \"$vehicleName\" y todos sus registros de servicio. Esta acción no se puede deshacer.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
