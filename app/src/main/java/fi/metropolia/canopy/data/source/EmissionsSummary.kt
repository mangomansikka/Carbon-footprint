package fi.metropolia.canopy.data.source

data class EmissionsSummary(
    val bus: Double,
    val metro: Double,
    val petrol: Double,
    val diesel: Double,
    val hybrid: Double,
    val electric: Double,
    val unknown: Double,
    val moped: Double
)