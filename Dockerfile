# 多階段構建 Dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build

# 設置工作目錄
WORKDIR /app

# 複製 pom.xml 並下載依賴（利用 Docker 快取層）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 複製原始碼
COPY src ./src

# 構建應用程式
RUN mvn clean package -DskipTests

# 運行階段
FROM eclipse-temurin:17-jre-alpine

# 安裝必要工具
RUN apk add --no-cache \
    curl \
    netcat-openbsd \
    && rm -rf /var/cache/apk/*

# 創建非 root 用戶
RUN addgroup -g 1001 -S formflow && \
    adduser -S formflow -u 1001 -G formflow

# 設置工作目錄
WORKDIR /app

# 從構建階段複製 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 更改所有者
RUN chown -R formflow:formflow /app

# 切換到非 root 用戶
USER formflow

# 健康檢查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 設置 JVM 參數
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"

# 啟動應用程式
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]