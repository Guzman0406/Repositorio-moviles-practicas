package com.autosync.main.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.autosync.main.ui.components.CustomTextField
import com.autosync.main.ui.icons.FacebookIcon
import com.autosync.main.ui.icons.GoogleIcon

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegistro: () -> Unit,
    callbackManager: com.facebook.CallbackManager,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Register Facebook Callback (Preserved)
    DisposableEffect(Unit) {
        val loginManager = com.facebook.login.LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : com.facebook.FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) { viewModel.signInWithFacebook(result.accessToken) }
            override fun onCancel() {}
            override fun onError(error: com.facebook.FacebookException) { viewModel.signInWithGoogle("FAIL") }
        })
        onDispose { }
    }

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Background Elements (Subtle Gradient or Shapes could be added here)
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val configuration = androidx.compose.ui.platform.LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                
                // Logo / Branding
                Text(
                    text = "AutoSYNC",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "PRIME",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                if (state.generalError != null) {
                    Text(
                        text = state.generalError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = "Email",
                    placeholder = "ejemplo@correo.com",
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) },
                    isError = state.emailError != null,
                    errorMessage = state.emailError
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.onRecordarmeChange(!state.recordarme) }
                    ) {
                        Checkbox(
                            checked = state.recordarme, 
                            onCheckedChange = { viewModel.onRecordarmeChange(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Text(
                            "Recordarme", 
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(
                        onClick = { showForgotPasswordDialog = true },
                        contentPadding = PaddingValues(25.dp)
                    ) {
                        Text(
                            "¿Has olvidado tu contraseña?", 
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha=0.5f))
                    Text("O conecta con", modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha=0.5f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Login Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Google & Facebook Logic preserved
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val googleSignInClient = remember {
                         val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(com.autosync.main.R.string.default_web_client_id)) 
                            .requestEmail()
                            .build()
                        com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                    }
                     val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == android.app.Activity.RESULT_OK) {
                            try {
                                val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                                account?.idToken?.let { viewModel.signInWithGoogle(it) }
                            } catch (e: Exception) { viewModel.signInWithGoogle("FAIL") }
                        }
                    }

                    SocialButton(GoogleIcon, "Google") { launcher.launch(googleSignInClient.signInIntent) }
                    SocialButton(FacebookIcon, "Facebook") { 
                         com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(
                            context as androidx.activity.ComponentActivity,
                            listOf("email", "public_profile")
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Nuevo usuario?", color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.7f))
                    TextButton(onClick = onNavigateToRegistro) {
                        Text("Crea una cuenta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
    
    // Forgot Password Dialog (Styled)
    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(state.email) }
        var isSending by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        var successMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Recuperar Contraseña", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    Text("Ingresa tu correo asociado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = resetEmail,
                        onValueChange = { resetEmail = it; errorMsg = null },
                        label = "Email",
                        placeholder = ""
                    )
                    if (errorMsg != null) Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    if (successMsg != null) Text(successMsg!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                         if (resetEmail.isBlank()) errorMsg = "Email inválido"
                         else {
                             isSending = true
                             viewModel.resetPassword(resetEmail) { success, error ->
                                 isSending = false
                                 if (success) successMsg = "Enlace enviado."
                                 else errorMsg = error
                             }
                         }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    if (isSending) CircularProgressIndicator(modifier = Modifier.size(16.dp)) else Text("Enviar")
                }
            },
            dismissButton = {
                 TextButton(onClick = { showForgotPasswordDialog = false }) { Text("Cerrar", color = MaterialTheme.colorScheme.onSurface) }
            }
        )
    }
}

@Composable
fun SocialButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.width(150.dp).height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Icon(icon, null, tint = Color.Unspecified) // Keep original brand colors
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
