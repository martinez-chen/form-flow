# FormFlow - 表單工作流系統

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-supported-blue.svg)](https://www.docker.com/)

## 系統概述

FormFlow 是一個完整的工單管理系統，支援用戶開單、群組接單、派工管理及通知功能。系統採用前後端分離架構，提供 REST API 接口與現代化前端介面。

### 核心功能

- ✅ **用戶開單模組**：支援動態表單配置、文件上傳、預設模板、草稿儲存
- 🔄 **群組接單模組**：工單池、群組分類、搶單機制、接單限制
- 🔄 **派工管理模組**：團隊管理、智慧派工、手動派工、成員接單  
- 🔄 **通知系統**：開單通知、結單通知、狀態變更通知、多通道支援

### 技術架構

- **後端**：Spring Boot 3.2.0 + Java 17 + DDD架構
- **資料庫**：PostgreSQL 15 (主要) + Redis (快取)
- **消息隊列**：RabbitMQ
- **安全**：Spring Security + JWT
- **文檔**：Swagger/OpenAPI 3
- **測試**：JUnit 5 + AssertJ + TestContainers

## 快速開始

### 前置需求

- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### 1. 啟動基礎服務

使用 Docker Compose 啟動 PostgreSQL、Redis、RabbitMQ 等服務：

```bash
# 啟動所有基礎服務
docker-compose up -d

# 檢查服務狀態
docker-compose ps

# 查看日誌
docker-compose logs -f
```

### 2. 服務端點

- **PostgreSQL**: `localhost:5432`
  - 資料庫: `formflow`
  - 用戶: `formflow`
  - 密碼: `formflow123`

- **Redis**: `localhost:6379`

- **RabbitMQ**: 
  - AMQP: `localhost:5672`
  - 管理界面: http://localhost:15672
  - 用戶: `formflow` / `formflow123`

- **pgAdmin**: http://localhost:5050
  - 用戶: `admin@formflow.com` / `admin123`

### 3. 啟動應用程式

```bash
# 編譯並運行測試
mvn clean test

# 啟動應用程式
mvn spring-boot:run
```

### 4. 訪問應用程式

- **應用程式**: http://localhost:8080
- **Swagger API 文檔**: http://localhost:8080/swagger-ui.html
- **健康檢查**: http://localhost:8080/actuator/health

## API 文檔

### 主要端點

#### 工單管理 API

```http
# 創建工單
POST /api/v1/orders
Content-Type: application/json

{
  "title": "修復登入問題",
  "description": "用戶無法登入系統",
  "category": "IT_SUPPORT", 
  "priority": "HIGH",
  "creatorId": 1,
  "groupId": null,
  "teamId": null,
  "dueDate": "2024-01-15T10:00:00"
}

# 獲取所有工單
GET /api/v1/orders

# 獲取特定工單
GET /api/v1/orders/{id}

# 獲取我創建的工單
GET /api/v1/orders/my-orders?creatorId=1

# 獲取指派給我的工單
GET /api/v1/orders/my-assignments?assigneeId=1
```

### 完整 API 文檔

啟動應用後訪問 [Swagger UI](http://localhost:8080/swagger-ui.html) 查看完整的 API 文檔。

## 整合測試

### 使用 cURL 測試

```bash
# 1. 創建工單
curl -X POST "http://localhost:8080/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "系統故障報修",
    "description": "服務器無法啟動",
    "category": "SYSTEM_MAINTENANCE",
    "priority": "URGENT",
    "creatorId": 1
  }'

# 2. 獲取所有工單
curl -X GET "http://localhost:8080/api/v1/orders"

# 3. 獲取特定工單（假設ID為1）
curl -X GET "http://localhost:8080/api/v1/orders/1"
```

### 使用 Postman

1. 導入 Postman Collection：
   - 訪問 http://localhost:8080/v3/api-docs
   - 複製 JSON 內容到 Postman

2. 設置環境變量：
   - `baseUrl`: `http://localhost:8080`

### 資料庫檢查

連接到 PostgreSQL 檢查資料：

```bash
# 使用 Docker 進入資料庫容器
docker exec -it formflow-postgres psql -U formflow -d formflow

# 查看表結構
\dt

# 查看工單資料
SELECT * FROM orders;

# 查看用戶資料
SELECT * FROM users;
```

## 開發指南

### 項目結構

```
src/
├── main/java/com/formflow/
│   ├── domain/          # 領域層
│   │   └── order/
│   │       ├── model/       # 聚合根和值物件
│   │       └── repository/  # 倉儲接口
│   ├── application/     # 應用層
│   │   └── order/
│   │       ├── command/     # 命令
│   │       ├── service/     # 應用服務
│   │       └── dto/         # 資料傳輸物件
│   ├── infrastructure/  # 基礎設施層
│   │   └── persistence/
│   │       └── jpa/         # JPA 實現
│   ├── interfaces/      # 介面層
│   │   └── rest/
│   │       └── controller/  # REST 控制器
│   └── config/         # 配置類
└── test/               # 測試代碼
```

### 運行測試

```bash
# 運行所有測試
mvn test

# 運行特定測試類
mvn test -Dtest=OrderControllerTest

# 運行特定測試方法
mvn test -Dtest=OrderControllerTest#shouldCreateOrderSuccessfully
```

### 代碼覆蓋率

```bash
# 生成測試覆蓋率報告
mvn jacoco:report

# 查看報告
open target/site/jacoco/index.html
```

## 部署

### Docker 容器化部署

```bash
# 構建 Docker 映像
docker build -t formflow-app:latest .

# 啟動完整環境（包含應用程式）
docker-compose -f docker-compose.yml up -d
```

### 生產環境配置

1. 更新 `application.yml` 中的生產環境配置
2. 設置環境變量
3. 配置 SSL/TLS
4. 設置監控和日誌

## 貢獻指南

1. Fork 本項目
2. 創建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交變更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 開啟 Pull Request

## 技術支援

- **文檔**: [API 文檔](http://localhost:8080/swagger-ui.html)
- **問題回報**: 請使用 GitHub Issues
- **討論**: GitHub Discussions

## 授權條款

本項目採用 MIT 授權條款。詳見 [LICENSE](LICENSE) 文件。