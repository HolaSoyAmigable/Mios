package com.example.mios.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object AppIconManager {

    private const val NAMESPACE = "com.example.mios"

    data class AppIcon(
        val id: String,
        val label: String,
        val aliasClass: String,
        val enabledByDefault: Boolean,
        val previewColor: Long
    )

    val icons = listOf(
        AppIcon("default", "Original", "$NAMESPACE.IconDefault", true, 0xFF3A3A44),
        AppIcon("cyan", "Cian", "$NAMESPACE.IconCyan", false, 0xFF006C84),
        AppIcon("violet", "Violeta", "$NAMESPACE.IconViolet", false, 0xFF4527A0),
        AppIcon("amber", "Ámbar", "$NAMESPACE.IconAmber", false, 0xFFB26A00)
    )

    private fun component(context: Context, icon: AppIcon) =
        ComponentName(context.packageName, icon.aliasClass)

    private fun isEnabled(context: Context, icon: AppIcon): Boolean =
        when (context.packageManager.getComponentEnabledSetting(component(context, icon))) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> false
            else -> icon.enabledByDefault
        }

    fun currentIconId(context: Context): String =
        icons.firstOrNull { isEnabled(context, it) }?.id ?: icons.first().id

    fun apply(context: Context, iconId: String) {
        val target = icons.firstOrNull { it.id == iconId } ?: return
        if (currentIconId(context) == target.id) return

        val pm = context.packageManager
        // Primero se habilita el nuevo y despues se deshabilitan los demas
        pm.setComponentEnabledSetting(
            component(context, target),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        icons.filter { it.id != target.id }.forEach { other ->
            if (isEnabled(context, other)) {
                pm.setComponentEnabledSetting(
                    component(context, other),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}