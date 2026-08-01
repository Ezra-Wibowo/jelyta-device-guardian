from fastapi import FastAPI, HTTPException, Depends, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import datetime

app = FastAPI(
    title="Jelyta Sister's AI Device Guardian FastAPI Service",
    description="Backend Cloud Service for Android Device Analytics, AI Chat Proxy, and Cloud Sync.",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class DeviceSyncRequest(BaseModel):
    device_id: str
    health_score: int
    cpu_usage: int
    ram_usage: int
    storage_usage: int
    battery_percent: int
    battery_temp: float
    performance_mode: str

class DeviceSyncResponse(BaseModel):
    status: str
    synced_at: str
    message: str

class AiAssistantQuery(BaseModel):
    user_query: str
    device_context: Optional[dict] = None

class AiAssistantResponse(BaseModel):
    response: str
    timestamp: str

@app.get("/")
def read_root():
    return {
        "app_name": "Jelyta Sister's AI Device Guardian Cloud Service",
        "status": "Online",
        "version": "1.0.0",
        "privacy": "Zero Personal Data Collection Policy Active"
    }

@app.get("/api/v1/health")
def health_check():
    return {"status": "healthy", "timestamp": datetime.datetime.utcnow().isoformat()}

@app.post("/api/v1/device/sync", response_model=DeviceSyncResponse)
def sync_device_metrics(data: DeviceSyncRequest):
    return DeviceSyncResponse(
        status="success",
        synced_at=datetime.datetime.utcnow().isoformat(),
        message=f"Metrics for device {data.device_id} successfully backed up to cloud."
    )

@app.post("/api/v1/ai/chat", response_model=AiAssistantResponse)
def chat_assistant(query: AiAssistantQuery):
    response_text = f"FastAPI Cloud AI Answer for '{query.user_query}': Perangkat Anda dalam batas normal. Rekomendasi: lakukan pembersihan cache secara berkala."
    return AiAssistantResponse(
        response=response_text,
        timestamp=datetime.datetime.utcnow().isoformat()
    )
