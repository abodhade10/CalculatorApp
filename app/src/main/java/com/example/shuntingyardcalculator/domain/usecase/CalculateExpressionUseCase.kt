package com.example.shuntingyardcalculator.domain.usecase

import com.example.shuntingyardcalculator.domain.model.CalculationException
import com.example.shuntingyardcalculator.domain.model.CalculationResult
import com.example.shuntingyardcalculator.domain.repository.Calculator
import javax.inject.Inject

class CalculateExpressionUseCase @Inject constructor(
    private val calculator: Calculator,
) {
    operator fun invoke(expression: String): CalculationResult {
        if (expression.isBlank()) {
            throw CalculationException.EmptyExpressionException()
        }
        return calculator.calculate(expression.trim())
    }
}
