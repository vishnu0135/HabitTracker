# Habit Tracker App 📅🔥

## Overview
The **Habit Tracker App** is an Android application designed to help users build and maintain good habits by tracking daily activities. The app provides reminders, daily check-ins, streak tracking, and detailed weekly and monthly analytics to motivate users and support personal growth.

---

## Features ✨

### 🏠 Home Dashboard
- Quick navigation to:
    - My Habits
    - Today’s Habits
    - Analytics
- Clean, premium UI with card-based navigation

### ➕ Habit Management
- Add new habits with:
    - Title & description
    - Category & priority
    - Reminder time
    - Frequency (Daily / Weekly / Custom)
    - Start date
- Edit existing habits
- Delete habits with confirmation dialog

### ⏰ Reminders & Notifications
- Scheduled habit reminders using AlarmManager
- Supports exact alarms for Android 12+
- Notifications persist across device reboot

### ✅ Daily Habit Check-in
- Dedicated **Today’s Habits** screen
- Users can mark habits as:
    - Completed
    - Missed
- Check-in enabled only after habit start time
- Automatic handling of pending and missed habits

### 🔥 Streak Tracking
- Daily streak calculation for each habit
- Visual streak badges shown across the app

### 📊 Analytics
- Weekly analytics with bar and pie charts
- Monthly analytics with pie charts
- Habit-specific analytics
- Visual summaries of completed vs missed habits

### 👤 User Profile
- Displays user name, age, and email
- Data stored using SharedPreferences
- Secure logout option with confirmation

### ℹ️ About & Contact
- About Us screen explaining app purpose
- Developer information
- Contact email with direct email intent

---

## Technology Stack 🛠️

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM
- **Local Database:** Room
- **Asynchronous Operations:** Kotlin Coroutines
- **Charts & Analytics:** MPAndroidChart
- **Preferences:** SharedPreferences
- **Notifications:** AlarmManager & NotificationManager

---

## Data Persistence Strategy 💾

- **Room Database**
    - Stores habits and habit completion records
    - Enables offline-first functionality
- **SharedPreferences**
    - Stores user profile data
    - Manages login/session state

---

## Hardware & System Features ⚙️

- System notifications for habit reminders
- Exact alarm scheduling for reliable reminders
- Rescheduling reminders after device reboot
- Minimal background processing for battery efficiency

---

## Installation & Setup 🚀

1. Clone the repository:
   ```bash
   git clone <repository-url>
2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical Android device (Android 8.0+ recommended)