package fi.metropolia.canopy.ui.overview

import androidx.compose.ui.graphics.Color

/**
 * EmissionSlice data class for representing a slice in the emission donut chart
 */
data class EmissionSlice(
    val label: String,
    val value: Double,
    val color: Color,
    val iconKey: String? = null
)

