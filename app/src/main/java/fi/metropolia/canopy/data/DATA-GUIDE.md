## The purpose of data package

This and **domain/model** are both the **M** of MVVM.
- Data acquisition and storage

---

## The purpose of the packages inside data:**

### data/location/
- Interfaces with Android location services
- Emits raw location data
- Adapts system APIs into domain-friendly formats

```
data/location/
├── LocationTracker.kt
└── LocationObserver.kt
```

### data/repository/
- Single source of truth for trip data
- Decides whether data comes from local storage, memory or network

```
data/repository/
└── TripRepository.kt
```

### data/source/
- Concrete data sources (Room, files, mock data etc.)

```
data/source/
└── LocalTripDataSource.kt
```

**IMPORTANT**
- No UI logic
- Can depend on Android APIs