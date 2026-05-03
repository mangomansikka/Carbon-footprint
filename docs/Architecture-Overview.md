# Canopy - Architecture Overview

## Project Overview
Canopy is a Kotlin-based Android application for tracking and visualizing commuting-related carbon emissions. The app supports automatic tracking, manual trip entry, emission summaries, monthly statistics, CSV export, and a shared virtual tree visualization.

## Architecture Style
The project uses a **layered MVVM architecture** with a single-activity Compose UI. The codebase is organized into presentation, domain/state, data, service, and utility layers.

---

## 1. Presentation Layer (UI)

### UI Framework
- **Jetpack Compose** for declarative UI
- **Material 3** for theming and components
- **Navigation Compose** for screen navigation
- **Single-activity architecture** with `CanopyActivity`

### Main Activity and Navigation
- **`CanopyActivity`** hosts the Compose navigation graph and bottom navigation bar.
- The app has a **center floating action button** that opens tracking.
- Current routes in the navigation graph:
  - `landingScreen`
  - `locationScreen`
  - `overviewScreen`
  - `homeScreen`
  - `ecoScreen`
  - `manualScreen`

### Screens
- **`LandingScreen`**  
  Start screen / landing page.

- **`LocationScreen`**  
  Live tracking screen where the user can start and stop trip tracking and open manual entry.

- **`ManualInputScreen`**  
  Manual trip entry screen. This screen still exists in the navigation graph, but it is not part of the bottom bar.

- **`OverviewScreen`**  
  Emission summary screen with donut chart, mode breakdown, total emissions, and CSV export.

- **`HomeScreen`**  
  Footprint statistics screen with yearly footprint summary, monthly graph, and calendar view.

- **`TreeScreen`**  
  Shared virtual tree visualization / eco view.

### ViewModels
- **`TripViewModel`**
  - Manages trip tracking state
  - Loads emissions by mode
  - Supports manual trip saving
  - Ends trips and triggers persistence
  - Exports data to CSV and locks current data after export

- **`GraphViewModel`**
  - Loads monthly emissions
  - Computes total yearly emissions
  - Calculates month-to-month percentage change
  - Loads calendar/day-based trip data
  - Supports deleting trips from a selected date range

---

## 2. Domain Layer

### Tracking State
- **`TrackingState`** is a global session state object used during active tracking.
- It stores:
  - last known coordinates
  - current speed
  - trip start and end coordinates
  - accumulated distance per mode
  - accumulated emissions per mode
  - used transport modes
  - tracking status

### Domain Responsibilities
- Accumulate trip metrics while tracking is active
- Share state between the service layer, repository layer, and UI layer
- Keep the current trip session available until it is saved to the database

---

## 3. Data Layer

### Repository Layer
- **`TripRepository`**
  - Saves trip summaries
  - Saves manual trips
  - Reads aggregated emissions by mode
  - Reads monthly emissions
  - Provides trip lists and date-filtered locations
  - Supports deleting trips
  - Supports locking all current data

- **`UserRepository`**
  - Handles user-related data and preferences
  - Used by export flow for role-aware CSV generation

### Room Database
- **`CanopyDatabase`** provides local persistence.

### DAO Layer
- **`LocationDAO`**
  - Aggregated emission queries
  - Monthly emissions query
  - Date-range trip query
  - Day-with-data query
  - Locking support

- **`UserDAO`**
  - User-related persistence

### Entities and Data Models
- **`LocationEntity`**
  - Latitude / longitude
  - Start and end coordinates
  - Transport mode string
  - Total emissions in grams
  - Mode-specific emission totals
  - Walking and cycling distances
  - Timestamp

- **`UserEntity`**
  - Stores user profile or role information

- **`EmissionsSummary`**
  - Aggregated emissions by transport type

- **`MonthlyEmission`**
  - Monthly aggregated emissions data

---

## 4. Service Layer

### Background Tracking
- **`TrackingService`**
  - Foreground service for continuous tracking
  - Uses `FusedLocationProviderClient`
  - Uses `ActivityRecognitionManager`
  - Starts and stops via service actions:
    - `ACTION_START`
    - `ACTION_STOP`
  - Requests location updates at a 2000 ms interval
  - Calculates emissions in real time
  - Persists trip data to the database

### Activity Recognition
- **`ActivityRecognitionReceiver`**
  - Receives transport/activity updates from Google Play Services

---

## 5. Utilities

- **`CarbonHelper`**
  - Calculates emissions from distance and transport mode
  - Uses mode-specific emission factors
  - Metro currently has an emission factor of `0.0`

- **`ActivityRecognitionManager`**
  - Starts and stops activity recognition requests
  - Uses a 2000 ms update interval

- **`PermissionManager`**
  - Handles runtime permission checks
  - Required permissions include:
    - `ACCESS_FINE_LOCATION`
    - `ACTIVITY_RECOGNITION`

- **`ExportUtils`**
  - Generates CSV exports
  - Saves CSV files
  - Launches share/email intents
  - Supports campus assignment logic for export output

- **`CampusResolver`**
  - Resolves trip coordinates to campus names
  - Used by manual trip saving and export-related logic

---

## 6. Key Technologies & Libraries

### Core Android
- **Kotlin**: Primary language
- **Jetpack Compose**: UI layer
- **Room**: Local database persistence
- **ViewModel**: Lifecycle-aware UI state management
- **Navigation Compose**: Screen navigation
- **KSP**: Room compiler integration

### Google Play Services
- **Location Services**: GPS/location tracking
- **Activity Recognition**: Transport mode detection
- **Maps**: Included as a dependency in Gradle

### Permissions and Services
- Fine location permission
- Activity recognition permission
- Foreground service for tracking
- Notification permission on newer Android versions

---

## 7. Data Flow

1. The user opens the tracking screen from the center action button in `CanopyActivity`.
2. `LocationScreen` requests the required permissions.
3. If permissions are granted, `TripViewModel.startTracking()` starts `TrackingService`.
4. `TrackingService` receives location updates and activity recognition updates.
5. `CarbonHelper` calculates emissions based on transport mode and distance.
6. `TrackingState` accumulates the current trip session data.
7. When the trip ends, `TripViewModel.endTrip()` saves a summary to the database.
8. `TripRepository` persists the trip through `LocationDAO`.
9. `OverviewScreen` and `HomeScreen` load aggregated data through their view models.
10. `ExportUtils` can generate a CSV export from stored trips.

---

## 8. Database Schema

### `LocationEntity`
Stores trip-level and summary-level commuting data:
- coordinates
- start/end coordinates
- transport modes
- emissions in grams and kilograms
- walking and cycling distances
- timestamp

### `UserEntity`
Stores user-related information such as role or preferences.

### Aggregation Queries
The database supports:
- emissions by transport mode
- emissions by month
- all trip records
- records by date range
- days containing data
- locked-data checks

---

## 9. App Components

### Main Activity
- **`CanopyActivity`**
  - Single activity host
  - Sets up `CanopyMinnoTheme`
  - Contains the navigation graph
  - Hosts the bottom bar and center tracking button

### Bottom Bar Navigation
The current bottom bar contains:
- Home
- Eco / Tree
- Overview
- Stats / Graphs

The center floating action button opens the tracking screen.

### Export Flow
- CSV export is triggered from `OverviewScreen`
- After export, the current data is locked

---

## 10. Build Configuration

### Android Configuration
- **Namespace**: `fi.metropolia.canopy`
- **Application ID**: `fi.metropolia.canopy`
- **Min SDK**: 26
- **Compile SDK**: 36
- **Target SDK**: 35
- **Kotlin JVM Target**: 11
- **Compose Enabled**: Yes

### Dependencies
- Jetpack Compose
- Navigation Compose
- Room
- Google Play Services Location
- Google Play Services Activity Recognition
- Google Play Services Maps
- KSP for Room compiler
