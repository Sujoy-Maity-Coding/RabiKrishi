package com.sujoy.smartfarm.Domain.UseCase.Expense

import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(

    private val repository: Repo

) {

    operator fun invoke(

        farmId: String,

        expense: Expense

    ) = repository.addExpense(

        farmId,

        expense

    )

}