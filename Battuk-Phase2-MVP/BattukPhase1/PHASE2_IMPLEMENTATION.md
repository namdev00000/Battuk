# Battuk — Phase 2 Implementation

This build extends the Phase 1 MVP described in `battuk-app-plan.md`. Phase 2 contains the six additions specified in the plan:

1. **Budgets & limits** — monthly category budgets with progress and notification when a new expense crosses the limit.
2. **Loan/EMI scheduling** — EMI records, next due date, pause/resume, monthly rollover, and on-device notifications.
3. **Streaks & badges** — current/best logging streak plus 1/3/7/30-day badges computed from local expense dates.
4. **Cloud backup/sync** — provider-neutral full sync package export/import using Android's document picker. Users can save the JSON package to Drive/another cloud provider and import it on another phone. The plan does not specify a cloud vendor, so no vendor SDK or account requirement was invented.
5. **Deeper inflation modeling** — quantity-weighted product pricing, spend-weighted category changes, and optional same-month seasonal normalization using the user's recorded history.
6. **Full UI bilingual toggle** — English/Marathi interface text is localized through the app's text layer and preference is persisted with DataStore. Produce names remain bilingual in all modes.

## Database

Room is upgraded from version 1 to version 2 with a real migration. Existing Phase 1 tables are retained; Phase 2 adds `Budget` and `LoanEmi`.

## Notifications

The app requests Android 13+ notification permission, creates separate budget/EMI channels, and schedules EMI reminders with `AlarmManager`. Active reminders are re-registered when the app starts.

## Build

Version is `2.0.0` / `versionCode 2`. The existing `workflow_dispatch` GitHub Actions flow is retained and renamed for Phase 2.

## Verification note

The project sources and migration structure were statically reviewed in this environment. A full Gradle/Android build could not be executed here because the container cannot resolve `services.gradle.org`; GitHub Actions is the intended build environment for the supplied project.
