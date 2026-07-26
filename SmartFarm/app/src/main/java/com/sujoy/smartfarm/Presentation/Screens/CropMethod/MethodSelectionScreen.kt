package com.sujoy.smartfarm.Presentation.Screens.CropMethod

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.Presentation.Components.CropMethod.FarmMethod
import com.sujoy.smartfarm.Presentation.Components.CropMethod.MethodErrorView
import com.sujoy.smartfarm.Presentation.Components.CropMethod.MethodLoadingView
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.Presentation.ViewModel.GeminiViewModel
import com.sujoy.smartfarm.ui.theme.*
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.R
import  com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedCropName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.Presentation.Utils.FarmMethod.translatedMethodLabel
import com.sujoy.smartfarm.Presentation.Utils.FarmMethod.translatedMethodTagline
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MethodSelectionScreen(
    cropId: String,
    cropName: String,
    district : String,
    season : String,
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel(),
    geminiViewModel: GeminiViewModel = hiltViewModel()
) {
    val state         by appViewModel.cropMethodState.collectAsState()
    val estimateState by geminiViewModel.estimateCostState.collectAsState()

    var selectedMethod by remember { mutableStateOf(FarmMethod.MIXED) }
    var farmSize       by remember { mutableStateOf("") }
    val currentLangCode = LocalConfiguration.current.locales[0].language

    LaunchedEffect(Unit) { appViewModel.getCropMethod(cropId, currentLangCode) }

    val size = farmSize.toDoubleOrNull()

    val cacheKey =
        if (size != null)
            "${selectedMethod.label.uppercase()}_${size}_$currentLangCode"
        else
            ""

    val aiResult =

        estimateState.results[cacheKey]

    val canContinue =

        farmSize.isNotBlank() &&

                aiResult != null

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = "${translatedCropName(cropName)} ${stringResource(R.string.farming_title_suffix)}",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            when {
                state.isLoading            -> MethodLoadingView()
                state.error.isNotEmpty()   -> MethodErrorView(state.error)

                state.cropMethod != null   -> {
                    val method = state.cropMethod!!
                    val accent = selectedMethod.color

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 20.dp, end = 20.dp,
                            top = 14.dp, bottom = 36.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // ── 1. Hero banner
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                GreenPrimary,
                                                Color(0xFF388E3C)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(WhitePure.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) { Text("🌾", fontSize = 26.sp) }
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            stringResource(R.string.choose_farming_method),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WhitePure
                                        )
                                        Text(
                                            stringResource(R.string.compare_cost_yield),
                                            fontSize = 11.sp,
                                            color = WhitePure.copy(alpha = 0.78f)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            FarmMethod.entries.forEach { fm ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(WhitePure.copy(alpha = 0.15f))
                                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                                ) {
                                                    Text(translatedMethodLabel(fm), fontSize = 9.sp, color = WhitePure, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── 2. Section label
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Tune,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    stringResource(R.string.select_your_method),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        // ── 3. Method selector — 3 rich cards
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Max),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FarmMethod.entries.forEach { fm ->
                                    val isSelected = fm == selectedMethod
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected)
                                                    Brush.verticalGradient(
                                                        listOf(fm.color, fm.color.copy(alpha = 0.85f))
                                                    )
                                                else
                                                    Brush.verticalGradient(
                                                        listOf(WhitePure, WhitePure)
                                                    )
                                            )
                                            .border(
                                                width = if (isSelected) 0.dp else 1.dp,
                                                color = OutlineGreen,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedMethod = fm }
                                            .padding(vertical = 16.dp, horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) WhitePure.copy(alpha = 0.2f)
                                                    else fm.color.copy(alpha = 0.1f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(fm.emoji, fontSize = 20.sp)
                                        }
                                        Text(
                                            translatedMethodLabel(fm),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) WhitePure else TextPrimary
                                        )
                                        Text(
                                            translatedMethodTagline(fm),
                                            fontSize = 9.sp,
                                            color = if (isSelected) WhitePure.copy(alpha = 0.8f)
                                            else TextSecondary,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 12.sp
                                        )
                                        AnimatedVisibility(visible = isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(WhitePure)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── 4. Cost & Yield metric cards
                        item {
                            val cost = aiResult?.estimatedCost
                                ?: when (selectedMethod) {
                                    FarmMethod.ORGANIC   -> method.organicCost
                                    FarmMethod.INORGANIC -> method.inorganicCost
                                    FarmMethod.MIXED     -> method.mixedCost
                                }
                            val yieldVal = aiResult?.estimatedYield
                                ?: when (selectedMethod) {
                                    FarmMethod.ORGANIC   -> method.organicYield
                                    FarmMethod.INORGANIC -> method.inorganicYield
                                    FarmMethod.MIXED     -> method.mixedYield
                                }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Max),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StyledMetricCard(
                                    icon = Icons.Outlined.CurrencyRupee,
                                    label = stringResource(R.string.estimated_cost_label),
                                    value = cost,
                                    accentColor = accent,
                                    modifier = Modifier.weight(1f)
                                )
                                StyledMetricCard(
                                    icon = Icons.Outlined.Agriculture,
                                    label = stringResource(R.string.expected_yield_label2),
                                    value = yieldVal,
                                    accentColor = accent,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // ── 5. AI Estimator section
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(WhitePure)
                                    .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(GreenContainer),
                                        contentAlignment = Alignment.Center
                                    ) { Text("🤖", fontSize = 16.sp) }
                                    Column {
                                        Text(
                                            stringResource(R.string.ai_cost_estimator_title),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            stringResource(R.string.ai_cost_estimator_subtitle),
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                HorizontalDivider(color = GreenContainer, thickness = 1.dp)

                                // Input + button
                                OutlinedTextField(

                                    value = farmSize,

                                    onValueChange = {

                                        farmSize = it

                                    },

                                    modifier = Modifier.fillMaxWidth(),

                                    label = { Text(stringResource(R.string.farm_size_label)) },

                                    placeholder = { Text(stringResource(R.string.farm_size_placeholder), color = TextSecondary.copy(alpha = 0.5f)) },

                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.SquareFoot,
                                            contentDescription = null,
                                            tint = GreenPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },

                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor   = GreenPrimary,
                                        unfocusedBorderColor = OutlineGreen,
                                        focusedLabelColor    = GreenPrimary,
                                        cursorColor          = GreenPrimary,
                                        focusedContainerColor   = WhitePure,
                                        unfocusedContainerColor = WhitePure
                                    )

                                )

                                Button(

                                    onClick = {
                                        if (farmSize.isNotBlank()) {
                                            android.util.Log.d("LANG_DEBUG", "Sending estimateCost with languageCode = $currentLangCode")

                                            geminiViewModel.estimateCost(
                                                EstimateCostRequest(
                                                    cropName = cropName,
                                                    farmingMethod = selectedMethod.label,
                                                    farmSize = farmSize.toDouble(),
                                                    languageCode = currentLangCode
                                                )
                                            )
                                        }
                                    },

                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenPrimary,
                                        contentColor   = WhitePure,
                                        disabledContainerColor = OutlineGreen,
                                        disabledContentColor   = WhitePure
                                    ),
                                    enabled = farmSize.isNotBlank() && !estimateState.isLoading

                                ) {
                                    if (estimateState.isLoading) {
                                        CircularProgressIndicator(
                                            color = WhitePure,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.estimating_label), fontWeight = FontWeight.SemiBold)
                                    } else {
                                        Text(stringResource(R.string.estimate_with_ai_btn), fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Error
                                AnimatedVisibility(visible = estimateState.error.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Outlined.ErrorOutline,
                                            contentDescription = null,
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            estimateState.error,
                                            color = Color(0xFFC62828),
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                if (farmSize.isBlank()) {

                                    Text(

                                        stringResource(R.string.enter_farm_size_warning),

                                        color = Color(0xFFFF9800),

                                        fontSize = 12.sp

                                    )

                                }
                                else if (aiResult == null) {

                                    Text(

                                        stringResource(R.string.estimate_before_continue),

                                        color = GreenPrimary,

                                        fontSize = 12.sp

                                    )

                                }
                            }
                        }

                        // ── 6. AI Result cards (Duration + Labour + Materials + Notes)
                        item {
                            AnimatedVisibility(
                                visible = aiResult != null,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                            ) {
                                aiResult?.let { result ->
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                        // Duration + Labour side by side
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Max),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            StyledMetricCard(

                                                icon = Icons.Outlined.DateRange,

                                                label = stringResource(R.string.estimated_duration_label),

                                                value = "${localizedDigits(result.estimatedDuration)} ${stringResource(R.string.unit_days)}",

                                                accentColor = accent,

                                                modifier = Modifier.weight(1f)

                                            )
                                            StyledMetricCard(

                                                icon = Icons.Outlined.Person,

                                                label = stringResource(R.string.labour_required_label),

                                                value = result.labourRequired,

                                                accentColor = accent,

                                                modifier = Modifier.weight(1f)

                                            )
                                        }

                                        // Materials card
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(WhitePure)
                                                .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(0.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.padding(bottom = 12.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(9.dp))
                                                        .background(GreenContainer),
                                                    contentAlignment = Alignment.Center
                                                ) { Text("📦", fontSize = 15.sp) }
                                                Text(
                                                    stringResource(R.string.required_materials_title),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            }

                                            HorizontalDivider(color = GreenContainer, thickness = 1.dp)
                                            Spacer(Modifier.height(10.dp))

                                            result.materials.forEachIndexed { i, material ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(
                                                            if (i % 2 == 0) OffWhite else WhitePure
                                                        )
                                                        .padding(horizontal = 12.dp, vertical = 9.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(6.dp)
                                                                .clip(CircleShape)
                                                                .background(accent)
                                                        )
                                                        Text(
                                                            material.name,
                                                            fontSize = 13.sp,
                                                            color = TextPrimary
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(accent.copy(alpha = 0.1f))
                                                            .padding(horizontal = 9.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            material.quantity,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = accent
                                                        )
                                                    }
                                                }
                                                if (i < result.materials.lastIndex) Spacer(Modifier.height(4.dp))
                                            }
                                        }

                                        // AI Recommendation note card
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(WhitePure)
                                                .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("🤖", fontSize = 16.sp)
                                                Text(
                                                    stringResource(R.string.ai_recommendation_title),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            }
                                            HorizontalDivider(color = GreenPrimary.copy(alpha = 0.15f))
                                            Text(

                                                result.notes,

                                                fontSize = 13.sp,
                                                color = TextPrimary,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── 7. Advantages card
                        item {
                            val advantages = when (selectedMethod) {
                                FarmMethod.ORGANIC   -> method.organicAdvantages
                                FarmMethod.INORGANIC -> method.inorganicAdvantages
                                FarmMethod.MIXED     -> method.mixedAdvantages
                            }
                            StyledProConCard(
                                title = stringResource(R.string.advantages_title),
                                items = advantages,
                                isAdvantage = true,
                                accentColor = accent
                            )
                        }

                        // ── 8. Disadvantages card
                        item {
                            val disadvantages = when (selectedMethod) {
                                FarmMethod.ORGANIC   -> method.organicDisadvantages
                                FarmMethod.INORGANIC -> method.inorganicDisadvantages
                                FarmMethod.MIXED     -> method.mixedDisadvantages
                            }
                            StyledProConCard(
                                title = stringResource(R.string.disadvantages_title),
                                items = disadvantages,
                                isAdvantage = false,
                                accentColor = accent
                            )
                        }

                        // ── 9. Continue button
                        item {
                            Spacer(Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    if (!canContinue) return@Button
                                    aiResult?.let {

                                        geminiViewModel.selectEstimate(it)

                                    }
                                    navController.navigate(

                                        FarmerRoutes.CreateFarmScreen(

                                            cropId = cropId,

                                            cropName = cropName,

                                            farmingMethod = selectedMethod.label,

                                            farmSize = farmSize.toDouble(),

                                            estimatedCost = aiResult!!.estimatedCost,

                                            estimatedYield = aiResult.estimatedYield,

                                            estimatedDuration = aiResult.estimatedDuration,

                                            labourRequired = aiResult.labourRequired,

                                            notes = aiResult.notes,

                                            district = district,
                                            season=season

                                        )

                                    )
                                },
                                enabled = canContinue,
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accent,
                                    contentColor   = WhitePure
                                )
                            ) {
                                Icon(
                                    Icons.Outlined.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Continue with ${selectedMethod.label} farming",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Styled metric card ────────────────────────────────────────────────────────

@Composable
private fun StyledMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(WhitePure)
            .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }
        Text(label, fontSize = 10.sp, color = TextSecondary, lineHeight = 13.sp)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 17.sp
        )
    }
}

// ── Styled pro/con card ────────────────────────────────────────────────────────

@Composable
private fun StyledProConCard(
    title: String,
    items: List<String>,
    isAdvantage: Boolean,
    accentColor: Color
) {
    val bgColor  = if (isAdvantage) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
    val dotColor = if (isAdvantage) Color(0xFF2E7D32) else Color(0xFFF57F17)
    val emoji    = if (isAdvantage) "✅" else "⚠️"

    var expanded by remember { mutableStateOf(true) }
    val chevronAngle by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(250),
        label = "chevron"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(WhitePure)
            .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
    ) {
        // Header — tappable to collapse
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) { Text(emoji, fontSize = 16.sp) }
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                // Item count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(dotColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "${items.size}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = dotColor
                    )
                }
            }
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp).rotate(chevronAngle)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 14.dp)) {
                HorizontalDivider(color = OutlineGreen.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(Modifier.height(10.dp))

                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (index % 2 == 0) OffWhite else WhitePure)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (index < items.lastIndex) Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}