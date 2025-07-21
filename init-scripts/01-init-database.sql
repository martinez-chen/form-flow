-- FormFlow 資料庫初始化腳本

-- 設置時區
SET timezone = 'Asia/Taipei';

-- 創建擴展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 創建枚舉類型
DO $$ BEGIN
    CREATE TYPE priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'URGENT');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE status_enum AS ENUM ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE role_enum AS ENUM ('ADMIN', 'USER', 'TEAM_LEADER', 'MEMBER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 插入測試數據（如果表存在的話）
INSERT INTO users (username, email, password_hash, full_name, role, created_at, updated_at) 
VALUES 
    ('admin', 'admin@formflow.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzwNOYzFjm6', '系統管理員', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('john_doe', 'john@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzwNOYzFjm6', 'John Doe', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('jane_smith', 'jane@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzwNOYzFjm6', 'Jane Smith', 'TEAM_LEADER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 插入群組測試數據
INSERT INTO groups (name, description, skills, max_concurrent_orders, created_at)
VALUES 
    ('IT支援團隊', 'IT技術支援及故障排除', '["Java", "Spring Boot", "PostgreSQL", "系統管理"]'::json, 10, CURRENT_TIMESTAMP),
    ('開發團隊', '軟體開發及維護', '["Java", "Python", "React", "DevOps"]'::json, 15, CURRENT_TIMESTAMP),
    ('測試團隊', '軟體測試及品質保證', '["測試自動化", "性能測試", "安全測試"]'::json, 8, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 插入範例工單數據（會在應用啟動後由Hibernate創建表結構後執行）

-- 創建函數來獲取當前時間
CREATE OR REPLACE FUNCTION get_current_timestamp()
RETURNS timestamp AS $$
BEGIN
    RETURN CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql;

-- 創建觸發器函數來自動更新 updated_at 欄位
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 顯示初始化完成訊息
DO $$
BEGIN
    RAISE NOTICE '=== FormFlow 資料庫初始化完成 ===';
    RAISE NOTICE '預設管理員帳號: admin / admin123';
    RAISE NOTICE '測試用戶: john_doe / password123';
    RAISE NOTICE '團隊領導: jane_smith / password123';
END $$;