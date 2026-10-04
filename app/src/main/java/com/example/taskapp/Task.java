package com.example.taskapp;

// מחלקת בסיס למשימה רגילה
public class Task implements Rewardable {
    private int id;
    private String title;
    private String subject;
    private String priority;
    private String dueDate;
    private boolean done;

    public Task(int id, String title, String subject, String priority, String dueDate) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }

    public String getTypeName() { return "משימה"; }

    @Override
    public int getPoints() { return 10 + getPriorityBonus(); }

    protected int getPriorityBonus() {
        if (priority.equals("גבוהה")) return 10;
        if (priority.equals("בינונית")) return 5;
        return 0;
    }

    @Override
    public String toString() {
        return title + " | " + subject + " | " + priority + " | " + dueDate + " | " + (done ? "בוצע" : "לא בוצע");
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public boolean isDone() { return done; }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public void setTitle(String title) { this.title = title; }
}