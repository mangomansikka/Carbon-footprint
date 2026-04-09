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
    fun generateCsv(trips: List<LocationEntity>, userRole: String): String {
        val csvBuilder = StringBuilder()

        csvBuilder.append("ID,User Role,Latitude,Longitude,Timestamp,Transport Modes,Total Carbon Emission (kg),Bus Emission (kg),Metro Emission (kg),Train Emission (kg),Petrol Car Emission (kg),Diesel Car Emission (kg),Hybrid Car Emission (kg),Electric Car Emission (kg),Unknown Car Emission (kg),Moped Emission (kg),Walking Distance (m),Cycling Distance (m)\n")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Filter: only include records with actual data
        val filteredTrips = trips
            .filter { it.transportModes.isNotEmpty() || it.carbonEmissionGrams > 0f }
            .sortedByDescending { it.timestamp }

        val totalCount = filteredTrips.size

        filteredTrips.forEachIndexed { index, trip ->
            val newId = totalCount - index
            csvBuilder.append("$newId,")
            csvBuilder.append("$userRole,")
            csvBuilder.append("${trip.latitude},")
            csvBuilder.append("${trip.longitude},")
            csvBuilder.append("${dateFormat.format(Date(trip.timestamp))},")
            csvBuilder.append("\"${trip.transportModes}\",")
            
            // Convert grams to kg for the export
            val totalEmissionKg = trip.carbonEmissionGrams / 1000f
            csvBuilder.append("$totalEmissionKg,")

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

        return csvBuilder.toString()
    }

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
