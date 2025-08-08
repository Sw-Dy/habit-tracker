# Habit Tracker Application

A desktop application for tracking daily habits, built with Java and JavaFX.

## Features

- Add and manage habits with names, descriptions, and frequencies
- Daily check-in to mark habits as done or missed
- View current streaks and completion rates
- Weekly and monthly progress reports
- Reminders for uncompleted habits
- Data persistence (habits are saved automatically)

## Requirements

- Java 8 or higher
- JavaFX (included in Java 8, separate dependency for Java 11+)

## Building the Application

### Using Maven

If you have Maven installed, you can build the application with:

```
mvn clean package
```

This will create an executable JAR file in the `target` directory.

### Without Maven

If you don't have Maven installed, you can compile the application manually:

1. Compile the Java files:
   ```
   javac -d target/classes src/main/java/com/habittracker/*.java
   ```

2. Create a JAR file:
   ```
   jar cvf HabitTracker.jar -C target/classes .
   ```

## Running the Application

### Using the JAR file

```
java -jar target/HabitTracker-1.0-SNAPSHOT.jar
```

or

```
java -jar HabitTracker.jar
```

### From the IDE

Run the `HabitTrackerApp` class which contains the `main` method.

## Usage

1. **Adding a Habit**
   - Click the "Add New Habit" button
   - Enter a name, optional description, and frequency
   - Click "Save"

2. **Daily Check-in**
   - For each habit, click "Mark Done" or "Mark Missed" to record your progress
   - Once checked in, you cannot change your status for that day

3. **Viewing Reports**
   - Click on "Reports" in the menu bar
   - Select "Weekly Report" or "Monthly Report" to view your progress

4. **Saving Data**
   - Data is automatically saved when you add/update habits or exit the application
   - You can also manually save by clicking "File" > "Save"

## Data Storage

Habit data is stored in a file named `habits.dat` in the application directory. This file contains serialized Java objects representing your habits and their completion history.

## Troubleshooting

- **Application won't start**: Ensure you have Java 8 or higher installed
- **Data not saving**: Check that the application has write permissions in its directory
- **UI elements not displaying correctly**: Ensure JavaFX is properly installed with your Java version