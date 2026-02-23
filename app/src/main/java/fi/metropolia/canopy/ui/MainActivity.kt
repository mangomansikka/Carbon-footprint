package fi.metropolia.canopy.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.location.LocationServices
import fi.metropolia.canopy.ui.LocationScreen
import fi.metropolia.canopy.ui.theme.CanopyMinnoTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        setContent {
            CanopyMinnoTheme {
                LocationScreen(fusedLocationClient)
            }
        }
    }
}