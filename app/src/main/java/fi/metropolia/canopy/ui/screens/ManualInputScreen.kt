package fi.metropolia.canopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.canopy.viewmodels.TripViewModel
import fi.metropolia.canopy.ui.overview.OverviewColors
import fi.metropolia.canopy.ui.theme.Darkbutton
import fi.metropolia.canopy.utils.viewModelFactories.TripViewModelFactory
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ManualInputScreen() {

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val viewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(context)
    )

    var selectedTripTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var distance by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("car") }
    var showSaved by remember { mutableStateOf(false) }

    val modes = listOf(
        "car",
        "bus",
        "train",
        "metro",
        "moped",
        "walking",
        "cycling"
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = selectedTripTimeMillis}

    val dateLabel = remember(selectedTripTimeMillis) {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedTripTimeMillis)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .navigationBarsPadding()
    ) {

        /* HEADER */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(OverviewColors.BgGreen)
                .padding(top = 48.dp, start = 20.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Manual Input",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

        /* CONTENT */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text("Distance (kilometres)", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text("Enter distance in km") }
            )

            Spacer(Modifier.height(24.dp))

            Text("Transport mode", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(12.dp))

            modes.forEach { mode ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = iconForMode(mode),
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))
                    RadioButton(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode }
                    )

                    Text(
                        text = mode.replaceFirstChar { it.uppercase() }
                    )
                }
            }


            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val picked = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.HOUR_OF_DAY,12)
                                set(Calendar.MINUTE,0)
                                set(Calendar.SECOND,0)
                                set(Calendar.MILLISECOND,0)
                            }
                            selectedTripTimeMillis = picked.timeInMillis },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Selected: $dateLabel")
            }


            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    val distKm = distance.toDoubleOrNull() ?: 0.0
                    val distM = distKm * 1000.0
                    viewModel.saveManualTrip(distM, selectedMode, selectedTripTimeMillis)
                    distance = ""
                    showSaved = true
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Darkbutton,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Save trip",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            if (showSaved) {
                Text(
                    text = "Trip saved!",
                    color = OverviewColors.BgGreen,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Extra space at the bottom to ensure the button can be scrolled
            // well above the bottom navigation bar
            Spacer(Modifier.height(80.dp))
        }
    }
}

private fun iconForMode(mode: String) = when (mode.lowercase()) {
    "bus" -> Icons.Default.DirectionsBus
    "metro" -> Icons.Default.DirectionsSubway
    "train" -> Icons.Default.Train
    "moped" -> Icons.Default.TwoWheeler
    "walking" -> Icons.Default.DirectionsWalk
    "cycling" -> Icons.Default.DirectionsBike
    else -> Icons.Default.DirectionsCar
}