package fi.metropolia.canopy.ui.LoginPage

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.metropolia.canopy.R
import fi.metropolia.canopy.ui.overview.OverviewColors
import fi.metropolia.canopy.ui.theme.Darkbutton

/**
 * LoginPage composable function for displaying the login screen
 */
@Composable
fun LoginPage(onLoginSuccess: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OverviewColors.BgGreen)
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.eco_footprint),
            contentDescription = "App Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Log in to your account",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF2E4E3F),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F4)),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Darkbutton,
                        unfocusedBorderColor = Darkbutton.copy(alpha = 0.5f),
                        focusedLabelColor = Darkbutton,
                        cursorColor = Darkbutton
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Darkbutton,
                        unfocusedBorderColor = Darkbutton.copy(alpha = 0.5f),
                        focusedLabelColor = Darkbutton,
                        cursorColor = Darkbutton
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onLoginSuccess() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Darkbutton,
                        contentColor = Color.White
                    )
                ) {
                    Text("Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { /* Navigate to registration */ }) {
                    Text(
                        text = "Don't have an account? Sign up",
                        color = Darkbutton,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.metropolia),
            contentDescription = "Metropolia Logo",
            modifier = Modifier.size(60.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    LoginPage()
}
