@echo off
echo Starting Habit Tracker Application...

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed or not in the PATH. Please install Java 8 or higher.
    pause
    exit /b 1
)

REM Check if the compiled class files exist
if not exist "target\classes\com\habittracker\HabitTrackerApp.class" (
    echo Compiled class files not found. Compiling the application...
    
    REM Create target directories
    mkdir target\classes 2>nul
    
    REM Compile the Java files
    javac -d target\classes src\main\java\com\habittracker\*.java
    
    if %ERRORLEVEL% NEQ 0 (
        echo Compilation failed. Please check the error messages above.
        pause
        exit /b 1
    )
    
    REM Copy resources
    mkdir target\classes\resources 2>nul
    copy src\main\resources\*.* target\classes\resources\ >nul
)

REM Run the application
echo Running Habit Tracker Application...
java -cp target\classes com.habittracker.HabitTrackerApp

pause