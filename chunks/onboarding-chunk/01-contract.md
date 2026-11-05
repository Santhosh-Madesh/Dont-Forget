# Feature Contract — Onboarding Feature (`onboarding-chunk`)

## 1. Goal
Provide a seamless, interactive 7-screen first-time onboarding experience that introduces new users to DONT FORGET, guides them through setting up their first test reminder and packing cargo, and persists the onboarding completion flag (`ONBOARDING_COMPLETED`) using `PreferencesRepository` and `PreferencesViewModel` so the flow runs only once upon first launch.

## 2. Scope

### In Scope
- **Preferences DataStore Integration & Root ViewModel**:
  - `PreferencesRepository` managing `ONBOARDING_COMPLETED` boolean key in Preferences DataStore.
  - Exposes `isOnboardingCompleted: Flow<Boolean>` and `suspend fun setOnboardingCompleted(completed: Boolean)`.
  - `PreferencesViewModel` created separately as the root startup ViewModel to check `isOnboardingCompleted` at launch and set start destination (`Route.Onboarding` vs `Route.Home`).
  - `OnboardingViewModel` is decoupled from `PreferencesRepository` and handles screen UI state and step navigation only.
- **Screen Step Enum (`OnboardingStep`)**:
  - `WELCOME` (Screen 1): Welcome & Value Proposition (*Get Started / Skip*)
  - `CHOOSE_CONTEXT` (Screen 2): Step 1 of 5 (*Where are you going?*)
  - `ADD_ITEMS` (Screen 3): Step 2 of 5 (*What do you need to take?*)
  - `CONFIGURE_TIMING` (Screen 4): Step 3 of 5 (*When should we remind you?*)
  - `CONFIRMATION_SUMMARY` (Screen 5): Step 4 of 5 (*Reminder Created!*)
  - `INTERACTIVE_PACKING` (Screen 6): Step 5 of 5 (*Live Test Alert Received & Cargo Verification*)
  - `COMPLETION_VICTORY` (Screen 7): Victory & Final Preparedness Summary
- **Screen 1: Welcome & Value Proposition (`OnboardingStep.WELCOME`)**:
  - Header graphic (checklist card with lock/check icon inside circle).
  - Title: "Never forget a thing before leaving."
  - Subtitle: "Create a checklist for where you're going and get reminded before you leave."
  - Primary CTA: "Get Started" -> Advances to `CHOOSE_CONTEXT`.
  - Secondary CTA: "Skip" -> Emits completion side-effect -> `PreferencesViewModel` sets `ONBOARDING_COMPLETED = true` -> Navigates to `Route.Home`.
- **Screen 2: Choose Context (`OnboardingStep.CHOOSE_CONTEXT`)**:
  - Progress indicator: `STEP 1 OF 5`.
  - Title: "Where are you going?"
  - Subtitle: "Choose a context for the things you need to remember."
  - Field: "Destination or Place" with location icon input box and helper hint ("e.g., Gym, Work desk, Flight gate").
  - Primary CTA: "Next ->" -> Advances to `ADD_ITEMS`.
- **Screen 3: Add Checklist Items (`OnboardingStep.ADD_ITEMS`)**:
  - Progress indicator: `STEP 2 OF 5`.
  - Title: "What do you need to take?"
  - Subtitle: "Add the things you don't want to forget when leaving for [context]."
  - Input field with `+` button to add custom items.
  - Interactive items list with remove `x` button per item (prefilled defaults: "Water bottle", "Towel", "Headphones").
  - Primary CTA: "Next ->" -> Advances to `CONFIGURE_TIMING`.
- **Screen 4: Set Departure & Notification Timing (`OnboardingStep.CONFIGURE_TIMING`)**:
  - Progress indicator: `STEP 3 OF 5`.
  - Title: "When should we remind you?"
  - Time selector component: Hour/Minute controls and AM/PM toggle (`06:00 PM`).
  - Notification Timing selector: "Now" (Test Alert option selected) + educational info card regarding flexible lead times.
  - Primary CTA: "Next ->" -> Advances to `CONFIRMATION_SUMMARY`.
- **Screen 5: Reminder Created Confirmation (`OnboardingStep.CONFIRMATION_SUMMARY`)**:
  - Progress indicator: `STEP 4 OF 5`.
  - Checkmark header badge, Title "Reminder Created!", Subtitle "You're all set. We'll remind you before you leave."
  - Summary card detailing context, departure time, reminder time (-30m), and packing checklist items preview chips.
  - Primary CTA: "Done ✓" -> Emits `TriggerInstantTestAlarm` side effect -> Calls abstract schedule lambda argument `onScheduleTestAlarm(title, content)` -> Advances to `INTERACTIVE_PACKING`.
- **Screen 6: Live Alert & Packing Verification (`OnboardingStep.INTERACTIVE_PACKING`)**:
  - Top Banner Chip: Bell icon "Live Test Alert Received".
  - Progress indicator: `STEP 5 OF 5`.
  - Title: "Look at that, your phone buzzed." Subtitle: "Did you actually notice, or were you about to walk out into the cold empty-handed? We did our part. Now prove you're ready."
  - Cargo packing list with interactive checkboxes ("Water bottle", "Towel", "Headphones") and "Pack all" quick action button.
  - Counter text: "Your Cargo X of Y".
  - Primary CTA: "Check off items above" / "Finish" -> Advances to `COMPLETION_VICTORY`.
- **Screen 7: Victory & Completion (`OnboardingStep.COMPLETION_VICTORY`)**:
  - Top close icon `X` and `• DON'T FORGET V2` tag.
  - Victory badge: "✓ Active • 4 items packed • Crisis averted".
  - Title: "Look at you. Fully prepared." Body text celebrating preparedness.
  - Verified Inventory summary chips ("Keys & FOB", "Work Badge", "Noise Canceling", "Water Bottle").
  - Primary CTA: "Got it, let's roll ->" -> Emits completion side-effect -> `PreferencesViewModel` sets `ONBOARDING_COMPLETED = true` in DataStore and navigates to `Route.Home` (popping `Route.Onboarding` from backstack).

### Out of Scope
- Direct access to `PreferencesRepository` from `OnboardingViewModel`.
- Inserting onboarding demo items into Room database as actual user reminders.

## 3. Data & Dependencies
- **New Dependency Required**: `androidx.datastore:datastore-preferences` (declared in `libs.versions.toml` and `app/build.gradle.kts`).
- **Abstracted Schedule Parameter**:
  - `onScheduleTestAlarm: (title: String, content: String) -> Unit` passed into `OnboardingScreen` composable arguments.
- **Persistence**:
  - `PreferencesRepository` managing `booleanPreferencesKey("onboarding_completed")`.

## 4. Exposed Surface
- `Route.Onboarding` route in `Route.kt`.
- `PreferencesRepository` & `PreferencesViewModel`.
- `OnboardingScreen` composable with `onScheduleTestAlarm` argument.
- `OnboardingViewModel`.

## 5. Acceptance Criteria
- **AC-ONB-1**: `PreferencesRepository` stores and reads `ONBOARDING_COMPLETED` boolean accurately via Preferences DataStore.
- **AC-ONB-2**: `PreferencesViewModel` checks `isOnboardingCompleted` at app launch. If false, app starts at `Route.Onboarding`. If true, app starts at `Route.Home`.
- **AC-ONB-3**: `OnboardingViewModel` manages step state using `OnboardingStep` enum (`WELCOME`, `CHOOSE_CONTEXT`, `ADD_ITEMS`, `CONFIGURE_TIMING`, `CONFIRMATION_SUMMARY`, `INTERACTIVE_PACKING`, `COMPLETION_VICTORY`) and does not access `PreferencesRepository` directly.
- **AC-ONB-4**: Screen 1 (`WELCOME`) renders title, subtitle, illustration, "Get Started" CTA, and "Skip" CTA.
- **AC-ONB-5**: Tapping "Skip" on Screen 1 invokes `PreferencesViewModel.markOnboardingCompleted()` and navigates to `Route.Home`.
- **AC-ONB-6**: Screen 2 (`CHOOSE_CONTEXT`) allows user to enter destination context, with step 1 of 5 progress indicator.
- **AC-ONB-7**: Screen 3 (`ADD_ITEMS`) allows adding and removing checklist items, dynamically referencing chosen context.
- **AC-ONB-8**: Screen 4 (`CONFIGURE_TIMING`) allows configuring departure time and selecting "Now" test alert.
- **AC-ONB-9**: Screen 5 (`CONFIRMATION_SUMMARY`) renders confirmation summary card showing chosen context, times, and cargo item chips.
- **AC-ONB-10**: Tapping "Done ✓" on Screen 5 invokes the abstracted `onScheduleTestAlarm` parameter function with title and content properties, triggering an instant device alarm/notification and advancing to `INTERACTIVE_PACKING`.
- **AC-ONB-11**: Screen 6 (`INTERACTIVE_PACKING`) displays "Live Test Alert Received" banner, interactive checklist items, packing counter, and "Pack all" button.
- **AC-ONB-12**: Screen 7 (`COMPLETION_VICTORY`) displays victory message, verified inventory chips, and "Got it, let's roll ->" button.
- **AC-ONB-13**: Tapping "Got it, let's roll ->" or close button on Screen 7 triggers `PreferencesViewModel.markOnboardingCompleted()` and navigates to `Route.Home`, clearing Onboarding from the backstack.
