package com.example.student

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.student.navigation.Screen
import com.example.student.ui.screens.AgregarEstudiantesScreen
import com.example.student.ui.screens.CalculoPromedioScreen
import com.example.student.ui.screens.DashboardScreen
import com.example.student.ui.screens.EditarEstudianteScreen
import com.example.student.ui.theme.StudentTheme
import com.example.student.viewmodel.EstudiantesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: EstudiantesViewModel = viewModel()) {
    val navController = rememberNavController()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Gestión de Estudiantes") }
                )
                // TabRow debajo del TopAppBar
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    // Tab 1: Estudiantes
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Estudiantes") },
                        icon = {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Estudiantes"
                            )
                        }
                    )

                    // Tab 2: Promedios
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Promedios") },
                        icon = {
                            Icon(
                                Icons.Filled.Assessment,
                                contentDescription = "Promedios"
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            // FAB solo visible en la tab de Estudiantes
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AgregarEstudiante.route) }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Estudiante")
                }
            }
        }
    ) { innerPadding ->
        // Contenido según la tab seleccionada
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // NavHost solo para la sección de Estudiantes
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(navController, viewModel)
                        }
                        composable(Screen.AgregarEstudiante.route) {
                            AgregarEstudiantesScreen(navController, viewModel)
                        }
                        composable(
                            route = Screen.EditarEstudiante.route,
                            arguments = listOf(navArgument("estudianteId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val estudianteId = backStackEntry.arguments?.getString("estudianteId")
                            EditarEstudianteScreen(
                                navController = navController,
                                viewModel = viewModel,
                                estudianteId = estudianteId
                            )
                        }
                    }
                }
                1 -> {
                    // Pantalla de Promedios
                    CalculoPromedioScreen(viewModel)
                }
            }
        }
    }
}