# Canopy 🌱

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=SQLite&logoColor=white)

Canopy is a personal system for visualizing commuting-related emissions. The application uses automatic and manual commuting data to display personal impact through a virtual tree.

---

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Status](#project-status)
- [Quick Start](#quick-start)
- [Installation](#installation)
- [Usage](#usage)
- [Documentation](#documentation)
- [Important Notes](#important-notes)
- [License](#license)

---

## Features

- **Automatic Location Detection** - Start automatic location tracking with a single button press
- **Distance Calculation** - Real-time calculation of commuting distance in meters
- **Transport Mode Detection** - Intelligent detection of transportation type (vehicle, walking, cycling)
- **Carbon Footprint Tracking** - Visual representation of your environmental impact
- **Personal Virtual Tree** - Personal visualization of your emissions
- **Interactive Dashboard** - Greenish UI with charts and statistics about your carbon footprint
- **Data exportation** - Exporting your data to a CSV file

---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| **Kotlin** | Primary language |
| **Android** | Mobile platform |
| **SQLite** | Local database |
| **Jetpack Compose** | UI framework |
| **Room ORM** | Database abstraction |
| **Activity Recognition API** | Transport mode detection |

---

## Project Status

This project is developed as part of Metropolia's multidisciplinary innovation project course and is **actively under development**.

---

## Quick Start

### Prerequisites

- Android Studio Panda 1 | 2025.3.1 or later
- Android SDK 28 or higher
- Android phone with Android 9.0+

### Installation

**Clone the repository:**
```bash
git clone https://github.com/mangomansikka/Carbon-footprint.git
cd Carbon-footprint
```

**Build and run the app:**
1. Open the project in Android Studio.
2. Connect an Android device or start an emulator.
3. Click the green "Run" button to build and install the app.

---

### Usage
1.   Start Tracking - Tap the location tracking button to begin automatic detection
2.   View Statistics - Check your carbon footprint on the dashboard
3.   Submit Data - Manual entry option available for commutes
4.   See Your Impact - View your personal tree visualization
5.   Edit Data - Delete wrong data through the calendar
6.   Export Data - Export your data to a CSV file

---

## Documentation
- [User Manual](docs/user-manual.md)
- [Architecture Overview](docs/Architecture-Overview.md)

---

## Important Notes

Throughout the process, an algorithm was being developed to improve location data and travel mode accuracy. Unfortunately, the project ran out of time and is being left as a "work-in-process". The algorithm should and will be developed once the project continues again. Find the algorithm in branch "Algorithm-WIP".

---

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
