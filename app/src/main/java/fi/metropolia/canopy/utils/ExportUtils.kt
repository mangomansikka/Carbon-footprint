package fi.metropolia.canopy.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import fi.metropolia.canopy.data.source.LocationEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExportUtils {
    private data class AssignmentResult(
        val startCampus: String,
        val endCampus: String,
        val assignedCampus: String,
        val assignmentMethod: String
    )

    private data class CampusAggregate(var totalEmissionKg: Double = 0.0, var tripCount: Int = 0)

    fun generateCsv(trips: List<LocationEntity>, userRole: String): String {
        val csvBuilder = StringBuilder()

        csvBuilder.append("ID,User Role,Trip Start Latitude,Trip Start Longitude,Trip End Latitude,Trip End Longitude,Timestamp,Start Campus,End Campus,Assigned Campus,Campus Assignment Method,Distance To Assigned Campus (m),Transport Modes,Total Carbon Emission (kg),Assigned Campus Emission (kg),Bus Emission (kg),Metro Emission (kg),Train Emission (kg),Petrol Car Emission (kg),Diesel Car Emission (kg),Hybrid Car Emission (kg),Electric Car Emission (kg),Unknown Car Emission (kg),Moped Emission (kg),Walking Distance (m),Cycling Distance (m)\n")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Filter: only include records with actual data
        val filteredTrips = trips
            .filter { it.transportModes.isNotEmpty() || it.carbonEmissionGrams > 0f }
            .sortedByDescending { it.timestamp }

        val campusAggregates = linkedMapOf(
            "Myllypuro" to CampusAggregate(),
            "Karamalmi" to CampusAggregate(),
            "Arabia" to CampusAggregate(),
            "Myyrmaki" to CampusAggregate(),
            "Off-campus" to CampusAggregate()
        )

        filteredTrips.forEachIndexed { index, trip ->
            val startLat = trip.startLatitude ?: trip.latitude.takeIf { it != 0.0 }
            val startLon = trip.startLongitude ?: trip.longitude.takeIf { it != 0.0 }
            val endLat = trip.endLatitude ?: trip.latitude.takeIf { it != 0.0 }
            val endLon = trip.endLongitude ?: trip.longitude.takeIf { it != 0.0 }

            val assignment = assignCampus(startLat, startLon, endLat, endLon)
            val assignedDistance = when (assignment.assignmentMethod) {
                "end-campus" -> CampusResolver.resolveCampus(endLat, endLon)?.distanceMeters
                "start-campus-fallback" -> CampusResolver.resolveCampus(startLat, startLon)?.distanceMeters
                else -> null
            }

            val tripTotalEmissionKg = trip.carbonEmissionGrams / 1000f
            campusAggregates[assignment.assignedCampus]?.apply {
                totalEmissionKg += tripTotalEmissionKg.toDouble()
                tripCount += 1
            }

            csvBuilder.append("${index + 1},")
            csvBuilder.append("${quoteCsv(userRole)},")
            csvBuilder.append("${formatCoordinate(startLat)},")
            csvBuilder.append("${formatCoordinate(startLon)},")
            csvBuilder.append("${formatCoordinate(endLat)},")
            csvBuilder.append("${formatCoordinate(endLon)},")
            csvBuilder.append("${dateFormat.format(Date(trip.timestamp))},")
            csvBuilder.append("${quoteCsv(assignment.startCampus)},")
            csvBuilder.append("${quoteCsv(assignment.endCampus)},")
            csvBuilder.append("${quoteCsv(assignment.assignedCampus)},")
            csvBuilder.append("${quoteCsv(assignment.assignmentMethod)},")
            csvBuilder.append("${assignedDistance ?: ""},")
            csvBuilder.append("${quoteCsv(trip.transportModes)},")
            csvBuilder.append("$tripTotalEmissionKg,")
            csvBuilder.append("$tripTotalEmissionKg,")

            csvBuilder.append("${trip.emissionBussKg},")
            csvBuilder.append("${trip.emissionMetroKg},")
            csvBuilder.append("${trip.emissionTrainKg},")
            csvBuilder.append("${trip.emissionPetrolCarKg},")
            csvBuilder.append("${trip.emissionDieselCarKg},")
            csvBuilder.append("${trip.emissionHybridCarKg},")
            csvBuilder.append("${trip.emissionElectricCarKg},")
            csvBuilder.append("${trip.emissionUnknownCarKg},")
            csvBuilder.append("${trip.emissionMopedKg},")
            csvBuilder.append("${trip.walkingDistanceM},")
            csvBuilder.append("${trip.cyclingDistanceM}\n")
        }

        csvBuilder.append("\n")
        csvBuilder.append("Campus,Total Emissions (kg),Trip Count\n")
        campusAggregates.forEach { (campus, aggregate) ->
            csvBuilder.append("${quoteCsv(campus)},${aggregate.totalEmissionKg},${aggregate.tripCount}\n")
        }

        return csvBuilder.toString()
    }

    private fun assignCampus(
        startLatitude: Double?,
        startLongitude: Double?,
        endLatitude: Double?,
        endLongitude: Double?
    ): AssignmentResult {
        val endMatch = CampusResolver.resolveCampus(endLatitude, endLongitude)
        val startMatch = CampusResolver.resolveCampus(startLatitude, startLongitude)

        val startCampus = if (startMatch?.withinThreshold == true) startMatch.name else "Off-campus"
        val endCampus = if (endMatch?.withinThreshold == true) endMatch.name else "Off-campus"

        if (endMatch?.withinThreshold == true) {
            return AssignmentResult(
                startCampus = startCampus,
                endCampus = endCampus,
                assignedCampus = endMatch.name,
                assignmentMethod = "end-campus"
            )
        }

        if (startMatch?.withinThreshold == true) {
            return AssignmentResult(
                startCampus = startCampus,
                endCampus = endCampus,
                assignedCampus = startMatch.name,
                assignmentMethod = "start-campus-fallback"
            )
        }

        return AssignmentResult(
            startCampus = "Off-campus",
            endCampus = "Off-campus",
            assignedCampus = "Off-campus",
            assignmentMethod = "off-campus"
        )
    }

    private fun quoteCsv(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    private fun formatCoordinate(value: Double?): String = value?.toString() ?: ""

    // Function to save CSV to a file and return the Uri
    fun saveCsvToFile(context: Context, csvContent: String, fileName: String = "carbon_footprint_data.csv"): Uri? {
        return try {
            val file = File(context.cacheDir, fileName)
            FileWriter(file).use { it.write(csvContent) }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Function to send email with CSV attachment
    fun sendEmailWithAttachment(context: Context, csvUri: Uri, recipientEmail: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail ?: ""))
            putExtra(Intent.EXTRA_SUBJECT, "Carbon Footprint Data Export")
            putExtra(Intent.EXTRA_TEXT, "Attached is your carbon footprint data in CSV format.")
            putExtra(Intent.EXTRA_STREAM, csvUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Send Email"))
    }

    // Combined function: Generate CSV, save to file, and send email
    fun exportAndEmailData(context: Context, trips: List<LocationEntity>, userRole: String, recipientEmail: String? = null) {
        if (trips.isEmpty()) {
            Toast.makeText(context, "No trip data available to export", Toast.LENGTH_SHORT).show()
            return
        }

        val csvContent = generateCsv(trips, userRole)
        val csvUri = saveCsvToFile(context, csvContent)
        
        if (csvUri != null) {
            sendEmailWithAttachment(context, csvUri, recipientEmail)
        } else {
            Toast.makeText(context, "Failed to create CSV file", Toast.LENGTH_SHORT).show()
        }
    }
}
