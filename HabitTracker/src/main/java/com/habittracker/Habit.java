package com.habittracker;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a habit that can be tracked daily.
 */
public class Habit implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String frequency;
    private Map<LocalDate, Boolean> completionLog;
    private int currentStreak;
    private int bestStreak;

    /**
     * Creates a new habit with the given name and description.
     * 
     * @param name        The name of the habit
     * @param description A description of the habit
     * @param frequency   The frequency of the habit (daily, weekly, monthly)
     */
    public Habit(String name, String description, String frequency) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.completionLog = new HashMap<>();
        this.currentStreak = 0;
        this.bestStreak = 0;
    }

    /**
     * Gets the name of the habit.
     * 
     * @return The habit name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the habit.
     * 
     * @return The habit description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the frequency of the habit.
     * 
     * @return The habit frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Marks the habit as done for the given date.
     * 
     * @param date The date to mark as done
     */
    public void markDone(LocalDate date) {
        completionLog.put(date, true);
        updateStreaks();
    }

    /**
     * Marks the habit as missed for the given date.
     * 
     * @param date The date to mark as missed
     */
    public void markMissed(LocalDate date) {
        completionLog.put(date, false);
        updateStreaks();
    }

    /**
     * Checks if the habit has been checked in (either done or missed) for today.
     * 
     * @return true if the habit has been checked in for today, false otherwise
     */
    public boolean isCheckedInToday() {
        return completionLog.containsKey(LocalDate.now());
    }

    /**
     * Gets the current streak of consecutive days the habit was completed.
     * 
     * @return The current streak
     */
    public int getCurrentStreak() {
        return currentStreak;
    }

    /**
     * Gets the best streak of consecutive days the habit was completed.
     * 
     * @return The best streak
     */
    public int getBestStreak() {
        return bestStreak;
    }

    /**
     * Gets the completion log for this habit.
     * 
     * @return The completion log
     */
    public Map<LocalDate, Boolean> getCompletionLog() {
        return completionLog;
    }

    /**
     * Updates the current and best streaks based on the completion log.
     */
    private void updateStreaks() {
        currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate date = today;

        // Count backwards from today to find the current streak
        while (completionLog.containsKey(date) && completionLog.get(date)) {
            currentStreak++;
            date = date.minusDays(1);
        }

        // Update best streak if current streak is better
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
    }

    /**
     * Calculates the completion rate over the last n days.
     * 
     * @param days The number of days to calculate the rate for
     * @return The completion rate as a value between 0.0 and 1.0
     */
    public double getCompletionRate(int days) {
        LocalDate today = LocalDate.now();
        int completed = 0;
        int total = 0;

        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays(i);
            if (completionLog.containsKey(date)) {
                total++;
                if (completionLog.get(date)) {
                    completed++;
                }
            }
        }

        return total > 0 ? (double) completed / total : 0.0;
    }
}