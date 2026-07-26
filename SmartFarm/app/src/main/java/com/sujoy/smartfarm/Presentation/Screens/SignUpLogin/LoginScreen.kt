package com.sujoy.smartfarm.Presentation.Screens.SignUpLogin

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTextField
import com.sujoy.smartfarm.Presentation.Components.Dashboard.PrimaryButton
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val authState by appViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState.success) {
        if (authState.success == "Login Successful") {
            navController.navigate(FarmerRoutes.LanguageSelectionScreen) {
                popUpTo(FarmerRoutes.LoginScreen) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = OffWhite
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Green header banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌾", fontSize = 52.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Rabi Krishi",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhitePure
                    )
                    Text(
                        text = "Sign in to your account",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WhitePure.copy(alpha = 0.8f)
                    )
                }
            }

            // Form card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(WhitePure)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome back 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    FarmTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email address"
                    )

                    FarmTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (authState.error.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = authState.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    PrimaryButton(text = "Sign in", onClick = {
                        appViewModel.login(email = email, password = password)
                    })

                    if (authState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                            color = GreenPrimary,
                            trackColor = GreenContainer
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        TextButton(onClick = { navController.navigate(FarmerRoutes.SignUpScreen) }) {
                            Text(
                                text = "Create one",
                                color = GreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}