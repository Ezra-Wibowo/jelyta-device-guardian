# Clean Architecture Specification - Jelyta Sister's AI Device Guardian

**Package Name:** `com.jelyta.deviceguardian`

This project follows **Enterprise Clean Architecture** principles, strictly separating concerns into decoupled layers: **Domain**, **Data**, **Presentation**, and **Core / DI**.

---

## 📐 Clean Architecture Overview Diagram

```mermaid
graph TD
    subgraph Presentation Layer
        UI[Jetpack Compose Views] --> VM[ViewModels StateFlow/UiState]
    end

    subgraph Domain Layer
        VM --> UC[Single Responsibility Use Cases]
        UC --> RI[Repository Interfaces]
        UC --> DM[Domain Models]
    end

    subgraph Data Layer
        RI <|.. RImpl[Repository Implementations]
        RImpl --> MAP[Entity & Dto Mappers]
        RImpl --> LDS[Room Database & Local DAOs]
        RImpl --> RDS[Gemini REST AI & FastAPI Client]
        RImpl --> HDS[System Hardware Telemetry API]
    end

    subgraph Core & Infrastructure
        AC[AppContainer / DI Modules]
        NAV[Navigation Compose NavHost]
        WM[WorkManager Periodic Health Worker]
        NOTIF[Notification Helper]
    end
```

---

## 🗂️ Dependency Graph

```mermaid
graph LR
    subgraph Core Modules
        AppModule --> DatabaseModule
        AppModule --> NetworkModule
        AppModule --> AiModule
    end

    subgraph Repository Module
        DatabaseModule --> RepositoryModule
        NetworkModule --> RepositoryModule
        AiModule --> RepositoryModule
    end

    subgraph Use Case Module
        RepositoryModule --> UseCaseModule
    end

    subgraph View Models
        UseCaseModule --> DashboardViewModel
        UseCaseModule --> OptimizerViewModel
        UseCaseModule --> SecurityViewModel
        UseCaseModule --> AssistantViewModel
        UseCaseModule --> CloudSyncViewModel
    end
```

---

## 🔄 Data Flow Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant View as GuardianDashboardScreen
    participant VM as DashboardViewModel
    participant UC as OptimizeDeviceUseCase
    participant Repo as DeviceRepositoryImpl
    participant Local as Room OptimizationLogDao

    User->>View: Clicks "One-Tap Boost"
    View->>VM: runTurboBoost()
    VM->>VM: _uiState.update { isHealing = true }
    VM->>UC: runTurboBoost()
    UC->>Repo: performTurboBoost()
    Repo-->>UC: Reclaimed 512 MB RAM
    UC->>Local: saveOptimizationLog(log)
    UC-->>VM: OptimizationLog(reclaimedMemoryMb = 512)
    VM->>VM: _uiState.update { isHealing = false, toastMessage = "Freed 512 MB" }
    VM-->>View: StateFlow updates UI
```

---

## 🧭 Navigation Flow

```mermaid
graph TD
    Dashboard[Dashboard Screen] <--> Optimizer[Optimizer & Self-Heal Screen]
    Dashboard <--> Security[Security Audit Screen]
    Dashboard <--> Assistant[AI Assistant & Translator Screen]
    Dashboard <--> Cloud[FastAPI Cloud Sync Screen]
```

---

## 🔒 Security & Enterprise Guarantees
1. **No Service Locator**: Migrated completely to clean modular dependency container (`AppContainer`).
2. **Single Responsibility Use Cases**: Every UseCase lives in its own dedicated file.
3. **Robust Result Wrappers**: Network and storage calls use structured `Result` / `Resource` / `NetworkResult` wrappers.
4. **Entity & Dto Mappers**: Strict isolation between Database Entities, Network DTOs, and Domain Models.
5. **Background Telemetry**: WorkManager background execution for periodic health checks and cloud sync.
