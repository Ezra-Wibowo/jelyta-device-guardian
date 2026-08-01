# Build Guide - Jelyta Sister's AI Device Guardian

## Android APK Build Process

### Prerequisites
- JDK 17
- Android SDK with API Level 34
- Gradle 8.x

### Build Debug APK
```bash
./gradlew assembleDebug
```
Output location: `app/build/outputs/apk/debug/app-debug.apk`

### Build Production Release APK
```bash
./gradlew assembleRelease
```
Output location: `app/build/outputs/apk/release/app-release.apk`

---

## Unit Testing
To run local JVM unit tests:
```bash
gradle :app:testDebugUnitTest
```
