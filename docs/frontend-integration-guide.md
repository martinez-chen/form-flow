# Form Flow API 前端整合指南

## 📋 概述

本文檔提供 Form Flow 工單管理系統 API 的前端整合指南，包含完整的 API 端點、請求/回應格式、錯誤處理以及實用的程式碼範例。

## 🔗 API 基本資訊

- **Base URL (開發)**: `http://localhost:8080`
- **Base URL (生產)**: `https://api.formflow.com`
- **API 版本**: v1
- **認證方式**: HTTP Basic Authentication
- **內容類型**: `application/json`

## 🚀 快速開始

### 1. 基本配置

```javascript
// API 配置
const API_CONFIG = {
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Basic ' + btoa('username:password')
  }
};

// Axios 設定範例
import axios from 'axios';

const apiClient = axios.create({
  baseURL: API_CONFIG.baseURL,
  headers: API_CONFIG.headers
});
```

### 2. 錯誤處理

```javascript
// 統一錯誤處理
apiClient.interceptors.response.use(
  response => response,
  error => {
    const { status, data } = error.response;
    
    switch(status) {
      case 400:
        console.error('請求參數錯誤:', data.message);
        break;
      case 401:
        console.error('未授權，請重新登入');
        break;
      case 404:
        console.error('資源不存在:', data.error);
        break;
      case 409:
        console.error('操作衝突:', data.error);
        break;
      case 500:
        console.error('服務器錯誤:', data.message);
        break;
    }
    
    return Promise.reject(error);
  }
);
```

## 📝 API 端點詳細說明

### 1. 創建工單

**端點**: `POST /api/v1/orders`

```javascript
// 創建基本工單
async function createOrder(orderData) {
  try {
    const response = await apiClient.post('/api/v1/orders', {
      title: orderData.title,
      description: orderData.description,
      category: orderData.category,
      priority: orderData.priority,
      creatorId: orderData.creatorId,
      groupId: orderData.groupId,        // 可選
      teamId: orderData.teamId,          // 可選
      dueDate: orderData.dueDate         // 可選，ISO 8601 格式
    });
    
    return {
      success: true,
      orderId: response.data.id,
      message: response.data.message
    };
  } catch (error) {
    return {
      success: false,
      error: error.response.data
    };
  }
}

// 使用範例
const newOrder = await createOrder({
  title: "修復登入問題",
  description: "用戶反映無法正常登入系統",
  category: "BUG",
  priority: "HIGH",
  creatorId: 1,
  dueDate: "2024-01-15T10:00:00"
});
```

### 2. 查詢工單

```javascript
// 獲取單一工單
async function getOrder(orderId) {
  try {
    const response = await apiClient.get(`/api/v1/orders/${orderId}`);
    return response.data;
  } catch (error) {
    if (error.response.status === 404) {
      return null; // 工單不存在
    }
    throw error;
  }
}

// 獲取所有工單
async function getAllOrders() {
  const response = await apiClient.get('/api/v1/orders');
  return response.data;
}

// 獲取我創建的工單
async function getMyOrders(creatorId) {
  const response = await apiClient.get('/api/v1/orders/my-orders', {
    params: { creatorId }
  });
  return response.data;
}

// 獲取指派給我的工單
async function getMyAssignments(assigneeId) {
  const response = await apiClient.get('/api/v1/orders/my-assignments', {
    params: { assigneeId }
  });
  return response.data;
}
```

### 3. 群組指派

```javascript
// 指派工單給群組
async function assignOrderToGroup(orderId, groupId, requesterId) {
  try {
    const response = await apiClient.put(
      `/api/v1/orders/${orderId}/assign-to-group`,
      {
        groupId: groupId,
        requesterId: requesterId
      }
    );
    
    return {
      success: true,
      message: response.data.message
    };
  } catch (error) {
    const { status, data } = error.response;
    
    if (status === 409) {
      // 工單狀態不允許指派
      return {
        success: false,
        error: 'CONFLICT',
        message: data.error
      };
    }
    
    throw error;
  }
}
```

## 🎨 React 整合範例

### 1. 工單管理 Hook

```javascript
import { useState, useEffect } from 'react';

export function useOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const data = await getAllOrders();
      setOrders(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const createOrder = async (orderData) => {
    setLoading(true);
    try {
      const result = await createOrder(orderData);
      if (result.success) {
        await fetchOrders(); // 重新獲取列表
      }
      return result;
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  return {
    orders,
    loading,
    error,
    createOrder,
    fetchOrders,
    assignToGroup: assignOrderToGroup
  };
}
```

### 2. 工單表單組件

```javascript
import React, { useState } from 'react';

export function OrderForm({ onSubmit, loading }) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'TASK',
    priority: 'MEDIUM',
    creatorId: 1,
    dueDate: ''
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.title.trim()) {
      newErrors.title = '標題不能為空';
    } else if (formData.title.length > 200) {
      newErrors.title = '標題不能超過 200 字符';
    }
    
    if (formData.description.length > 5000) {
      newErrors.description = '描述不能超過 5000 字符';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    const result = await onSubmit(formData);
    
    if (result.success) {
      // 重置表單
      setFormData({
        title: '',
        description: '',
        category: 'TASK',
        priority: 'MEDIUM',
        creatorId: 1,
        dueDate: ''
      });
      setErrors({});
    } else {
      // 處理服務器驗證錯誤
      if (result.error.fields) {
        const serverErrors = {};
        result.error.fields.forEach(field => {
          serverErrors[field.field] = field.message;
        });
        setErrors(serverErrors);
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="title">標題 *</label>
        <input
          id="title"
          type="text"
          value={formData.title}
          onChange={(e) => setFormData({...formData, title: e.target.value})}
          className={errors.title ? 'error' : ''}
        />
        {errors.title && <span className="error-message">{errors.title}</span>}
      </div>

      <div>
        <label htmlFor="description">描述</label>
        <textarea
          id="description"
          value={formData.description}
          onChange={(e) => setFormData({...formData, description: e.target.value})}
          className={errors.description ? 'error' : ''}
        />
        {errors.description && <span className="error-message">{errors.description}</span>}
      </div>

      <div>
        <label htmlFor="category">分類 *</label>
        <select
          id="category"
          value={formData.category}
          onChange={(e) => setFormData({...formData, category: e.target.value})}
        >
          <option value="TASK">任務</option>
          <option value="BUG">錯誤</option>
          <option value="FEATURE">功能</option>
          <option value="IMPROVEMENT">改進</option>
        </select>
      </div>

      <div>
        <label htmlFor="priority">優先級 *</label>
        <select
          id="priority"
          value={formData.priority}
          onChange={(e) => setFormData({...formData, priority: e.target.value})}
        >
          <option value="LOW">低</option>
          <option value="MEDIUM">中</option>
          <option value="HIGH">高</option>
          <option value="URGENT">緊急</option>
        </select>
      </div>

      <div>
        <label htmlFor="dueDate">截止日期</label>
        <input
          id="dueDate"
          type="datetime-local"
          value={formData.dueDate}
          onChange={(e) => setFormData({...formData, dueDate: e.target.value})}
        />
      </div>

      <button type="submit" disabled={loading}>
        {loading ? '創建中...' : '創建工單'}
      </button>
    </form>
  );
}
```

### 3. 工單列表組件

```javascript
import React from 'react';

export function OrderList({ orders, onAssignToGroup }) {
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('zh-TW');
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      'PENDING': { text: '待處理', className: 'status-pending' },
      'ASSIGNED': { text: '已指派', className: 'status-assigned' },
      'IN_PROGRESS': { text: '進行中', className: 'status-progress' },
      'COMPLETED': { text: '已完成', className: 'status-completed' },
      'CANCELLED': { text: '已取消', className: 'status-cancelled' }
    };
    
    const statusInfo = statusMap[status] || { text: status, className: '' };
    
    return (
      <span className={`status-badge ${statusInfo.className}`}>
        {statusInfo.text}
      </span>
    );
  };

  const getPriorityBadge = (priority) => {
    const priorityMap = {
      'LOW': { text: '低', className: 'priority-low' },
      'MEDIUM': { text: '中', className: 'priority-medium' },
      'HIGH': { text: '高', className: 'priority-high' },
      'URGENT': { text: '緊急', className: 'priority-urgent' }
    };
    
    const priorityInfo = priorityMap[priority] || { text: priority, className: '' };
    
    return (
      <span className={`priority-badge ${priorityInfo.className}`}>
        {priorityInfo.text}
      </span>
    );
  };

  return (
    <div className="order-list">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>標題</th>
            <th>分類</th>
            <th>優先級</th>
            <th>狀態</th>
            <th>創建時間</th>
            <th>截止日期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(order => (
            <tr key={order.id}>
              <td>{order.id}</td>
              <td>
                <div>
                  <strong>{order.title}</strong>
                  {order.description && (
                    <div className="order-description">{order.description}</div>
                  )}
                </div>
              </td>
              <td>{order.category}</td>
              <td>{getPriorityBadge(order.priority)}</td>
              <td>{getStatusBadge(order.status)}</td>
              <td>{formatDate(order.createdAt)}</td>
              <td>{formatDate(order.dueDate)}</td>
              <td>
                {(order.status === 'PENDING' || order.status === 'ASSIGNED') && (
                  <button
                    onClick={() => onAssignToGroup(order.id)}
                    className="btn-assign"
                  >
                    指派群組
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
```

## 🛠️ TypeScript 支援

### 型別定義

```typescript
// types/api.ts
export interface CreateOrderRequest {
  title: string;
  description?: string;
  category: 'TASK' | 'BUG' | 'FEATURE' | 'IMPROVEMENT';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  creatorId: number;
  groupId?: number;
  teamId?: number;
  dueDate?: string;
}

export interface OrderDTO {
  id: number;
  title: string;
  description?: string;
  category: string;
  priority: string;
  status: 'PENDING' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  creatorId: number;
  assigneeId?: number;
  groupId?: number;
  teamId?: number;
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
}

export interface AssignOrderToGroupRequest {
  groupId: number;
  requesterId: number;
}

export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

export interface ValidationError {
  error: string;
  message: string;
  fields?: Array<{
    field: string;
    message: string;
  }>;
}
```

### API 客戶端

```typescript
// services/orderApi.ts
import axios, { AxiosResponse } from 'axios';
import { CreateOrderRequest, OrderDTO, AssignOrderToGroupRequest, ApiResponse } from '../types/api';

class OrderApiService {
  private apiClient;

  constructor(baseURL: string) {
    this.apiClient = axios.create({
      baseURL,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  async createOrder(orderData: CreateOrderRequest): Promise<ApiResponse<{ id: number }>> {
    try {
      const response: AxiosResponse = await this.apiClient.post('/api/v1/orders', orderData);
      return {
        success: true,
        data: response.data
      };
    } catch (error: any) {
      return {
        success: false,
        error: error.response.data
      };
    }
  }

  async getOrder(orderId: number): Promise<OrderDTO | null> {
    try {
      const response: AxiosResponse<OrderDTO> = await this.apiClient.get(`/api/v1/orders/${orderId}`);
      return response.data;
    } catch (error: any) {
      if (error.response.status === 404) {
        return null;
      }
      throw error;
    }
  }

  async getAllOrders(): Promise<OrderDTO[]> {
    const response: AxiosResponse<OrderDTO[]> = await this.apiClient.get('/api/v1/orders');
    return response.data;
  }

  async assignOrderToGroup(
    orderId: number, 
    requestData: AssignOrderToGroupRequest
  ): Promise<ApiResponse> {
    try {
      const response = await this.apiClient.put(
        `/api/v1/orders/${orderId}/assign-to-group`,
        requestData
      );
      return {
        success: true,
        message: response.data.message
      };
    } catch (error: any) {
      return {
        success: false,
        error: error.response.data.error
      };
    }
  }
}

export const orderApi = new OrderApiService(process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080');
```

## 📊 測試建議

### 1. API 測試

```javascript
// __tests__/orderApi.test.js
import { orderApi } from '../services/orderApi';

describe('Order API', () => {
  test('should create order successfully', async () => {
    const orderData = {
      title: 'Test Order',
      category: 'TASK',
      priority: 'MEDIUM',
      creatorId: 1
    };

    const result = await orderApi.createOrder(orderData);
    
    expect(result.success).toBe(true);
    expect(result.data.id).toBeDefined();
  });

  test('should handle validation errors', async () => {
    const invalidOrderData = {
      title: '', // 空標題應該失敗
      category: 'TASK',
      priority: 'MEDIUM',
      creatorId: 1
    };

    const result = await orderApi.createOrder(invalidOrderData);
    
    expect(result.success).toBe(false);
    expect(result.error).toBeDefined();
  });
});
```

### 2. 組件測試

```javascript
// __tests__/OrderForm.test.js
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { OrderForm } from '../components/OrderForm';

describe('OrderForm', () => {
  test('should show validation errors for empty title', async () => {
    const mockOnSubmit = jest.fn();
    
    render(<OrderForm onSubmit={mockOnSubmit} loading={false} />);
    
    const submitButton = screen.getByText('創建工單');
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText('標題不能為空')).toBeInTheDocument();
    });
    
    expect(mockOnSubmit).not.toHaveBeenCalled();
  });
});
```

## 🔍 調試技巧

### 1. 網路請求監控

```javascript
// 在開發環境中添加請求/回應日誌
if (process.env.NODE_ENV === 'development') {
  apiClient.interceptors.request.use(request => {
    console.log('📤 API Request:', {
      method: request.method,
      url: request.url,
      data: request.data
    });
    return request;
  });

  apiClient.interceptors.response.use(
    response => {
      console.log('📥 API Response:', {
        status: response.status,
        data: response.data
      });
      return response;
    },
    error => {
      console.error('❌ API Error:', {
        status: error.response?.status,
        data: error.response?.data
      });
      return Promise.reject(error);
    }
  );
}
```

### 2. Mock 數據

```javascript
// mocks/orderData.js
export const mockOrders = [
  {
    id: 1,
    title: "修復登入問題",
    description: "用戶反映無法正常登入系統",
    category: "BUG",
    priority: "HIGH",
    status: "PENDING",
    creatorId: 1,
    createdAt: "2024-01-01T09:00:00",
    updatedAt: "2024-01-01T09:00:00"
  },
  // 更多測試數據...
];
```

## 📋 常見問題

### Q: 如何處理身份驗證？
A: 使用 HTTP Basic Authentication，在每個請求頭中包含 `Authorization: Basic <base64-encoded-credentials>`

### Q: 日期格式應該如何處理？
A: 使用 ISO 8601 格式 (例如: `2024-01-15T10:00:00`)，後端會自動解析

### Q: 如何處理檔案上傳？
A: 目前 API 不支援檔案上傳，如有需要請聯繫後端團隊

### Q: API 有頻率限制嗎？
A: 目前沒有實施頻率限制，但建議合理使用避免過度請求

### Q: 如何獲取即時更新？
A: 目前需要定期輪詢 API，未來可能會支援 WebSocket 或 Server-Sent Events

## 📞 支援聯繫

如有任何問題或建議，請聯繫：
- **Email**: support@formflow.com
- **文檔**: 查看 `/docs/api-swagger.yaml` 獲取最新 API 規格
- **GitHub**: 提交 Issue 或 PR

---

*最後更新: 2024-01-01*
*API 版本: v1.0.0*