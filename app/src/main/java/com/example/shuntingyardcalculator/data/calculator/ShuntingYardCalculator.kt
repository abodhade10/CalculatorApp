package com.example.shuntingyardcalculator.data.calculator

import com.example.shuntingyardcalculator.domain.model.CalculationException
import com.example.shuntingyardcalculator.domain.model.CalculationResult
import com.example.shuntingyardcalculator.domain.repository.Calculator
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class ShuntingYardCalculator @Inject constructor() : Calculator {

    override fun calculate(expression: String): CalculationResult {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            throw CalculationException.InvalidExpressionException("Expression cannot be empty")
        }
        val tokens = tokenize(trimmed)
        if (tokens.isEmpty()) {
            throw CalculationException.InvalidExpressionException("Expression cannot be empty")
        }
        val postfix = infixToPostfix(tokens)
        val postfixExpression = postfix.joinToString(" ")
        val result = evaluatePostfix(postfix)
        return CalculationResult(
            infixExpression = expression,
            postfixExpression = postfixExpression,
            result = result,
        )
    }

    /**
     * Splits an infix expression into tokens (numbers, operators, parentheses).
     * Ignores whitespace so expressions work with or without spaces between tokens.
     */
    private fun tokenize(expression: String): List<String> {
        if (expression.isBlank()) return emptyList()

        val tokens = mutableListOf<String>()
        var index = 0
        while (index < expression.length) {
            when (val char = expression[index]) {
                ' ', '\t', '\n' -> index++
                in '0'..'9', '.' -> {
                    val start = index
                    var hasDecimalPoint = char == '.'
                    index++
                    while (index < expression.length) {
                        when (val next = expression[index]) {
                            in '0'..'9' -> index++
                            '.' -> {
                                if (hasDecimalPoint) break
                                hasDecimalPoint = true
                                index++
                            }
                            else -> break
                        }
                    }
                    tokens.add(expression.substring(start, index))
                }
                '(' -> {
                    tokens.add("(")
                    index++
                }
                ')' -> {
                    tokens.add(")")
                    index++
                }
                '+', '*', '/', '^' -> {
                    tokens.add(char.toString())
                    index++
                }
                '-' -> {
                    val isUnary = tokens.isEmpty() ||
                        tokens.last() == "(" ||
                        tokens.last() in precedence
                    if (isUnary) {
                        index++
                        val numberStart = index
                        var hasDecimalPoint = false
                        if (index < expression.length && expression[index] == '.') {
                            hasDecimalPoint = true
                            index++
                        }
                        while (index < expression.length) {
                            when (val next = expression[index]) {
                                in '0'..'9' -> index++
                                '.' -> {
                                    if (hasDecimalPoint) break
                                    hasDecimalPoint = true
                                    index++
                                }
                                else -> break
                            }
                        }
                        if (numberStart == index) {
                            throw CalculationException.InvalidExpressionException(
                                "Missing operand for unary minus",
                            )
                        }
                        tokens.add("-" + expression.substring(numberStart, index))
                    } else {
                        tokens.add("-")
                        index++
                    }
                }
                else -> throw CalculationException.InvalidExpressionException(
                    "Invalid character: '$char'",
                )
            }
        }
        return tokens
    }

    /**
     * Converts an infix expression into postfix notation using the shunting-yard algorithm.
     */
    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val opStack = ArrayDeque<String>()

        for (token in tokens) {
            when {
                token == "(" -> {
                    opStack.addLast(token)
                }

                token == ")" -> {
                    while (opStack.isNotEmpty() && opStack.last() != "(") {
                        output.add(opStack.removeLast())
                    }
                    if (opStack.isEmpty()) {
                        throw CalculationException.InvalidExpressionException("Mismatched parentheses")
                    }
                    opStack.removeLast()
                }

                token in precedence -> {
                    while (opStack.isNotEmpty() && opStack.last() != "(") {
                        val top = opStack.last()
                        if (top !in precedence) break

                        val currentPriority = precedence.getValue(token)
                        val topPriority = precedence.getValue(top)

                        val shouldPop =
                            currentPriority < topPriority ||
                                (
                                    currentPriority == topPriority &&
                                        !rightAssociative.getValue(token)
                                    )

                        if (!shouldPop) break

                        output.add(opStack.removeLast())
                    }
                    opStack.addLast(token)
                }

                else -> {
                    token.toDoubleOrNull()
                        ?: throw CalculationException.InvalidExpressionException("Invalid number: $token")
                    output.add(token)
                }
            }
        }

        while (opStack.isNotEmpty()) {
            val operator = opStack.removeLast()
            if (operator == "(") {
                throw CalculationException.InvalidExpressionException("Mismatched parentheses")
            }
            output.add(operator)
        }

        return output
    }

    /**
     * Evaluates a postfix expression and returns the final result.
     */
    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = ArrayDeque<Double>()

        for (token in postfix) {
            if (token !in precedence) {
                stack.addLast(
                    token.toDoubleOrNull()
                        ?: throw CalculationException.InvalidExpressionException("Invalid number: $token"),
                )
            } else {
                if (stack.size < 2) {
                    throw CalculationException.InvalidExpressionException("Invalid expression")
                }

                val right = stack.removeLast()
                val left = stack.removeLast()

                val result = when (token) {
                    "+" -> left + right
                    "-" -> left - right
                    "*" -> left * right
                    "/" -> {
                        if (right == 0.0) {
                            throw CalculationException.DivisionByZeroException()
                        }
                        left / right
                    }
                    "^" -> left.pow(right)
                    else -> throw CalculationException.InvalidExpressionException("Unsupported operator")
                }

                stack.addLast(result)
            }
        }

        if (stack.size != 1) {
            throw CalculationException.InvalidExpressionException("Invalid expression")
        }

        return stack.removeLast()
    }

    companion object {
        // Higher value means higher priority.
        private val precedence = mapOf(
            "^" to 4,
            "*" to 3,
            "/" to 3,
            "+" to 2,
            "-" to 2,
        )

        // Right-associative: 2 ^ 3 ^ 2 means 2 ^ (3 ^ 2).
        private val rightAssociative = mapOf(
            "^" to true,
            "*" to false,
            "/" to false,
            "+" to false,
            "-" to false,
        )
    }
}
