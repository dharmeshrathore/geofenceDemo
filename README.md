# GeoFenceAppDemo

GeoFenceAppDemo is an Android app for demonstrating geo-fencing functionality, supporting Android 10+ (API 24 and above). The app uses Koin for dependency injection and integrates the Google Maps API for geofencing.

## Permissions

Add these permissions to AndroidManifest.xml for location access:
```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
 ```

## Project Configuration

 **Key Properties**
- Namespace: com.dharmesh.geofencedemo
- Compile SDK: 35
- Application ID: com.dharmesh.geofencedemo
- Minimum SDK: 24
- Target SDK: 35
- Gradle Version: 8.6
- The `api_key` used in the project is provided in gradle.properties.


# Features
- **Geofence Transitions**: The app handles three geofence transitions:
  - Enter
  - Dwell
  - Exit

- **Dependency Injection**: Uses Koin for ViewModel injection

- **Google Maps Integration**:  Supports geofencing on Google Maps

## Setup Instructions

1. **Clone the Repository**: Clone the repository to your local machine.

   ```sh
   git clone <repository_url>
   ```
2. **Open the Project:**: Open the project in Android Studio.
3. **Add Google Maps API Key:**: Add your Google Maps API key in the **AndroidManifest.xml** file.
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="YOUR_API_KEY" />
   ```
  4. **Add Broadcast Receiver:** Add the following receiver configuration to your **AndroidManifest.xml** file.
  ```xml
   <receiver
    android:name=".receiver.GeofenceEventReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.google.android.gms.location.Geofence" />
    </intent-filter>
</receiver>
  ```
5. **Run the App:** Run the app on an Android device or emulator.

