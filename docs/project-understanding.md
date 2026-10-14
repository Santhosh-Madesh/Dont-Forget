# Project Understanding — DONT FORGET

## 1. Product Goal & Problem Statement
**DONT FORGET** is a native Android checklist reminder app designed for users who want to ensure they do not leave behind necessary items when going out to a specific **Context** (e.g., College, Work, Office, Temple).

It reduces cognitive load before departure by:
1. Allowing users to configure reusable **Templates** (Context + Checklist items) or build custom Reminders from scratch.
2. Scheduling notifications at a customizable lead time before departure (default: **30 minutes before departure**).
3. Providing direct access to the Reminder Overview screen via notification clicks.
4. Allowing interactive packing (marking items Packed/Unpacked) and completing reminders.

---

## 2. Core Entities & Concepts

- **Context**: A place/destination (e.g., College, Work). Has a `Title` and a built-in Material Vector `Icon`.
- **Item**: A checklist item (e.g., Laptop, ID, Charger). Has a `Name` and a `Packed` status (`Packed` vs `Unpacked`).
- **Checklist**: An ordered list of Items (minimum 1 item required).
- **Template**: A reusable Context + Checklist configuration. A Context can have multiple Templates. Modifying a Reminder created from a Template does NOT affect the Template. Modifying/Deleting a Template does NOT affect existing Reminders created from it.
- **Reminder**: A scheduled checklist for a Context. Has:
  - Context (Title & Icon)
  - Checklist (List of Items)
  - Departure Date & Time
  - Notification Lead Time (minutes before departure, default = 30 mins)
  - Status (`Active` vs `Completed` or `Expired`)
  - Created Timestamp (for creation priority ordering)
  - Completed Timestamp (for 24-hour cleanup rule)

---

## 3. Key User Flows & Features

1. **Home Screen**:
   - Shows active Reminders.
   - Provides primary CTA to "+ New Reminder".
   - Access to Template Management.
2. **Create Reminder Flow**:
   - Step 1: Select an existing Template OR create Context (Name + selectable Material Vector Icon).
   - Step 2: Add/Modify Checklist Items (initial state: Unpacked, minimum 1 item).
   - Step 3: Set Departure Date & Departure Time (validated: cannot be in past).
   - Step 4: Set Notification Time (default: 30 mins before departure; customizable).
   - Step 5: Reminder Preview (allows editing details, option to save custom checklist as a new Template, cancel with confirmation, or confirm creation).
   - *Special Case*: If all items are packed during creation, prompt user if they still want a reminder notification or mark complete immediately.
3. **Reminder Overview & Management**:
   - View details (Context, Checklist, Departure Date/Time, Notification Time).
   - Interactive Item Packing: toggle individual items packed/unpacked, or "Pack All".
   - "Mark as Complete" CTA enabled when all items are packed.
   - Edit Reminder (Context, Checklist items/order, Departure time, Notification time).
   - Delete Reminder (with confirmation dialog).
   - Restore Completed Reminder: requires user to mark at least 1 item unpacked to make active again.
4. **Template Management**:
   - View all templates grouped or listed by Context.
   - Create new Template.
   - Edit Template (Context name, Checklist items, reorder items).
   - Delete Template (with confirmation dialog; does not touch existing reminders).
5. **Notifications & Deep Linking**:
   - Scheduled notification triggers at `Departure Time - Notification Lead Time`.
   - Notification CTA ("Check Checklist") opens app directly to the specific Reminder Overview screen.
   - If notification permission is disabled, present a warning banner/dialog (per `UI References/Create Reminder - Select departure and notification warning dialog Screen.png`) prompting the user to grant permission in System Settings.

---

## 4. Key Rules & Edge Cases

1. **Validation & Restrictions**:
   - A Reminder MUST have a Context and at least 1 Checklist Item.
   - Departure date & time CANNOT be in the past (time pickers restricted accordingly).
2. **Template Independence**:
   - Modifying/saving a Reminder never mutates its original Template unless explicitly saved as a new Template.
   - Context cannot have two templates with the same title or identical checklists.
3. **Ordering**:
   - Default checklist item order matches insertion order (re-ordering supported).
   - Reminders with identical departure date & time are notified based on creation priority.
4. **Completion & Auto-Deletion**:
   - Completing a Reminder cancels its scheduled notification.
   - On app launch, inactive/completed Reminders older than 24 hours are automatically purged from the database along with their scheduled notifications.
5. **Permissions & Icons**:
   - Built-in Material Vector Icons only (no user-uploaded custom images in v1).
   - Request `POST_NOTIFICATIONS` permission (Android 13+) and `SCHEDULE_EXACT_ALARM`.

---

## 5. Technical Architecture & Constraints

- **Language & Framework**: Kotlin 2.2.10, Jetpack Compose with Material 3.
- **Min SDK**: 24, **Target SDK**: 37, **Compile SDK**: 37.
- **UI Architecture**: Single Activity (`MainActivity`), Compose Navigation, MVVM pattern with `UiState`, `UiEvent`, and `UiSideEffect`.
- **Local Persistence**: Room Database (`androidx.room`) for persisting Reminders, Templates, Contexts, and Checklist Items.
- **Scheduling**: `AlarmManager` + `BroadcastReceiver` + `NotificationManager` for exact notification delivery.
- **Theme**: Light & Dark mode support according to design specs.

---

## 6. Confirmed Design Decisions (Owner Approved)

1. **Approved Dependencies**:
   - `androidx.navigation:navigation-compose`
   - `androidx.room:room-runtime`, `androidx.room:room-compiler`, `androidx.room:room-ktx`
   - `androidx.compose.material:material-icons-extended` (for built-in vector icons)
2. **Context Icons**:
   - Selectable built-in Material Vector icons (e.g. School, Work, Office, Place of Worship/Temple, Home, Gym, Shopping, Travel, Sports, Event). No custom image uploads.
3. **24-Hour Auto-Deletion**:
   - On app launch, check and delete inactive/completed reminders that have been inactive for >= 24 hours, along with canceling any associated scheduled notifications.
4. **Notification Permission Dialog/Banner**:
   - Warning banner/dialog displayed when notification permission is disabled, directing user to system settings (as shown in `Create Reminder - Select departure and notification warning dialog Screen.png`).
