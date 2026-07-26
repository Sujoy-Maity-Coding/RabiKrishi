package com.sujoy.smartfarm.Presentation.Utils.CropRecommend

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.R
import androidx.appcompat.app.AppCompatDelegate


@Composable
fun translatedLevel(value: String): String {
    return when (value.trim().lowercase()) {
        "low" -> stringResource(R.string.level_low)
        "medium" -> stringResource(R.string.level_medium)
        "high" -> stringResource(R.string.level_high)
        else -> value // fallback: unknown value, show as-is
    }
}

@Composable
fun translatedCategory(value: String): String {
    return when (value.trim().lowercase()) {
        "cereal" -> stringResource(R.string.category_cereal)
        "vegetable" -> stringResource(R.string.category_vegetable)
        "oilseed" -> stringResource(R.string.category_oilseed)
        "cash crop" -> stringResource(R.string.category_cash_crop)
        "fiber" -> stringResource(R.string.category_fiber)
        "spice" -> stringResource(R.string.category_spice)
        else -> value
    }
}

@Composable
fun translatedCropName(value: String): String {
    return when (value.trim().lowercase()) {
        "rice" -> stringResource(R.string.crop_rice)
        "potato" -> stringResource(R.string.crop_potato)
        "jute" -> stringResource(R.string.crop_jute)
        "mustard" -> stringResource(R.string.crop_mustard)
        "wheat" -> stringResource(R.string.crop_wheat)
        "maize" -> stringResource(R.string.crop_maize)
        "sunflower" -> stringResource(R.string.crop_sunflower)
        "groundnut" -> stringResource(R.string.crop_groundnut)
        "sugarcane" -> stringResource(R.string.crop_sugarcane)
        "tomato" -> stringResource(R.string.crop_tomato)
        "brinjal" -> stringResource(R.string.crop_brinjal)
        "onion" -> stringResource(R.string.crop_onion)
        "cabbage" -> stringResource(R.string.crop_cabbage)
        "cauliflower" -> stringResource(R.string.crop_cauliflower)
        "chilli" -> stringResource(R.string.crop_chilli)
        else -> value
    }
}

/** Word-swaps "days"/"tons/acre" inside Gemini's free-text fields, keeping numbers as-is. */
@Composable
fun translatedFreeText(value: String): String {
    val daysWord = stringResource(R.string.unit_days)
    val tonsWord = stringResource(R.string.unit_tons_per_acre)
    return value
        .replace("days", daysWord, ignoreCase = true)
        .replace("tons/acre", tonsWord, ignoreCase = true)
}

@Composable
fun translatedDistrictName(name: String): String {
    return when (name.trim()) {
        "Howrah" -> stringResource(R.string.district_howrah)
        "Hooghly" -> stringResource(R.string.district_hooghly)
        "Purba Medinipur" -> stringResource(R.string.district_purba_medinipur)
        "Paschim Medinipur" -> stringResource(R.string.district_paschim_medinipur)
        "Nadia" -> stringResource(R.string.district_nadia)
        "Murshidabad" -> stringResource(R.string.district_murshidabad)
        else -> name
    }
}

@Composable
fun translatedDistrictHint(name: String): String {
    return when (name.trim()) {
        "Howrah" -> stringResource(R.string.district_howrah_hint)
        "Hooghly" -> stringResource(R.string.district_hooghly_hint)
        "Purba Medinipur" -> stringResource(R.string.district_purba_medinipur_hint)
        "Paschim Medinipur" -> stringResource(R.string.district_paschim_medinipur_hint)
        "Nadia" -> stringResource(R.string.district_nadia_hint)
        "Murshidabad" -> stringResource(R.string.district_murshidabad_hint)
        else -> ""
    }
}

@Composable
fun translatedSeasonName(name: String): String {
    return when (name.trim()) {
        "Kharif" -> stringResource(R.string.season_kharif)
        "Rabi" -> stringResource(R.string.season_rabi)
        "Summer" -> stringResource(R.string.season_summer)
        else -> name
    }
}

@Composable
fun translatedSeasonMonths(name: String): String {
    return when (name.trim()) {
        "Kharif" -> stringResource(R.string.season_kharif_months)
        "Rabi" -> stringResource(R.string.season_rabi_months)
        "Summer" -> stringResource(R.string.season_summer_months)
        else -> ""
    }
}

@Composable
fun translatedSoilName(name: String): String {
    return when (name.trim()) {
        "Clayey" -> stringResource(R.string.soil_clayey)
        "Clayey Loam" -> stringResource(R.string.soil_clayey_loam)
        "Loam" -> stringResource(R.string.soil_loam)
        "Sandy" -> stringResource(R.string.soil_sandy)
        else -> name
    }
}

@Composable
fun translatedSoilTrait(name: String): String {
    return when (name.trim()) {
        "Clayey" -> stringResource(R.string.soil_clayey_trait)
        "Clayey Loam" -> stringResource(R.string.soil_clayey_loam_trait)
        "Loam" -> stringResource(R.string.soil_loam_trait)
        "Sandy" -> stringResource(R.string.soil_sandy_trait)
        else -> ""
    }
}


fun getCurrentLanguageCode(): String {
    val locales = AppCompatDelegate.getApplicationLocales()
    return if (!locales.isEmpty) (locales[0]?.language ?: "en") else "en"
}