# Shunting Yard Calculator

An Android calculator app that evaluates arithmetic expressions using the
Shunting-yard algorithm. The app is built with Kotlin and Jetpack Compose, and
separates the UI, calculation logic, and dependency wiring.

## What it does

- Accepts arithmetic expressions with decimal numbers and parentheses.
- Supports addition (`+`), subtraction (`-`), multiplication (`*`), division
  (`/`), and exponentiation (`^`).
- Converts infix input into postfix notation and evaluates the postfix
  expression.
- Displays both the postfix expression and the result.
- Reports invalid expressions, mismatched parentheses, and division by zero.
- Handles unary minus for negative numeric operands.

Operator precedence is exponentiation, multiplication/division, then
addition/subtraction. Exponentiation is right-associative, so `2 ^ 3 ^ 2` is
interpreted as `2 ^ (3 ^ 2)`.

## Project structure

Source files are under
`app/src/main/java/com/example/shuntingyardcalculator/`.

| Package or file | Responsibility |
| --- | --- |
| `presentation/CalculatorScreen.kt` | Compose UI and event callbacks |
| `presentation/CalculatorViewModel.kt` | Holds UI state and coordinates calculations |
| `presentation/CalculatorUiState.kt` | Immutable state rendered by the screen |
| `domain/usecase/CalculateExpressionUseCase.kt` | Validates non-empty input and invokes the calculator |
| `domain/repository/Calculator.kt` | Calculation interface used by the domain |
| `domain/model/` | Calculation result and calculation-specific errors |
| `data/calculator/ShuntingYardCalculator.kt` | Tokenizes, converts infix to postfix, and evaluates |
| `di/` | Hilt bindings and coroutine dispatcher provider |
| `MainActivity.kt` | App entry point; installs the themed Compose screen |
| `CalculatorApplication.kt` | Initializes Hilt for the application |

## Calculation flow

1. The user edits the expression or taps **Calculate** or **Clear** in
   `CalculatorScreen`.
2. The screen forwards events to `CalculatorViewModel`. The view model exposes
   `CalculatorUiState` as a `StateFlow`, which the screen observes with
   lifecycle-aware collection.
3. On calculation, the view model sets the loading state and invokes
   `CalculateExpressionUseCase` on the injected default coroutine dispatcher.
4. The use case rejects blank input and delegates to the `Calculator`
   interface. Hilt provides `ShuntingYardCalculator` as that implementation.
5. The calculator tokenizes the input, converts the infix tokens to postfix
   using operator precedence and an operator stack, then evaluates the postfix
   tokens with a value stack.
6. The view model puts the postfix expression and formatted result into UI
   state. Calculation errors are converted to an error message and displayed
   by the screen.
7. The screen recomposes from the updated state. **Clear** resets the state.

## Build and test

Build the debug app:

```bash
./gradlew :app:assembleDebug
```

Run local unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

The app uses `compileSdk` 36 and `minSdk` 24. Library and plugin versions are
centralized in `gradle/libs.versions.toml`.
