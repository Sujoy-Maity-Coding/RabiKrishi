package com.sujoy.smartfarm.Presentation.Screens.Dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenOnContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

/**
 * Opens the phone's own language settings screen.
 *
 * On Android 13+ this deep-links straight into "App languages" for this app
 * (Settings ▸ Apps ▸ SmartFarm ▸ App language) — the system UI that actually
 * controls per-app locale. On older Android versions that screen doesn't
 * exist, so we fall back to the general system locale settings instead.
 */
private fun openSystemLanguageSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    } else {
        Intent(Settings.ACTION_LOCALE_SETTINGS)
    }

    runCatching { context.startActivity(intent) }
        .onFailure {
            // Extremely rare fallback in case a device/OEM removed both screens.
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
}

/** Small helper: scales a composable down slightly while pressed, for a tactile feel. */
@Composable
private fun PressScale(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) { content() }
}

@Composable
fun LanguageSelectionScreen(

    onLanguageConfirmed: (String) -> Unit

) {
    val context = LocalContext.current

    Scaffold(containerColor = OffWhite) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Hero header ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(GreenPrimary, Color(0xFF388E3C))
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(WhitePure.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Language,
                        contentDescription = null,
                        tint = WhitePure,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    "Welcome",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
                Text(
                    "স্বাগতম  •  स्वागत है",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhitePure.copy(alpha = 0.9f)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    "Your language is controlled by your phone's settings",
                    fontSize = 13.sp,
                    color = WhitePure.copy(alpha = 0.85f)
                )
            }

            // ── Center content ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(GreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Change your language anytime",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "SmartFarm follows your phone's language. Open your device's " +
                            "language settings to add or switch to Bengali, Hindi, English, " +
                            "and more.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                SystemLanguageSettingsCard(
                    onClick = { openSystemLanguageSettings(context) }
                )
            }

            // ── Continue button ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = {
                        // No in-app locale switching anymore — language is
                        // whatever the system/app-language setting is set to.
                        // We just move the user forward.
                        onLanguageConfirmed(
                            context.resources.configuration.locales[0].language
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor = WhitePure
                    )
                ) {
                    Text(
                        "Continue  •  চালিয়ে যান  •  जारी रखें",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "You can change this anytime from your profile",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * A distinct, clearly-tappable card that hands off to the phone's own
 * "App languages" system settings screen.
 */
@Composable
private fun SystemLanguageSettingsCard(onClick: () -> Unit) {
    PressScale(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(WhitePure)
                .border(
                    width = 1.dp,
                    color = OutlineGreen.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(GreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = null,
                    tint = GreenOnContainer,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Open phone language settings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Change your device's app language here",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}