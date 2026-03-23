package com.autosync.main.ui.screens.servicios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.ui.components.CustomTextField
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarServicioScreen(
    serviceId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: RegistrarServicioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isEditMode = serviceId != null

    LaunchedEffect(serviceId) {
        if (serviceId != null && serviceId > 0) {
            viewModel.loadServiceForEdit(serviceId)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showNextDatePicker by remember { mutableStateOf(false) }
    var isServicioDropdownExpanded by remember { mutableStateOf(false) }
    var isVehicleDropdownExpanded by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? -> viewModel.onReceiptImageSelected(uri) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack()
        }
    }

    // Date Picker Dialogs logic reused
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.fecha)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.onFechaChange(it) }; showDatePicker = false }) { Text("OK", color = MaterialTheme.colorScheme.primary) } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showNextDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.nextServiceDate ?: System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showNextDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { viewModel.onNextServiceDateChange(it) }; showNextDatePicker = false }) { Text("OK", color = MaterialTheme.colorScheme.primary) } }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Servicio" else "Nuevo Servicio", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            
            // Section: Vehicle Selection
            Text("Vehículo", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = isVehicleDropdownExpanded,
                onExpandedChange = { isVehicleDropdownExpanded = !isVehicleDropdownExpanded }
            ) {
                CustomTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = state.selectedVehicleName ?: "Seleccionar...",
                    onValueChange = {},
                    label = "",
                    placeholder = "Selecciona el auto",
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isVehicleDropdownExpanded) },
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isVehicleDropdownExpanded,
                    onDismissRequest = { isVehicleDropdownExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    state.vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = { Text("${vehicle.make} ${vehicle.model}", color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { viewModel.onVehicleSelected(vehicle); isVehicleDropdownExpanded = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            // Section: Service Details
            Text("Detalles del Servicio", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = isServicioDropdownExpanded,
                onExpandedChange = { isServicioDropdownExpanded = !isServicioDropdownExpanded }
            ) {
                CustomTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = state.tipoServicio,
                    onValueChange = {},
                    label = "Tipo de Servicio",
                    placeholder = "Mantenimiento...",
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isServicioDropdownExpanded) },
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isServicioDropdownExpanded,
                    onDismissRequest = { isServicioDropdownExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    state.tiposServicio.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = { viewModel.onTipoServicioChange(tipo); isServicioDropdownExpanded = false }
                        )
                    }
                     DropdownMenuItem(
                        text = { Text("Otro...", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                        onClick = { viewModel.onTipoServicioChange("Otro"); isServicioDropdownExpanded = false }
                    )
                }
            }
            if (state.tipoServicio == "Otro") {
                Spacer(Modifier.height(8.dp))
                CustomTextField(modifier = Modifier.fillMaxWidth(), value = state.otroServicio, onValueChange = { viewModel.onOtroServicioChange(it) }, label = "Especifique", placeholder = "")
            }
            Spacer(Modifier.height(16.dp))

            CustomTextField(modifier = Modifier.fillMaxWidth(), value = state.taller, onValueChange = { viewModel.onTallerChange(it) }, label = "Taller / Mecánico", placeholder = "Ej. Taller Master")
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(state.fecha)),
                        onValueChange = {}, label = "Fecha", placeholder = "", readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.primary) }
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                }
                 CustomTextField(
                    modifier = Modifier.weight(1f),
                    value = state.costo,
                    onValueChange = { viewModel.onCostoChange(it) },
                    label = "Costo ($)",
                    placeholder = "0.00"
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                 CustomTextField(
                    modifier = Modifier.weight(1f),
                    value = state.serviceMileage,
                    onValueChange = { viewModel.onServiceMileageChange(it) },
                    label = "Km Servicio",
                    placeholder = "0"
                )
                 CustomTextField(
                    modifier = Modifier.weight(1f),
                    value = state.nextServiceMileage,
                    onValueChange = { viewModel.onNextServiceMileageChange(it) },
                    label = "Km Próximo Srv",
                    placeholder = "0"
                )
            }
            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.nextServiceDate?.let { SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(it)) } ?: "N/A",
                    onValueChange = {}, label = "Fecha Próximo Servicio", placeholder = "Opcional", readOnly = true,
                    trailingIcon = { Icon(Icons.Default.EventRepeat, null, tint = MaterialTheme.colorScheme.primary) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showNextDatePicker = true })
            }
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Notas Adicionales") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(Modifier.height(32.dp))

            // Section: Receipt Logic (Same as before but styled)
            Text("Comprobante", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            
            // Logic for bitmap loading preserved
            val bitmap = remember(state.receiptImageUri, state.existingReceiptImageUrl) {
                 when {
                    state.receiptImageUri != null -> {
                        try {
                            if (android.os.Build.VERSION.SDK_INT < 28) {
                                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, state.receiptImageUri)
                            } else {
                                val source = android.graphics.ImageDecoder.createSource(context.contentResolver, state.receiptImageUri!!)
                                android.graphics.ImageDecoder.decodeBitmap(source)
                            }
                        } catch (e: Exception) { null }
                    }
                    !state.existingReceiptImageUrl.isNullOrBlank() -> {
                        try {
                            val decodedString = android.util.Base64.decode(state.existingReceiptImageUrl, android.util.Base64.DEFAULT)
                            android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        } catch (e: Exception) { null }
                    }
                    else -> null
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.3f), RoundedCornerShape(16.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                 if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Recibo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                    Icon(Icons.Default.Edit, "Editar", tint = Color.White)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Receipt, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                        Text("Adjuntar Recibo/Factura", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { viewModel.registrarServicio() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(16.dp),
                enabled = state.isValid && !state.isLoading
            ) {
                if(state.isLoading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                else Text(if (isEditMode) "GUARDAR CAMBIOS" else "REGISTRAR SERVICIO", fontWeight = FontWeight.Bold)
            }
        }
    }
}