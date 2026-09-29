package com.example.taskapp;

public class ExamTask extends Task {

    private int topics;

    public ExamTask(int id,
                    String title,
                    String subject,
                    String priority,
                    String dueDate,
                    int topics) {

        super(id, title, subject, priority, dueDate);

        this.topics = topics;
    }

    @Override
    public String getTypeName() {
        return "מבחן";
    }

    @Override
    public int getPoints() {
        return 20 + topics * 3 + getPriorityBonus();
    }

    public int getTopics() {
        return topics;
    }

    @Override
    public String toString() {
        return super.toString() +
                " | נושאים: " +
                topics;
    }
}