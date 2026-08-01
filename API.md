# REST API Specification - Jelyta Sister's AI Device Guardian

## Endpoints

### Health Check
- **GET** `/api/v1/health`
- **Response:** `{"status": "healthy", "timestamp": "2026-07-31T17:30:00Z"}`

### Device Metrics Sync
- **POST** `/api/v1/device/sync`
- **Request Body:**
  ```json
  {
    "device_id": "DEV-GUARDIAN-78492",
    "health_score": 88,
    "cpu_usage": 32,
    "ram_usage": 64,
    "storage_usage": 45,
    "battery_percent": 82,
    "battery_temp": 31.5,
    "performance_mode": "NORMAL"
  }
  ```
- **Response:** `{"status": "success", "synced_at": "...", "message": "..."}`

### AI Assistant Proxy
- **POST** `/api/v1/ai/chat`
- **Request Body:** `{"user_query": "Bagaimana kondisi baterai?", "device_context": {...}}`
- **Response:** `{"response": "...", "timestamp": "..."}`
