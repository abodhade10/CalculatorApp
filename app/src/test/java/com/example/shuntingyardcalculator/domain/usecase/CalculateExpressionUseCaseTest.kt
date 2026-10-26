package com.example.shuntingyardcalculator.domain.usecase

import com.example.shuntingyardcalculator.data.calculator.ShuntingYardCalculator
import com.example.shuntingyardcalculator.domain.model.CalculationException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateExpressionUseCaseTest {

    private lateinit var useCase: CalculateExpressionUseCase

    @Before
    fun setUp() {
        useCase = CalculateExpressionUseCase(ShuntingYardCalculator())
    }

    @Test
    fun `delegates calculation to calculator`() {
        val result = useCase("2 + 2")
        assertEquals(4.0, result.result, 0.0001)
    }

    @Test(expected = CalculationException.EmptyExpressionException::class)
    fun `blank input throws empty expression`() {
        useCase("   ")
    }
}
