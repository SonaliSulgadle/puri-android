# Puri (풀이) — Android App

## Project overview
Android app for foreigners in South Korea. Camera → AI analysis →
actionable guidance in English.

## Tech stack
Kotlin, Jetpack Compose, MVVM + Clean Architecture, Hilt, Room,
DataStore, CameraX, Gemini API via Retrofit, Firebase,
kotlinx.serialization, JUnit5 + MockK

## Package
com.puri.app (debug: com.puri.app.debug)

## Architecture
- core/ — theme, analytics, camera, common utilities
- data/ — repositories, Room, Retrofit, DataStore, prompt builders
- domain/ — use cases, models, repository interfaces
- feature/ — screen-level MVVM (solve, saved, history, address, profile)
- navigation/ — NavGraph, MainScaffold

## Key files
- data/remote/prompt/ — ImagePromptBuilder, TextPromptBuilder,
  AddressPromptBuilder, PromptConstants
- data/remote/GeminiResponseParser.kt — parses SIMPLE/PROCESS formats
- feature/solve/ — main feature, SolveViewModel + SolveScreen
- data/local/db/ — Room entities, DAOs, RoomConverters

## DO NOT modify
- local.properties (contains API keys)
- google-services.json
- app/release/ (keystore)

## Current state
About to release V1 to Play Store. Final testing phase.