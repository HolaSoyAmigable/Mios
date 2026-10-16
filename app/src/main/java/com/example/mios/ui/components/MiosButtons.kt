package com.example.mios.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mios.ui.theme.ThemeRules
import androidx.compose.ui.unit.dp

@Composable
private fun MiosOutlinedButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier,
    enabled: Boolean
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
            disabledContentColor = color.copy(alpha = 0.38f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = if (enabled) 1f else 0.38f))
    ) {
        Text(text)
    }
}

// Verde fijo: confirmar
@Composable
fun ConfirmButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    MiosOutlinedButton(text, onClick, ThemeRules.ConfirmGreen, modifier, enabled)

// Rojo fijo: cancelar / eliminar
@Composable
fun CancelButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    MiosOutlinedButton(text, onClick, ThemeRules.CancelRed, modifier, enabled)

// Neutro: acciones secundarias
@Composable
fun NeutralButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    MiosOutlinedButton(text, onClick, MaterialTheme.colorScheme.onSurface, modifier, enabled)