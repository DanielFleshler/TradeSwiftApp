# TradeSwift - Android Stock Trading App

TradeSwift is a modern Android stock trading application designed to provide users with real-time stock market data visualization and seamless trading capabilities.  This README provides a comprehensive overview of the app's features, technical details, and build instructions.

## Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Technical Details](#technical-details)
  - [Architecture & Components](#architecture--components)
  - [Dependencies](#dependencies)
  - [Key Features Implementation](#key-features-implementation)



## Features

- **Real-time Stock Charts:** Interactive line charts visualizing stock price movements throughout the trading day.
- **User Authentication:** Secure user authentication system using Firebase Authentication.
- **Account Management:** Personalized account dashboard with balance tracking.
- **Stock Portfolio:** Track and manage your stock investments.
- **Price Monitoring:** Real-time price updates with positive/negative change indicators.
- **Registration:** New user registration functionality.


## Screenshots

<div style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: center;">
  <img src="https://github.com/user-attachments/assets/b8932062-70fb-4909-86eb-97e64ff8c0c1" alt="Screenshot 1" width="300" />
  <img src="https://github.com/user-attachments/assets/0848a013-61e1-4755-997b-be1e754a310e" alt="Screenshot 2" width="300" />
  <img src="https://github.com/user-attachments/assets/49480fa0-ef7c-46ea-8860-ab5bea8bc273" alt="Screenshot 3" width="300" />
  <img src="https://github.com/user-attachments/assets/196dfecf-dcc0-4d55-a40b-413790a53233" alt="Screenshot 4" width="300" />
  <img src="https://github.com/user-attachments/assets/881ca1ae-fdb8-4b44-b849-1febddb05055" alt="Screenshot 5" width="300" />
  <img src="https://github.com/user-attachments/assets/53166668-8632-4784-bccb-0e9aaffaed9f" alt="Screenshot 6" width="300" />
</div>


## Technical Details

### Architecture & Components

- **Platform:** Android (Java)
- **Minimum SDK:** 24 (Android 7.0 Nougat) - *Updated based on app/build.gradle.kts*
- **Target SDK:** 34
- **Build System:** Gradle 8.7.2
- **Architecture:**  (Mention architecture patterns used, e.g., MVVM, MVP, MVI, if applicable)

### Dependencies

- **UI Components:**
    - AndroidX AppCompat v1.7.0
    - Material Design Components v1.12.0
    - ConstraintLayout v2.2.0
    - MPAndroidChart (for stock charts)
    - SwipeRefreshLayout
    - Navigation Component
    - Core SplashScreen

- **Backend Services:**
    - Firebase Authentication v23.1.0
    - Firebase Realtime Database v21.0.0
    - Gson
    - OkHttp

### Key Features Implementation

#### Stock Chart Visualization

- Custom implementation using MPAndroidChart library.
- Real-time price updates (describe how this is achieved).
- Interactive touch controls (zoom, drag).
- Dynamic color schemes based on price movement.
- Time-based X-axis formatting.
- Gradient backgrounds for visual appeal.

#### User Management

- Firebase Authentication integration (email/password).
- User profile management (name, email).
- Account balance tracking (Firebase Realtime Database).
- Registration functionality.

#### Data Fetching

- Integration with Yahoo Finance API (or specify the API used).
- Asynchronous data fetching using ExecutorService for improved performance.
- Data parsing using Gson.
