package com.example.shuntingyardcalculator.presentation

import com.example.shuntingyardcalculator.data.calculator.ShuntingYardCalculator
import com.example.shuntingyardcalculator.domain.usecase.CalculateExpressionUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalculatorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(): CalculatorViewModel {
        return CalculatorViewModel(
            calculateExpressionUseCase = CalculateExpressionUseCase(ShuntingYardCalculator()),
            defaultDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun `calculate success updates postfix and result`() = runTest {
        val viewModel = createViewModel()
        viewModel.onExpressionChange("1 + 4 * 2 - 5")

        viewModel.calculate()

        val state = viewModel.uiState.value
        assertEquals("1 4 2 * + 5 -", state.postfixExpression)
        assertEquals("4.0", state.result)
        assertNull(state.errorMessage)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `calculate empty expression shows error`() = runTest {
        val viewModel = createViewModel()
        viewModel.onExpressionChange("   ")

        viewModel.calculate()

        val state = viewModel.uiState.value
        assertEquals("Expression cannot be empty", state.errorMessage)
        assertEquals("", state.result)
        assertTrue(!state.isLoading)
    }

    @Test
    fun `calculate invalid expression shows error`() = runTest {
        val viewModel = createViewModel()
        viewModel.onExpressionChange("1 +")

        viewModel.calculate()

        val state = viewModel.uiState.value
        assertTrue(state.errorMessage!!.contains("Invalid expression"))
        assertTrue(!state.isLoading)
    }

    @Test
    fun `clear resets state`() = runTest {
        val viewModel = createViewModel()
        viewModel.onExpressionChange("2 + 2")
        viewModel.calculate()
        viewModel.clear()

        val state = viewModel.uiState.value
        assertEquals(CalculatorUiState(), state)
    }

    @Test
    fun `onExpressionChange clears previous error`() = runTest {
        val viewModel = createViewModel()
        viewModel.onExpressionChange("1 +")
        viewModel.calculate()
        viewModel.onExpressionChange("2 + 2")

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
