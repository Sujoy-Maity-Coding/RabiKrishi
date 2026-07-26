package com.sujoy.smartfarm.Presentation.Screens.ExpenseTracker

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Presentation.Components.Expense.ExpenseCard
import com.sujoy.smartfarm.Presentation.Components.Dashboard.FarmTopBar
import com.sujoy.smartfarm.Presentation.ViewModel.AppViewModel
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.*

// ── Category metadata ─────────────────────────────────────────────────────────

private data class CategoryMeta(val emoji: String, val color: Color)

private val categoryMeta = mapOf(
    "Seed"       to CategoryMeta("🌱", Color(0xFF2E7D32)),
    "Fertilizer" to CategoryMeta("🧪", Color(0xFF1565C0)),
    "Pesticide"  to CategoryMeta("🛡️", Color(0xFFAD1457)),
    "Labour"     to CategoryMeta("👷", Color(0xFFF57F17)),
    "Irrigation" to CategoryMeta("💧", Color(0xFF0277BD)),
    "Transport"  to CategoryMeta("🚛", Color(0xFF37474F)),
    "Equipment"  to CategoryMeta("⚙️", Color(0xFF4E342E)),
    "Others"     to CategoryMeta("📦", Color(0xFF546E7A)),
)

private fun categoryEmoji(cat: String) = categoryMeta[cat]?.emoji ?: "📦"
private fun categoryColor(cat: String) = categoryMeta[cat]?.color ?: TextSecondary

// Stored category values (e.g. "Seed", "Fertilizer") stay in English for data
// consistency in Firestore — this only translates what's shown on screen.
@Composable
private fun translatedCategory(category: String): String = when (category) {
    "Seed"       -> stringResource(R.string.category_seed)
    "Fertilizer" -> stringResource(R.string.category_fertilizer)
    "Pesticide"  -> stringResource(R.string.category_pesticide)
    "Labour"     -> stringResource(R.string.category_labour)
    "Irrigation" -> stringResource(R.string.category_irrigation)
    "Transport"  -> stringResource(R.string.category_transport)
    "Equipment"  -> stringResource(R.string.category_equipment)
    "Others"     -> stringResource(R.string.category_others)
    else         -> category
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmExpenseScreen(

    farmId: String,

    navController: NavHostController,
    viewModel: AppViewModel = hiltViewModel()

) {
    val context = LocalContext.current
    val invalidAmountMessage = stringResource(R.string.invalid_amount_toast)

    val expenseState    by viewModel.expenseListState.collectAsState()
    val farmState       by viewModel.farmDetailsState.collectAsState()
    val addExpenseState by viewModel.addExpenseState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getFarmById(farmId)
        viewModel.getExpenses(farmId)
    }

    val totalExpense = expenseState.expenses.sumOf { it.amount }

    val estimatedCost = farmState
        .farm
        ?.aiEstimatedCost
        ?.replace("₹", "")
        ?.replace(",", "")
        ?.toDoubleOrNull()
        ?: 0.0

    val remainingBudget = estimatedCost - totalExpense

    val budgetStatus = when {
        remainingBudget > 0    -> stringResource(R.string.budget_within_status)
        remainingBudget == 0.0 -> stringResource(R.string.budget_fully_used_status)
        else                   -> stringResource(R.string.budget_exceeded_status)
    }

    val statusColor = when {
        remainingBudget > 0    -> Color(0xFF2E7D32)
        remainingBudget == 0.0 -> Color(0xFFF9A825)
        else                   -> Color(0xFFC62828)
    }

    var showBottomSheet    by remember { mutableStateOf(false) }
    var selectedCategory   by remember { mutableStateOf("Seed") }
    var amount             by remember { mutableStateOf("") }
    var note               by remember { mutableStateOf("") }
    var expanded           by remember { mutableStateOf(false) }

    LaunchedEffect(addExpenseState.success) {
        if (addExpenseState.success.isNotEmpty()) {
            showBottomSheet  = false
            amount           = ""
            note             = ""
            selectedCategory = "Seed"
        }
    }

    // Budget progress
    val budgetProgress = if (estimatedCost > 0)
        (totalExpense / estimatedCost).toFloat().coerceIn(0f, 1f)
    else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = budgetProgress,
        animationSpec = tween(900, easing = EaseOut),
        label = "budgetProgress"
    )

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            FarmTopBar(
                title = stringResource(R.string.expense_tracker_title),
                showBack = true,
                onBack = { navController.popBackStack() }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── 1. Budget summary hero card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(GreenPrimary, Color(0xFF388E3C))
                            )
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(WhitePure.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) { Text("💰", fontSize = 20.sp) }
                        Column {
                            Text(
                                stringResource(R.string.budget_summary_label),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhitePure
                            )
                            Text(
                                farmState.farm?.farmName ?: "",
                                fontSize = 11.sp,
                                color = WhitePure.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // 3 stat tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BudgetTile(
                            label = stringResource(R.string.ai_estimated_label),
                            value = localizedDigits("₹%.0f".format(estimatedCost)),
                            modifier = Modifier.weight(1f)
                        )
                        BudgetTile(
                            label = stringResource(R.string.spent_so_far_label),
                            value = localizedDigits("₹%.0f".format(totalExpense)),
                            modifier = Modifier.weight(1f)
                        )
                        BudgetTile(
                            label = stringResource(R.string.remaining_label),
                            value = localizedDigits("₹%.0f".format(remainingBudget)),
                            valueColor = if (remainingBudget >= 0) Color(0xFF69F0AE) else Color(0xFFFF5252),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Progress bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(R.string.budget_used_label),
                                fontSize = 11.sp,
                                color = WhitePure.copy(alpha = 0.75f)
                            )
                            Text(
                                localizedDigits("${(budgetProgress * 100).toInt()}%"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhitePure
                            )
                        }
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (budgetProgress >= 1f) Color(0xFFFF5252) else Color(0xFF69F0AE),
                            trackColor = WhitePure.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round
                        )
                    }

                    // Status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WhitePure.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            budgetStatus,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhitePure
                        )
                    }
                }
            }

            // ── 2. Category quick summary chips
            item {
                val byCategory = expenseState.expenses
                    .groupBy { it.category }
                    .mapValues { (_, list) -> list.sumOf { it.amount } }

                if (byCategory.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(WhitePure)
                            .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Outlined.PieChart,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                stringResource(R.string.spending_by_category_label),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        HorizontalDivider(color = GreenContainer)
                        byCategory.entries.forEach { (cat, spent) ->
                            val pct = if (totalExpense > 0) (spent / totalExpense).toFloat() else 0f
                            val catColor = categoryColor(cat)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(catColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) { Text(categoryEmoji(cat), fontSize = 14.sp) }
                                Text(translatedCategory(cat), fontSize = 12.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                // Mini bar
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(catColor.copy(alpha = 0.15f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(pct)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(catColor)
                                    )
                                }
                                Text(
                                    localizedDigits("₹%.0f".format(spent)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor
                                )
                            }
                        }
                    }
                }
            }

            // ── 3. Add expense button
            item {
                Button(

                    onClick = { showBottomSheet = true },

                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor   = WhitePure
                    )

                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_expense_btn), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // ── 4. Section label
            item {
                if (expenseState.expenses.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.all_expenses_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenContainer)
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                localizedDigits(stringResource(R.string.items_count_label, expenseState.expenses.size)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenOnContainer
                            )
                        }
                    }
                } else {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(WhitePure)
                            .border(1.dp, OutlineGreen, RoundedCornerShape(18.dp))
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("📊", fontSize = 36.sp)
                        Text(
                            stringResource(R.string.no_expenses_yet_title),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            stringResource(R.string.no_expenses_yet_subtitle),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // ── 5. Expense list
            items(
                expenseState.expenses,
                key = { it.expenseId }
            ) { expense ->
                StyledExpenseCard(expense = expense)
            }
        }
    }

    // ── Bottom sheet — Add Expense
    if (showBottomSheet) {

        ModalBottomSheet(

            onDismissRequest = { showBottomSheet = false },
            containerColor = WhitePure,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Sheet header
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("➕", fontSize = 16.sp) }
                        Text(
                            stringResource(R.string.add_expense_btn),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    // Selected category emoji preview
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(categoryColor(selectedCategory).copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "${categoryEmoji(selectedCategory)} ${translatedCategory(selectedCategory)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = categoryColor(selectedCategory)
                        )
                    }
                }

                HorizontalDivider(color = GreenContainer)

                // Category dropdown
                ExposedDropdownMenuBox(

                    expanded = expanded,

                    onExpandedChange = { expanded = !expanded }

                ) {
                    OutlinedTextField(

                        value = translatedCategory(selectedCategory),

                        onValueChange = {},

                        readOnly = true,

                        label = { Text(stringResource(R.string.category_label)) },

                        leadingIcon = {
                            Text(categoryEmoji(selectedCategory), fontSize = 18.sp,
                                modifier = Modifier.padding(start = 4.dp))
                        },

                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },

                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = sheetFieldColors()

                    )

                    ExposedDropdownMenu(

                        expanded = expanded,

                        onDismissRequest = { expanded = false },
                        containerColor = WhitePure

                    ) {
                        listOf(
                            "Seed", "Fertilizer", "Pesticide", "Labour",
                            "Irrigation", "Transport", "Equipment", "Others"
                        ).forEach { category ->
                            DropdownMenuItem(

                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(categoryEmoji(category), fontSize = 16.sp)
                                        Text(translatedCategory(category), fontSize = 13.sp, color = TextPrimary)
                                    }
                                },

                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                },
                                modifier = Modifier.background(
                                    if (selectedCategory == category) GreenContainer
                                    else Color.Transparent
                                )
                            )
                        }
                    }
                }

                // Amount field
                OutlinedTextField(

                    value = amount,

                    onValueChange = { amount = it },

                    modifier = Modifier.fillMaxWidth(),

                    label = { Text(stringResource(R.string.amount_label)) },

                    placeholder = { Text(stringResource(R.string.amount_placeholder), color = TextSecondary.copy(alpha = 0.5f)) },

                    leadingIcon = {
                        Icon(Icons.Outlined.CurrencyRupee, contentDescription = null,
                            tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    },

                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = sheetFieldColors()

                )

                // Note field
                OutlinedTextField(

                    value = note,

                    onValueChange = { note = it },

                    modifier = Modifier.fillMaxWidth(),

                    label = { Text(stringResource(R.string.note_label)) },

                    placeholder = {
                        Text(stringResource(R.string.note_placeholder), color = TextSecondary.copy(alpha = 0.5f))
                    },

                    leadingIcon = {
                        Icon(Icons.Outlined.Notes, contentDescription = null,
                            tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    },

                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = sheetFieldColors()

                )

                // Save button
                Button(

                    onClick = {

                        showBottomSheet = false

                        if (amount.toDoubleOrNull() == null) {
                            Toast.makeText(context, invalidAmountMessage, Toast.LENGTH_SHORT).show()
                        }

                        val expense = Expense(
                            category = selectedCategory,
                            amount   = amount.toDoubleOrNull() ?: 0.0,
                            note     = note,
                            date     = System.currentTimeMillis()
                        )

                        viewModel.addExpense(farmId = farmId, expense = expense)

                    },

                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor   = WhitePure
                    )

                ) {
                    Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.save_expense_btn), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ── Styled expense card (replaces ExpenseCard component) ──────────────────────

@Composable
private fun StyledExpenseCard(expense: Expense) {
    val catColor = categoryColor(expense.category)
    val catEmoji = categoryEmoji(expense.category)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WhitePure)
            .border(1.dp, OutlineGreen, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(catColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) { Text(catEmoji, fontSize = 22.sp) }

        // Info
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                translatedCategory(expense.category),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (expense.note.isNotEmpty()) {
                Text(
                    expense.note,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            Text(
                localizedDigits(
                    java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                        .format(java.util.Date(expense.date))
                ),
                fontSize = 10.sp,
                color = TextSecondary
            )
        }

        // Amount badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(catColor.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Text(
                localizedDigits("₹%.0f".format(expense.amount)),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = catColor
            )
        }
    }
}

// ── Budget tile (inside hero card) ───────────────────────────────────────────

@Composable
private fun BudgetTile(
    label: String,
    value: String,
    valueColor: Color = WhitePure,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(WhitePure.copy(alpha = 0.12f))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 9.sp, color = WhitePure.copy(alpha = 0.75f), fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

// ── Field colors helper ───────────────────────────────────────────────────────

@Composable
private fun sheetFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor     = GreenPrimary,
    unfocusedBorderColor   = OutlineGreen,
    focusedLabelColor      = GreenPrimary,
    unfocusedLabelColor    = TextSecondary,
    cursorColor            = GreenPrimary,
    focusedContainerColor  = WhitePure,
    unfocusedContainerColor = WhitePure
)