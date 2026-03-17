package fi.metropolia.canopy.ui.homeview
import android.hardware.camera2.params.BlackLevelPattern
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import fi.metropolia.canopy.R

import fi.metropolia.canopy.ui.overview.OverviewColors
import fi.metropolia.canopy.ui.theme.Darkbutton

@Composable
fun LandingScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            painter = painterResource(R.drawable.tree),
            contentDescription = "Community tree",
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(10.dp)


        )

        Spacer(modifier = Modifier.height(32.dp))


        Text(
            text = "Track your carbon footprint",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,

            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))


        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = { navController.navigate("homeScreen") },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Darkbutton,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Start Tracking",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                style = MaterialTheme.typography.titleLarge,



                )
        }

        Spacer(modifier = Modifier.height(90.dp))

        Image(
            painter = painterResource(id = R.drawable.metropolia),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(80.dp)

        )
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