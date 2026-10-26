package com.example.shuntingyardcalculator.domain.model

sealed class CalculationException(message: String) : Exception(message) {

    class EmptyExpressionException :
        CalculationException("Expression cannot be empty")

    class InvalidExpressionException(message: String) :
        CalculationException(message)

    class DivisionByZeroException :
        CalculationException("Division by zero")
}
