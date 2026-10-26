package com.example.shuntingyardcalculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shuntingyardcalculator.di.DefaultDispatcher
import com.example.shuntingyardcalculator.domain.model.CalculationException
import com.example.shuntingyardcalculator.domain.usecase.CalculateExpressionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculateExpressionUseCase: CalculateExpressionUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onExpressionChange(expression: String) {
        _uiState.update {
            it.copy(expression = expression, errorMessage = null)
        }
    }

    fun calculate() {
        val expression = _uiState.value.expression
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val calculationResult = withContext(defaultDispatcher) {
                    calculateExpressionUseCase(expression)
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        postfixExpression = calculationResult.postfixExpression,
                        result = formatResult(calculationResult.result),
                        errorMessage = null,
                    )
                }
            } catch (exception: CalculationException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        postfixExpression = "",
                        result = "",
                        errorMessage = exception.message,
                    )
                }
            }
        }
    }

    fun clear() {
        _uiState.value = CalculatorUiState()
    }

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            "${value.toLong()}.0"
        } else {
            value.toString()
        }
    }
}
