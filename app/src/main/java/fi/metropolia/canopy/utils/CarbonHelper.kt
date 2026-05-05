package fi.metropolia.canopy.utils

object CarbonHelper {

    // emissions factors for each transportation (kgco2/km)
    const val BUS: Double = 0.0769
    const val TRAIN: Double = 0.0012
    const val TRAM: Double = 0.0
    const val METRO: Double = 0.0
    const val MOPED: Double = 0.083185
    const val CAR: Double = 0.16664

    fun calculate(distanceMeters: Double, transportMode: String): Double {
        val distanceKm = distanceMeters / 1000.0
        val mode = transportMode.lowercase().trim()

        return when {
            mode.contains("bus") -> distanceKm * BUS
            mode.contains("car") || mode == "in_vehicle" || mode == "in vehicle" -> distanceKm * CAR
            mode.contains("train") -> distanceKm * TRAIN
            mode.contains("metro") || mode == "subway" -> distanceKm * METRO
            mode.contains("tram") -> distanceKm * TRAM
            mode.contains("moped") || mode == "scooter" -> distanceKm * MOPED
            else -> 0.0 // walking, cycling, still, etc.
        }
    }
}
