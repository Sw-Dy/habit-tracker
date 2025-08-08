@echo off
echo Compiling Habit Tracker Application...

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed or not in the PATH. Please install Java 8 or higher.
    pause
    exit /b 1
)

REM Create target directories
mkdir target\classes 2>nul

REM Compile the Java files
echo Compiling Java files...
javac -d target\classes src\main\java\com\habittracker\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed. Please check the error messages above.
    pause
    exit /b 1
)

REM Copy resources
echo Copying resources...
mkdir target\classes\resources 2>nul
copy src\main\resources\*.* target\classes\ >nul

echo Compilation successful. You can now run the application using run.bat
pause