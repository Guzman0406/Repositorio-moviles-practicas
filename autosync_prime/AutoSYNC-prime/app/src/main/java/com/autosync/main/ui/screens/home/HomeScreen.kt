package com.autosync.main.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.autosync.main.ui.theme.* 


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToRegistrarServicio: () -> Unit,
    onNavigateToInvoiceDetail: (Int) -> Unit,
    onNavigateToVehicleHistory: (Int) -> Unit,
    onNavigateToAssistant: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Check Onboarding
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("autosync_prefs", android.content.Context.MODE_PRIVATE)
        if (!prefs.getBoolean("onboarding_complete", false)) {
            showOnboarding = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Charcoal
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                       Text("AutoSYNC", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                       Spacer(Modifier.width(8.dp))
                       // Sutil indicador "Pro"
                       Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)) {
                           Text("PRIME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                       }
                    }
                },
                actions = {
                    // Notification Icon Logic
                    // Notification Icon Logic
                    Box {
                        IconButton(onClick = { showNotificationDialog = true }) {
                            BadgedBox(
                                badge = { 
                                    if (state.unreadCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = Color.White
                                        ) { 
                                            Text("${state.unreadCount}", fontWeight = FontWeight.Bold) 
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = MaterialTheme.colorScheme.onBackground)
                            }
                        }

                        // Notification Dropdown
                        DropdownMenu(
                            expanded = showNotificationDialog,
                            onDismissRequest = { showNotificationDialog = false },
                            modifier = Modifier
                                .width(300.dp)
                                .heightIn(max = 400.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Notificaciones", 
                                    style = MaterialTheme.typography.titleMedium, 
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            }

                            if (state.notifications.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Outlined.NotificationsOff, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Sin notificaciones nuevas", 
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                state.notifications.forEach { notification ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    notification.type, 
                                                    style = MaterialTheme.typography.bodyMedium, 
                                                    fontWeight = if (!notification.read) FontWeight.Bold else FontWeight.Normal,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    notification.message, 
                                                    style = MaterialTheme.typography.bodySmall, 
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                    maxLines = 2
                                                )
                                            }
                                        },
                                        onClick = { 
                                            viewModel.markAsRead(notification.id)
                                            // Optional: Navigate if notification has deep link or similar
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Info, 
                                                contentDescription = null, 
                                                tint = if (!notification.read) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        modifier = Modifier.background(
                                            if (!notification.read) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
                                        )
                                    )
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                }
                            }
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Welcome Header (Modern)
            state.user?.let {
                Text(
                    text = "Hola, ${it.nombre}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    text = "¿Cómo está tu flota hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            Spacer(Modifier.height(24.dp))

            // 2. Vehicle Dashboard (Horizontal Carousel)
            if (state.vehicles.isEmpty()) {
                EmptyStateCard(onNavigateToAddVehicle)
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.vehicles) { vehicle ->
                        PremiumVehicleCard(
                            vehicle = vehicle,
                            onClick = { onNavigateToVehicleHistory(vehicle.id) }
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))

            // 3. Quick Actions (Innovation)
            Text(
                "Acciones Rápidas", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { QuickActionButton(Icons.Filled.Add, "Nuevo Auto", onNavigateToAddVehicle) }
                item { QuickActionButton(Icons.Filled.Build, "Servicio", onNavigateToRegistrarServicio) }
                item { 
                    QuickActionButton(Icons.Filled.Map, "Talleres", { 
                        val gmmIntentUri = Uri.parse("geo:0,0?q=talleres+mecanicos+y+vulcanizadoras")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            val fallbackIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            try { context.startActivity(fallbackIntent) } catch(e: Exception) { e.printStackTrace() }
                        }
                    })
                }
                item { QuickActionButton(Icons.Filled.Help, "Ayuda", { showFaqDialog = true }) }
                item { QuickActionButton(Icons.Filled.SmartToy, "Asistente", onNavigateToAssistant) }
            }

            Spacer(Modifier.height(32.dp))

            // 4. Recent Activity (Modern List)
            Text(
                "Actividad Reciente", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))
            
            if (state.recentServices.isEmpty()) {
                Text(
                    "Sin actividad reciente", 
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            } else {
                 Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                     state.recentServices.take(5).forEach { service ->
                        val vehicle = state.vehicles.find { it.id == service.vehicleId }
                        PremiumServiceItem(
                            service = service, 
                            vehicleName = vehicle?.make ?: "Auto",
                            onClick = { onNavigateToInvoiceDetail(service.id) }
                        )
                     }
                 }
            }
            Spacer(Modifier.height(80.dp)) // Bottom padding
        }
    }
    
    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            icon = { Icon(Icons.Outlined.Help, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { 
                Text(
                    "Centro de Ayuda", 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FaqItem(
                        question = "¿Mis datos están respaldados?",
                        answer = "Sí, toda tu información se sincroniza automáticamente en la nube de Google."
                    )
                    FaqItem(
                        question = "¿Cómo agrego otro vehículo?",
                        answer = "Toca el botón 'Nuevo Auto' en la pantalla principal o ve a la sección 'Mis Vehículos'."
                    )
                    FaqItem(
                        question = "¿Cuándo me notificarán?",
                        answer = "Recibirás la notificación un día antes y el mismo día de tu próximo servicio programado."
                    )
                    FaqItem(
                        question = "¿Puedo ver talleres cercanos?",
                        answer = "Sí, usa el botón 'Talleres' en Acciones Rápidas para abrir Google Maps con opciones cercanas."
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showFaqDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Column {
        Text(
            question, 
            style = MaterialTheme.typography.titleSmall, 
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            answer, 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    }
}

@Composable
fun OnboardingDialog(onDismiss: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    val titles = listOf("¡Bienvenido a AutoSYNC!", "Agrega tu Flota", "Mecánico IA", "¡Listo!")
    val descriptions = listOf(
        "Tu asistente personal para el control total de tus vehículos. Olvídate de multas y fallas mecánicas.",
        "Comienza tocando el botón '+' o 'Nuevo Auto' para registrar tu primer vehículo.",
        "¿Dudas mecánicas? Nuestro Asistente IA (Groq) te responde al instante en Acciones Rápidas.",
        "Eso es todo. ¡Disfruta la experiencia AutoSYNC Prime!"
    )
    val icons = listOf(Icons.Default.Verified, Icons.Default.DirectionsCar, Icons.Default.SmartToy, Icons.Default.CheckCircle)

    AlertDialog(
        onDismissRequest = {}, // Force interaction
        icon = { Icon(icons[step], null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) },
        title = { Text(titles[step], fontWeight = FontWeight.Bold) },
        text = { 
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(descriptions[step], style = MaterialTheme.typography.bodyLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (index == step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha=0.2f))
                        )
                    }
                }
            } 
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step < 3) step++ else onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (step < 3) "Siguiente" else "Comenzar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}

// --- Components ---

fun calculateHealth(current: Int, lastService: Int): Float {
    if (current == 0) return 1.0f 
    val diff = current - lastService
    val limit = 7500f // 7.5k km service interval
    val health = 1.0f - (diff / limit)
    return health.coerceIn(0.0f, 1.0f)
}

@Composable
fun PremiumVehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            // Background Decoration
            Icon(
                Icons.Default.DirectionsCar, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                modifier = Modifier.size(140.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
            )
            
            Column {
                Text(
                    vehicle.make, 
                    style = MaterialTheme.typography.titleSmall, 
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    vehicle.model.uppercase(), 
                    style = MaterialTheme.typography.headlineMedium, 
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${vehicle.year}", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(Modifier.weight(1f))
                
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                         Text(
                            "KILOMETRAJE", 
                            style = MaterialTheme.typography.labelSmall, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Text(
                            "${vehicle.currentMileage} km", 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Static Visual Indicator (Verified/Active)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Verified, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
    }
}

@Composable
fun PremiumServiceItem(service: Service, vehicleName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
       Box(
           modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.background),
           contentAlignment = Alignment.Center
       ) {
           Icon(Icons.Filled.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
       }
       Spacer(Modifier.width(16.dp))
       Column(modifier = Modifier.weight(1f)) {
           Text(service.serviceType, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
           Text(vehicleName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
       }
       Text(
           "$ ${service.cost?.toInt() ?: 0}", 
           style = MaterialTheme.typography.titleMedium, 
           fontWeight = FontWeight.Bold, 
           color = MaterialTheme.colorScheme.onSurface
       )
    }
}

@Composable
fun EmptyStateCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text("Agregar tu primer auto", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
