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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.canopy.R
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

    var distance by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("car") }
    var showSaved by remember { mutableStateOf(false) }

    var selectedTripTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    val campusOptions = listOf(
        stringResource(R.string.campus_myllypuro),
        stringResource(R.string.campus_karamalmi),
        stringResource(R.string.campus_arabia),
        stringResource(R.string.campus_myyrmaki),
        stringResource(R.string.non_campus_trip)
    )
    var selectedCampus by remember { mutableStateOf<String?>(null) }
    var campusDropdownExpanded by remember { mutableStateOf(false) }
    var showCampusInfo by remember { mutableStateOf(false) }
    var showCampusValidationError by remember { mutableStateOf(false) }

    val modes = listOf(
        "car",
        "bus",
        "train",
        "metro",
        "moped",
        "walking",
        "bicycle"
    )

    val calendar = Calendar.getInstance().apply {
        timeInMillis = selectedTripTimeMillis
    }

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
                text = stringResource(R.string.manual_input),
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

            Text(stringResource(R.string.distance_km_label), style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text(stringResource(R.string.enter_distance_hint)) }
            )

            Spacer(Modifier.height(24.dp))

            Text(stringResource(R.string.transport_mode_label), style = MaterialTheme.typography.titleMedium)

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
                        text = getModeDisplayName(mode)
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
                                set(Calendar.HOUR_OF_DAY, 12)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            selectedTripTimeMillis = picked.timeInMillis 
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.selected_date_label, dateLabel))
            }


            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.assign_to_campus), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { showCampusInfo = true }) {
                    Icon(Icons.Default.Info, contentDescription = stringResource(R.string.campus_info_desc))
                }
            }
            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { campusDropdownExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(selectedCampus ?: stringResource(R.string.select_campus))
                }
                DropdownMenu(
                    expanded = campusDropdownExpanded,
                    onDismissRequest = { campusDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    campusOptions.forEach { campus ->
                        DropdownMenuItem(
                            text = { Text(campus) },
                            onClick = {
                                selectedCampus = campus
                                campusDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (showCampusValidationError) {
                Text(
                    text = stringResource(R.string.campus_validation_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    if (selectedCampus == null) {
                        showCampusValidationError = true
                        return@Button
                    }

                    val distKm = distance.toDoubleOrNull() ?: 0.0
                    val distM = distKm * 1000.0
                    val nonCampusString = context.getString(R.string.non_campus_trip)
                    val campusToSend = if (selectedCampus == nonCampusString) null else selectedCampus
                    viewModel.saveManualTrip(distM, selectedMode, selectedTripTimeMillis, campusToSend)
                    distance = ""
                    showSaved = true
                    showCampusValidationError = false
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
                    text = stringResource(R.string.save_trip),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(Modifier.height(16.dp))

            if (showSaved) {
                Text(
                    text = stringResource(R.string.trip_saved),
                    color = OverviewColors.BgGreen,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Extra space at the bottom to ensure the button can be scrolled
            // well above the bottom navigation bar
            Spacer(Modifier.height(80.dp))

            if (showCampusInfo) {
                AlertDialog(
                    onDismissRequest = { showCampusInfo = false },
                    title = { Text(stringResource(R.string.campus_assignment_title)) },
                    text = {
                        Text(stringResource(R.string.campus_assignment_text))
                    },
                    confirmButton = {
                        Button(onClick = { showCampusInfo = false }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }
        }
    }
}

private fun iconForMode(mode: String) = when (mode.lowercase()) {
    "bus" -> Icons.Default.DirectionsBus
    "metro" -> Icons.Default.DirectionsSubway
    "train" -> Icons.Default.Train
    "moped" -> Icons.Default.TwoWheeler
    "walking" -> Icons.Default.DirectionsWalk
    "bicycle" -> Icons.Default.DirectionsBike
    else -> Icons.Default.DirectionsCar
}
