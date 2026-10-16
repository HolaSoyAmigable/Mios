package com.example.mios.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.min

data class ThemeSettings(val borderHue: Float, val appHue: Float) {
    companion object {
        val Default = ThemeSettings(ThemeRules.DEFAULT_BORDER_HUE, ThemeRules.DEFAULT_APP_HUE)
    }
}

object ThemeRules {

    // ---- Colores reservados: fijos, el usuario nunca puede cambiarlos ----
    val ConfirmGreen = Color(0xFF22DD55) // tono ~136
    val CancelRed = Color(0xFFFF3B3B)    // tono 0
    val AccentPink = Color(0xFFFF4FD8)   // tono ~313

    const val DEFAULT_BORDER_HUE = 200f
    const val DEFAULT_APP_HUE = 264f

    // Zonas prohibidas (inicio, fin) en grados; pueden cruzar 0 grados. Incluyen margen.
    private val reservedZones = listOf(
        335f to 25f,  // rojo
        290f to 335f, // rosa
        85f to 155f   // verde
    )

    // Rangos permitidos para cada selector (distintos entre si)
    val borderRanges = listOf(170f to 225f)
    val appRanges = listOf(255f to 282f, 35f to 65f)

    // Colores que se le ofrecen al usuario
    val borderSwatches: List<Float> = swatchHues(borderRanges, 8)
    val appSwatches: List<Float> = swatchHues(appRanges, 4)

    private fun swatchHues(ranges: List<Pair<Float, Float>>, perRange: Int): List<Float> =
        ranges.flatMap { (start, end) ->
            (0 until perRange).map { i -> start + (end - start) * i / (perRange - 1) }
        }

    // ---- Colores derivados del tono ----
    fun borderColor(hue: Float): Color = hsv(hue, 0.90f, 1.00f)
    fun accentColor(hue: Float): Color = hsv(hue, 0.75f, 1.00f)
    fun windowColor(hue: Float): Color = hsv(hue, 0.30f, 0.17f)     // menos saturado, para ventanas
    fun backgroundColor(hue: Float): Color = hsv(hue, 0.20f, 0.06f)

    private fun hsv(h: Float, s: Float, v: Float): Color =
        Color(android.graphics.Color.HSVToColor(floatArrayOf(normalize(h), s, v)))

    // ---- Validacion ----
    fun resolve(borderHue: Float?, appHue: Float?): ThemeSettings =
        ThemeSettings(sanitizeBorderHue(borderHue), sanitizeAppHue(appHue))

    fun sanitizeBorderHue(hue: Float?): Float {
        val candidate = clampToRanges(hue ?: DEFAULT_BORDER_HUE, borderRanges)
        return if (isReservedHue(candidate)) DEFAULT_BORDER_HUE else candidate
    }

    fun sanitizeAppHue(hue: Float?): Float {
        val candidate = clampToRanges(hue ?: DEFAULT_APP_HUE, appRanges)
        return if (isReservedHue(candidate)) DEFAULT_APP_HUE else candidate
    }

    fun isReservedHue(hue: Float): Boolean {
        val h = normalize(hue)
        return reservedZones.any { (start, end) -> inZone(h, start, end) }
    }

    fun hueDistance(a: Float, b: Float): Float {
        val d = abs(normalize(a) - normalize(b))
        return min(d, 360f - d)
    }

    private fun clampToRanges(hue: Float, ranges: List<Pair<Float, Float>>): Float {
        val h = normalize(hue)
        if (ranges.any { (start, end) -> h in start..end }) return h
        return ranges
            .flatMap { listOf(it.first, it.second) }
            .minByOrNull { hueDistance(h, it) } ?: h
    }

    private fun inZone(h: Float, start: Float, end: Float): Boolean =
        if (start <= end) h in start..end else (h >= start || h <= end)

    private fun normalize(h: Float): Float = ((h % 360f) + 360f) % 360f
}