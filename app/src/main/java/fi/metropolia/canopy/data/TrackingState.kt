package fi.metropolia.canopy.data

object TrackingState {

    var totalDistanceMeters: Double = 0.0

    // Keeps unique transport modes used during trip
    val usedTransportModes: MutableSet<String> = mutableSetOf()

    var lastLatitude: Double? = null
    var lastLongitude: Double? = null

    fun reset() {
        totalDistanceMeters = 0.0
        usedTransportModes.clear()
        lastLatitude = null
        lastLongitude = null
    }
}