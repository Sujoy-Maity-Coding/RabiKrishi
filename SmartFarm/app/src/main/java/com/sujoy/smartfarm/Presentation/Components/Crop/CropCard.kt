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

@Composable
fun CropCard(
    crop: Crop,
    meta: CropMeta,
    rank: Int,
    navController: NavHostController
) {
    var expanded by remember { mutableStateOf(false) }

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
                            "#$rank",
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
                                crop.cropName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(meta.category, fontSize = 11.sp, color = TextSecondary)
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
                        "${crop.recommendationScore}% match",
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
                StatPill(icon = "💧", label = meta.waterNeed,    modifier = Modifier.weight(1f))
                StatPill(icon = "📅", label = meta.growthDays,   modifier = Modifier.weight(1f))
                StatPill(icon = "📈", label = meta.marketDemand, modifier = Modifier.weight(1f))
            }

            // ── Expandable details
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = GreenContainer, thickness = 1.dp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Crop details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    DetailRow(label = "Water requirement", value = meta.waterNeed)
                    DetailRow(label = "Growth duration",   value = meta.growthDays)
                    DetailRow(label = "Market demand",     value = meta.marketDemand)
                    DetailRow(label = "Crop category",     value = meta.category)
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
                    text = if (expanded) "Collapse" else "View details",
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
                            cropId   = crop.cropId,
                            cropName = crop.cropName
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
                    text = "Select farming method →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

