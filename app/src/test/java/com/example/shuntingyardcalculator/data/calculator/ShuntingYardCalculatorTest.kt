package com.example.shuntingyardcalculator.data.calculator

import com.example.shuntingyardcalculator.domain.model.CalculationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShuntingYardCalculatorTest {

    private lateinit var calculator: ShuntingYardCalculator

    @Before
    fun setUp() {
        calculator = ShuntingYardCalculator()
    }

    @Test
    fun `operator precedence multiplication before addition`() {
        val result = calculator.calculate("1 + 4 * 2 - 5")
        assertEquals("1 4 2 * + 5 -", result.postfixExpression)
        assertEquals(4.0, result.result, 0.0001)
    }

    @Test
    fun `parentheses override precedence`() {
        val result = calculator.calculate("(1 + 4) * 2")
        assertEquals("1 4 + 2 *", result.postfixExpression)
        assertEquals(10.0, result.result, 0.0001)
    }

    @Test
    fun `exponentiation is right associative`() {
        val result = calculator.calculate("2 ^ 3 ^ 2")
        assertEquals(512.0, result.result, 0.0001)
    }

    @Test
    fun `division`() {
        val result = calculator.calculate("10 / 2")
        assertEquals(5.0, result.result, 0.0001)
    }

    @Test
    fun `decimal numbers`() {
        val result = calculator.calculate("1.5 + 2.5")
        assertEquals(4.0, result.result, 0.0001)
    }

    @Test
    fun `expression without whitespace`() {
        val result = calculator.calculate("1+4*2-5")
        assertEquals(4.0, result.result, 0.0001)
    }

    @Test
    fun `infix to postfix conversion`() {
        val result = calculator.calculate("1 + 4 * 2 - 5")
        assertEquals("1 4 2 * + 5 -", result.postfixExpression)
    }

    @Test(expected = CalculationException.DivisionByZeroException::class)
    fun `division by zero throws`() {
        calculator.calculate("10 / 0")
    }

    @Test
    fun `invalid character throws`() {
        try {
            calculator.calculate("1 & 2")
            error("Expected exception")
        } catch (exception: CalculationException.InvalidExpressionException) {
            assertTrue(exception.message!!.contains("Invalid character"))
        }
    }

    @Test
    fun `mismatched parentheses throws`() {
        try {
            calculator.calculate("(1 + 2")
            error("Expected exception")
        } catch (exception: CalculationException.InvalidExpressionException) {
            assertTrue(exception.message!!.contains("Mismatched parentheses"))
        }
    }

    @Test
    fun `missing operand throws`() {
        try {
            calculator.calculate("1 +")
            error("Expected exception")
        } catch (exception: CalculationException.InvalidExpressionException) {
            assertTrue(exception.message!!.contains("Invalid expression"))
        }
    }

    @Test
    fun `empty expression throws from calculator tokenize`() {
        try {
            calculator.calculate("")
            error("Expected exception")
        } catch (exception: CalculationException.InvalidExpressionException) {
            assertTrue(exception.message!!.contains("empty"))
        }
    }

    @Test
    fun `unary minus at start`() {
        val result = calculator.calculate("-3 + 5")
        assertEquals(2.0, result.result, 0.0001)
    }

    @Test
    fun `unary minus after parenthesis`() {
        val result = calculator.calculate("(-2) * 3")
        assertEquals(-6.0, result.result, 0.0001)
    }
}
