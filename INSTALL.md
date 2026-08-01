# Installation Guide - Jelyta Sister's AI Device Guardian

## Android Application

### Minimum Requirements
- **OS Version:** Android 7.0 (API Level 24) or higher
- **RAM:** Minimum 2GB RAM
- **Storage:** 25MB free space
- **Permissions:** Internet (for Gemini AI Core Engine and FastAPI Cloud Sync)

### Installation Steps
1. Download the latest `app-release.apk` from the release section.
2. Open the `.apk` file on your Android device.
3. If prompted, enable **"Install from unknown sources"** in your system settings.
4. Complete installation and open **Jelyta Sister's AI Device Guardian**.

---

## FastAPI Backend Cloud Service

### Prerequisites
- Docker & Docker Compose installed
- Python 3.11+ (if running without Docker)

### Quick Start with Docker
```bash
git clone https://github.com/jelyta/device-guardian.git
cd device-guardian
docker-compose up --build -d
```
The FastAPI REST API will be accessible at `http://localhost:8000`.
Interactive Swagger UI documentation is available at `http://localhost:8000/docs`.
