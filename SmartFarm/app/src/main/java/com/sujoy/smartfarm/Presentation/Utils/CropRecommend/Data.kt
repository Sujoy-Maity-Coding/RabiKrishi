package com.sujoy.smartfarm.Presentation.Utils.CropRecommend

import androidx.compose.ui.graphics.Color

// ── Data models for rich dropdown items ───────────────────────────────────────

data class DistrictItem(val name: String, val emoji: String, val hint: String)
data class SeasonItem(val name: String, val emoji: String, val months: String, val color: Color)
data class SoilItem(val name: String, val emoji: String, val trait: String)
data class MonthItem(val number: Int, val name: String, val shortName: String)

// ── Datasets ─────────────────────────────────────────────────────────────────

val districtItems = listOf(
    DistrictItem("Howrah",           "🏙️", "Urban-fringe farmland"),
    DistrictItem("Hooghly",          "🌊", "Riverside alluvial plains"),
    DistrictItem("Purba Medinipur",  "🌴", "Coastal & delta region"),
    DistrictItem("Paschim Medinipur","🌲", "Laterite upland terrain"),
    DistrictItem("Nadia",            "🌾", "Rich crop heartland"),
    DistrictItem("Murshidabad",      "🏺", "Historic silk & paddy belt"),
)

val seasonItems = listOf(
    SeasonItem("Kharif",  "☀️", "Jun – Nov",  Color(0xFFFF8F00)),
    SeasonItem("Rabi",    "❄️", "Nov – Apr",  Color(0xFF1976D2)),
    SeasonItem("Summer",  "🌤️", "Mar – Jun",  Color(0xFF388E3C)),
)

val soilItems = listOf(
    SoilItem("Clayey",       "🪨", "High water retention"),
    SoilItem("Clayey Loam",  "🌰", "Balanced drainage"),
    SoilItem("Loam",         "🟫", "Ideal fertility"),
    SoilItem("Sandy",        "🏖️", "Fast draining"),
)

val monthNames = listOf(
    "January","February","March","April","May","June",
    "July","August","September","October","November","December"
)
val monthShort = listOf(
    "Jan","Feb","Mar","Apr","May","Jun",
    "Jul","Aug","Sep","Oct","Nov","Dec"
)