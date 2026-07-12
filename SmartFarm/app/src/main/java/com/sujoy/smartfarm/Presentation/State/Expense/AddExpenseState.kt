package com.sujoy.smartfarm.Presentation.State.Expense

data class AddExpenseState(

    val isLoading: Boolean = false,

    val success: String = "",

    val error: String = ""

)