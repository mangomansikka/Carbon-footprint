package fi.metropolia.canopy.ui.homeview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import fi.metropolia.canopy.ui.theme.Darkbutton
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import fi.metropolia.canopy.data.repository.UserRepository
import fi.metropolia.canopy.data.source.CanopyDatabase
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val db = remember { CanopyDatabase.getInstance(context) }
    val userRepository = remember { UserRepository(db.userDao()) }

    val userRole by userRepository.userRole.collectAsState(initial = "student")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(R.drawable.tree),
            contentDescription = "Community tree",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Track your carbon footprint",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = userRole == "student",
                onClick = {
                    scope.launch {
                        userRepository.changeRole("student")
                    }
                }
            )
            Text("Student")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = userRole == "staff",
                onClick = {
                    scope.launch {
                        userRepository.changeRole("staff")
                    }
                }
            )
            Text("Staff")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("homeScreen") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(80.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Darkbutton,
                contentColor = Color.White
            )
        ) {
            Text("Start Tracking")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.metropolia),
            contentDescription = "App Logo",
            modifier = Modifier.size(70.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityTreeCardPreview() {
    val navController = rememberNavController()
    MaterialTheme {
        LandingScreen(navController = navController)
    }
}