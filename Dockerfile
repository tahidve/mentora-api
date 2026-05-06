# ==============================================================
# MENTORA BACKEND — Dockerfile (Pre-built JAR approach)
# JAR được build trên máy local trước bằng: ./gradlew clean build -x test
# Sau đó Docker chỉ đóng gói JAR đã có sẵn, không cần build lại
# ==============================================================

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy JAR đã build sẵn từ thư mục build/libs/
COPY build/libs/*-SNAPSHOT.jar app.jar

# Tạo thư mục upload
RUN mkdir -p /app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
