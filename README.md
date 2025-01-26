# TradeSwift

TradeSwift is a modern Android stock trading application that provides real-time stock market data visualization and trading capabilities.

## Features

- **Real-time Stock Charts**: Interactive line charts showing stock price movements throughout the trading day
- **User Authentication**: Secure user authentication system using Firebase Auth
- **Account Management**: Personal account dashboard with balance tracking
- **Stock Portfolio**: Track and manage your stock investments
- **Price Monitoring**: Real-time price updates with positive/negative change indicators

## Technical Details

### Architecture & Components

- **Platform**: Android (Java)
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: Latest
- **Build System**: Gradle 8.7.2

### Dependencies

- **UI Components**:
  - AndroidX AppCompat v1.7.0
  - Material Design Components v1.12.0
  - ConstraintLayout v2.2.0
  - MPAndroidChart (for stock charts)

- **Backend Services**:
  - Firebase Authentication v23.1.0
  - Firebase Realtime Database v21.0.0

- **Testing**:
  - JUnit 4.13.2
  - Espresso 3.6.1

### Key Features Implementation

#### Stock Chart Visualization
- Custom implementation using MPAndroidChart library
- Real-time price updates
- Interactive touch controls (zoom, drag)
- Dynamic color schemes based on price movement
- Time-based X-axis formatting
- Gradient backgrounds for visual appeal

#### User Management
- Firebase Authentication integration
- User profile management
- Account balance tracking
- Email-based authentication

## Project Structure 
