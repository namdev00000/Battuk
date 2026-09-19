# Battuk Phase 1 implementation map

This project implements the Phase 1 MVP described in `battuk-app-plan.md`.

| Plan area | Implementation |
|---|---|
| Profile creation | Onboarding screen + Room Profile |
| Family members | Family screen + Room FamilyMember |
| Categories | Seed categories + custom category creation |
| Bilingual Weekly Market | 36 preloaded produce items in English + Marathi |
| Single Entry | Add Expense screen with item/category/price/quantity/date/member/receipt |
| Bulk Entry | Multi-item store trip + one trip receipt + saved linked expenses |
| Remember the Things | Shopping lists + checked items + Bulk Entry handoff |
| Live clock/date | Home header |
| Mini calendar | Current-month date grid + expense markers + selected-day expense view |
| Calculator | Basic arithmetic with precedence, saved history, copy and paste-to-entry |
| Income | Add Income screen + Room Income + report/dashboard summaries |
| Reports | Day/week/month/6-month/year filters; category/family/income-expense summaries |
| Price history | Per-item local trend screen |
| Visual Dashboard | Family spending, current-vs-previous-week comparison, top categories, inflation entry point |
| Inflation Calculator | Product selection + price-change calculation from local purchase history |
| Receipts | App-private saved receipt URI + gallery preview |
| Settings | Theme, sound toggle, manage-by-year view/export/archive state, family/categories/receipts navigation |
| Currency | Rupee-only labels and fixed currency text |
| CSV backup | Actual CSV file created in cache and shared via Android FileProvider |
| Local-first | Room + DataStore + app-private media; no login/cloud/external price feed |
| GitHub Actions | `workflow_dispatch` debug build + uploaded APK artifact |

## Verification limitation

The container used to prepare this project does not have an Android SDK or a working Gradle installation, and outbound Gradle distribution download is unavailable here. The source tree was therefore checked structurally, but the debug APK could not be compiled in this environment. GitHub Actions is included as the intended build path.
