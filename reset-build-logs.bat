@echo off
REM ============================================================
REM Reset DB, rebuild project, wait for Quarkus, run tests
REM Works with Quarkus 3.31.x + PostgreSQL Docker
REM ============================================================

SET POSTGRES_CONTAINER=postgres
SET POSTGRES_USER=postgres
SET POSTGRES_DB=postgres
SET POSTGRES_PORT=15432

REM 1️⃣ Wait for PostgreSQL to be ready
echo ============================================================
echo Waiting for PostgreSQL to accept connections...

:pg_ready
docker exec -i %POSTGRES_CONTAINER% pg_isready -U %POSTGRES_USER% -d %POSTGRES_DB% >nul 2>&1
IF ERRORLEVEL 1 (
    timeout /t 2 >nul
    goto pg_ready
)
echo PostgreSQL is ready!

REM 2️⃣ Optional: Drop warehouse table inside Docker (skip if using auto-generation)
echo ============================================================
echo Dropping warehouse table inside Docker (if exists)...
docker exec -i %POSTGRES_CONTAINER% psql -U %POSTGRES_USER% -d %POSTGRES_DB% -c "DROP TABLE IF EXISTS warehouse CASCADE;"

REM 3️⃣ Recreate warehouse sequence safely
echo ============================================================
echo Creating warehouse sequence inside Docker...
docker exec -i %POSTGRES_CONTAINER% psql -U %POSTGRES_USER% -d %POSTGRES_DB% -c "CREATE SEQUENCE IF NOT EXISTS warehouse_seq START WITH 1;"

REM 4️⃣ Build the project
echo ============================================================
echo Running Maven clean package...
mvnw clean package -Dquarkus.http.test-port=8085 --no-transfer-progress

IF %ERRORLEVEL% NEQ 0 (
    echo Maven build failed. See logs above.
    pause
    exit /b 1
)

REM 5️⃣ Wait for Quarkus to be ready
echo ============================================================
echo Waiting for Quarkus application to be ready on port 8085...

SET QUARKUS_READY=0
FOR /L %%i IN (1,1,30) DO (
    powershell -Command "$response = try { Invoke-WebRequest -Uri 'http://localhost:8085/q/health/ready' -UseBasicParsing; $response.StatusCode } catch { 0 }; exit $response" > temp_status.txt
    SET /P STATUS=<temp_status.txt
    IF "%STATUS%"=="200" (
        SET QUARKUS_READY=1
        GOTO quarkus_ready
    )
    timeout /t 2 >nul
)
:quarkus_ready
IF "%QUARKUS_READY%"=="0" (
    echo Quarkus did not start within 60 seconds. Exiting.
    exit /b 1
)
echo Quarkus is ready!

REM 6️⃣ Run integration tests
echo ============================================================
echo Running Maven verify (tests + coverage)...
mvnw verify -Dquarkus.coverage -Dquarkus.http.test-port=8085 --no-transfer-progress

IF %ERRORLEVEL% NEQ 0 (
    echo Maven tests failed. See logs above.
    pause
    exit /b 1
)

echo ============================================================
echo Build and tests finished successfully!
echo ============================================================
pause
