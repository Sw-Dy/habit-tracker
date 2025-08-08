package com.habittracker;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the collection of habits, including loading and saving them to a
 * file.
 */
public class HabitManager {
    private static final String DATA_FILE = "habits.dat";
    private List<Habit> habits;

    /**
     * Creates a new habit manager.
     */
    public HabitManager() {
        habits = new ArrayList<>();
    }

    /**
     * Adds a new habit to the collection.
     * 
     * @param habit The habit to add
     */
    public void addHabit(Habit habit) {
        habits.add(habit);
        saveHabits(); // Save immediately after adding
    }

    /**
     * Removes a habit from the collection.
     * 
     * @param habit The habit to remove
     */
    public void removeHabit(Habit habit) {
        habits.remove(habit);
        saveHabits(); // Save immediately after removing
    }

    /**
     * Gets the list of all habits.
     * 
     * @return The list of habits
     */
    public List<Habit> getHabits() {
        return new ArrayList<>(habits); // Return a copy to prevent external modification
    }

    /**
     * Gets the list of habits that have not been checked in for today.
     * 
     * @return The list of unchecked habits
     */
    public List<Habit> getUncheckedHabitsForToday() {
        return habits.stream()
                .filter(habit -> !habit.isCheckedInToday())
                .collect(Collectors.toList());
    }

    /**
     * Saves the habits to a file.
     */
    public void saveHabits() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(habits);
            System.out.println("Habits saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving habits: " + e.getMessage());
        }
    }

    /**
     * Loads the habits from a file.
     */
    @SuppressWarnings("unchecked")
    public void loadHabits() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No saved habits found. Starting with an empty list.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            habits = (List<Habit>) ois.readObject();
            System.out.println("Loaded " + habits.size() + " habits.");

            // Update streaks for all habits
            for (Habit habit : habits) {
                // This will recalculate the streaks based on the loaded data
                if (habit.isCheckedInToday()) {
                    boolean todayStatus = habit.getCompletionLog().get(LocalDate.now());
                    if (todayStatus) {
                        habit.markDone(LocalDate.now());
                    } else {
                        habit.markMissed(LocalDate.now());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading habits: " + e.getMessage());
            habits = new ArrayList<>(); // Start with an empty list if there's an error
        }
    }
}