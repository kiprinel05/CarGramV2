# CarGram 🚗📸

**Instagram for Cars - Share Your Automotive Passion**

CarGram is a modern Android social media application designed specifically for car enthusiasts. Built with Jetpack Compose and following Material Design 3 principles, it provides a platform where users can share photos of their vehicles, discover other car enthusiasts, and build a community around automotive passion.

## 🎯 Project Vision

CarGram aims to be the go-to social platform for car enthusiasts, offering a specialized experience that combines the best features of Instagram with automotive-specific functionality. Whether you're a classic car collector, a modern sports car owner, or simply passionate about automobiles, CarGram provides the perfect space to showcase your vehicles and connect with like-minded enthusiasts.

## ✨ Key Features

### 🔐 Authentication & User Management
- **Secure Authentication**: Firebase Authentication with email/password
- **User Registration**: Complete registration flow with username creation
- **Profile Management**: Customizable user profiles with profile pictures
- **Dark Mode Support**: Toggle between light and dark themes

### 🚗 Vehicle Profile System
- **VIN Decoder Integration**: Automatic vehicle information extraction using VIN numbers
- **Manual Vehicle Entry**: Option to manually input vehicle specifications
- **Vehicle Details**: Store and display comprehensive vehicle information including:
  - Brand, Model, Year, Trim
  - Engine specifications (HP, displacement)
  - Transmission type
  - Fuel type
  - VIN number

### 📱 Social Media Features
- **Photo Sharing**: Upload and share high-quality photos of your vehicles
- **Feed System**: Scroll through posts from the community
- **Like System**: Like and interact with other users' posts
- **Favorites**: Save posts to your favorites collection
- **Sorting Options**: Sort posts by timeline or most liked
- **Tab Navigation**: Switch between "For You" and "Favorites" feeds

### 🎨 Modern UI/UX
- **Material Design 3**: Latest Material Design principles
- **Jetpack Compose**: Modern declarative UI framework
- **Responsive Design**: Optimized for various screen sizes
- **Smooth Animations**: Fluid transitions and micro-interactions
- **Custom Backgrounds**: Dynamic backgrounds with dark mode support

### 🏗️ Architecture & Technology
- **MVVM Architecture**: Clean separation of concerns
- **Repository Pattern**: Centralized data management
- **Dependency Injection**: Hilt for dependency management
- **Local Database**: Room database for offline data persistence
- **Firebase Integration**: Authentication, Firestore, and Storage
- **Kotlin Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management

## 🛠️ Technical Stack

### Frontend
- **Jetpack Compose**: Modern Android UI toolkit
- **Material Design 3**: Latest design system
- **Navigation Compose**: Type-safe navigation
- **Coil**: Image loading and caching
- **Hilt Navigation**: Dependency injection for navigation

### Backend & Data
- **Firebase Authentication**: User authentication
- **Firebase Firestore**: Cloud database
- **Firebase Storage**: File storage
- **Room Database**: Local data persistence
- **DataStore**: Preferences storage

### Architecture
- **MVVM Pattern**: Model-View-ViewModel
- **Repository Pattern**: Data abstraction layer
- **Use Case Pattern**: Business logic encapsulation
- **Dependency Injection**: Hilt framework

### Development Tools
- **Kotlin**: Primary programming language
- **Gradle**: Build system
- **Android Studio**: IDE
- **Git**: Version control

## 📱 Screens & Navigation

### Authentication Flow
1. **Login Screen**: Email/password authentication
2. **Registration Screen**: New user account creation
3. **Vehicle Profile Setup**: VIN decoding or manual entry

### Main Application
1. **Feed Screen**: Main social feed with posts
2. **Create Post Screen**: Photo upload and caption
3. **Profile Screen**: User profile and vehicle information
4. **Settings Screen**: App preferences and logout

## 🚀 Development Roadmap

### Core Features ✅
- [x] User authentication system
- [x] Vehicle profile creation
- [x] Basic photo sharing
- [x] Feed system
- [x] Like and favorite functionality
- [x] Dark mode support

## 🏃‍♂️ Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.8+
- Google Services account for Firebase

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/CarGramV2.git
   cd CarGramV2
   ```

2. **Set up Firebase**
   - Create a new Firebase project
   - Enable Authentication, Firestore, and Storage
   - Download `google-services.json` and place it in the `app/` directory

3. **Configure API Keys**
   - Add your VIN decoder API key in the appropriate configuration file
   - Update Firebase configuration if needed

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## 📁 Project Structure

```
CarGramV2/
├── app/
│   ├── src/main/
│   │   ├── java/com/proiect/cargram/
│   │   │   ├── data/
│   │   │   │   ├── api/           # API interfaces and models
│   │   │   │   ├── local/         # Room database and DAOs
│   │   │   │   ├── model/         # Data models
│   │   │   │   └── repository/    # Repository implementations
│   │   │   ├── di/                # Dependency injection modules
│   │   │   ├── ui/
│   │   │   │   ├── components/    # Reusable UI components
│   │   │   │   ├── navigation/    # Navigation setup
│   │   │   │   ├── screens/       # Screen composables
│   │   │   │   ├── theme/         # App theme and styling
│   │   │   │   └── viewmodel/     # ViewModels
│   │   │   └── MainActivity.kt
│   │   └── res/                   # Resources (drawables, strings, etc.)
│   └── build.gradle.kts
├── gradle/
└── build.gradle.kts
```

## 🔧 Configuration

### VIN Decoder API
- Integrate with a VIN decoder service for automatic vehicle information extraction
- Handle API rate limits and error cases
- Implement fallback to manual entry


**Built with ❤️ for the automotive community**

*CarGram - Where Passion Meets Innovation* 