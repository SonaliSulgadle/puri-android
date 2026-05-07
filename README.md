# Puri (풀이)

**Daily life assistant for foreigners in South Korea.**

A pocket AI assistant that helps foreigners navigate daily life in South Korea. 
Point your camera at anything confusing — trash bins, washing machine
buttons, bus signs, appliances — and get an instant explanation in English.

---

## Features

- **Snap & Solve** — Photo-based AI analysis of Korean text and objects
- **Ask Anything** — Text queries about daily life in Korea
- **Address Converter** — Convert any Korean address format to Naver Map–ready
- **12 Offline Guides** — Trash sorting, subway, bus, medical, appliances and more
- **No account required** — All data stored locally on device

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM + MVI
- **DI:** Hilt
- **Database:** Room + DataStore
- **AI:** Google Gemini API (via Retrofit)
- **Camera:** CameraX
- **Analytics:** Firebase Analytics + Crashlytics
- **Min SDK:** API 26 (Android 8.0)

## Architecture

app/

├── core/           # Shared utilities, theme, analytics

├── data/           # Repositories, Room, Retrofit, DataStore

├── domain/         # Use cases, models, repository interfaces

├── feature/        # Screen-level MVVM (solve, saved, history, address, profile)

└── navigation/     # NavGraph, Screen routes

## Getting Started

1. Clone the repo
2. Add `GEMINI_API_KEY=your_key` to `local.properties`
3. Add `google-services.json` to `app/` directory
4. Run on API 26+ device or emulator

## License

Apache 2.0

This project is for portfolio and educational viewing purposes.  
Commercial use, redistribution, or derivative works are not permitted  
without explicit written permission.

© 2026 Sonali Sulgadle
