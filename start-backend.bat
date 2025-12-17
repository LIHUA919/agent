@echo off
echo Starting AI Agent Backend Service...
echo.

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven 3.6+
    pause
    exit /b 1
)

REM 创建日志目录
if not exist "logs" mkdir logs

REM 创建截图目录
if not exist "screenshots" mkdir screenshots

REM 进入后端目录
cd backend

echo Building backend project...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo Error: Maven compilation failed
    echo Please check your Maven installation and dependencies
    pause
    exit /b 1
)

echo Starting backend service on http://localhost:8080
echo Press Ctrl+C to stop the service
echo.
mvn spring-boot:run