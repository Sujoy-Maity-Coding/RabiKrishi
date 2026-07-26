package com.sujoy.smartfarm.Presentation.Utils.FarmMethod

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.Presentation.Components.CropMethod.FarmMethod
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.R

@Composable
fun translatedMethodLabel(method: FarmMethod): String = when (method) {
    FarmMethod.ORGANIC -> stringResource(R.string.method_organic)
    FarmMethod.INORGANIC -> stringResource(R.string.method_inorganic)
    FarmMethod.MIXED -> stringResource(R.string.method_mixed)
}

@Composable
fun translatedMethodTagline(method: FarmMethod): String = when (method) {
    FarmMethod.ORGANIC -> stringResource(R.string.method_organic_tagline)
    FarmMethod.INORGANIC -> stringResource(R.string.method_inorganic_tagline)
    FarmMethod.MIXED -> stringResource(R.string.method_mixed_tagline)
}

@Composable
fun translatedMethodLabelFromString(label: String): String {
    return when (label.trim().lowercase()) {
        "organic" -> stringResource(R.string.method_organic)
        "inorganic" -> stringResource(R.string.method_inorganic)
        "mixed" -> stringResource(R.string.method_mixed)
        else -> label
    }
}

@Composable
fun translatedLandArea(landArea: String): String {
    val acreWord = stringResource(R.string.acre_suffix)
    val replaced = landArea.replace("Acre", acreWord, ignoreCase = true)
    return localizedDigits(replaced)
}