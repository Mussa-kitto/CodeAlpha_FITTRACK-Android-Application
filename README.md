# FITTRACK: Native Android Fitness Tracking Platform
### Software Engineering & Architecture Documentation

FITTRACK is an enterprise-grade native Android fitness tracking application designed to provide reliable, offline-first personal activity telemetry, goal tracking, analytics, and workout management. Built entirely in Java using AndroidX Material 3 components and the Room Persistence Library, FITTRACK implements standard MVVM architecture to deliver a high-performance, accessible, and privacy-focused user experience.

---

## Technical Specifications & Architecture

### Core Stack
* **Target SDK**: Android 34 / 35 (Minimum SDK 24 / Android 7.0)
* **Programming Language**: Java 11
* **UI Architecture**: Material Design 3 (AndroidX), ConstraintLayout, Custom Canvas Views
* **Persistence Layer**: Room Database (`fitness_tracker_db`), SharedPreferences
* **Architecture Pattern**: Model-View-ViewModel (MVVM) + Repository Pattern
* **Concurrency & Async**: Java ExecutorService & LiveData Observers

### Architecture Diagram
```
[ UI Layer: Activities & Fragments ]
                 │
          (LiveData Observers)
                 ▼
[ ViewModel Layer: FitnessViewModel ]
                 │
                 ▼
[ Data Layer: FitnessRepository ]
        ├───► Room Database (FitnessActivityDao)
        └───► Local Preferences (GoalPreferences, ThemePreferences, UserProfilePreferences)
```

---

## Functional Requirements & Module Specifications

### 1. Dashboard & Real-Time Telemetry
* **Activity Telemetry**: Calculates daily steps, active duration, calories burned (kcal), and distance.
* **Goal Progress Monitoring**: Visual linear and circular progress indicators tracking completion towards configured daily step, calorie, and active time targets.
* **Weekly Bar Chart Component**: Custom Canvas-rendered bar chart supporting step, calorie, and duration telemetry with touch tooltips and automated axis label scaling.
* **Recent Activity Log**: Chronological view of recent workouts with action menus for editing and deletion.

### 2. Analytics & Trend Analysis
* **Time Range Filtering**: Telemetry aggregation across 7-day, 30-day, and 90-day periods.
* **Streak & Active Days Calculation**: Automated streak analysis derived directly from local database records.
* **Multi-Metric Trend Visualization**: Individual steps and calorie burn bar charts with non-overlapping date interval markers.

### 3. Workout Management & Manual Entry
* **Categorized Workout Engine**: Support for Outdoor Running, Walking, Cycling, Gym & Strength, Yoga, and HIIT.
* **Validation Subsystem**: Positive constraints on duration, calories, and step metrics.
* **CRUD Subsystem**: Create, Read, Update, and Delete operations executed asynchronously on background threads.

### 4. Milestones & Achievements System
* **Dynamic Milestone Evaluator**: Real-time evaluation of database records against key milestones:
  * First Activity Completion
  * Cumulative 1,000 Calories Burned
  * 10,000 Single-Day Step Target
  * 7-Day Active Streak
  * Cumulative 5.0 Kilometer Distance Target
* **Progress Tracking**: Tracks percentage completion toward locked and unlocked milestones.

### 5. Profile Management & System Preferences
* **Profile Configuration**: User name, fitness motto/goal, and local circular avatar image management.
* **Unit System Selection**: Configurable Metric (kilometers) and Imperial (miles) distance calculations.
* **Theme System**: Full support for Light, Dark, and System Default themes utilizing `AppCompatDelegate`.
* **Data Privacy & Reset**: Local data wipe functionality with confirmation dialogs.

---

## Directory Structure

```
com.example.fitnesstrackerapp/
├── SplashActivity.java                       # Launch Screen
├── MainActivity.java                         # Primary Shell Activity
├── data/
│   ├── entity/FitnessActivity.java          # Room Entity Definition
│   ├── dao/FitnessActivityDao.java           # Room Data Access Object
│   ├── database/AppDatabase.java              # Database Singleton
│   └── repository/FitnessRepository.java     # Repository Implementation
├── ui/
│   ├── dashboard/DashboardFragment.java      # Dashboard View
│   ├── progress/StatisticsFragment.java     # Telemetry & Trends View
│   ├── workouts/WorkoutsFragment.java       # Workout Categories View
│   ├── achievements/
│   │   ├── AchievementsFragment.java        # Milestones View
│   │   └── AchievementModel.java            # Milestone Model
│   ├── profile/ProfileFragment.java         # User Settings & Profile View
│   ├── addactivity/AddEditActivity.java     # Workout Entry Form
│   ├── details/ActivityDetailActivity.java    # Workout Details Activity
│   ├── adapters/
│   │   ├── ActivityAdapter.java             # Activity RecyclerView Adapter
│   │   ├── HistoryAdapter.java              # Timeline Section Adapter
│   │   ├── HistoryListItem.java             # Timeline Wrapper
│   │   └── AchievementAdapter.java          # Milestones Adapter
│   └── views/BarChartView.java              # Custom Canvas Chart
├── viewmodel/FitnessViewModel.java          # LiveData ViewModel
└── utils/
    ├── DateUtils.java                       # Date & ISO Helper
    ├── GoalPreferences.java                # Goal Preferences
    ├── ThemePreferences.java                # Theme Preferences
    └── UserProfilePreferences.java          # Profile & Unit Preferences
```

---

## Database Schema Specification

### Entity: `fitness_activities`
* `id` (INTEGER, Primary Key, Auto-Increment)
* `exerciseType` (TEXT, Non-Null)
* `durationMinutes` (INTEGER, Non-Null, > 0)
* `caloriesBurned` (INTEGER, Non-Null, >= 0)
* `steps` (INTEGER, Non-Null, >= 0)
* `date` (TEXT, Non-Null, ISO format `YYYY-MM-DD`)
* `timestamp` (INTEGER, Non-Null)
* `notes` (TEXT, Nullable)
* `createdAt` (INTEGER, Non-Null)

---

## System Color Palette Specification

| Component / Role | Color Code | Hex Value |
| :--- | :--- | :--- |
| **Primary Forest Green** | `@color/primary` | `#123B2A` |
| **Secondary Emerald** | `@color/secondary` | `#20A36A` |
| **Accent Lime** | `@color/accent` | `#C7F36B` |
| **Background Off-White** | `@color/background` | `#F6F8F4` |
| **Surface White** | `@color/surface` | `#FFFFFF` |
| **Calories Amber** | `@color/color_calories` | `#F59E0B` |
| **Workout Blue** | `@color/color_workout` | `#2563EB` |
| **Distance Sky Blue** | `@color/color_distance` | `#0284C7` |

---

## Installation & Deployment

### Build Environment Requirements
* Android Studio Ladybug / Jellyfish / 2024.1 or higher
* Java Development Kit (JDK 11+)
* Android SDK Platform 34 or 35

### Build Commands
```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/FITTRACK.git
cd FITTRACK

# Assemble Debug APK
./gradlew assembleDebug
```

---

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
