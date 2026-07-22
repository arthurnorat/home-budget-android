# HomeBudget Android

Android companion app for HomeBudget — quick expense entry on the go, syncing with the shared web dashboard.

This app is not a full budgeting tool. Its main purpose is to let you log an expense the moment it happens, feeding the shared expenses table. Browsing history, editing, and analysis (50/30/20 summary, fixed expense import, income tracking) live only in the web app.

## Stack

- Kotlin + Jetpack Compose (Material 3)
- Retrofit + OkHttp + kotlinx.serialization (no other external dependencies)
- `ViewModel` + `StateFlow` (Jetpack Lifecycle)
- MVVM (Screen + ViewModel + Repository)

## Status

In active development.
