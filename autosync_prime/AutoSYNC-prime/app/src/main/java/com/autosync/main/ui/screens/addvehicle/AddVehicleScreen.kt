package com.autosync.main.ui.screens.addvehicle

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.autosync.main.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddVehicleViewModel = hiltViewModel()
) {
    val marca by viewModel.marca.collectAsState()
    val modelo by viewModel.modelo.collectAsState()
    val year by viewModel.year.collectAsState()
    val licensePlate by viewModel.licensePlate.collectAsState()
    val currentMileage by viewModel.currentMileage.collectAsState()
    val modelSuggestions by viewModel.modelSuggestions.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()

    var isModelsDropdownExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let { viewModel.onImageSelected(it) } }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Registrar Vehículo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
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
            
            // Image Picker Section (Premium Style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Vehículo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Change Overlay
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                    Icon(Icons.Default.Image, "Cambiar", tint = Color.White, modifier = Modifier.size(48.dp))
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DirectionsCar, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Toca para subir foto", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))

            // Form Fields
            Text("Detalles del Auto", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))

            CustomTextField(
                modifier = Modifier.fillMaxWidth(), 
                value = marca, 
                onValueChange = viewModel::onMarcaChange, 
                label = "Marca", 
                placeholder = "Ej. Tesla"
            )
            Spacer(Modifier.height(16.dp))

            // Modelo Dropdown with Styling
            ExposedDropdownMenuBox(
                expanded = isModelsDropdownExpanded,
                onExpandedChange = { isModelsDropdownExpanded = !isModelsDropdownExpanded }
            ) {
                CustomTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    value = modelo,
                    onValueChange = { viewModel.onModeloChange(it) },
                    label = "Modelo",
                    placeholder = "Selecciona un modelo",
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelsDropdownExpanded) },
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isModelsDropdownExpanded,
                    onDismissRequest = { isModelsDropdownExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    modelSuggestions.forEach {
                        DropdownMenuItem(
                            text = { Text(it.modelName, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                viewModel.onModelSelected(it)
                                isModelsDropdownExpanded = false
                            },
                             contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CustomTextField(
                    modifier = Modifier.weight(1f), 
                    value = year, 
                    onValueChange = viewModel::onYearChange, 
                    label = "Año", 
                    placeholder = "2024"
                )
                CustomTextField(
                    modifier = Modifier.weight(1f), 
                    value = licensePlate, 
                    onValueChange = viewModel::onLicensePlateChange, 
                    label = "Placas", 
                    placeholder = "XXX-000"
                )
            }
            Spacer(Modifier.height(16.dp))

            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentMileage,
                onValueChange = viewModel::onMileageChange,
                label = "Kilometraje Actual (km)",
                placeholder = "0"
            )
            Spacer(Modifier.height(48.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveVehicle()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.DirectionsCar, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Vehículo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
