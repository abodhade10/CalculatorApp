package com.example.shuntingyardcalculator.presentation

data class CalculatorUiState(
    val expression: String = "",
    val postfixExpression: String = "",
    val result: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)
