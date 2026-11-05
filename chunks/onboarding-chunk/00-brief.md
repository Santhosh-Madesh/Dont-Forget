# Brief — Onboarding Feature (`onboarding-chunk`)

follow `android-chunk-workflow`

## 1. Name & Scope
- **Name**: `onboarding-chunk`
- **Scope**: Implement a 7-screen interactive first-time onboarding flow introducing users to DONT FORGET core value proposition and guiding them through a hands-on interactive demo reminder creation & packing exercise. App startup onboarding state (`ONBOARDING_COMPLETED`) is managed via `PreferencesRepository` and checked by `PreferencesViewModel` at launch. `OnboardingViewModel` manages screen UI state and is decoupled from `PreferencesRepository`. The onboarding test alert triggers an instant system alarm using an abstracted `ReminderAlarmScheduler` schedule function passed via screen arguments.

## 2. Context & Architecture Pointers
- Read `README.md` and `chunks/chunk-00-foundation/01-contract.md`
- Follow MVI ViewModel pattern (`UiState`, `UiEvent`, `UiSideEffect`) inheriting from `BaseViewModel`.
- Preferences DataStore managed by `PreferencesRepository` and `PreferencesViewModel`.
- `OnboardingViewModel` handles step navigation, form inputs, packing state, and alarm trigger side-effects without accessing `PreferencesRepository` directly.

## 3. Relevant Spec & UI References
- UI References:
  - `onboarding-screen1.png`: Welcome & Value Proposition (Get Started / Skip)
  - `onboarding-screen2.png`: Step 1 of 5 - Choose Context (`OnboardingStep.CHOOSE_CONTEXT`)
  - `onboarding-screen3.png`: Step 2 of 5 - Add Items (`OnboardingStep.ADD_ITEMS`)
  - `onboarding-screen4.png`: Step 3 of 5 - Departure & Notification Timing (`OnboardingStep.CONFIGURE_TIMING`)
  - `onboarding-screen5.png`: Step 4 of 5 - Created Confirmation (`OnboardingStep.CONFIRMATION_SUMMARY`)
  - `onboarding-screen6.png`: Step 5 of 5 - Live Alert & Packing Verification (`OnboardingStep.INTERACTIVE_PACKING`)
  - `onboarding-screen7.png`: Onboarding Completion / Victory (`OnboardingStep.COMPLETION_VICTORY`)

## 4. Contract Points & Dependencies
- **Consumes**: Preferences DataStore (`androidx.datastore:datastore-preferences`), `ReminderAlarmScheduler` (abstracted schedule callback parameter)
- **Exposes**: `OnboardingScreen`, `PreferencesRepository`, `PreferencesViewModel`, `OnboardingViewModel`
- **Navigation Out**: `Route.Home` (clearing `Route.Onboarding` from backstack)

## 5. Ownership Boundaries
- **May touch**: `com/example/dontforget/feature/onboarding/**`, `com/example/dontforget/data/preferences/**`, `com/example/dontforget/core/navigation/Route.kt`, `MainActivity.kt`
- **May NOT touch**: Existing feature screens (`feature/home`, `feature/createreminder`, `feature/templates`, `feature/notifications`), database schemas (`data/local`).
