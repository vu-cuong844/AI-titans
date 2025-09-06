@echo off
echo Running Knowledge Sharing Platform Tests...

echo.
echo Running Auth Service Tests...
cd auth-service
mvn test
if %errorlevel% neq 0 (
    echo Auth Service tests failed!
    pause
    exit /b 1
)

echo.
echo Running Document Service Tests...
cd ../document-service
mvn test
if %errorlevel% neq 0 (
    echo Document Service tests failed!
    pause
    exit /b 1
)

echo.
echo All tests completed successfully!
pause
