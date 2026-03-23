package com.autosync.main.ui.screens.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.ui.components.CustomTextField
import com.autosync.main.ui.icons.FacebookIcon
import com.autosync.main.ui.icons.GoogleIcon
import com.autosync.main.util.TermsAndConditions

@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    callbackManager: com.facebook.CallbackManager
) {
    val viewModel: RegistroViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    
    DisposableEffect(Unit) {
        val loginManager = com.facebook.login.LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : com.facebook.FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) { viewModel.signInWithFacebook(result.accessToken) }
            override fun onCancel() {}
            override fun onError(error: com.facebook.FacebookException) {}
        })
        onDispose { }
    }

    val googleSignInClient = remember {
        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.autosync.main.R.string.default_web_client_id)) 
            .requestEmail()
            .build()
        com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            try {
                val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account?.idToken?.let { viewModel.signInWithGoogle(it) }
            } catch (e: Exception) {}
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Términos y Condiciones", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(TermsAndConditions.TEXT, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) { Text("Cerrar", color = MaterialTheme.colorScheme.primary) }
            }
        )
    }

    LaunchedEffect(state.isRegistroSuccessful) {
        if (state.isRegistroSuccessful) onRegistroSuccess()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val config = androidx.compose.ui.platform.LocalConfiguration.current
            val sH = config.screenHeightDp.dp
            Spacer(modifier = Modifier.height(sH * 0.01f))
            
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Crear Cuenta",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Únete a AutoSYNC",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f)
            )

            if (state.generalError != null) {
                Text(
                    text = state.generalError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = "Nombre Completo",
                placeholder = "Tu nombre",
                isError = state.nombreError != null,
                errorMessage = state.nombreError,
                leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "Email",
                placeholder = "tuemail@ejemplo.com",
                isError = state.emailError != null,
                errorMessage = state.emailError,
                leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña",
                placeholder = "••••••••",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.passwordError != null,
                errorMessage = state.passwordError
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = "Confirmar Contraseña",
                placeholder = "••••••••",
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.aceptaTerminos, 
                    onCheckedChange = { viewModel.onAceptaTerminosChange(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary, checkmarkColor = MaterialTheme.colorScheme.onPrimary, uncheckedColor = MaterialTheme.colorScheme.outline)
                )
                Text("Acepto los ", color = MaterialTheme.colorScheme.onBackground)
                TextButton(onClick = { showTermsDialog = true }) {
                    Text("Términos", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (state.terminosError != null) {
                Text(state.terminosError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.registrar() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                else Text("REGISTRARME", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha=0.5f))
                Text(" O regístrate con ", color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha=0.5f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                com.autosync.main.ui.screens.login.SocialButton(GoogleIcon, "Google") { googleLauncher.launch(googleSignInClient.signInIntent) }
                com.autosync.main.ui.screens.login.SocialButton(FacebookIcon, "Facebook") { 
                    com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(context as androidx.activity.ComponentActivity, listOf("email", "public_profile"))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("¿Ya tienes cuenta?", color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.7f))
                TextButton(onClick = onNavigateToLogin) {
                    Text("Inicia Sesión", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
