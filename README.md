# ☕ Online Coffee Shop - Android App

A modern, feature-rich Android application for ordering coffee online with Firebase backend integration.

## 📱 Features

### 🔐 Authentication
- User registration and login
- Firebase Authentication integration
- Google Sign-in support
- Secure user session management

### ☕ Product Management
- Browse coffee products by categories
- Detailed product information with variants
- Product search and filtering
- Admin product management (add, update, delete)

### 🛒 Shopping Cart
- Add/remove items from cart
- Modify quantities
- Real-time cart updates
- Persistent cart across sessions

### 📦 Order Management
- Place orders with multiple items
- Order history tracking
- Order status updates
- Order details view

### 👤 User Profile
- User profile management
- Account settings
- Order history access

### 📊 Dashboard (Optional)
- Sales analytics with charts
- Order statistics
- Product performance metrics

## 🛠️ Tech Stack

### Frontend
- **Language**: Java
- **UI Framework**: Android Native (Activities & Fragments)
- **UI Components**: Material Design Components
- **Architecture**: MVC Pattern
- **Lifecycle**: Android Architecture Components (ViewModel, LiveData)

### Backend & Database
- **Authentication**: Firebase Auth
- **Database**: Firebase Firestore
- **Real-time Database**: Firebase Realtime Database
- **Storage**: Firebase Storage

### Libraries & Dependencies
- **Image Loading**: Glide 4.11.0
- **Charts**: MPAndroidChart 3.1.0
- **Network**: Retrofit 2.9.0
- **RecyclerView**: AndroidX RecyclerView 1.3.2
- **Credentials**: Google Play Services Auth

## 📋 Requirements

### System Requirements
- **Minimum SDK**: Android 7.0 (API level 24)
- **Target SDK**: Android 14 (API level 35)
- **Compile SDK**: Android 14 (API level 35)
- **Java Version**: 11

### Development Requirements
- Android Studio Arctic Fox or newer
- Gradle 8.0+
- Google Services Plugin
- Firebase Project Setup

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/newwbI3/Online-Coffee-Shop-.git
cd Online-Coffee-Shop-
```

### 2. Firebase Setup
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Place it in the `app/` directory (already present in your project)

### 3. Firebase Configuration
Enable the following Firebase services:
- **Authentication** (Email/Password, Google Sign-in)
- **Firestore Database**
- **Realtime Database**
- **Storage**

### 4. Build the Project
```bash
./gradlew build
```

### 5. Run the App
- Open the project in Android Studio
- Connect an Android device or start an emulator
- Click "Run" or use `Shift + F10`

## 📁 Project Structure

```
app/
├── src/main/java/com/example/onlinecoffeeshop/
│   ├── adapter/          # RecyclerView adapters
│   ├── controller/       # Business logic controllers
│   ├── helper/          # Utility classes and helpers
│   ├── model/           # Data models
│   │   ├── CartItem.java
│   │   ├── Category.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Product.java
│   │   ├── ProductVariant.java
│   │   └── User.java
│   ├── repository/      # Data access layer
│   ├── view/           # UI Activities and Fragments
│   │   ├── auth/       # Authentication screens
│   │   ├── cart/       # Shopping cart functionality
│   │   ├── home/       # Home screen
│   │   ├── order/      # Order management
│   │   ├── product/    # Product-related screens
│   │   └── profile/    # User profile management
│   └── MainActivity.java
├── src/main/res/        # Resources (layouts, drawables, etc.)
└── src/main/AndroidManifest.xml
```

## 🔧 Configuration

### Firebase Rules
Make sure to configure appropriate Firestore and Storage security rules for your app.

### Permissions
The app requires the following permissions:
- `INTERNET` - For network communication
- `ACCESS_NETWORK_STATE` - For checking network connectivity

## 🚀 Features in Detail

### Authentication Flow
- Login/Register screens with validation
- Integration with Firebase Auth
- Google Sign-in option
- Automatic session management

### Product Catalog
- Category-based product browsing
- Product variants (size, type, etc.)
- Rich product details with images
- Search and filter capabilities

### Shopping Experience
- Intuitive cart management
- Real-time price calculations
- Seamless checkout process
- Order confirmation and tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🙏 Acknowledgments

- Firebase for backend services
- Material Design for UI components
- MPAndroidChart for analytics visualization
- Glide for efficient image loading

**Made with ❤️ for coffee lovers**
