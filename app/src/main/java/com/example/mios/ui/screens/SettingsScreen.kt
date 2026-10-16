package com.example.mios.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Reemplazar por la pagina real del creador cuando exista
private const val CREATOR_PAGE_URL = "https://github.com/HolaSoyAmigable/Mios"

private enum class PasswordFlow { None, AskAdd, SetNew, Manage, VerifyForChange, VerifyForRemove }

@Composable
fun SettingsScreen(navController: NavHostController, viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    val username = settings?.username ?: ""
    val lockEnabled = settings?.lockEnabled ?: false
    val uriHandler = LocalUriHandler.current

    var showUsernameDialog by remember { mutableStateOf(false) }
    var passwordFlow by remember { mutableStateOf(PasswordFlow.None) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Configuración", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        ListItem(
            headlineContent = { Text("Nombre de usuario") },
            supportingContent = { Text(username.ifBlank { "Sin nombre" }) },
            modifier = Modifier.clickable { showUsernameDialog = true }
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text("Contraseña") },
            supportingContent = { Text(if (lockEnabled) "Activada" else "Desactivada") },
            modifier = Modifier.clickable {
                passwordFlow = if (lockEnabled) PasswordFlow.Manage else PasswordFlow.AskAdd
            }
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text("Personalizar") },
            supportingContent = { Text("Próximamente") }
        )
        HorizontalDivider()

        ListItem(
            headlineContent = { Text("Página del creador") },
            modifier = Modifier.clickable {
                runCatching { uriHandler.openUri(CREATOR_PAGE_URL) }
            }
        )
        HorizontalDivider()

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Creado por HolaSoyAmigable por el amor al arte",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }

    if (showUsernameDialog) {
        UsernameDialog(
            initial = username,
            onDismiss = { showUsernameDialog = false },
            onConfirm = {
                viewModel.setUsername(it)
                showUsernameDialog = false
            }
        )
    }

    when (passwordFlow) {
        PasswordFlow.None -> Unit

        PasswordFlow.AskAdd -> AlertDialog(
            onDismissRequest = { passwordFlow = PasswordFlow.None },
            title = { Text("Contraseña") },
            text = {
                Text("¿Deseas agregar una contraseña a la aplicación? Si la olvidas, no podrás volver a ingresar.")
            },
            confirmButton = {
                TextButton(onClick = { passwordFlow = PasswordFlow.SetNew }) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { passwordFlow = PasswordFlow.None }) { Text("No") }
            }
        )

        PasswordFlow.SetNew -> SetPinDialog(
            onDismiss = { passwordFlow = PasswordFlow.None },
            onConfirm = { pin ->
                viewModel.setPin(pin) { passwordFlow = PasswordFlow.None }
            }
        )

        PasswordFlow.Manage -> AlertDialog(
            onDismissRequest = { passwordFlow = PasswordFlow.None },
            title = { Text("Contraseña") },
            text = {
                Column {
                    TextButton(onClick = { passwordFlow = PasswordFlow.VerifyForChange }) {
                        Text("Cambiar contraseña")
                    }
                    TextButton(onClick = { passwordFlow = PasswordFlow.VerifyForRemove }) {
                        Text("Quitar contraseña", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { passwordFlow = PasswordFlow.None }) { Text("Cancelar") }
            }
        )

        PasswordFlow.VerifyForChange -> VerifyPinDialog(
            viewModel = viewModel,
            onDismiss = { passwordFlow = PasswordFlow.None },
            onVerified = { passwordFlow = PasswordFlow.SetNew }
        )

        PasswordFlow.VerifyForRemove -> VerifyPinDialog(
            viewModel = viewModel,
            onDismiss = { passwordFlow = PasswordFlow.None },
            onVerified = {
                viewModel.removePin()
                passwordFlow = PasswordFlow.None
            }
        )
    }
}

@Composable
private fun UsernameDialog(initial: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nombre de usuario") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.take(24) },
                label = { Text("Nombre") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun SetPinDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    val mismatch = repeat.isNotEmpty() && pin != repeat
    val valid = pin.length >= MIN_PIN_LENGTH && pin == repeat

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contraseña") },
        text = {
            Column {
                Text("Ingresa tu contraseña ($MIN_PIN_LENGTH a $MAX_PIN_LENGTH dígitos, solo números).")
                Spacer(modifier = Modifier.height(12.dp))
                PinTextField(value = pin, onValueChange = { pin = it }, label = "Contraseña")
                Spacer(modifier = Modifier.height(4.dp))
                PinTextField(
                    value = repeat,
                    onValueChange = { repeat = it },
                    label = "De nuevo",
                    isError = mismatch,
                    errorText = "No coinciden"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pin) }, enabled = valid) { Text("Listo") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun VerifyPinDialog(
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var checking by remember { mutableStateOf(false) }

    fun submit() {
        if (checking || pin.length < MIN_PIN_LENGTH) return
        checking = true
        viewModel.verifyPin(pin) { ok ->
            checking = false
            if (ok) {
                onVerified()
            } else {
                error = true
                pin = ""
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contraseña actual") },
        text = {
            PinTextField(
                value = pin,
                onValueChange = {
                    error = false
                    pin = it
                },
                label = "Contraseña",
                isError = error,
                errorText = "Contraseña incorrecta",
                onDone = { submit() }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { submit() },
                enabled = pin.length >= MIN_PIN_LENGTH && !checking
            ) { Text("Continuar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}