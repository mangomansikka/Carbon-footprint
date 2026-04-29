package fi.metropolia.canopy.ui.homeview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fi.metropolia.canopy.R
import fi.metropolia.canopy.ui.overview.OverviewColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import fi.metropolia.canopy.data.repository.UserRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import kotlinx.coroutines.launch

@Composable
fun LandingScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = remember { CanopyDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }


    val userRole by userRepository.userRole.collectAsState(initial = null)

    var showSaved by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(R.drawable.eco_footprint),
            contentDescription = "Carbon footprint",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (userRole) {
                null -> "Loading..."
                "" -> "Welcome to Canopy"
                else -> "Welcome back!"
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Text(
            text = "Track your carbon footprint",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (userRole == "") "Choose your role to begin" else "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2E4E3F),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F4)),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {

            Column(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when {

                    userRole == null -> {
                        CircularProgressIndicator()
                    }


                    userRole == "" -> {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        userRepository.changeRole("student")
                                        showSaved = true
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Student")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        userRepository.changeRole("staff")
                                        showSaved = true
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Staff")
                        }

                        if (showSaved) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your choice has been saved ✓",
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }


                    else -> {
                        Text(
                            text = "${userRole!!.replaceFirstChar { it.uppercase() }} account ✓",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRoleDialog = true }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.metropolia),
            contentDescription = "App Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Change role") },
            text = {
                Column {
                    TextButton(onClick = {
                        scope.launch { userRepository.changeRole("student") }
                        showRoleDialog = false
                    }) {
                        Text("Student")
                    }

                    TextButton(onClick = {
                        scope.launch { userRepository.changeRole("staff") }
                        showRoleDialog = false
                    }) {
                        Text("Staff")
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LandingPreview() {
    MaterialTheme {
        LandingScreen()
    }
}