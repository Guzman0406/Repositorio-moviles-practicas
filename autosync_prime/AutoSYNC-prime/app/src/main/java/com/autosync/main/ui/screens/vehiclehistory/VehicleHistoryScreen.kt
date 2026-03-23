package com.autosync.main.ui.screens.vehiclehistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddService: (Int) -> Unit,
    onNavigateToInvoiceDetail: (Int) -> Unit = {},
    viewModel: VehicleHistoryViewModel = hiltViewModel()
) {
    val vehicle by viewModel.vehicle.collectAsState()
    val services by viewModel.services.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showVehicleDetailsDialog by remember { mutableStateOf(false) }
    var serviceToDelete by remember { mutableStateOf<Service?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && serviceToDelete != null) {
        DeleteServiceDialog(
            serviceName = serviceToDelete!!.serviceType,
            onConfirm = {
                viewModel.deleteService(serviceToDelete!!)
                showDeleteDialog = false
                serviceToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                serviceToDelete = null
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Historial del Vehículo",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        vehicle?.let { 
                             Text(
                                "${it.make} ${it.model}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text("Cargando historial...", color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                    }
                }
                vehicle == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("No se encontró información del vehículo", color = MaterialTheme.colorScheme.onBackground)
                        Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text("Volver")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        VehicleSummaryCard(
                            vehicle = vehicle!!,
                            onViewDetails = { showVehicleDetailsDialog = true }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            "LÍNEA DE TIEMPO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 2.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        if (services.isEmpty()) {
                             Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha=0.5f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.5f), modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Sin servicios registrados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(services) { service ->
                                    ServiceHistoryCard(
                                        service = service,
                                        onViewInvoice = { onNavigateToInvoiceDetail(service.id) },
                                        onDeleteClick = {
                                            serviceToDelete = service
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { vehicle?.let { onNavigateToAddService(it.id) } },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("NUEVO SERVICIO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showVehicleDetailsDialog && vehicle != null) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showVehicleDetailsDialog = false },
            title = { Text("Detalles del Vehículo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow("Marca", vehicle!!.make)
                    DetailRow("Modelo", vehicle!!.model)
                    DetailRow("Año", vehicle!!.year.toString())
                    DetailRow("Placas", vehicle!!.licensePlate)
                }
            },
            confirmButton = {
                TextButton(onClick = { showVehicleDetailsDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VehicleSummaryCard(
    vehicle: Vehicle,
    onViewDetails: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsCar, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.make, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(vehicle.model, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                Text("${vehicle.year} • ${vehicle.licensePlate}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            IconButton(onClick = onViewDetails) {
                 Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ServiceHistoryCard(
    service: Service,
    onViewInvoice: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    val serviceIcon = if (service.serviceType.contains("aceite", ignoreCase = true)) Icons.Default.Build else Icons.Default.Settings

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
            .clickable { onViewInvoice() }
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(serviceIcon, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(service.serviceType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(dateFormat.format(service.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                if (service.cost != null && service.cost > 0) {
                    Text(
                        currencyFormat.format(service.cost),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (!service.details.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(service.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f), maxLines = 2)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha=0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                 TextButton(onClick = onViewInvoice) {
                     Text("Ver Factura", color = MaterialTheme.colorScheme.primary)
                 }
                 Spacer(modifier = Modifier.width(4.dp))
                 IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                     Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha=0.7f), modifier = Modifier.size(18.dp))
                 }
            }
        }
    }
}

@Composable
fun DeleteServiceDialog(
    serviceName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Servicio", color = MaterialTheme.colorScheme.onSurface) },
        text = { Text("¿Eliminar \"$serviceName\" permanentemente?", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Eliminar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurface) }
        }
    )
}
