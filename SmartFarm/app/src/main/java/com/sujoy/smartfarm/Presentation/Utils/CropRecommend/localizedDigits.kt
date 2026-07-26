package com.sujoy.smartfarm.Presentation.Utils.CropRecommend

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

private val bengaliDigits = charArrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
private val hindiDigits   = charArrayOf('०','१','२','३','४','५','६','७','८','९')

/** Converts any ASCII 0-9 digits found in [text] to the current locale's native numerals. */
@Composable
fun localizedDigits(text: String): String {
    val languageTag = LocalConfiguration.current.locales[0].language
    val digitMap = when (languageTag) {
        "bn" -> bengaliDigits
        "hi" -> hindiDigits
        else -> return text // English/default: leave as-is
    }
    return text.map { ch ->
        if (ch in '0'..'9') digitMap[ch - '0'] else ch
    }.joinToString("")
}