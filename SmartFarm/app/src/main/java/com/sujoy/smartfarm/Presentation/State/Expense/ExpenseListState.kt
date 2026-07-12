package com.sujoy.smartfarm.Presentation.State.Expense

import com.sujoy.smartfarm.Domain.model.Expense.Expense

data class ExpenseListState(

    val isLoading: Boolean = false,

    val expenses: List<Expense> = emptyList(),

    val error: String = ""

)