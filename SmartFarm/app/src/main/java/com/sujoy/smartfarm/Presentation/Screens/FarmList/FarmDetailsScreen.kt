package com.sujoy.smartfarm.Presentation.Screens.FarmList

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.ViewTimeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.AI.scheduler.SmartScheduleEngine
import com.sujoy.smartfarm.Domain.model.TaskItem
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.Components.FarmList.SectionCard
import com.sujoy.smartfarm.Presentation.Components.FarmList.phaseIcon
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.Presentation.Utils.DateUtils
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenOnContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OffWhite
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

// ── Risk helpers (shared visual language with CropUpdate / CropHistory) ─────

private fun riskColor(risk: String?): Color = when (risk?.lowercase()) {
    "healthy", "low"         -> Color(0xFF2E7D32)
    "medium", "medium risk"  -> Color(0xFFF57F17)
    "high", "high risk"      -> Color(0xFFE65100)
    "critical"                -> Color(0xFFC62828)
    else                      -> TextSecondary
}

private fun riskEmoji(risk: String?): String = when (risk?.lowercase()) {
    "healthy", "low"         -> "✅"
    "medium", "medium risk"  -> "⚠️"
    "high", "high risk"      -> "🔶"
    "critical"                -> "🚨"
    else                      -> "❔"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDetailsScreen(
    farmId: String,
    cropId: String,
    navController: NavHostController,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val state               by appViewModel.cropScheduleState.collectAsState()
    val farmState           by appViewModel.farmDetailsState.collectAsState()
    val completedTasksState by appViewModel.completedTasksState.collectAsState()
    val latestAIState by appViewModel.latestAIState.collectAsState()
    val inspectionRemainingDays = remember(latestAIState.update) {

        val latest = latestAIState.update

        if (latest == null || latest.nextInspectionDate == 0L) {

            -1

        } else {

            val diff =

                ((latest.nextInspectionDate - System.currentTimeMillis()) /
                        (24L * 60L * 60L * 1000L))
                    .toInt()

            diff

        }

    }

    LaunchedEffect(Unit) {
        appViewModel.getCropSchedule(cropId)
        appViewModel.getFarmById(farmId)
        appViewModel.getCompletedTasks(farmId)
        appViewModel.getLatestAIStatus(farmId)
    }

    val farm   = farmState.farm

    // ── Day & progress calculation
    val currentDay = farmState.farm?.let { farm ->
        ((System.currentTimeMillis() - farm.startDate) / (1000L * 60 * 60 * 24)).toInt()
    } ?: 0

    val phases    = state.schedule?.phases ?: emptyList()
    val totalDays = phases.maxOfOrNull { it.endDay }?.coerceAtLeast(1) ?: 1

    val currentPhase = phases.firstOrNull {

        currentDay in it.startDay..it.endDay

    }

    val totalScheduleTasks = phases
        .flatMap { it.tasks }
        .size

    val plannedTasks = phases
        .flatMap { it.tasks }
        .filter {

            it.day == currentDay

        }

    val smartTasks = SmartScheduleEngine.generateTodayTasks(

        plannedTasks = plannedTasks,

        aiResult = latestAIState.update?.aiResult

    )
    val aiTaskCount = smartTasks.count { it.isAI }

    val totalTasks = totalScheduleTasks + aiTaskCount

    val completedTasksCount =

        completedTasksState.completedTasks.count { completedTask ->

            phases
                .flatMap { phase -> phase.tasks }
                .any { task -> task.taskId == completedTask.taskId } ||

                    smartTasks.any { task ->

                        task.taskId == completedTask.taskId

                    }

        }

    val progressFloat =

        if(totalTasks == 0)
            0f
        else

            completedTasksCount
                .toFloat() /
                    totalTasks.toFloat()

    val progressPct =

        (progressFloat * 100)
            .toInt()

    val accent = farm?.farmingMethod?.let {
        com.sujoy.smartfarm.Presentation.Components.FarmList.methodColor(
            it
        )
    }

    var selectedTask by remember { mutableStateOf<TaskItem?>(null) }
    var showAIReport by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = "Farm details",
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            when {

                // ── Loading
                state.isLoading || farmState.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
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

                // ── Error
                state.error.isNotEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Text("Failed to load", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(state.error, fontSize = 12.sp, color = TextSecondary)
                    }
                }

                // ── Content
                state.schedule != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // ── 1. Hero banner
                        item {
                            accent?.let {
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(it)
                                    .padding(18.dp)
                            }?.let {
                                Box(
                                    modifier = it
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
                                                    "${com.sujoy.smartfarm.Presentation.Components.FarmList.methodEmoji(
                                                        farm?.farmingMethod!!
                                                    )} ${farm?.farmingMethod ?: ""}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = WhitePure
                                                )
                                            }
                                            // Active dot
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
                                                Text("Day ${currentDay+1}", fontSize = 10.sp, color = WhitePure.copy(alpha = 0.8f))
                                            }
                                        }
                                        Text(
                                            farm?.cropName ?: "",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WhitePure
                                        )
                                        Text(
                                            "${farm?.farmName ?: ""}  •  ${farm?.landArea ?: ""}",
                                            fontSize = 12.sp,
                                            color = WhitePure.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }
                        }

                        // ── 2. Progress card
                        item {
                            SectionCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Crop progress",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(

                                            "${DateUtils.calculateTaskDateString(

                                                farmStartDate = farm!!.startDate,

                                                taskDay = currentDay,

                                                completedTasks = completedTasksState.completedTasks

                                            )}",

                                            fontSize = 11.sp,

                                            color = TextSecondary,
                                            modifier = Modifier.padding(top = 1.dp)
                                        )
                                        Text(

                                            "$completedTasksCount / $totalTasks Tasks Completed",

                                            fontSize = 11.sp,

                                            color = TextSecondary
                                        )
                                    }
                                    // Big percentage badge
                                    if (accent != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(accent.copy(alpha = 0.12f))
                                                .padding(horizontal = 14.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "$progressPct%",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accent
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(14.dp))

                                // Animated progress bar
                                val animatedProgress by animateFloatAsState(
                                    targetValue = progressFloat,
                                    animationSpec = tween(1000, easing = EaseOut),
                                    label = "progress"
                                )
                                if (accent != null) {
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp)),
                                        color = accent,
                                        trackColor = accent.copy(alpha = 0.12f),
                                        strokeCap = StrokeCap.Round
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                // Phase milestones row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Start", fontSize = 10.sp, color = TextSecondary)
                                    if (accent != null) {
                                        Text(
                                            currentPhase?.title ?: "Completed",
                                            fontSize = 10.sp,
                                            color = accent,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text("Day ${totalDays+1}", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        // ── 3. Current phase card
                        item {
                            SectionCard {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    if (accent != null) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(accent.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                phaseIcon(currentPhase?.title ?: ""),
                                                contentDescription = null,
                                                tint = accent,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            "Current phase",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            currentPhase?.title ?: "Harvest complete",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Text(

                                            "${DateUtils.calculateTaskDateString(

                                                farmStartDate = farm!!.startDate,

                                                taskDay = currentPhase?.startDay ?: 1,

                                                completedTasks = completedTasksState.completedTasks

                                            )}  -  ${
                                                DateUtils.calculateTaskDateString(

                                                    farmStartDate = farm.startDate,

                                                    taskDay = currentPhase?.endDay ?: totalDays,

                                                    completedTasks = completedTasksState.completedTasks

                                                )
                                            }",

                                            fontSize = 11.sp,

                                            color = TextSecondary,

                                            modifier = Modifier.padding(top = 1.dp)

                                        )
                                    }
                                }
                            }
                        }

                        // ── 4. Today's tasks card
                        item {
                            SectionCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (accent != null) {
                                            Icon(
                                                Icons.Outlined.Today,
                                                contentDescription = null,
                                                tint = accent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Text(
                                            "Today's tasks",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                    if (accent != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(accent.copy(alpha = 0.12f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                "${smartTasks.size} tasks",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = accent
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                if (smartTasks.isEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(OffWhite)
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("✅", fontSize = 20.sp)
                                        Text(
                                            "No specific tasks for today. Continue with current phase activities.",
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            lineHeight = 17.sp
                                        )
                                    }
                                } else {
                                    smartTasks.forEachIndexed { i, task ->
                                        val isDone =

                                            completedTasksState.completedTasks.any {

                                                it.taskId == task.taskId

                                            }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isDone) GreenContainer
                                                    else OffWhite
                                                )
                                                .clickable {

                                                    if (!task.isAI) {

                                                        plannedTasks

                                                            .firstOrNull {

                                                                it.taskId == task.taskId

                                                            }?.let {

                                                                selectedTask = it

                                                            }

                                                    }

                                                }
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            accent?.let {
                                                CheckboxDefaults.colors(
                                                    checkedColor   = it,
                                                    uncheckedColor = OutlineGreen
                                                )
                                            }?.let {
                                                Checkbox(
                                                    checked = if (task.canComplete) isDone else false,
                                                    onCheckedChange =
                                                    if (task.canComplete) { checked ->
                                                        Log.d(
                                                            "AI_TASK",
                                                            "Clicked: ${task.taskId}, checked=$checked"
                                                        )

                                                        val plannedTask =

                                                            plannedTasks.firstOrNull {

                                                                it.taskId == task.taskId

                                                            }

                                                        if (plannedTask != null) {

                                                            appViewModel.updateTaskStatus(

                                                                farmId = farmId,

                                                                task = plannedTask,

                                                                completed = checked

                                                            )

                                                        }

                                                    } else null,
                                                    colors = it
                                                )
                                            }
                                            Text(
                                                text =
                                                if (task.isAI)
                                                    "🤖 ${task.title}"
                                                else
                                                    task.title,
                                                fontSize = 13.sp,
                                                color = if (isDone) TextSecondary else TextPrimary,
                                                textDecoration = if (isDone) TextDecoration.LineThrough
                                                else TextDecoration.None,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                Icons.Outlined.ChevronRight,
                                                contentDescription = null,
                                                tint = OutlineGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        if (i < smartTasks.lastIndex) Spacer(Modifier.height(6.dp))
                                    }
                                }
                            }
                        }

                        item {

                            if (latestAIState.update != null) {

                                val remaining = inspectionRemainingDays

                                val isDue = remaining <= 0

                                val bgColor =
                                    if (isDue)
                                        Color(0xFFFFF3F3)
                                    else
                                        Color(0xFFF4FFF4)

                                val borderColor =
                                    if (isDue)
                                        Color(0xFFE53935)
                                    else
                                        Color(0xFF43A047)

                                val icon =
                                    if (isDue) "🚨" else "🔔"

                                val title =
                                    if (isDue)
                                        "Inspection Due"
                                    else
                                        "Next AI Inspection"

                                val message =
                                    if (isDue)
                                        "Upload a new crop image today for the next AI analysis."
                                    else
                                        "Next inspection in $remaining day${if (remaining == 1) "" else "s"}."

                                Card(

                                    modifier = Modifier.fillMaxWidth(),

                                    shape = RoundedCornerShape(16.dp),

                                    border = BorderStroke(1.dp, borderColor),

                                    colors = CardDefaults.cardColors(
                                        containerColor = bgColor
                                    )

                                ) {

                                    Row(

                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),

                                        verticalAlignment = Alignment.CenterVertically

                                    ) {

                                        Text(

                                            text = icon,

                                            fontSize = 28.sp

                                        )

                                        Spacer(Modifier.width(12.dp))

                                        Column {

                                            Text(

                                                text = title,

                                                fontWeight = FontWeight.Bold,

                                                fontSize = 16.sp

                                            )

                                            Spacer(Modifier.height(4.dp))

                                            Text(

                                                text = message,

                                                fontSize = 13.sp,

                                                color = Color.Gray

                                            )

                                        }

                                    }

                                }

                            }

                        }

                        // ── 4b. AI Recommendation card — restyled to match theme
                        latestAIState.update?.let { update ->
                            val ai = update.aiResult
                            val riskAccent = riskColor(ai.riskLevel)

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(WhitePure)
                                        .border(1.5.dp, riskAccent.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                                        .clickable { showAIReport = true }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // AI icon box
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(riskAccent.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Outlined.SmartToy,
                                            contentDescription = null,
                                            tint = riskAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "AI Health Report",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            ai.diseaseName.ifBlank { "No disease detected" },
                                            fontSize = 12.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(GreenContainer)
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    "💚 ${ai.healthScore}/100",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GreenPrimary
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(riskAccent.copy(alpha = 0.12f))
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    "${riskEmoji(ai.riskLevel)} ${ai.riskLevel.ifBlank { "--" }}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = riskAccent
                                                )
                                            }
                                        }
                                    }

                                    Icon(
                                        Icons.Outlined.ChevronRight,
                                        contentDescription = null,
                                        tint = riskAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { navController.navigate(FarmerRoutes.CropUpdateScreen(farmId = farmId, currentDay = currentDay)) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accent ?: GreenPrimary, contentColor = WhitePure)
                            ) {
                                Icon(Icons.Outlined.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Update crop status", fontWeight = FontWeight.Bold)
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = { navController.navigate(FarmerRoutes.CropHealthHistoryScreen(farmId = farmId)) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, accent ?: GreenPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = accent ?: GreenPrimary)
                            ) {
                                Icon(Icons.Outlined.ViewTimeline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Crop health history", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = { navController.navigate(FarmerRoutes.FarmExpenseScreen(farmId)) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, accent ?: GreenPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = accent ?: GreenPrimary)
                            ) {
                                Icon(Icons.Outlined.CurrencyRupee, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Farm expenses", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // ── 5. All phases section
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                if (accent != null) {
                                    Icon(
                                        Icons.Outlined.ViewTimeline,
                                        contentDescription = null,
                                        tint = accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    "Full schedule",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        items(phases) { phase ->
                            var expanded    by remember { mutableStateOf(phase == currentPhase) }
                            var dialogTask  by remember { mutableStateOf<TaskItem?>(null) }

                            val isCurrentPhase = phase == currentPhase
                            val phaseProgress  = when {
                                currentDay > phase.endDay   -> 1f
                                currentDay < phase.startDay -> 0f
                                else -> (currentDay - phase.startDay).toFloat() /
                                        (phase.endDay - phase.startDay).coerceAtLeast(1).toFloat()
                            }
                            val phaseDone = currentDay > phase.endDay
                            val phaseCardAccent = if (isCurrentPhase) accent else
                                if (phaseDone) Color(0xFF43A047) else OutlineGreen

                            // Phase row with timeline dot
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Timeline dot + line
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(top = 12.dp)
                                ) {
                                    when {
                                        isCurrentPhase -> accent
                                        phaseDone      -> Color(0xFF43A047)
                                        else           -> GreenContainer
                                    }?.let {
                                        Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                it
                                            )
                                    }?.let {
                                        Box(
                                            modifier = it,
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (phaseDone && !isCurrentPhase) {
                                                Icon(
                                                    Icons.Outlined.Check,
                                                    contentDescription = null,
                                                    tint = WhitePure,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            } else {
                                                Text(
                                                    "${phases.indexOf(phase) + 1}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCurrentPhase || phaseDone) WhitePure
                                                    else GreenOnContainer
                                                )
                                            }
                                        }
                                    }
                                    if (phases.last() != phase) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(if (expanded) 20.dp else 14.dp)
                                                .background(
                                                    if (phaseDone) Color(0xFF43A047).copy(alpha = 0.4f)
                                                    else OutlineGreen.copy(alpha = 0.5f)
                                                )
                                        )
                                    }
                                }

                                // Phase card
                                if (accent != null) {
                                    Card(
                                        onClick = { expanded = !expanded },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(bottom = 4.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCurrentPhase)
                                                accent.copy(alpha = 0.07f) else WhitePure
                                        ),
                                        elevation = CardDefaults.cardElevation(0.dp),
                                        border = phaseCardAccent?.let {
                                            BorderStroke(
                                                width = if (isCurrentPhase) 1.5.dp else 1.dp,
                                                color = it
                                            )
                                        }
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {

                                            // Phase header row
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clip(RoundedCornerShape(9.dp))
                                                            .background(
                                                                if (isCurrentPhase) accent.copy(alpha = 0.15f)
                                                                else if (phaseDone) Color(0xFF43A047).copy(alpha = 0.1f)
                                                                else OffWhite
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            phaseIcon(phase.title),
                                                            contentDescription = null,
                                                            tint = if (isCurrentPhase) accent
                                                            else if (phaseDone) Color(0xFF43A047)
                                                            else TextSecondary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                    Column {
                                                        Text(
                                                            phase.title,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isCurrentPhase) accent else TextPrimary
                                                        )
                                                        Text(
                                                            "Day ${DateUtils.calculateTaskDateString(

                                                                farmStartDate = farm!!.startDate,

                                                                taskDay = phase.startDay,

                                                                completedTasks = completedTasksState.completedTasks

                                                            )} - ${
                                                                DateUtils.calculateTaskDateString(

                                                                    farmStartDate = farm.startDate,

                                                                    taskDay = phase.endDay,

                                                                    completedTasks = completedTasksState.completedTasks

                                                                )
                                                            }",
                                                            fontSize = 10.sp,
                                                            color = TextSecondary,
                                                            modifier = Modifier.padding(top = 1.dp)
                                                        )
                                                    }
                                                }
                                                // Status badge / chevron
                                                when {
                                                    isCurrentPhase -> Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(accent)
                                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                                    ) {
                                                        Text("Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WhitePure)
                                                    }

                                                    phaseDone -> Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFF43A047).copy(alpha = 0.12f))
                                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                                    ) {
                                                        Text("Done ✓", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                    }

                                                    else -> Icon(
                                                        if (expanded) Icons.Outlined.KeyboardArrowUp
                                                        else Icons.Outlined.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = OutlineGreen,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }

                                            // Phase progress mini bar (only for current phase)
                                            if (isCurrentPhase && phaseProgress > 0f) {
                                                Spacer(Modifier.height(10.dp))
                                                val animPP by animateFloatAsState(
                                                    targetValue = phaseProgress,
                                                    animationSpec = tween(800, easing = EaseOut),
                                                    label = "phaseProgress"
                                                )
                                                LinearProgressIndicator(
                                                    progress = { animPP },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(5.dp)
                                                        .clip(RoundedCornerShape(3.dp)),
                                                    color = accent,
                                                    trackColor = accent.copy(alpha = 0.15f),
                                                    strokeCap = StrokeCap.Round
                                                )
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    "Phase ${(phaseProgress * 100).toInt()}% done",
                                                    fontSize = 9.sp,
                                                    color = accent,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            // Expanded tasks
                                            androidx.compose.animation.AnimatedVisibility(visible = expanded) {
                                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                                    HorizontalDivider(color = OutlineGreen.copy(alpha = 0.4f), thickness = 1.dp)
                                                    Spacer(Modifier.height(10.dp))

                                                    phase.tasks.forEachIndexed { i, task ->
                                                        val isDone =

                                                            completedTasksState.completedTasks.any {

                                                                it.taskId == task.taskId

                                                            }
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clip(RoundedCornerShape(10.dp))
                                                                .background(if (isDone) GreenContainer else OffWhite)
                                                                .clickable { dialogTask = task }
                                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Checkbox(
                                                                checked = isDone,
                                                                onCheckedChange = { checked ->
                                                                    appViewModel.updateTaskStatus(

                                                                        farmId = farmId,

                                                                        task = task,

                                                                        completed = checked

                                                                    )
                                                                },
                                                                colors = CheckboxDefaults.colors(
                                                                    checkedColor   = accent,
                                                                    uncheckedColor = OutlineGreen
                                                                ),
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                            Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                    text = task.title,
                                                                    fontSize = 12.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    color = if (isDone) TextSecondary else TextPrimary,
                                                                    textDecoration = if (isDone) TextDecoration.LineThrough
                                                                    else TextDecoration.None
                                                                )
                                                                Text(
                                                                    "Day ${
                                                                        DateUtils.calculateTaskDateString(

                                                                            farmStartDate = farm!!.startDate,

                                                                            taskDay = task.day,

                                                                            completedTasks = completedTasksState.completedTasks

                                                                        )
                                                                    }",
                                                                    fontSize = 10.sp,
                                                                    color = TextSecondary,
                                                                    modifier = Modifier.padding(top = 1.dp)
                                                                )
                                                            }
                                                            Icon(
                                                                Icons.Outlined.Info,
                                                                contentDescription = null,
                                                                tint = OutlineGreen,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                        if (i < phase.tasks.lastIndex) Spacer(Modifier.height(5.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Task detail dialog
                            dialogTask?.let { task ->
                                AlertDialog(
                                    onDismissRequest = { dialogTask = null },
                                    shape = RoundedCornerShape(20.dp),
                                    containerColor = WhitePure,
                                    title = {
                                        Text(
                                            task.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    },
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(GreenContainer)
                                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    "📅 Day ${
                                                        DateUtils.calculateTaskDateString(

                                                            farmStartDate = farm!!.startDate,

                                                            taskDay = task.day,

                                                            completedTasks = completedTasksState.completedTasks

                                                        )
                                                    }",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = GreenOnContainer
                                                )
                                            }
                                            HorizontalDivider(color = OutlineGreen.copy(alpha = 0.5f))
                                            Text(
                                                task.description,
                                                fontSize = 13.sp,
                                                color = TextPrimary,
                                                lineHeight = 19.sp
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        accent?.let {
                                            ButtonDefaults.buttonColors(
                                                containerColor = it,
                                                contentColor   = WhitePure
                                            )
                                        }?.let {
                                            Button(
                                                onClick = { dialogTask = null },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = it
                                            ) { Text("Got it", fontWeight = FontWeight.SemiBold) }
                                        }
                                    }
                                )
                            }
                        }

                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
            }
        }
    }
    // Add this import at top if missing:
// import androidx.compose.material3.ModalBottomSheet
// import androidx.compose.material3.rememberModalBottomSheetState

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showAIReport) {
        latestAIState.update?.let { update ->
            val ai = update.aiResult
            val riskAccent = riskColor(ai.riskLevel)

            ModalBottomSheet(
                onDismissRequest = { showAIReport = false },
                sheetState = sheetState,
                containerColor = WhitePure,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // Sheet header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(riskAccent.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.SmartToy, contentDescription = null, tint = riskAccent, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("AI Health Report", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Day $currentDay", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(riskAccent.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "${riskEmoji(ai.riskLevel)} ${ai.riskLevel.ifBlank { "--" }}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = riskAccent
                                )
                            }
                        }
                    }

                    // Disease + health score bar
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(OffWhite)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                ai.diseaseName.ifBlank { "🌾 No disease detected" },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (ai.currentPhase.isNotBlank()) {
                                Text("📍 ${ai.currentPhase}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Health score", fontSize = 11.sp, color = TextSecondary)
                                Text("${ai.healthScore}/100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = riskAccent)
                            }
                            val animHealth by animateFloatAsState(
                                targetValue = (ai.healthScore / 100f).coerceIn(0f, 1f),
                                animationSpec = tween(800, easing = EaseOut), label = "health"
                            )
                            LinearProgressIndicator(
                                progress = { animHealth },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = riskAccent,
                                trackColor = riskAccent.copy(alpha = 0.12f),
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }

                    // Recommendations
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(WhitePure)
                                .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("💊 Recommendations", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                            HorizontalDivider(color = GreenContainer)
                            AiRecommendationRow("💊", "Medicine", ai.recommendedMedicine)
                            AiRecommendationRow("📦", "Quantity", ai.medicineQuantity)
                            AiRecommendationRow("💧", "Irrigation", ai.irrigationAdvice)
                            AiRecommendationRow("🌱", "Fertilizer", ai.fertilizerAdvice, isLast = true)
                        }
                    }

                    // Today's AI tasks
                    if (ai.todayTasks.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(WhitePure)
                                    .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("✅ Today's AI tasks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                                HorizontalDivider(color = GreenContainer)
                                ai.todayTasks.forEach { task ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(GreenContainer)
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("✔️", fontSize = 12.sp)
                                        Text(task, fontSize = 12.sp, color = GreenOnContainer, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    // Preventive tips
                    if (ai.preventiveTips.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(WhitePure)
                                    .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🛡 Preventive tips", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                                HorizontalDivider(color = GreenContainer)
                                ai.preventiveTips.forEach { tip ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
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
                                        Text(tip, fontSize = 12.sp, color = TextPrimary, lineHeight = 17.sp, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiRecommendationRow(
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
            Text(emoji, fontSize = 12.sp)
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenPrimary
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            fontSize = 12.sp,
            color = TextPrimary,
            lineHeight = 17.sp
        )
        if (!isLast) Spacer(Modifier.height(12.dp))
    }
}