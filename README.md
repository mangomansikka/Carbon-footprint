# Canopy

Canopy is a communal system for visualizing commuting-related emissions.
The application uses automatic and manual commuting data to display
collective impact through a shared virtual tree.

## Project Context
This project is developed as part of a Metropolia University course 
and is still heavily under development.

---

### Example project architecture

```
┌─────────────┐
│     UI      │  ← Compose, visuals, buttons
├─────────────┤
│ ViewModel   │  ← UI state & coordination
├─────────────┤
│   Domain    │  ← Rules, calculations, meaning
├─────────────┤
│    Data     │  ← GPS, database, network
└─────────────┘
```

**Each layer only talks downward.**

---

### Package structure with files:

```
fi.metropolia.canopy
│
├── ui/
│   ├── screen/
│   │   ├── LocationScreen.kt
│   │   └── TreeScreen.kt
│   ├── components/
│   │   └── CommonButtons.kt
│   └── theme/
│       └── CanopyMinnoTheme.kt
│
├── viewmodel/
│   └── TripViewModel.kt
│
├── domain/
│   ├── model/
│   │   ├── Trip.kt
│   │   └── Emissions.kt
│   ├── tracker/
│   │   └── TripAccumulator.kt
│   └── calculator/
│       └── EmissionsCalculator.kt
│
├── data/
│   ├── location/
│   │   ├── LocationTracker.kt
│   │   └── LocationObserver.kt
│   ├── repository/
│   │   └── TripRepository.kt
│   └── source/
│       └── LocalTripDataSource.kt
│
├── util/
│   └── DistanceUtils.kt
│
└── MainActivity.kt
```