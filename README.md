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
  
<img width="392" height="863" alt="login light" src="https://github.com/user-attachments/assets/69f0b437-6fcb-4cc2-b7df-aa6b30525f48" />
<img width="389" height="860" alt="login dark" src="https://github.com/user-attachments/assets/c436141f-dee1-4284-8440-52d8d72f1436" />

<img width="383" height="860" alt="register 1 dark" src="https://github.com/user-attachments/assets/8833079d-892c-4b05-a585-8af84b38e65b" />
<img width="389" height="861" alt="register 1 light" src="https://github.com/user-attachments/assets/255aa29b-eff7-494c-9c32-a7444dcf5a3f" />

### 🚗 Vehicle Profile System
- **VIN Decoder Integration**: Automatic vehicle information extraction using VIN numbers
- **Manual Vehicle Entry**: Option to manually input vehicle specifications
- **Vehicle Details**: Store and display comprehensive vehicle information including:
  - Brand, Model, Year, Trim
  - Engine specifications (HP, displacement)
  - Transmission type
  - Fuel type
  - VIN number
<img width="388" height="860" alt="register 2 dark" src="https://github.com/user-attachments/assets/47250372-cac9-4629-af30-5d92bd344560" />
<img width="387" height="860" alt="register 2 light" src="https://github.com/user-attachments/assets/408a7643-64e2-4dfc-b4e2-c1c2501c74e8" />

### 📱 Social Media Features
- **Photo Sharing**: Upload and share high-quality photos of your vehicles
- **Feed System**: Scroll through posts from the community
- **Like System**: Like and interact with other users' posts
- **Favorites**: Save posts to your favorites collection
- **Sorting Options**: Sort posts by timeline or most liked
- **Tab Navigation**: Switch between "For You" and "Favorites" feeds

  
<img width="388" height="860" alt="main-light" src="https://github.com/user-attachments/assets/acffc74a-51e9-4018-a0a8-6a8a77c67dad" />
<img width="387" height="862" alt="home dark" src="https://github.com/user-attachments/assets/f533902f-cb1f-4746-abc1-1fe38eb1e430" />
<img width="385" height="860" alt="user - light" src="https://github.com/user-attachments/assets/e3ae69b2-62f0-4e91-9119-28a8b1087e03" />
<img width="386" height="862" alt="user - dark" src="https://github.com/user-attachments/assets/a5a833ce-1045-4f1f-9322-9af80d091968" />
<img width="385" height="867" alt="add-light" src="https://github.com/user-attachments/assets/5e80f118-0828-49c7-b552-c96278879776" />
<img width="394" height="859" alt="upload - dark" src="https://github.com/user-attachments/assets/af01f12f-f5c4-441f-a3d6-98073f90964c" />
<img width="385" height="862" alt="settings - light" src="https://github.com/user-attachments/assets/9771c071-f25c-42bd-a4ae-9c8c48ab723a" />
<img width="392" height="861" alt="settings - dark" src="https://github.com/user-attachments/assets/1de2d46a-2112-4eaa-8163-2af1e01d3d0f" />

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
