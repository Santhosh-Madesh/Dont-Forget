---
name: jetpack-compose-accessibility
description: Accessibility best practices, standards, and rules for Android Jetpack Compose including icons/images, keyboard navigation, typography scaling (sp), headings, switches/toggles with stateDescription, custom clickables, button TalkBack semantics, and element grouping with mergeDescendants and clearAndSetSemantics.
---

# Jetpack Compose Accessibility Skill Guidelines

Comprehensive accessibility rules, standards, and practices for Android Jetpack Compose applications.

---

## 1. Images & Icons Accessibility

- **Meaningful Images & Icons**: Always provide a clear, localized `contentDescription` (via string resources) for icons and images that convey actionable meaning or context (e.g., Back buttons, Add/Remove actions, Close buttons).
- **Decorative Images & Icons**: Always set `contentDescription = null` for purely decorative visual elements (e.g., background graphics, decorative vector shapes, status icons sitting alongside clear text labels).

```kotlin
// Meaningful Icon
Icon(
    imageVector = Icons.Default.Add,
    contentDescription = stringResource(R.string.cd_add_item)
)

// Decorative Icon
Icon(
    imageVector = Icons.Default.CheckCircle,
    contentDescription = null
)
```

---

## 2. Keyboard Accessibility for Input Fields

- Always configure appropriate `KeyboardOptions` (e.g., `imeAction = ImeAction.Next`, `ImeAction.Done`, `keyboardType = KeyboardType.Text`) and `KeyboardActions` on `OutlinedTextField` / `TextField` composables.
- Ensure pressing keyboard action keys (like Enter / Done / Next) triggers the expected action or moves focus to the next field seamlessly without requiring touch interaction.

```kotlin
OutlinedTextField(
    value = inputText,
    onValueChange = onInputChanged,
    singleLine = true,
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions = KeyboardActions(
        onDone = { if (inputText.isNotBlank()) onAddItem() }
    )
)
```

---

## 3. Typography & Theme Text Sizing (`sp`)

- **Rule**: All font sizes in custom themes, typography styles, and composable `Text` elements MUST be defined using `sp` (Scale-independent Pixels).
- **Prohibition**: NEVER hardcode text sizes using `dp` or raw numbers. Using `sp` guarantees that system font accessibility scaling (user font size preferences in Android Settings) is respected.

```kotlin
Text(
    text = stringResource(R.string.title),
    fontSize = 16.sp, // Always use sp for typography
    fontWeight = FontWeight.Bold
)
```

---

## 4. Section & Screen Headings (`heading()`)

- Mark major screen titles, step headers, dialog headers, and section labels with `modifier = Modifier.semantics { heading() }`.
- This enables screen reader users (TalkBack) to navigate efficiently through the UI by jumping between structural headings.

```kotlin
Text(
    text = stringResource(R.string.section_title),
    style = MaterialTheme.typography.headlineLarge,
    modifier = Modifier.semantics { heading() }
)
```

---

## 5. Switches, Checkboxes & Toggleables (`Role.Switch`, `Role.Checkbox` & `stateDescription`)

- For switch, checkbox, or toggleable components, assign `role = Role.Switch` or `Role.Checkbox` on the parent container semantics.
- Use **`stateDescription`** (instead of `contentDescription`) to communicate dynamic state (e.g., *"Packed"*, *"Not packed"*, *"Enabled"*, *"Disabled"*).
- Combine `toggleableState` and `stateDescription` so TalkBack announces state changes natively:

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .semantics(mergeDescendants = true) {
            role = Role.Checkbox
            toggleableState = if (isPacked) ToggleableState.On else ToggleableState.Off
            stateDescription = if (isPacked) packedStateCd else unpackedStateCd
        }
        .clickable { onTogglePacked(item) }
) { ... }
```

---

## 6. Custom Clickable Components

- For custom interactive components (e.g., clickable `Card`, `Row`, `Box`, or custom layout buttons), explicitly declare `role = Role.Button` in `semantics` or use `Modifier.clickable(role = Role.Button, onClickLabel = ...)` along with an appropriate accessibility description.

```kotlin
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .semantics {
            role = Role.Button
            contentDescription = actionTextCd
        }
        .clickable { onAction() }
) { ... }
```

---

## 7. Button TalkBack Semantics & Redundancy Prevention

- **Plain-Text Buttons**: Material 3 `Button` and `TextButton` composables natively supply `Role.Button` and merge child `Text` content. Omit redundant `.semantics { role = Role.Button; contentDescription = text }` on plain-text buttons.
- **Buttons with Emojis or Symbols (`✓`, `->`)**: Override semantics using `clearAndSetSemantics` to prevent TalkBack from reading symbols literally:

```kotlin
Button(
    onClick = onNext,
    modifier = Modifier.clearAndSetSemantics {
        role = Role.Button
        contentDescription = nextCleanCd
    }
) {
    Text(text = "Next ->")
}
```

---

## 8. Grouping Elements with `mergeDescendants` & `clearAndSetSemantics`

- **Grouping Related Elements (`mergeDescendants = true`)**: Use `semantics(mergeDescendants = true)` on container layouts when multiple related text/badge elements (e.g., title header + value badge) should be announced together as a single logical unit.
- **Overriding Child Subtrees (`clearAndSetSemantics`)**: Use `clearAndSetSemantics` when a custom unified sentence is specified on a container, completely replacing child text nodes to prevent duplicate announcements.

```kotlin
// Grouping related nodes
Column(
    modifier = Modifier.semantics(mergeDescendants = true) {}
) {
    Text(text = "Status:")
    Text(text = "Active")
}

// Overriding child text completely to prevent repetition
Column(
    modifier = Modifier.clearAndSetSemantics {
        contentDescription = customFullSentenceCd
    }
) { ... }
```
