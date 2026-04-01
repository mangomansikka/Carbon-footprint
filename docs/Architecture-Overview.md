# Canopy - Architecture Overview

## Project Overview
Canopy is a Kotlin-based Android application designed to track and visualize commuting-related carbon emissions. The app uses automatic location tracking and manual input to calculate emissions from various transportation modes, presenting the collective impact through a shared virtual tree visualization.

## Architecture Pattern
The application follows a **Clean Architecture** approach with MVVM (Model-View-ViewModel) pattern, organized into the following layers:

### 1. Presentation Layer (UI)
- **Framework**: Jetpack Compose for declarative UI
- **Navigation**: Navigation Compose for screen transitions
- **Theme**: Custom Material 3 theme (`CanopyMinnoTheme`)

#### Screens
- **LandingScreen**: Welcome/dashboard screen
- **HomeScreen**: Main carbon footprint visualization with charts
- **LocationScreen**: Trip tracking controls
- **ManualInputScreen**: Manual trip entry
- **OverviewScreen**: Emissions summary and statistics

#### ViewModels
- **TripViewModel**: Manages trip data and emissions calculations
- **GraphViewModel**: Handles chart data for visualizations

### 2. Domain Layer
- **TrackingState**: Global state object managing tracking session data
  - Location coordinates, speed, distance
  - Transport mode detection and emissions
  - Activity recognition confidence levels

### 3. Data Layer
#### Repository
- **TripRepository**: Handles data operations between domain and data source
  - Saves trip summaries with emissions calculations
  - Retrieves historical trip data and emissions by mode/month
  - Supports manual trip entry

#### Data Source
- **CanopyDatabase**: Room database for local persistence
  - **LocationEntity**: Stores location points with transport modes and emissions
  - **LocationDAO**: Data access object for database operations
  - **EmissionsSummary**: Aggregated emissions data by transport type
  - **MonthlyEmission**: Time-based emissions aggregation

### 4. Service Layer
#### Background Services
- **TrackingService**: Foreground service for continuous location tracking
  - Uses Google Play Services FusedLocationProviderClient
  - Integrates with ActivityRecognitionManager for transport mode detection
  - Processes location updates and calculates real-time emissions
  - Persists tracking data to local database

- **ActivityRecognitionReceiver**: Broadcast receiver for activity recognition updates

### 5. Utilities
- **CarbonHelper**: Emission calculation engine
  - Emission factors for different transport modes (bus, train, car, etc.)
  - Distance-based CO2 calculation in kg/km

- **ActivityRecognitionManager**: Manages Google Play Services activity recognition
  - Detects transport modes (walking, vehicle, still, etc.)
  - Updates TrackingState with confidence levels

- **PermissionManager**: Handles runtime permissions for location and activity recognition

## Key Technologies & Libraries

### Core Android
- **Kotlin**: Primary programming language
- **Jetpack Compose**: UI framework
- **Room**: Local database persistence
- **ViewModel**: Lifecycle-aware state management
- **Navigation Compose**: Screen navigation

### Google Play Services
- **Location Services**: GPS and location updates
- **Activity Recognition**: Transport mode detection
- **Maps**: Geographic visualization (integrated)

### Permissions & Services
- Location permissions (fine, coarse, background)
- Activity recognition permission
- Foreground service for continuous tracking
- Notification permissions

## Data Flow

1. **Trip Initiation**: User starts tracking via LocationScreen
2. **Location Updates**: TrackingService receives GPS updates every 2 seconds
3. **Activity Recognition**: ActivityRecognitionManager detects transport mode
4. **Emission Calculation**: CarbonHelper calculates CO2 based on distance and mode
5. **State Updates**: TrackingState accumulates distance and emissions
6. **Persistence**: Location data saved to Room database via TripRepository
7. **Visualization**: ViewModels provide data to Compose UI for charts and summaries

## Database Schema

### LocationEntity Table
- Primary key: auto-generated ID
- Location: latitude, longitude, timestamp
- Transport modes: comma-separated string
- Emissions: total grams + mode-specific kg values
- Distances: walking/cycling meters

## App Components

### Main Activity
- **CanopyActivity**: Single activity with Compose navigation
- Hosts the main navigation graph with bottom navigation bar
- Central floating action button for quick trip start

### Manifest Configuration
- Internet, location, and activity recognition permissions
- Foreground service declaration
- Activity recognition receiver registration

## Build Configuration
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Kotlin JVM Target**: 11
- **Compose Enabled**: Yes

