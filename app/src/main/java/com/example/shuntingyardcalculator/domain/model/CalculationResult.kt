package com.example.shuntingyardcalculator.domain.model

data class CalculationResult(
    val infixExpression: String,
    val postfixExpression: String,
    val result: Double,
)
