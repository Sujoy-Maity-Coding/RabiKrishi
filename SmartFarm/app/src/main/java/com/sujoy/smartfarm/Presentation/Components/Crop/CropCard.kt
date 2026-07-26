package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Presentation.Navigation.FarmerRoutes
import com.sujoy.smartfarm.ui.theme.GreenContainer
import com.sujoy.smartfarm.ui.theme.GreenOnContainer
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.OutlineGreen
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedLevel
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedCategory
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedCropName
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.translatedFreeText

@Composable
fun CropCard(
    crop: Crop,
    meta: CropMeta,
    rank: Int,
    district: String,    // ← new
    season: String,
    navController: NavHostController
) {
    var expanded by remember { mutableStateOf(false) }

    val cropId = CropIds.getId(crop.cropName) ?: ""

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = if (rank == 1) 1.5.dp else 1.dp,
            color = if (rank == 1) GreenPrimary else OutlineGreen
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Top row: rank badge + name + score badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rank badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (rank == 1) GreenPrimary else GreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rank == 1) Text("👑", fontSize = 17.sp)
                        else Text(
                            "#${localizedDigits("$rank")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenOnContainer
                        )
                    }
                    // Emoji + name + category
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(meta.emoji, fontSize = 18.sp)
                            Text(
                                translatedCropName(crop.cropName),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(translatedCategory(crop.category), fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Match score badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GreenContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        localizedDigits(stringResource(R.string.match_suffix, crop.recommendationScore)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenOnContainer
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Animated match score bar
            MatchScoreBar(score = crop.recommendationScore, accentColor = meta.accentColor)

            Spacer(Modifier.height(12.dp))

            // ── Quick stat pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill(
                    icon = "💧",
                    label = translatedLevel(crop.waterRequirement),
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    icon = "📅",
                    label = localizedDigits(translatedFreeText(crop.growthDuration)),
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    icon = "📈",
                    label = translatedLevel(crop.marketDemand),
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Expandable details
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = GreenContainer, thickness = 1.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.crop_details_label),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    DetailRow(
                        label = stringResource(R.string.water_requirement_label),
                        value = translatedLevel(crop.waterRequirement)
                    )

                    DetailRow(
                        label = stringResource(R.string.growth_duration_label),
                        value = localizedDigits(translatedFreeText(crop.growthDuration))
                    )

                    DetailRow(
                        label = stringResource(R.string.expected_yield_label),
                        value = localizedDigits(translatedFreeText(crop.expectedYield))
                    )

                    DetailRow(
                        label = stringResource(R.string.market_demand_label),
                        value = translatedLevel(crop.marketDemand)
                    )

                    DetailRow(
                        label = stringResource(R.string.crop_category_label),
                        value = translatedCategory(crop.category)
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }

            // ── Expand / Collapse hint
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) stringResource(R.string.collapse_label) else stringResource(R.string.view_details_label),
                    fontSize = 11.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (expanded) Icons.Outlined.KeyboardArrowUp
                    else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Select Method button
            Button(
                onClick = {
                    navController.navigate(
                        FarmerRoutes.MethodSelectionScreen(
                            cropId   = cropId,
                            cropName = crop.cropName,
                            district, season
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = WhitePure
                )
            ) {
                Text(
                    text = stringResource(R.string.select_farming_method_btn),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

object CropIds {

    private val ids = mapOf(

        "rice" to "rice",
        "paddy" to "rice",

        "jute" to "jute",

        "potato" to "potato",

        "mustard" to "mustard",

        "wheat" to "wheat",

        "maize" to "maize",

        "sunflower" to "sunflower",

        "groundnut" to "groundnut",

        "sugarcane" to "sugarcane",

        "tomato" to "tomato",

        "brinjal" to "brinjal",

        "onion" to "onion",

        "cabbage" to "cabbage",

        "cauliflower" to "cauliflower",

        "chilli" to "chilli",

        "boro" to "rice",

        "aman" to "rice",

        "aus" to "rice"
    )

    fun getId(cropName: String): String? {
        return ids.entries.firstOrNull {
            cropName.contains(it.key, ignoreCase = true)
        }?.value
    }
}