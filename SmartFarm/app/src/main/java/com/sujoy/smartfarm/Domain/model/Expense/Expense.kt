package com.sujoy.smartfarm.Domain.model.Expense

data class Expense(

    var expenseId: String = "",

    var category: String = "",

    var amount: Double = 0.0,

    var note: String = "",

    var date: Long = System.currentTimeMillis()

)