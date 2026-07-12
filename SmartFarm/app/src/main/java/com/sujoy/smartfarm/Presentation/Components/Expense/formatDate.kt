package com.sujoy.smartfarm.Presentation.Components.Expense

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(

    time: Long

): String {

    val formatter =

        SimpleDateFormat(

            "dd MMM yyyy",

            Locale.getDefault()

        )

    return formatter.format(

        Date(time)

    )

}