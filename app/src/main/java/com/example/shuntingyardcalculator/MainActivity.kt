package com.example.shuntingyardcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.shuntingyardcalculator.presentation.CalculatorScreen
import com.example.shuntingyardcalculator.ui.theme.ShuntingYardCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShuntingYardCalculatorTheme {
                CalculatorScreen(modifier = Modifier)
            }
        }
    }
}
