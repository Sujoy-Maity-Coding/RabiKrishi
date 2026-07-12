package com.sujoy.smartfarm.Presentation.Screens.CropUpdate

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.Domain.model.AI.AIResult
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.FarmList.SectionCard
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.Presentation.ViewModel.GeminiViewModel
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenOnContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure
import android.Manifest
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import java.util.Locale

// ── Status helpers ──────────────────────────────────────────────────────────

private fun riskColor(risk: String?): Color = when (risk?.lowercase()) {
    "healthy", "low"          -> Color(0xFF2E7D32)
    "medium", "medium risk"   -> Color(0xFFF57F17)
    "high", "high risk"       -> Color(0xFFE65100)
    "critical"                -> Color(0xFFC62828)
    else                      -> TextSecondary
}

private fun riskEmoji(risk: String?): String = when (risk?.lowercase()) {
    "healthy", "low"          -> "✅"
    "medium", "medium risk"   -> "⚠️"
    "high", "high risk"       -> "🔶"
    "critical"                -> "🚨"
    else                      -> "❔"
}

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CropUpdateScreen(

    farmId: String,

    currentDay: Int,

    navController: NavHostController,

    appViewModel: AppViewModel = hiltViewModel(),

    geminiViewModel: GeminiViewModel = hiltViewModel()

) {
    var isListening by remember { mutableStateOf(false) }
    val context= LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
    val farmState by appViewModel.farmDetailsState.collectAsState()

    LaunchedEffect(farmId) {
        appViewModel.getFarmById(farmId)
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { selectedImageUri = it }

    val geminiState by geminiViewModel.uiState.collectAsState()
    val saveState by appViewModel.saveDailyUpdateState.collectAsState()

    LaunchedEffect(saveState.success) {
        if (saveState.success.isNotEmpty()) {
            Toast.makeText(context, saveState.success, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    var plantHeight by remember { mutableStateOf("") }
    var farmerNote by remember { mutableStateOf("") }
    var leafColor by remember { mutableStateOf("Green") }
    var soilMoisture by remember { mutableStateOf("Medium") }
    var pestFound by remember { mutableStateOf(false) }
    var floweringStarted by remember { mutableStateOf(false) }
    var fruitStarted by remember { mutableStateOf(false) }

    val leafColors = listOf(
        "Dark Green", "Green", "Light Green", "Yellow", "Brown", "Spotted", "Dry"
    )

    val moistureLevels = listOf(
        "Very Dry", "Dry", "Medium", "Wet", "Waterlogged"
    )

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val translatedNote by geminiViewModel.translatedNote.collectAsState()
    LaunchedEffect(translatedNote) {
        if (translatedNote.isNotBlank()) {
            farmerNote = if (farmerNote.isBlank()) translatedNote
            else "$farmerNote $translatedNote"
        }
    }
    val languages = listOf(
        "English"  to "en-IN",
        "বাংলা"    to "bn-IN",
        "हिन्दी"   to "hi-IN",
    )

    var selectedLang by remember { mutableStateOf(languages[0]) }

    val recognizerIntent = remember(selectedLang) {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLang.second)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLang.second)
        }
    }

    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) {
                isListening = false
                Toast.makeText(context, "Could not understand, try again", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.firstOrNull() ?: ""
                if (spokenText.isNotBlank()) {
                    if (selectedLang.second == "en-IN") {
                        // English — no translation needed, append directly
                        farmerNote = if (farmerNote.isBlank()) spokenText
                        else "$farmerNote $spokenText"
                    } else {
                        // Bengali/Hindi — send to Gemini for translation
                        geminiViewModel.translateToEnglish(spokenText)
                    }
                }
                isListening = false
            }
            override fun onPartialResults(partialResults: android.os.Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
                // optional: show partial preview
            }
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        onDispose {
            speechRecognizer.destroy()
        }
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = "Crop Health Analysis",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        val farm = farmState.farm

        when {

            // ── Loading farm details
            farmState.isLoading || farm == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("🌾", fontSize = 32.sp) }
                        CircularProgressIndicator(color = GreenPrimary, strokeWidth = 3.dp)
                        Text("Loading farm details…", fontSize = 13.sp, color = TextSecondary)
                    }
                }
            }

            // ── Error loading farm
            farmState.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Text(
                            "Failed to load",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(farmState.error, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // ── Content
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── 1. Farm info hero card
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(GreenPrimary)
                                .padding(18.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(WhitePure.copy(alpha = 0.18f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            "🌱 ${farm.farmingMethod}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = WhitePure
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF69F0AE))
                                        )
                                        Text(
                                            "Day $currentDay",
                                            fontSize = 10.sp,
                                            color = WhitePure.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Text(
                                    farm.cropName,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhitePure
                                )
                                Text(
                                    "${farm.farmName}  •  ${farm.landArea} Acre",
                                    fontSize = 12.sp,
                                    color = WhitePure.copy(alpha = 0.75f)
                                )

                                Spacer(Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { (currentDay / 120f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = WhitePure,
                                    trackColor = WhitePure.copy(alpha = 0.25f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${(currentDay / 120f * 100).toInt().coerceIn(0, 100)}% of crop cycle completed",
                                    fontSize = 10.sp,
                                    color = WhitePure.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // ── 2. Rice leaf image
                    item {
                        SectionCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "Rice Leaf Image",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            selectedImageUri?.let { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
                                )
                                Spacer(Modifier.height(14.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GreenPrimary)
                                    .clickable { launcher.launch("image/*") }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Image,
                                        contentDescription = null,
                                        tint = WhitePure,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        if (selectedImageUri == null) "Select Rice Leaf Image" else "Change Image",
                                        color = WhitePure,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // ── 3. Analyzing indicator
                    if (geminiState.isLoading) {
                        item {
                            SectionCard {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(color = GreenPrimary, strokeWidth = 3.dp)
                                    Text(
                                        "Analyzing crop using Gemini AI…",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // ── 4. Farmer observation
                    item {
                        SectionCard {
                            Text(
                                "👨‍🌾 Farmer Observation",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = plantHeight,
                                onValueChange = { plantHeight = it },
                                label = { Text("Plant Height (cm)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = farmFieldColors()
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                languages.forEach { (label, code) ->
                                    val isSelected = selectedLang.second == code
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) GreenPrimary else WhitePure)
                                            .border(1.dp, OutlineGreen, RoundedCornerShape(8.dp))
                                            .clickable { selectedLang = Pair(label, code) }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) WhitePure else TextPrimary
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {

                                OutlinedTextField(
                                    value = farmerNote,
                                    onValueChange = { farmerNote = it },
                                    label = { Text("Farmer Note") },
                                    placeholder = {
                                        Text(
                                            "Tap mic to speak or type here…",
                                            color = TextSecondary.copy(alpha = 0.5f),
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = farmFieldColors(),
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            if (isListening) {
                                                speechRecognizer.stopListening()
                                                isListening = false
                                            } else {
                                                speechRecognizer.startListening(recognizerIntent)
                                            }
                                        }) {
                                            Icon(
                                                imageVector = if (isListening)
                                                    Icons.Outlined.MicOff
                                                else
                                                    Icons.Outlined.Mic,
                                                contentDescription = "Voice input",
                                                tint = if (isListening) Color(0xFFC62828) else GreenPrimary
                                            )
                                        }
                                    }
                                )

                                // Listening indicator
                                AnimatedVisibility(visible = isListening) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFFEBEE))
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Pulsing red dot
                                        val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
                                            initialValue = 0.4f,
                                            targetValue = 1f,
                                            animationSpec = infiniteRepeatable(
                                                tween(600),
                                                RepeatMode.Reverse
                                            ),
                                            label = "dot"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFC62828).copy(alpha = pulse))
                                        )
                                        Text(
                                            "Listening… speak now",
                                            fontSize = 12.sp,
                                            color = Color(0xFFC62828),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(Modifier.weight(1f))
                                        TextButton(
                                            onClick = {
                                                speechRecognizer.stopListening()
                                                isListening = false
                                            },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Stop", fontSize = 11.sp, color = Color(0xFFC62828))
                                        }
                                    }
                                }

                                // Clear button
                                if (farmerNote.isNotBlank()) {
                                    TextButton(
                                        onClick = { farmerNote = "" },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Clear note", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Text(
                                "Leaf Color",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                leafColors.forEach { color ->
                                    SelectableChip(
                                        label = color,
                                        selected = leafColor == color,
                                        onClick = { leafColor = color }
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Text(
                                "Soil Moisture",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                moistureLevels.forEach { moisture ->
                                    SelectableChip(
                                        label = moisture,
                                        selected = soilMoisture == moisture,
                                        onClick = { soilMoisture = moisture }
                                    )
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            ObservationCheckRow(
                                checked = pestFound,
                                onCheckedChange = { pestFound = it },
                                label = "Pest Found",
                                emoji = "🐛"
                            )
                            Spacer(Modifier.height(6.dp))
                            ObservationCheckRow(
                                checked = floweringStarted,
                                onCheckedChange = { floweringStarted = it },
                                label = "Flowering Started",
                                emoji = "🌸"
                            )
                            Spacer(Modifier.height(6.dp))
                            ObservationCheckRow(
                                checked = fruitStarted,
                                onCheckedChange = { fruitStarted = it },
                                label = "Fruit Started",
                                emoji = "🌾"
                            )
                        }
                    }

                    // ── 5. Analyze button
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (selectedImageUri != null && !geminiState.isLoading)
                                        GreenPrimary
                                    else
                                        OutlineGreen
                                )
                                .clickable(
                                    enabled = selectedImageUri != null && !geminiState.isLoading
                                ) {
                                    val image = selectedImageUri ?: return@clickable

                                    val request = GeminiRequest(
                                        imageUri = image,
                                        cropName = farm.cropName,
                                        farmingMethod = farm.farmingMethod,
                                        currentDay = currentDay,
                                        fieldArea = farm.landArea.toDoubleOrNull() ?: 0.0,
                                        district = "",
                                        season = "",
                                        plantHeight = plantHeight.toDoubleOrNull() ?: 0.0,
                                        leafColor = leafColor,
                                        soilMoisture = soilMoisture,
                                        pestFound = pestFound,
                                        floweringStarted = floweringStarted,
                                        fruitStarted = fruitStarted,
                                        farmerNote = farmerNote
                                    )

                                    geminiViewModel.analyzeCrop(request)
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (geminiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = WhitePure
                                    )
                                    Text("Analyzing…", color = WhitePure, fontWeight = FontWeight.SemiBold)
                                } else {
                                    Text(
                                        "Analyze Crop",
                                        color = WhitePure,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    // ── 6. Error
                    geminiState.error.takeIf { it.isNotBlank() }?.let { error ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFDECEA))
                                    .border(1.dp, Color(0xFFF5B5B0), RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = error,
                                    color = Color(0xFFB71C1C),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // ── 7. AI analysis summary
                    geminiState.result?.let { result ->
                        item {
                            SectionCard {
                                Text(
                                    "🤖 AI Analysis",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    InfoTile(
                                        emoji = "🦠",
                                        label = "Disease",
                                        value = result.diseaseName.ifBlank { "None detected" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoTile(
                                        emoji = "🎯",
                                        label = "Confidence",
                                        value = "${result.confidence}%",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    InfoTile(
                                        emoji = "💚",
                                        label = "Health Score",
                                        value = "${result.healthScore}/100",
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoTile(
                                        emoji = riskEmoji(result.riskLevel),
                                        label = "Risk Level",
                                        value = result.riskLevel.ifBlank { "--" },
                                        valueColor = riskColor(result.riskLevel),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (result.severity.isNotBlank()) {
                                    Spacer(Modifier.height(10.dp))
                                    InfoTile(
                                        emoji = "📊",
                                        label = "Severity",
                                        value = result.severity,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // ── 8. Recommendations
                    geminiState.result?.let { result ->
                        item {
                            SectionCard {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.WaterDrop,
                                        contentDescription = null,
                                        tint = GreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        "AI Recommendations",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Spacer(Modifier.height(14.dp))

                                RecommendationRow("🌿", "Organic Treatment", result.organicTreatment)
                                RecommendationRow("🧪", "Chemical Treatment", result.chemicalTreatment)
                                RecommendationRow("💊", "Recommended Medicine", result.recommendedMedicine)
                                RecommendationRow("📦", "Medicine Quantity", result.medicineQuantity)
                                RecommendationRow("💧", "Irrigation Advice", result.irrigationAdvice)
                                RecommendationRow("🌱", "Fertilizer Advice", result.fertilizerAdvice, isLast = true)
                            }
                        }
                    }

                    // ── 9. Today's tasks
                    geminiState.result?.let { result ->
                        if (result.todayTasks.isNotEmpty()) {
                            item {
                                SectionCard {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = GreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "Today's Tasks",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    result.todayTasks.forEachIndexed { i, task ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(GreenContainer)
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.CheckCircle,
                                                contentDescription = null,
                                                tint = GreenPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                task,
                                                fontSize = 12.sp,
                                                color = GreenOnContainer
                                            )
                                        }
                                        if (i < result.todayTasks.lastIndex) Spacer(Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }

                    // ── 10. Preventive tips
                    geminiState.result?.let { result ->
                        if (result.preventiveTips.isNotEmpty()) {
                            item {
                                SectionCard {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Shield,
                                            contentDescription = null,
                                            tint = GreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "Preventive Tips",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    result.preventiveTips.forEach { tip ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 6.dp)
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(GreenPrimary)
                                            )
                                            Text(
                                                tip,
                                                fontSize = 12.sp,
                                                color = TextPrimary,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }

                                    if (result.weatherWarning.isNotBlank()) {
                                        Spacer(Modifier.height(14.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFFFF3E0))
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                "⛈️ ${result.weatherWarning}",
                                                fontSize = 12.sp,
                                                color = Color(0xFFE65100)
                                            )
                                        }
                                    }

                                    if (result.nextInspectionDays > 0) {
                                        Spacer(Modifier.height(10.dp))
                                        Text(
                                            "🔍 Next inspection recommended in ${result.nextInspectionDays} day(s)",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {

                        geminiState.result?.let { result ->

                            Button(

                                modifier = Modifier.fillMaxWidth(),

                                enabled = !saveState.isLoading,

                                onClick = {

                                    val imageUri = selectedImageUri ?: return@Button
                                    val farm = farmState.farm ?: return@Button

                                    val update = DailyFarmUpdate(

                                        day = currentDay,

                                        date = System.currentTimeMillis(),

                                        nextInspectionDate =

                                        System.currentTimeMillis() +

                                                (result.nextInspectionDays * 24L * 60L * 60L * 1000L),

                                        plantHeight = plantHeight.toDoubleOrNull() ?: 0.0,

                                        leafColor = leafColor,

                                        soilMoisture = soilMoisture,

                                        pestFound = pestFound,

                                        diseaseFound = result.diseaseName.isNotBlank(),

                                        floweringStarted = floweringStarted,

                                        fruitStarted = fruitStarted,

                                        farmerNote = farmerNote,

                                        aiResult = AIResult(

                                            diseaseName = result.diseaseName,

                                            farmingMethod = farm.farmingMethod,

                                            confidence = result.confidence,

                                            severity = result.severity,

                                            healthScore = result.healthScore,

                                            riskLevel = result.riskLevel,

                                            organicTreatment = result.organicTreatment,

                                            chemicalTreatment = result.chemicalTreatment,

                                            recommendedMedicine = result.recommendedMedicine,

                                            medicineQuantity = result.medicineQuantity,

                                            irrigationAdvice = result.irrigationAdvice,

                                            fertilizerAdvice = result.fertilizerAdvice,

                                            todayTasks = result.todayTasks,

                                            preventiveTips = result.preventiveTips,

                                            nextInspectionDays = result.nextInspectionDays,

                                            currentPhase = result.currentPhase,

                                            progress = result.progress,

                                            postponeTasks = result.postponeTasks,

                                            extraTasks = result.extraTasks,

                                            cancelTasks = result.cancelTasks,

                                            priority = result.priority

                                        )

                                    )

                                    appViewModel.saveDailyUpdate(

                                        farmId = farmId,

                                        update = update,

                                        imageUri = imageUri

                                    )

                                }

                            ) {

                                if (saveState.isLoading) {

                                    CircularProgressIndicator(

                                        modifier = Modifier.size(20.dp),

                                        strokeWidth = 2.dp

                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text("Saving...")

                                } else {

                                    Text("Save Analysis")

                                }

                            }

                        }

                    }

                    item { Spacer(Modifier.height(10.dp)) }
                }
            }
        }
    }
}

// ── Reusable sub-components ────────────────────────────────────────────────

@Composable
private fun InfoTile(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .background(OffWhite)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 13.sp)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            maxLines = 2
        )
    }
}

@Composable
private fun RecommendationRow(
    emoji: String,
    label: String,
    value: String,
    isLast: Boolean = false
) {
    if (value.isBlank()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(emoji, fontSize = 13.sp)
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenPrimary
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontSize = 12.sp,
            color = TextPrimary,
            lineHeight = 17.sp
        )
        if (!isLast) Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun ObservationCheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    emoji: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) GreenContainer else Color.Transparent)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = GreenPrimary,
                uncheckedColor = OutlineGreen
            )
        )
        Text(emoji, fontSize = 14.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) GreenPrimary else WhitePure)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = OutlineGreen,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) WhitePure else TextPrimary
        )
    }
}

@Composable
private fun farmFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GreenPrimary,
    unfocusedBorderColor = OutlineGreen,
    focusedLabelColor = GreenPrimary,
    unfocusedLabelColor = TextSecondary,
    cursorColor = GreenPrimary,
    focusedContainerColor = WhitePure,
    unfocusedContainerColor = WhitePure
)