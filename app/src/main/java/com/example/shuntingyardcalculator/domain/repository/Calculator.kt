package com.example.shuntingyardcalculator.domain.repository

import com.example.shuntingyardcalculator.domain.model.CalculationResult

interface Calculator {
    fun calculate(expression: String): CalculationResult
}
