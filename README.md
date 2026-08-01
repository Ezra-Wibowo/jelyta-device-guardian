# Jelyta Sister's AI Device Guardian

**Package Name:** `com.jelyta.deviceguardian`

**Jelyta Sister's AI Device Guardian** is a lightweight, privacy-first Android application powered by modern Jetpack Compose, Kotlin Coroutines, Room Database, Gemini 3.5 Flash AI Engine, and FastAPI Cloud Sync Backend.

---

## 🌟 Key Features

1. **AI Device Monitor & Health Score**
   - Real-time CPU, RAM, Storage, Battery level, and Battery Temperature metrics.
   - Dynamic 0–100 Device Health Gauge with animated visual indicators.

2. **AI Optimizer & Self-Healing Engine**
   - One-Tap Turbo Boost for background memory reclamation.
   - Cache Cleaner for temporary file purge.
   - Automatic Self-Healing diagnostics for crash prevention and memory pressure relief.

3. **AI Security & Privacy Audit**
   - Audits camera, microphone, location, and contact permissions for all installed applications.
   - Privacy Protection Score calculation.
   - 100% Privacy Guarantee: Zero access to SMS, personal chats, contacts, or photos.

4. **AI Assistant & Translator**
   - Gemini-powered Device Assistant answering user queries regarding RAM, battery, and device performance.
   - Multilingual AI Translator and Subtitle Generator tool supporting Indonesian, English, Japanese, Mandarin, and more.

5. **Performance Profiles**
   - Normal, Battery Saver, and Turbo Performance modes.

6. **FastAPI Cloud Sync & Backup**
   - REST API cloud backend with FastAPI, Swagger documentation, and Docker Compose integration.

---

## 🚀 How to Run the Android Application

1. Open the project in Android Studio or Google AI Studio Build.
2. Ensure Kotlin & Jetpack Compose dependencies are synced via Gradle.
3. Configure your Gemini API key in the Secrets Panel or `.env`:
   ```bash
   GEMINI_API_KEY=your_actual_api_key
   ```
4. Build and Run on an Android Device or Emulator (API 24+).

---

## 🐳 How to Run the FastAPI Backend Service

```bash
# Clone the repository
git clone https://github.com/jelyta/device-guardian.git
cd device-guardian

# Run via Docker Compose
docker-compose up --build -d

# Access Swagger API Docs
http://localhost:8000/docs
```

---

## 🛡️ Privacy Statement

This application strictly adheres to official Android system APIs and operates on a local-first privacy model. No personal media, messages, contacts, or sensitive communications are accessed or transmitted.
