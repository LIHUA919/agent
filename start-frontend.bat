@echo off
echo Starting AI Agent Frontend...
echo.

REM 检查Node.js环境
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Node.js is not installed or not in PATH
    echo Please install Node.js 16 or higher
    pause
    exit /b 1
)

REM 进入前端目录
cd chat-agent-ui

echo Installing dependencies...
call npm install
if %errorlevel% neq 0 (
    echo Error: npm install failed
    pause
    exit /b 1
)

echo.
echo Starting frontend development server on http://localhost:3000
echo Press Ctrl+C to stop the service
echo.
call npm start