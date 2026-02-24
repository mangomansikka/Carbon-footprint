## The purpose of domain package

- Core business logic

---

## The purpose of the packages inside domain:

### domain/model/
- This and data both are the **M** of MVVM
- Pure data models (trip, emissions etc.)
- Concepts, not database tables
- No Android imports

```
domain/model/
├── Trip.kt
└── Emissions.kt
```

### domain/tracker/
- Example package
- Loqic for accumulating trip data
- Calculates distance, speed time
- Does not know about UI or persistence
- Why not in ViewModel? Because logic may be reused, it can be unit-tested and it's not UI-specific
```
domain/tracker/
└── TripAccumulator.kt
```

### domain/calculator/
- Example package
- Emissions calculation loqic
- Converts distance + transport mode into CO2 values
```
domain/calculator/
└── EmissionsCalculator.kt
```


**IMPORTANT**
- Must be testable without Android
- Contains the "brain" of the app