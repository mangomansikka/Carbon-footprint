package fi.metropolia.canopy.ui.homeview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.canopy.R
import fi.metropolia.canopy.data.source.LocationEntity
import fi.metropolia.canopy.utils.viewModelFactories.GraphViewModelFactory
import fi.metropolia.canopy.viewmodels.GraphViewModel
import java.util.Calendar
import java.util.Locale

private val BgGreen = Color(0xFF6F9C73)
private val LightGreen = Color(0xFFAED3B0)
private val AccentGreen = Color(0xFF58F0B1)

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: GraphViewModel = viewModel(factory = GraphViewModelFactory(context))
    
    val monthlyEmissions by viewModel.monthlyEmissions.collectAsState()
    val totalEmissionsKg by viewModel.totalEmissionsKg.collectAsState()
    val percentageChange by viewModel.percentageChange.collectAsState()
    val viewState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMonthlyEmissions()
    }

    val last4Months = remember(monthlyEmissions) {
        val calendar = Calendar.getInstance()
        (0..3).map { i ->
            val cal = calendar.clone() as Calendar
            cal.add(Calendar.MONTH, -3 + i)
            val monthKey = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: ""
            val value = (monthlyEmissions[monthKey] ?: 0.0).toFloat()
            monthName to value
        }
    }

    val chartPoints = remember(last4Months) {
        val maxVal = last4Months.maxOf { it.second }.coerceAtLeast(1f)
        last4Months.map { (it.second / maxVal) * 100f + 20f }
    }

    val animatedValueState = animateFloatAsState(targetValue = totalEmissionsKg.toFloat(), label = "co2Animation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(24.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "My Footprint",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White)

            Spacer(Modifier.height(16.dp))

            AnimatedFootprintHeader(animatedValueState, percentageChange)

            Spacer(Modifier.height(24.dp))

            ToggleViewButtons(viewState)

            Spacer(Modifier.height(32.dp))

            if (viewState.value) {
                EmissionsCalendar(viewModel)
            } else {
                MonthlyGraphSection(chartPoints, last4Months)
            }

            Spacer(Modifier.height(40.dp))

            PlantGirlSection()
        }
    }
}

@Composable
fun AnimatedFootprintHeader(valueState: State<Float>, percentageChange: Double) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = String.format("%.2f", valueState.value),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "ton CO₂/year", style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }

        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (percentageChange >= 0) Icons.Default.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = if (percentageChange <= 0) AccentGreen else Color(0xFFFF6B6B)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = String.format("%.1f", Math.abs(percentageChange)) + "% " + 
                       (if (percentageChange >= 0) "increase" else "decrease") + " since last month",
                color = Color.White
            )
        }
    }
}

@Composable
fun ToggleViewButtons(viewState: MutableState<Boolean>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { viewState.value = false },
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (!viewState.value) Color.White else LightGreen, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Monthly", fontSize = 12.sp)
        }

        Spacer(Modifier.width(8.dp))

        Button(
            onClick = { viewState.value = true },
            modifier = Modifier.weight(1f).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (viewState.value) Color.White else LightGreen, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Calendar", fontSize = 12.sp)
        }
    }
}

@Composable
fun MonthlyGraphSection(chartPoints: List<Float>, last4Months: List<Pair<String, Float>>) {
    Column {
        LineChart(chartPoints)
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            last4Months.forEach {
                Text(
                    it.first,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun EmissionsCalendar(viewModel: GraphViewModel) {
    val daysWithData by viewModel.daysWithData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDaysWithData()
    }

    val calendarData = remember {
        val cal = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
        val mName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
        val yr = cal.get(Calendar.YEAR)
        val monthIdx = cal.get(Calendar.MONTH) // 0-indexed
        val dInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDay = cal.get(Calendar.DAY_OF_WEEK)
        val off = when (firstDay) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
        Triple(mName, yr, dInMonth) to (off to monthIdx)
    }
    
    val (baseInfo, extra) = calendarData
    val (monthName, year, daysInMonth) = baseInfo
    val (offset, monthIndex) = extra
    
    var showDialog by remember { mutableStateOf(false) }
    var selectedDateStr by remember { mutableStateOf("") }
    var dayRange by remember { mutableStateOf(0L to 0L) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$monthName $year",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val totalSlots = offset + daysInMonth
        val rows = (totalSlots + 6) / 7
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (r in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (c in 0 until 7) {
                        val index = r * 7 + c
                        if (index < offset || index >= totalSlots) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val day = index - offset + 1
                            val dateKey = String.format("%04d-%02d-%02d", year, monthIndex + 1, day)
                            val hasData = daysWithData.contains(dateKey)

                            CalendarDay(
                                day = day,
                                monthName = monthName,
                                hasData = hasData,
                                onDayClick = { start, end, dateStr ->
                                    dayRange = start to end
                                    selectedDateStr = dateStr
                                    viewModel.loadCalenderData(start, end)
                                    showDialog = true
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        TripDetailsDialog(
            date = selectedDateStr,
            viewModel = viewModel,
            onDismiss = { showDialog = false },
            dayStart = dayRange.first,
            dayEnd = dayRange.second
        )
    }
}

@Composable
fun CalendarDay(
    day: Int, 
    monthName: String, 
    hasData: Boolean,
    onDayClick: (Long, Long, String) -> Unit, 
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable {
                val cal = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
                }
                val start = cal.timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
                val end = cal.timeInMillis
                onDayClick(start, end, "$day $monthName")
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = day.toString(), color = Color.White)
            if (hasData) {
                Spacer(Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(AccentGreen, CircleShape)
                )
            }
        }
    }
}

@Composable
fun TripDetailsDialog(date: String, viewModel: GraphViewModel, onDismiss: () -> Unit, dayStart: Long, dayEnd: Long) {
    val trips by viewModel.calenderData.collectAsState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Trips on $date") },
        text = {
            if (trips.isEmpty()) {
                Text("No data for this day.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(trips) { trip ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(trip.transportModes.ifEmpty { "Trip" }, fontWeight = FontWeight.Bold)
                                Text("${String.format("%.2f", trip.carbonEmissionGrams / 1000.0)} kg CO₂", fontSize = 12.sp)
                            }
                            IconButton(onClick = { viewModel.deleteLocationsById(trip.id, dayStart, dayEnd) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun PlantGirlSection() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.4f)
                quadraticBezierTo(size.width * 0.25f, size.height * 0.2f, size.width * 0.5f, size.height * 0.45f)
                quadraticBezierTo(size.width * 0.75f, size.height * 0.7f, size.width, size.height * 0.4f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path = path, color = LightGreen)
        }
        Image(painter = painterResource(R.drawable.plant_girl), contentDescription = null, modifier = Modifier.align(Alignment.BottomCenter).height(240.dp))
    }
}

@Composable
fun LineChart(points: List<Float>) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)) {
        if (points.size < 2) return@Canvas
        val space = size.width / (points.size - 1)
        for (i in 0 until points.size - 1) {
            val start = Offset(space * i, size.height - points[i])
            val end = Offset(space * (i + 1), size.height - points[i + 1])
            
            drawLine(
                color = AccentGreen.copy(alpha = 0.3f), 
                start = start, 
                end = end, 
                strokeWidth = 16f
            )
            
            drawLine(
                color = AccentGreen, 
                start = start, 
                end = end, 
                strokeWidth = 6f
            )
            
            drawCircle(
                color = AccentGreen, 
                radius = 8f, 
                center = start
            )
            
            if (i == points.size - 2) drawCircle(
                color = AccentGreen, 
                radius = 8f, 
                center = end
            )
        }
    }
}
