package com.example.shuntingyardcalculator.di

import com.example.shuntingyardcalculator.data.calculator.ShuntingYardCalculator
import com.example.shuntingyardcalculator.domain.repository.Calculator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalculatorModule {

    @Binds
    @Singleton
    abstract fun bindCalculator(impl: ShuntingYardCalculator): Calculator
}
