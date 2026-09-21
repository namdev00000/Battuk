# Battuk — House Budget & Consumption Tracking App (Phase 1 / v1)

Native Kotlin + Jetpack Compose Android app. No Android Studio required —
built and tested entirely through GitHub Actions, matching your existing workflow.

## What's implemented in this v1 build

- Profile onboarding (name, optional DOB, photo) + Family Members (name, relation, DOB, earning-member toggle)
- Categories: Weekly Market, Kirana Store, Clothes, Shoes, Toiletries, Milk Products,
  Fast Food, Entertainment, Loan/EMI, Petrol/Diesel, Other
- Bilingual (English/Marathi) seeded item list for Weekly Market vegetables, leafy greens, and fruits
- **Single Entry** and **Bulk Entry** (Items → Receipt → Review steps) expense logging, with receipt photo capture
- **Remember the Things** shopping lists, with tick-off and "Convert to Bulk Entry"
- **Calculator**: floating button bottom-right, saved history, copy-result / paste-to-expense
- **Reports** (Day/Week/Month/6-Month/Year filters): category pie chart, weekly trend bar chart, top items
- **Visual Dashboard**: per-family-member spend, top categories, tap an item for its price trend
- **Item Price History**: per-item price trend chart + purchase list
- **Inflation Calculator** (extra feature): pick a product + time range, get an on-demand % price change
- **Mini Calendar**: month grid with expense-day markers, tap a day to see that day's spend
- **Income Entry** per family member
- **Receipts Gallery**: browse all attached receipt photos
- **Settings**: theme (Light/Dark/System, toggle fixed top-right on every screen), sound on/off (with a
  generated UI click sound), Year Management (set active year, export a year's data as CSV), fixed
  currency (Indian Rupees ₹, not user-changeable)
- Everything is stored **locally on-device** via Room — no login, no cloud, no internet permission used

## Known v1 rough edges (intentional scope choices, not bugs)

- The Family Members screen is reused for both onboarding and later "manage family" access from
  Settings; its "Continue" button always leads Home, which is harmless but not perfectly tailored
  to the settings context yet.
- Category and Item management from Settings is currently browse-only (categories/items are
  seeded on first launch); add/edit UI for custom categories/items can be added in v2.
- Item name matching for price history/inflation is by exact name — a custom-typed item with a
  slightly different spelling than the seeded list will be tracked as a separate item.
- The Inflation Calculator and Visual Dashboard use simple, transparent average-price-change math,
  not a weighted or seasonally-adjusted model (as scoped in the plan).

## Build matrix (chosen together, verified compatible)

| Tool | Version |
|---|---|
| JDK | 17 |
| Gradle | 8.7 (real wrapper jar included, verified) |
| Android Gradle Plugin | 8.4.2 |
| Kotlin | 1.9.24 |
| Compose Compiler | 1.5.14 |
| compileSdk / targetSdk | 34 |
| minSdk | 26 (Android 8.0+) |

## Building the APK yourself

This repo's Gradle root **is** the repository root — `settings.gradle.kts` sits at the top level,
so no `working-directory` override is needed in CI.

```bash
./gradlew assembleDebug
```

The debug APK will be at:

```
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions

`.github/workflows/build-debug-apk.yml` is already included in this zip. Push this project to a
GitHub repo (keeping this exact folder structure — don't nest it inside another folder), then run
the workflow from the Actions tab (`workflow_dispatch`) or just push to `main`. Download the APK
from the workflow run's Artifacts section once it finishes, then install it on your phone
(you'll need to allow "install from unknown sources" the first time).
