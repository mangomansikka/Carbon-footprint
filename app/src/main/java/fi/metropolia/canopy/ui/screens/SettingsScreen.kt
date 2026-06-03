package fi.metropolia.canopy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fi.metropolia.canopy.ui.overview.OverviewColors
import androidx.compose.ui.platform.LocalContext
import fi.metropolia.canopy.data.repository.UserRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current

    val db = remember {
        CanopyDatabase.getInstance(context)
    }

    val userRepository = remember {
        UserRepository(db.userDao())
    }

    val userRole by userRepository.userRole.collectAsState(initial = "student")

    val savedCustomCo2 by userRepository.customCo2.collectAsState(initial = 0.0)

    var customCo2 by remember(savedCustomCo2) {
        mutableStateOf(
            if (savedCustomCo2 == 0.0) "" else savedCustomCo2.toString()
        )
    }

    var autoTracking by remember { mutableStateOf(true) }
    var locationTracking by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    val darkButton = Color(0xFF4A3D35)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .background(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    ),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text("← Back")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF1F8F4)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Role: ${
                        userRole.replaceFirstChar {
                            it.uppercase()
                        }
                    }"
                )

                Text("Email: Not connected")
                Text("Campus: Myyrmäki")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Vehicle Settings",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customCo2,
                    onValueChange = { customCo2 = it },
                    label = { Text("CO₂ emissions (g/km)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// TRACKING
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Tracking",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Automatic Trip Detection")

                    Switch(
                        checked = autoTracking,
                        onCheckedChange = { autoTracking = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = darkButton,
                            checkedThumbColor = Color.White
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Location Tracking")

                    Switch(
                        checked = locationTracking,
                        onCheckedChange = { locationTracking = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = darkButton,
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PREFERENCES
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notifications")



                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = darkButton,
                            checkedThumbColor = Color.White
                        )
                    )
                }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode")



                    Switch(
                        checked = darkMode,
                        onCheckedChange = { darkMode = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = darkButton,
                            checkedThumbColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ABOUT
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Canopy")
                Text("Version 1.0")
                Text("Metropolia University")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))



        Button(
            onClick = {
                scope.launch {
                    userRepository.updateCustomCo2(
                        customCo2.toDoubleOrNull() ?: 0.0
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = darkButton,
                contentColor = Color.White
            )
        ) {
            Text("Save Settings")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}