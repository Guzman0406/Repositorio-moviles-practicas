package com.example.dashboard.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavController) {
    val viewModel: FormScreenViewModel = viewModel()
    val formData by viewModel.formData.collectAsState()
    val savedForms by viewModel.savedForms.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.resetSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEditing) "Editar Formulario" else "Nuevo Formulario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveForm() },
                        enabled = formData.nombre.isNotEmpty() && formData.email.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Guardar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Datos Personales",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    FormTextField(
                        value = formData.nombre,
                        onValueChange = { viewModel.updateFormField("nombre", it) },
                        label = "Nombre Completo *"
                    )

                    FormTextField(
                        value = formData.email,
                        onValueChange = { viewModel.updateFormField("email", it) },
                        label = "Email *",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    FormTextField(
                        value = formData.telefono,
                        onValueChange = { viewModel.updateFormField("telefono", it) },
                        label = "Teléfono",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    FormTextField(
                        value = formData.direccion,
                        onValueChange = { viewModel.updateFormField("direccion", it) },
                        label = "Dirección"
                    )

                    FormTextField(
                        value = formData.fechaNacimiento,
                        onValueChange = { viewModel.updateFormField("fechaNacimiento", it) },
                        label = "Fecha de Nacimiento",
                        placeholder = "DD/MM/AAAA"
                    )

                    FormTextField(
                        value = formData.genero,
                        onValueChange = { viewModel.updateFormField("genero", it) },
                        label = "Género"
                    )

                    OutlinedTextField(
                        value = formData.notas,
                        onValueChange = { viewModel.updateFormField("notas", it) },
                        label = { Text("Notas Adicionales") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.saveForm() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formData.nombre.isNotEmpty() && formData.email.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isEditing) "Actualizar" else "Guardar")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditing) {
                        Button(
                            onClick = { viewModel.resetForm() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Nuevo Formulario")
                        }
                    }
                }
            }

            if (savedForms.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Text(
                            text = "Formularios Guardados",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn {
                            items(savedForms.size) { index ->
                                val form = savedForms[index]
                                ListItem(
                                    headlineContent = { Text(form.nombre) },
                                    supportingContent = { Text(form.email) },
                                    trailingContent = {
                                        Row {
                                            IconButton(
                                                onClick = { viewModel.loadFormForEdit(form) }
                                            ) {
                                                Icon(
                                                    Icons.Filled.Edit,
                                                    contentDescription = "Editar",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteForm(form) }
                                            ) {
                                                Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                                Divider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
}