package com.example.taskapp;

public class HomeworkTask extends Task {

    private int exercises;

    public HomeworkTask(int id,
                        String title,
                        String subject,
                        String priority,
                        String dueDate,
                        int exercises) {

        super(id, title, subject, priority, dueDate);

        this.exercises = exercises;
    }

    @Override
    public String getTypeName() {
        return "שיעורי בית";
    }

    @Override
    public int getPoints() {
        return 10 + exercises * 2 + getPriorityBonus();
    }

    public int getExercises() {
        return exercises;
    }

    @Override
    public String toString() {
        return super.toString() +
                " | תרגילים: " +
                exercises;
    }
}