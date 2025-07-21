# 表單工作流系統規格文件

## 系統概述

建立一個完整的工單管理系統，支援用戶開單、群組接單、派工管理及通知功能。系統採用前後端分離架構，提供 REST API 接口與現代化前端介面。

## 功能模組

### 1. 用戶開單模組
- **表單建立**：支援動態表單配置，包含基本資訊、優先級、分類、描述等欄位
- **文件上傳**：支援多種檔案格式上傳，包含圖片、文件、影片等
- **預設模板**：提供常用工單類型的預設模板
- **草稿儲存**：支援表單暫存功能

### 2. 群組接單模組
- **工單池**：顯示可接取的工單列表，支援篩選與排序
- **群組分類**：依據技能、部門或專案進行群組劃分
- **搶單機制**：支援先到先得的接單方式
- **接單限制**：設定群組或個人同時處理工單數量上限

### 3. 派工管理模組
- **團隊管理**：建立團隊架構，設定負責人角色權限
- **智慧派工**：基於成員技能、工作負荷、優先級進行自動派工建議
- **手動派工**：負責人可直接指派工單給特定成員
- **成員接單**：團隊成員可主動接取派給團隊的工單

### 4. 通知系統
- **開單通知**：工單建立後自動通知相關群組或負責人
- **結單通知**：工單完成後通知開單者及相關人員
- **狀態變更通知**：工單狀態變更時的即時通知
- **多通道支援**：支援站內信、郵件、推播等通知方式

## 技術架構

### 前端架構
- **框架**：React 18 + TypeScript
- **狀態管理**：Redux Toolkit
- **路由管理**：React Router v6
- **UI 組件庫**：Ant Design 5.0
- **表單處理**：React Hook Form
- **HTTP 客戶端**：Axios
- **即時通訊**：Socket.io-client

### 後端架構
- **框架**：Spring Boot 3.x
- **語言**：Java 17+
- **架構模式**：Domain-Driven Design (DDD)
- **資料庫**：PostgreSQL (主要) + Redis (快取)
- **ORM**：Spring Data JPA + Hibernate
- **驗證**：Spring Security + JWT
- **檔案儲存**：AWS S3 或本地儲存
- **即時通訊**：WebSocket + STOMP
- **任務隊列**：Spring Boot + RabbitMQ
- **建構工具**：Maven

## DDD 架構設計

### 領域劃分 (Bounded Context)

#### 1. 工單領域 (Order Domain)
- **聚合根**: Order (工單)
- **實體**: OrderComment, OrderAttachment, OrderLog
- **值物件**: Priority, Status, Category
- **領域服務**: OrderAssignmentService, OrderStatusTransitionService
- **倉儲**: OrderRepository, OrderCommentRepository

#### 2. 用戶領域 (User Domain)
- **聚合根**: User (用戶)
- **實體**: UserProfile
- **值物件**: Email, Username, Role
- **領域服務**: UserAuthenticationService
- **倉儲**: UserRepository

#### 3. 群組領域 (Group Domain)
- **聚合根**: Group (群組)
- **實體**: GroupMember
- **值物件**: Skills, MaxConcurrentOrders
- **領域服務**: GroupAssignmentService
- **倉儲**: GroupRepository

#### 4. 團隊領域 (Team Domain)
- **聚合根**: Team (團隊)
- **實體**: TeamMember
- **值物件**: Leadership
- **領域服務**: TeamManagementService
- **倉儲**: TeamRepository

#### 5. 通知領域 (Notification Domain)
- **聚合根**: Notification (通知)
- **值物件**: NotificationType, Message
- **領域服務**: NotificationDispatchService
- **倉儲**: NotificationRepository

### 架構分層

#### 1. 領域層 (Domain Layer)
```
com.formflow.domain
├── order/
│   ├── model/
│   │   ├── Order.java (聚合根)
│   │   ├── OrderComment.java
│   │   ├── OrderAttachment.java
│   │   ├── Priority.java (值物件)
│   │   └── Status.java (值物件)
│   ├── service/
│   │   ├── OrderAssignmentService.java
│   │   └── OrderStatusTransitionService.java
│   └── repository/
│       └── OrderRepository.java (介面)
├── user/
├── group/
├── team/
└── notification/
```

#### 2. 應用層 (Application Layer)
```
com.formflow.application
├── order/
│   ├── command/
│   │   ├── CreateOrderCommand.java
│   │   ├── AssignOrderCommand.java
│   │   └── CompleteOrderCommand.java
│   ├── query/
│   │   ├── OrderQuery.java
│   │   └── OrderSearchQuery.java
│   ├── service/
│   │   ├── OrderApplicationService.java
│   │   └── OrderQueryService.java
│   └── dto/
│       ├── OrderDTO.java
│       └── CreateOrderRequest.java
```

#### 3. 基礎設施層 (Infrastructure Layer)
```
com.formflow.infrastructure
├── persistence/
│   ├── jpa/
│   │   ├── entity/
│   │   │   ├── OrderEntity.java
│   │   │   └── UserEntity.java
│   │   └── repository/
│   │       ├── JpaOrderRepository.java
│   │       └── OrderRepositoryImpl.java
├── messaging/
│   ├── rabbitmq/
│   │   └── OrderEventPublisher.java
├── security/
│   └── jwt/
│       └── JwtAuthenticationService.java
└── storage/
    └── s3/
        └── S3FileStorageService.java
```

#### 4. 介面層 (Interface Layer)
```
com.formflow.interfaces
├── rest/
│   ├── controller/
│   │   ├── OrderController.java
│   │   ├── UserController.java
│   │   └── GroupController.java
│   ├── dto/
│   └── mapper/
├── websocket/
│   └── NotificationWebSocketHandler.java
└── config/
    ├── SecurityConfig.java
    └── WebSocketConfig.java
```

### 領域事件 (Domain Events)

#### 工單領域事件
- `OrderCreatedEvent`: 工單建立事件
- `OrderAssignedEvent`: 工單指派事件
- `OrderCompletedEvent`: 工單完成事件
- `OrderStatusChangedEvent`: 工單狀態變更事件

#### 事件處理器
```java
@EventHandler
public class OrderEventHandler {
    // 處理工單建立後的通知發送
    @EventListener
    public void handle(OrderCreatedEvent event);
    
    // 處理工單指派後的通知發送
    @EventListener
    public void handle(OrderAssignedEvent event);
}
```

## 資料庫設計

### 核心表結構

#### users (用戶表)
```sql
id: SERIAL PRIMARY KEY
username: VARCHAR(50) UNIQUE NOT NULL
email: VARCHAR(100) UNIQUE NOT NULL
password_hash: VARCHAR(255) NOT NULL
full_name: VARCHAR(100)
role: ENUM('admin', 'user', 'team_leader', 'member')
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

#### groups (群組表)
```sql
id: SERIAL PRIMARY KEY
name: VARCHAR(100) NOT NULL
description: TEXT
skills: JSON
max_concurrent_orders: INTEGER DEFAULT 5
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

#### teams (團隊表)
```sql
id: SERIAL PRIMARY KEY
name: VARCHAR(100) NOT NULL
leader_id: INTEGER REFERENCES users(id)
group_id: INTEGER REFERENCES groups(id)
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

#### orders (工單表)
```sql
id: SERIAL PRIMARY KEY
title: VARCHAR(200) NOT NULL
description: TEXT
category: VARCHAR(50)
priority: ENUM('low', 'medium', 'high', 'urgent')
status: ENUM('pending', 'assigned', 'in_progress', 'completed', 'cancelled')
creator_id: INTEGER REFERENCES users(id)
assignee_id: INTEGER REFERENCES users(id)
group_id: INTEGER REFERENCES groups(id)
team_id: INTEGER REFERENCES teams(id)
due_date: TIMESTAMP
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
completed_at: TIMESTAMP
```

#### order_attachments (附件表)
```sql
id: SERIAL PRIMARY KEY
order_id: INTEGER REFERENCES orders(id)
file_name: VARCHAR(255)
file_path: VARCHAR(500)
file_size: BIGINT
file_type: VARCHAR(50)
uploaded_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

#### order_logs (工單操作紀錄表)
```sql
id: SERIAL PRIMARY KEY
order_id: INTEGER REFERENCES orders(id)
user_id: INTEGER REFERENCES users(id)
action: ENUM('created', 'assigned', 'accepted', 'started', 'completed', 'cancelled', 'commented')
old_status: VARCHAR(50)
new_status: VARCHAR(50)
comment: TEXT
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

#### order_comments (工單留言表)
```sql
id: SERIAL PRIMARY KEY
order_id: INTEGER REFERENCES orders(id)
user_id: INTEGER REFERENCES users(id)
content: TEXT NOT NULL
is_internal: BOOLEAN DEFAULT FALSE
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

#### notifications (通知表)
```sql
id: SERIAL PRIMARY KEY
user_id: INTEGER REFERENCES users(id)
order_id: INTEGER REFERENCES orders(id)
type: ENUM('order_created', 'order_assigned', 'order_completed', 'status_changed')
title: VARCHAR(200)
message: TEXT
is_read: BOOLEAN DEFAULT FALSE
created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

## API 設計

### REST API 設計 (Spring Boot)

#### 認證相關 API
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    POST /api/v1/auth/login          // 用戶登入
    POST /api/v1/auth/register       // 用戶註冊
    POST /api/v1/auth/refresh        // 刷新 Token
    POST /api/v1/auth/logout         // 用戶登出
}
```

#### 工單管理 API
```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    GET    /api/v1/orders              // 獲取工單列表 (分頁)
    POST   /api/v1/orders              // 建立工單
    GET    /api/v1/orders/{id}         // 獲取工單詳情
    PUT    /api/v1/orders/{id}         // 更新工單
    DELETE /api/v1/orders/{id}         // 刪除工單
    POST   /api/v1/orders/{id}/assign  // 派工
    POST   /api/v1/orders/{id}/accept  // 接單
    POST   /api/v1/orders/{id}/complete// 完成工單
    GET    /api/v1/orders/{id}/logs    // 獲取工單操作紀錄
    POST   /api/v1/orders/{id}/comments// 新增工單留言
    GET    /api/v1/orders/{id}/comments// 獲取工單留言
}
```

#### 工單查詢 API
```java
@RestController 
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    GET /api/v1/orders/search         // 進階搜尋工單
        // 參數: keyword, status, priority, creator, assignee, dateRange, category
        // 支援 Pageable 分頁參數
    GET /api/v1/orders/analytics      // 工單統計分析
    GET /api/v1/orders/export         // 匯出工單資料 (CSV/Excel)
    GET /api/v1/orders/my-orders      // 我的工單
    GET /api/v1/orders/my-assignments // 指派給我的工單
}
```

#### 群組管理 API
```java
@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {
    GET    /api/v1/groups              // 獲取群組列表
    POST   /api/v1/groups              // 建立群組
    PUT    /api/v1/groups/{id}         // 更新群組
    DELETE /api/v1/groups/{id}         // 刪除群組
    GET    /api/v1/groups/{id}/orders  // 獲取群組可接工單
    POST   /api/v1/groups/{id}/members // 加入群組
    DELETE /api/v1/groups/{id}/members/{userId} // 退出群組
}
```

#### 團隊管理 API
```java
@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {
    GET    /api/v1/teams               // 獲取團隊列表
    POST   /api/v1/teams               // 建立團隊
    PUT    /api/v1/teams/{id}          // 更新團隊
    DELETE /api/v1/teams/{id}          // 刪除團隊
    GET    /api/v1/teams/{id}/members  // 獲取團隊成員
    POST   /api/v1/teams/{id}/members  // 新增團隊成員
    DELETE /api/v1/teams/{id}/members/{userId} // 移除團隊成員
}
```

#### 通知 API
```java
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    GET /api/v1/notifications         // 獲取通知列表 (分頁)
    PUT /api/v1/notifications/{id}/read // 標記單一通知已讀
    PUT /api/v1/notifications/read-all  // 全部標記已讀
    DELETE /api/v1/notifications/{id}   // 刪除通知
}
```

#### WebSocket API (即時通訊)
```java
@Controller
public class NotificationWebSocketController {
    // 通知推送端點
    @MessageMapping("/notifications/subscribe")
    @SendToUser("/queue/notifications")
    
    // 工單狀態變更推送
    @MessageMapping("/orders/status")
    @SendTo("/topic/orders")
}
```

## 前端頁面結構

### 主要頁面
- **登入/註冊頁**：用戶認證入口
- **儀表板**：總覽頁面，顯示統計資訊和快速操作
- **工單管理**：工單列表、詳情、建立、編輯頁面
- **工單查詢**：進階搜尋、篩選、統計分析頁面
- **工單紀錄**：操作歷程、留言討論頁面
- **群組中心**：群組列表、可接工單池
- **團隊管理**：團隊成員管理、派工介面
- **個人中心**：個人資訊、通知設定
- **系統設定**：管理員功能頁面

### 組件設計
- **OrderForm**：工單表單組件，支援動態欄位
- **OrderCard**：工單卡片展示組件
- **OrderSearchBar**：工單搜尋欄組件，支援多條件篩選
- **OrderTimeline**：工單時間軸組件，展示操作歷程
- **StatusBadge**：狀態標籤組件
- **FileUpload**：檔案上傳組件
- **CommentThread**：留言討論組件
- **AnalyticsChart**：統計圖表組件
- **NotificationPanel**：通知面板組件
- **UserSelector**：用戶選擇器組件

## 業務流程

### 開單流程
1. 用戶填寫工單表單
2. 上傳相關附件
3. 選擇目標群組或團隊
4. 提交工單
5. 系統發送開單通知

### 接單流程
1. 群組成員查看可接工單
2. 點擊接單或由負責人派工
3. 工單狀態變更為已指派
4. 發送接單通知給創建者

### 派工流程
1. 團隊負責人查看待派工單
2. 選擇合適的團隊成員
3. 指派工單給成員
4. 發送派工通知

### 完工流程
1. 處理人員完成工單
2. 更新工單狀態為已完成
3. 系統發送結單通知
4. 記錄完成時間和結果

## 權限控制

### 角色權限定義
- **管理員**：系統全權限，可管理所有用戶、群組、團隊
- **團隊負責人**：可管理所屬團隊，派工給團隊成員
- **一般用戶**：可建立工單、查看自己的工單
- **團隊成員**：可接取團隊工單、查看指派給自己的工單

### 資料存取控制
- 工單創建者可查看和編輯自己的工單
- 群組成員可查看群組內的可接工單
- 團隊成員只能查看指派給自己或團隊的工單
- 管理員擁有全部工單的查看權限

## 部署架構

### 開發環境
- **前端**：localhost:3000 (React Dev Server)
- **後端**：localhost:8080 (Spring Boot)
- **資料庫**：localhost:5432 (PostgreSQL)
- **快取**：localhost:6379 (Redis)
- **消息隊列**：localhost:5672 (RabbitMQ)

### 生產環境
- **前端**：CDN 部署 (CloudFront + S3)
- **後端**：容器化部署 (Docker + Kubernetes)
- **資料庫**：RDS PostgreSQL
- **快取**：ElastiCache Redis
- **消息隊列**：Amazon MQ (RabbitMQ)
- **負載均衡**：ALB
- **監控**：CloudWatch + ELK Stack + Micrometer

## 安全考量

### 資料安全
- 密碼使用 Spring Security BCryptPasswordEncoder 加密儲存
- JWT token 設定適當的過期時間
- Spring Security 整合 rate limiting
- 檔案上傳類型和大小限制 (Spring Boot MultipartFile)
- Spring Data JPA 自動 SQL 注入防護
- 使用 @PreAuthorize 和 @PostAuthorize 進行方法級權限控制

### 通訊安全
- 全站 HTTPS 加密
- API 跨域請求控制
- 敏感資料傳輸加密
- WebSocket 連線驗證

## 效能優化

### 前端優化
- 組件懶載入
- 圖片延遲載入
- 資料分頁載入
- 狀態快取機制
- Bundle 分割優化

### 後端優化
- 資料庫索引優化 (JPA @Index 註解)
- Spring Cache 查詢結果快取 (@Cacheable, @CacheEvict)
- HikariCP 連線池管理
- @Async 非同步處理機制
- CDN 靜態資源加速
- Spring Boot Actuator 健康檢查和監控

## 測試策略

### 單元測試
- 前端組件測試 (Jest + Testing Library)
- 後端單元測試 (JUnit 5 + AssertJ + Mockito)
- 領域模型測試 (DDD 聚合根和值物件測試，使用 AssertJ 流暢語法)
- Repository 層測試 (@DataJpaTest + AssertJ)
- Service 層測試 (@ExtendWith(MockitoExtension.class) + AssertJ)

### 整合測試
- REST API 整合測試 (@SpringBootTest + TestRestTemplate + AssertJ)
- 資料庫整合測試 (@DataJpaTest + TestContainers + AssertJ)
- Spring Security 整合測試 (@WithMockUser + AssertJ)
- WebSocket 整合測試 (AssertJ for message verification)
- 第三方服務整合測試 (WireMock + AssertJ)

### 端對端測試
- 關鍵業務流程測試 (Cypress)
- Spring Boot 測試切片 (@WebMvcTest, @JsonTest + AssertJ)
- 跨瀏覽器相容性測試
- 行動裝置響應式測試

### 測試工具和框架
- **AssertJ**: 流暢的斷言庫，提供豐富的 API 和可讀性
- **Mockito**: Mock 框架
- **TestContainers**: 整合測試容器化
- **WireMock**: HTTP 服務 Mock
- **Spring Boot Test**: 整合測試支援