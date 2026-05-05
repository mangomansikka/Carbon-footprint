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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            contentDescription = stringResource(R.string.track_footprint_desc),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (userRole) {
                null -> stringResource(R.string.loading)
                "" -> stringResource(R.string.welcome_to_canopy)
                else -> stringResource(R.string.welcome_back)
            },
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Text(
            text = stringResource(R.string.track_footprint_desc),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (userRole == "") stringResource(R.string.choose_role_hint) else "",
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
                            Text(stringResource(R.string.role_student))
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
                            Text(stringResource(R.string.role_staff))
                        }

                        if (showSaved) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.choice_saved),
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }


                    else -> {
                        val roleDisplay = if (userRole == "student") stringResource(R.string.role_student) else stringResource(R.string.role_staff)
                        Text(
                            text = stringResource(R.string.account_confirmed_format, roleDisplay),
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
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text(stringResource(R.string.change_role)) },
            text = {
                Column {
                    TextButton(onClick = {
                        scope.launch { userRepository.changeRole("student") }
                        showRoleDialog = false
                    }) {
                        Text(stringResource(R.string.role_student))
                    }

                    TextButton(onClick = {
                        scope.launch { userRepository.changeRole("staff") }
                        showRoleDialog = false
                    }) {
                        Text(stringResource(R.string.role_staff))
                    }
                }
            },
            confirmButton = {}
        )
    }
}
