package com.habittracker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main application class for the Habit Tracker application.
 */
public class HabitTrackerApp extends Application {

    private HabitManager habitManager;
    private VBox habitsContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the habit manager
        habitManager = new HabitManager();
        habitManager.loadHabits(); // Load saved habits

        // Set up the main layout
        BorderPane root = new BorderPane();

        // Create the menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create the main content area
        VBox mainContent = new VBox(10);
        mainContent.setPadding(new Insets(10));

        // Add habit button
        Button addHabitButton = new Button("Add New Habit");
        addHabitButton.setOnAction(e -> showAddHabitDialog());

        // Habits list container
        habitsContainer = new VBox(5);
        habitsContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(habitsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        mainContent.getChildren().addAll(addHabitButton, scrollPane);
        root.setCenter(mainContent);

        // Create the scene
        Scene scene = new Scene(root, 600, 500);

        // Apply CSS styles
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Habit Tracker");
        primaryStage.setOnCloseRequest(e -> {
            habitManager.saveHabits(); // Save habits on exit
        });

        // Display the window
        primaryStage.show();

        // Refresh the habits display
        refreshHabitsDisplay();

        // Set up reminder check
        setupReminderCheck();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> habitManager.saveHabits());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            habitManager.saveHabits();
            Platform.exit();
        });
        fileMenu.getItems().addAll(saveItem, exitItem);

        // Reports menu
        Menu reportsMenu = new Menu("Reports");
        MenuItem weeklyReportItem = new MenuItem("Weekly Report");
        weeklyReportItem.setOnAction(e -> showWeeklyReport());
        MenuItem monthlyReportItem = new MenuItem("Monthly Report");
        monthlyReportItem.setOnAction(e -> showMonthlyReport());
        reportsMenu.getItems().addAll(weeklyReportItem, monthlyReportItem);

        menuBar.getMenus().addAll(fileMenu, reportsMenu);
        return menuBar;
    }

    private void showAddHabitDialog() {
        // Create the dialog
        Dialog<Habit> dialog = new Dialog<>();
        dialog.setTitle("Add New Habit");
        dialog.setHeaderText("Enter details for your new habit");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Habit name");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description (optional)");
        descriptionField.setPrefRowCount(3);

        ComboBox<String> frequencyCombo = new ComboBox<>();
        frequencyCombo.getItems().addAll("Daily", "Weekly", "Monthly");
        frequencyCombo.setValue("Daily");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Frequency:"), 0, 2);
        grid.add(frequencyCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        Platform.runLater(() -> nameField.requestFocus());

        // Convert the result to a habit when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    return null;
                }

                String description = descriptionField.getText().trim();
                String frequency = frequencyCombo.getValue();

                return new Habit(name, description, frequency);
            }
            return null;
        });

        Optional<Habit> result = dialog.showAndWait();

        result.ifPresent(habit -> {
            habitManager.addHabit(habit);
            refreshHabitsDisplay();
        });
    }

    private void refreshHabitsDisplay() {
        habitsContainer.getChildren().clear();

        List<Habit> habits = habitManager.getHabits();
        if (habits.isEmpty()) {
            Label emptyLabel = new Label("No habits added yet. Click 'Add New Habit' to get started.");
            habitsContainer.getChildren().add(emptyLabel);
        } else {
            for (Habit habit : habits) {
                habitsContainer.getChildren().add(createHabitPane(habit));
            }
        }
    }

    private Pane createHabitPane(Habit habit) {
        VBox habitPane = new VBox(5);
        habitPane.setPadding(new Insets(10));
        habitPane.getStyleClass().add("habit-pane");

        // Habit header with name and streak
        HBox header = new HBox(10);
        Label nameLabel = new Label(habit.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label streakLabel = new Label(String.format("Streak: %d days", habit.getCurrentStreak()));
        streakLabel.setStyle("-fx-text-fill: #007700;");

        header.getChildren().addAll(nameLabel, streakLabel);

        // Description
        Label descLabel = new Label(habit.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");

        // Progress indicators
        Label weeklyProgressLabel = new Label(String.format("Last 7 days: %.0f%%", habit.getCompletionRate(7) * 100));
        Label monthlyProgressLabel = new Label(
                String.format("Last 30 days: %.0f%%", habit.getCompletionRate(30) * 100));

        // Check-in buttons for today
        HBox checkInBox = new HBox(10);
        checkInBox.setPadding(new Insets(5, 0, 0, 0));

        Button doneButton = new Button("Mark Done");
        doneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        doneButton.setOnAction(e -> {
            habit.markDone(LocalDate.now());
            refreshHabitsDisplay();
        });

        Button missedButton = new Button("Mark Missed");
        missedButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        missedButton.setOnAction(e -> {
            habit.markMissed(LocalDate.now());
            refreshHabitsDisplay();
        });

        // Disable buttons if already checked in today
        if (habit.isCheckedInToday()) {
            doneButton.setDisable(true);
            missedButton.setDisable(true);
            checkInBox.getChildren().add(new Label("Already checked in today"));
        } else {
            checkInBox.getChildren().addAll(doneButton, missedButton);
        }

        habitPane.getChildren().addAll(header, descLabel, weeklyProgressLabel, monthlyProgressLabel, checkInBox);
        return habitPane;
    }

    private void showWeeklyReport() {
        showReport("Weekly Report", 7);
    }

    private void showMonthlyReport() {
        showReport("Monthly Report", 30);
    }

    private void showReport(String title, int days) {
        Stage reportStage = new Stage();
        reportStage.setTitle(title);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label headerLabel = new Label(title);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Habit> table = new TableView<>();

        TableColumn<Habit, String> nameColumn = new TableColumn<>("Habit");
        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(150);

        TableColumn<Habit, String> streakColumn = new TableColumn<>("Current Streak");
        streakColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getCurrentStreak())));
        streakColumn.setPrefWidth(100);

        TableColumn<Habit, String> completionColumn = new TableColumn<>(String.format("Completion (%d days)", days));
        completionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.1f%%", cellData.getValue().getCompletionRate(days) * 100)));
        completionColumn.setPrefWidth(120);

        table.getColumns().addAll(nameColumn, streakColumn, completionColumn);
        table.getItems().addAll(habitManager.getHabits());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> reportStage.close());

        content.getChildren().addAll(headerLabel, table, closeButton);

        Scene scene = new Scene(content, 400, 500);
        // Apply CSS styles to the report window
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        reportStage.setScene(scene);
        reportStage.show();
    }

    private void setupReminderCheck() {
        // Check for uncompleted habits at a specific time
        Thread reminderThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3600000); // Check every hour

                    // Only show reminders during active hours (8 AM to 10 PM)
                    int hour = java.time.LocalTime.now().getHour();
                    if (hour >= 8 && hour <= 22) {
                        Platform.runLater(() -> {
                            List<Habit> uncheckedHabits = habitManager.getUncheckedHabitsForToday();
                            if (!uncheckedHabits.isEmpty()) {
                                showReminderAlert(uncheckedHabits);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        reminderThread.setDaemon(true); // Set as daemon so it doesn't prevent app from closing
        reminderThread.start();
    }

    private void showReminderAlert(List<Habit> uncheckedHabits) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Habit Reminder");
        alert.setHeaderText("You have uncompleted habits for today");

        StringBuilder content = new StringBuilder();
        content.append("The following habits need to be checked in today:\n\n");

        for (Habit habit : uncheckedHabits) {
            content.append("• ").append(habit.getName()).append("\n");
        }

        alert.setContentText(content.toString());
        alert.show();
    }
}