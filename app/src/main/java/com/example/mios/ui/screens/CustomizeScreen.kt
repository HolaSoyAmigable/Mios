package com.example.mios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mios.ui.components.ConfirmButton
import com.example.mios.ui.components.NeutralButton
import com.example.mios.ui.theme.ThemeRules
import com.example.mios.util.AppIconManager

@Composable
fun CustomizeScreen(navController: NavHostController, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val saved = remember { viewModel.themeSettings.value }

    // Borrador: no se guarda hasta pulsar "Listo"
    var borderHue by remember { mutableFloatStateOf(saved.borderHue) }
    var appHue by remember { mutableFloatStateOf(saved.appHue) }
    var iconId by remember { mutableStateOf(AppIconManager.currentIconId(context)) }

    val border = ThemeRules.borderColor(borderHue)
    val window = ThemeRules.windowColor(appHue)
    val accent = ThemeRules.accentColor(appHue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Personalizar", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = ThemeRules.CancelRed)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(
            "Tono de color de los bordes",
            "Elige el tono de los bordes de la aplicación."
        )
        SwatchGrid(
            hues = ThemeRules.borderSwatches,
            selected = borderHue,
            colorOf = { ThemeRules.borderColor(it) },
            onSelect = { borderHue = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            "Tono de color de la aplicación",
            "De este tono se deriva uno menos saturado para las ventanas."
        )
        SwatchGrid(
            hues = ThemeRules.appSwatches,
            selected = appHue,
            colorOf = { ThemeRules.accentColor(it) },
            onSelect = { appHue = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(
            "Ícono de la aplicación",
            "Elige uno de los íconos incluidos. Al cambiarlo, el sistema puede tardar unos segundos en actualizarlo, e incluso cerrar la app un instante."
        )
        IconPicker(selectedId = iconId, borderColor = border, onSelect = { iconId = it })

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader("Vista previa", "Así se verán las ventanas y los botones.")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(window)
                .border(1.5.dp, border, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Ventana de ejemplo", color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(accent)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Botón", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ConfirmButton(
                text = "Listo",
                onClick = {
                    viewModel.saveTheme(borderHue, appHue, iconId) { navController.popBackStack() }
                }
            )
            NeutralButton(
                text = "Restablecer",
                onClick = {
                    borderHue = ThemeRules.DEFAULT_BORDER_HUE
                    appHue = ThemeRules.DEFAULT_APP_HUE
                    iconId = "default"
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionHeader(title: String, description: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Text(
        description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun SwatchGrid(
    hues: List<Float>,
    selected: Float,
    colorOf: (Float) -> Color,
    onSelect: (Float) -> Unit
) {
    val selectedHue = hues.minByOrNull { ThemeRules.hueDistance(it, selected) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        hues.chunked(4).forEach { rowHues ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                rowHues.forEach { hue ->
                    val isSelected = hue == selectedHue
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colorOf(hue))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.25f),
                                shape = CircleShape
                            )
                            .clickable { onSelect(hue) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IconPicker(selectedId: String, borderColor: Color, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppIconManager.icons.chunked(2).forEach { rowIcons ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowIcons.forEach { icon ->
                    val isSelected = icon.id == selectedId
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) borderColor else borderColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelect(icon.id) }
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(icon.previewColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(icon.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}