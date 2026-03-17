package fi.metropolia.canopy.utils

object CarbonHelper {

    //emissions factors for each transportation (kgco2/hkm)
    const val BUS : Double = 0.0769
    const val TRAIN : Double = 0.0012
    const val TRAM : Double = 0.0
    const val METRO : Double = 0.0
    const val MOPED_SCOOTER : Double = 0.08318518518657718 // a couple decimals less accurate than the actual number
    const val CAR_UNKNOWN : Double = 0.16664 //power not known

    fun calculate(distance: Double, transportMode: String): Double {
        // assuming that distance is in meters and transportMode is a string

        var emissions : Double
        val distanceInKm : Double = distance / 1000

        // Inside CarbonHelper.kt
        emissions = when (transportMode.lowercase().trim()) {
            "bus", "car/bus" -> {
                distanceInKm * BUS
            }

            "car", "in vehicle" -> {
                distanceInKm * CAR_UNKNOWN
            }

            "train", "train/high-speed" -> {
                distanceInKm * TRAIN
            }

            "walking", "running", "bicycle", "still" -> {
                0.0
            }

            else -> {
                0.0
            }
        }

        return emissions

    }
}
