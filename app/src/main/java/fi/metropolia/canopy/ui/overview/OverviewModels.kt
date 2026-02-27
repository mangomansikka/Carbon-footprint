package fi.metropolia.canopy.ui.overview

import androidx.compose.ui.graphics.Color

enum class CompareDimension(val title: String) {
    ME("Oma"),
    CAMPUS("Kampukset"),
    ROLE("Roolit")
}

data class EmissionSlice(
    val label: String,
    val value: Double,
    val color: Color,
    val iconKey: String? = null // jos haluat mapata iconit myöhemmin
)

data class EmissionBreakdown(
    val totalTonsPerYear: Double,
    val slices: List<EmissionSlice>
)

data class CompareTarget(
    val id: String,
    val name: String,
    val breakdown: EmissionBreakdown
)