package com.sujoy.smartfarm.Domain.UseCase.Expense

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(

    private val repository: Repo

) {

    operator fun invoke(

        farmId: String

    ) = repository.getExpenses(

        farmId

    )

}