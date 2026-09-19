# Battuk — Phase 2

Battuk is a local-first household budget and consumption tracker. This package extends the Phase 1 MVP with the Phase 2 features from the planning document.

## Phase 2 features

- Budgets & limits with category progress and budget-crossing notifications
- Loan / EMI schedules with due-date reminders
- Logging streaks and badges
- Provider-neutral cloud backup/sync package (JSON export/import; the Android picker can target Drive or another cloud provider)
- Weighted inflation model with optional seasonal normalization
- English / Marathi UI toggle

## Tech

- Kotlin
- Jetpack Compose + Material 3
- Room (database v2 with migration from v1)
- DataStore
- Coil
- GitHub Actions

## Run

Use the included GitHub Actions workflow under `.github/workflows/build-debug.yml` with `workflow_dispatch`. The generated artifact is `battuk-phase2-debug-apk`.

## Important

The planning document names “optional cloud backup/sync” but does not choose a cloud provider. Therefore Phase 2 uses a provider-neutral sync package rather than silently adding Firebase, Google Drive SDK, or another account system.
