## The purpose of UI package

The **V** of MVVM. Only layer that knows about Compose.

- Contains Jetpack Compose views and UI components
- No business logic
- Observes state from ViewModels
- Responsible only for layout and user interaction

**IMPORTANT**
- UI must not access services directly
- Does not calculate, store or decide
- **If it thinks, it's in the wrong place**

For example:

```
ui/
├── screen/
│   ├── LocationScreen.kt
│   ├── TreeScreen.kt
│   └── SettingsScreen.kt
│
├── components/
│   ├── StartStopButton.kt
│   └── StatCard.kt
│
└── theme/
└── CanopyMinnoTheme.kt

```