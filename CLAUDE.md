# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Language

Always respond in Traditional Chinese (繁體中文).

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.example.composeproject.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean + build
./gradlew clean assembleDebug
```

## Architecture

Single-Activity app (`MainActivity`) using Jetpack Compose. Navigation is handled manually via a `sealed class ScreenFlag` — no Navigation Component is used. Screen transitions are driven by `AnimatedContent` in `MainActivity`.

**State flow:**
- `MainActivityViewModel` holds a `MutableStateFlow<HomeScreenState>` which contains `currentScreenFlag` and `homeListData`
- `HomeScreenState` drives which screen `AnimatedContent` renders
- `setScreenFlag()` is the only way to navigate; back press is handled via `OnBackPressedDispatcher`

**Theme system:**
- `CustomTheme` wraps the entire app and provides `LocalCustomColors` via `CompositionLocalProvider`
- All colors are accessed through `LocalCustomColors.current.xxx` — do not use hardcoded `Color(...)` values in UI code
- Dark mode color switching is done inside `CustomTheme` (currently both modes use the same `Colors()` instance)

**Responsive sizing:**
- Use `Int.mdp` / `Int.msp` (from `Utils/SizeUtils.kt`) instead of `.dp` / `.sp` for all sizes and font sizes
- These scale relative to a 375dp reference width to maintain consistent proportions across screen sizes
- `REFERENCE_SCREEN_WIDTH_DP = 375f` — match this to the design spec width

**Custom Modifiers** (`ui/modifier/`):
- `delayClick { }` — debounced click (default 500ms), use this instead of `.clickable` to prevent double-taps
- `notRippleClickable { }` — clickable with no ripple effect

**Adding a new example screen:**
1. Add a new `data object` to `sealed class ScreenFlag` with a `@StringRes titleRes`
2. Add the string resource to `res/values/strings.xml`
3. Add the screen to `getListData()` in `MainActivityViewModel`
4. Add a `when` branch in `MainActivity`'s `AnimatedContent` block

## Naming Conventions

All generated code must follow these rules:

- **類別 / 物件 / sealed class / Composable 函數** — `PascalCase`（例如 `HomeScreen`、`ScreenFlag`）
- **一般函數 / ViewModel 方法 / 變數 / 屬性** — `camelCase`（例如 `setScreenFlag`、`isListLoading`）
- **私有 StateFlow 欄位** — 底線前綴 `_` + `camelCase`（例如 `_homeScreenState`），公開版本去掉底線
- **常數** — `SCREAMING_SNAKE_CASE`（例如 `REFERENCE_SCREEN_WIDTH_DP`）
- **Extension 屬性（自訂 dp/sp）** — 全小寫縮寫（例如 `mdp`、`msp`）
- **自訂 Modifier** — `camelCase`（例如 `delayClick`、`notRippleClickable`）
- **資料夾** — 全小寫（例如 `ui/common/`、`data/`），唯一例外為 `Utils/`（沿用既有命名）

## Key Conventions

- `ui/common/` — reusable Composables (inputs, pickers, loading mask, swipe items)
- `ui/modifier/` — custom `Modifier` extensions
- `example/` — standalone example screens shown in the home list
- `data/` — plain data classes; `Msg.read` uses `mutableStateOf` directly on a data class field (Compose state in model layer)
- `HomeScreen` composable in `MainActivity.kt` is the active home list — the one in `ui/screens/HomeScreen.kt` is legacy/unused
- `android.os.Handler()` is used in `ViewModel.fetchData()` — this is deprecated; prefer `viewModelScope.launch { delay(...) }` for new async work
- ViewModel mixes `MutableStateFlow` (for screen state) and Compose `mutableStateOf` (for other fields) — new state should use `StateFlow` for consistency
