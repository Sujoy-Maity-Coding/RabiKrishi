package com.sujoy.smartfarm.Presentation.Screens.Profile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.FarmList.SectionCard
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
import android.provider.Settings
import androidx.compose.material.icons.outlined.Language
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.R

// ── Customer care / company details — edit to your real values ──────────────
private const val SUPPORT_EMAIL = "sujoymaity526@gmail.com"
private const val APP_VERSION = "1.0.0"

private fun initialsOf(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty()  -> "?"
        parts.size == 1  -> parts[0].take(1).uppercase()
        else              -> (parts.first().take(1) + parts.last().take(1)).uppercase()
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
        targetValue = if (isPressed) 0.96f else 1f,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val profileState by appViewModel.profileState.collectAsState()
    val myFarmsState by appViewModel.myFarmsState.collectAsState()
    val updateProfileState by appViewModel.updateProfileState.collectAsState()

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        appViewModel.getFarmerProfile()
        appViewModel.getMyFarms()
    }

    // ── Close edit sheet automatically once the save succeeds
    var showEditSheet by remember { mutableStateOf(false) }
    LaunchedEffect(updateProfileState.success) {
        if (updateProfileState.success.isNotEmpty()) {
            showEditSheet = false
            appViewModel.getFarmerProfile()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // ── Staggered entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        visible = true
    }

    val isRefreshing = profileState.isLoading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            appViewModel.getFarmerProfile()
            appViewModel.getMyFarms()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            containerColor = OffWhite,
            topBar = {
                FarmTopBar(
                    title = stringResource(R.string.profile_title),
                    showBack = true,
                    onBack = { navController.popBackStack() }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                when {

                    profileState.isLoading && profileState.farmer == null -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GreenPrimary, strokeWidth = 3.dp)
                        }
                    }

                    profileState.error.isNotEmpty() && profileState.farmer == null -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(profileState.error, fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    else -> {
                        val farmer = profileState.farmer

                        // ── User card with editable avatar
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 4 }
                        ) {
                            SectionCard {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box {
                                        Box(
                                            modifier = Modifier
                                                .size(84.dp)
                                                .clip(CircleShape)
                                                .background(GreenPrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                initialsOf(farmer?.name ?: ""),
                                                fontSize = 30.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WhitePure
                                            )
                                        }
                                        // Edit badge
                                        PressScale(
                                            onClick = { showEditSheet = true },
                                            modifier = Modifier.align(Alignment.BottomEnd)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(WhitePure)
                                                    .border(2.dp, OffWhite, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Filled.Edit,
                                                    contentDescription = stringResource(R.string.profile_edit_desc),
                                                    tint = GreenPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(14.dp))

                                    val defaultFarmerName = stringResource(R.string.profile_default_farmer_name)
                                    Text(
                                        farmer?.name?.ifBlank { defaultFarmerName } ?: defaultFarmerName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        farmer?.email ?: "",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )

                                    if (!farmer?.phoneNumber.isNullOrBlank()) {
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            farmer?.phoneNumber ?: "",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(Modifier.height(14.dp))

                                    // Farmer ID — tap to copy
                                    PressScale(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(farmer?.userId ?: ""))
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(OffWhite)
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Badge,
                                                contentDescription = null,
                                                tint = TextSecondary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                stringResource(
                                                    R.string.profile_id_prefix,
                                                    farmer?.userId?.take(8) ?: "--"
                                                ),
                                                fontSize = 10.sp,
                                                color = TextSecondary
                                            )
                                            Icon(
                                                Icons.Outlined.ContentCopy,
                                                contentDescription = stringResource(R.string.profile_copy_id_desc),
                                                tint = TextSecondary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Stat row — tappable, real data
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400, delayMillis = 80)) +
                                    slideInVertically(tween(400, delayMillis = 80)) { it / 4 }
                        ) {
                            PressScale(
                                onClick = {
                                    navController.navigate(FarmerRoutes.MyFarmsScreen)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(WhitePure)
                                        .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(13.dp))
                                            .background(GreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.Agriculture,
                                            contentDescription = null,
                                            tint = GreenPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            stringResource(R.string.profile_my_farms_title),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            if (myFarmsState.isLoading) stringResource(R.string.profile_loading)
                                            else stringResource(R.string.profile_farms_registered, myFarmsState.farms.size),
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(GreenPrimary.copy(alpha = 0.1f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            "${myFarmsState.farms.size}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GreenPrimary
                                        )
                                    }
                                    Icon(
                                        Icons.Outlined.ChevronRight,
                                        contentDescription = null,
                                        tint = OutlineGreen
                                    )
                                }
                            }
                        }

                        // ── About & Support
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(450, delayMillis = 140)) +
                                    slideInVertically(tween(450, delayMillis = 140)) { it / 4 }
                        ) {
                            SectionCard {
                                Text(
                                    stringResource(R.string.profile_about_support_title),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(11.dp))
                                            .background(GreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) { Text("🌾", fontSize = 17.sp) }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(stringResource(R.string.profile_app_name), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text(stringResource(R.string.profile_version, APP_VERSION), fontSize = 11.sp, color = TextSecondary)
                                    }
                                }

                                Spacer(Modifier.height(14.dp))
                                HorizontalDivider(color = OutlineGreen.copy(alpha = 0.4f))
                                Spacer(Modifier.height(14.dp))

                                // Customer care — tap to email
                                PressScale(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:$SUPPORT_EMAIL")
                                            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.profile_support_email_subject))
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    SupportRow(
                                        icon = Icons.Outlined.Email,
                                        title = stringResource(R.string.profile_customer_care_title),
                                        subtitle = SUPPORT_EMAIL
                                    )
                                }

                                Spacer(Modifier.height(6.dp))

                                // Share app
                                PressScale(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                context.getString(
                                                    R.string.profile_share_message,
                                                    "https://play.google.com/store/apps/details?id=${context.packageName}"
                                                )
                                            )
                                        }
                                        context.startActivity(
                                            Intent.createChooser(shareIntent, context.getString(R.string.profile_share_app_title))
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    SupportRow(
                                        icon = Icons.Outlined.Share,
                                        title = stringResource(R.string.profile_share_app_title),
                                        subtitle = stringResource(R.string.profile_share_app_subtitle)
                                    )
                                }

                                Spacer(Modifier.height(6.dp))

                                // Change language
                                PressScale(
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
                                            data = Uri.parse("package:${context.packageName}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    SupportRow(
                                        icon = Icons.Outlined.Language,
                                        title = stringResource(R.string.profile_change_language_title),
                                        subtitle = stringResource(R.string.profile_change_language_subtitle)
                                    )
                                }
                            }
                        }

//                        Spacer(Modifier.height(24.dp))

                        // ── Logout button
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(500, delayMillis = 200))
                        ) {
                            PressScale(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFFDECEA))
                                        .padding(vertical = 14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Outlined.Logout,
                                        contentDescription = null,
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        stringResource(R.string.profile_logout_btn),
                                        color = Color(0xFFC62828),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Logout confirmation
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = WhitePure,
            icon = {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = Color(0xFFC62828)
                )
            },
            title = { Text(stringResource(R.string.profile_logout_dialog_title), fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Text(
                    stringResource(R.string.profile_logout_dialog_text),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    appViewModel.logout()
                    navController.navigate(FarmerRoutes.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text(stringResource(R.string.profile_logout_btn), color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.profile_cancel), color = TextSecondary)
                }
            }
        )
    }

    // ── Edit profile bottom sheet
    if (showEditSheet) {
        val farmer = profileState.farmer
        var name by remember(farmer) { mutableStateOf(farmer?.name ?: "") }
        var phone by remember(farmer) { mutableStateOf(farmer?.phoneNumber ?: "") }

        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            containerColor = WhitePure
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    stringResource(R.string.profile_edit_sheet_title),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.profile_full_name_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = OutlineGreen,
                        focusedLabelColor = GreenPrimary,
                        cursorColor = GreenPrimary
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(stringResource(R.string.profile_phone_label)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = OutlineGreen,
                        focusedLabelColor = GreenPrimary,
                        cursorColor = GreenPrimary
                    )
                )

                if (updateProfileState.error.isNotEmpty()) {
                    Text(
                        updateProfileState.error,
                        fontSize = 12.sp,
                        color = Color(0xFFC62828)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (name.isNotBlank()) GreenPrimary else OutlineGreen)
                        .clickable(enabled = name.isNotBlank() && !updateProfileState.isLoading) {
                            appViewModel.updateFarmerProfile(name.trim(), phone.trim())
                        }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (updateProfileState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = WhitePure
                        )
                    } else {
                        Text(
                            stringResource(R.string.profile_save_changes_btn),
                            color = WhitePure,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SupportRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(GreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = OutlineGreen,
            modifier = Modifier.size(18.dp)
        )
    }
}