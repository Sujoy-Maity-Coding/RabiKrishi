package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDropDown(

    selectedMonth: Int,

    onMonthSelected: (Int) -> Unit

) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val months = listOf(

        "January",
        "February",
        "March",
        "April",
        "May",
        "June",

        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    val selectedText =

        if (selectedMonth in 1..12)
            months[selectedMonth - 1]
        else
            ""

    ExposedDropdownMenuBox(

        expanded = expanded,

        onExpandedChange = {

            expanded = !expanded
        }

    ) {

        OutlinedTextField(

            value = selectedText,

            onValueChange = {},

            readOnly = true,

            label = {
                Text("Month")
            },

            trailingIcon = {

                ExposedDropdownMenuDefaults
                    .TrailingIcon(
                        expanded = expanded
                    )
            },

            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(

            expanded = expanded,

            onDismissRequest = {

                expanded = false
            }

        ) {

            months.forEachIndexed { index, month ->

                DropdownMenuItem(

                    text = {

                        Text(month)
                    },

                    onClick = {

                        onMonthSelected(
                            index + 1
                        )

                        expanded = false
                    }
                )
            }
        }
    }
}