package fi.metropolia.canopy.ui.homeview
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
import fi.metropolia.canopy.R

@Composable
fun LandingScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF6E6)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            painter = painterResource(R.drawable.canopy),
            contentDescription = "Community tree",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))


        Text(
            text = "Track your carbon footprint and grow your community tree",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF2F5A3C),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Your daily travel choices help the tree grow greener.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = { /* navigate next */ },
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F6F4F),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Start Tracking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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
    MaterialTheme {
        LandingScreen()
    }
}