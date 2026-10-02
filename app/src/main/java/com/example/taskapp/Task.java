package com.example.taskapp;

// מחלקת בסיס למשימה רגילה.
public class Task implements Rewardable {
    private int id;
    private String title;
    private String subject;
    private String priority;
    private String dueDate;
    private boolean done;

    // בנאי שמקבל את פרטי המשימה ומאתחל אותה כלא בוצעה.
    public Task(int id, String title, String subject, String priority, String dueDate) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }

    // מחזירה את סוג המשימה.
    public String getTypeName() { return "משימה"; }

    // מחשבת את הנקודות של משימה רגילה.
    @Override
    public int getPoints() { return 10 + getPriorityBonus(); }

    // מחשבת בונוס לפי רמת העדיפות.
    protected int getPriorityBonus() {
        if (priority.equals("גבוהה")) return 10;
        if (priority.equals("בינונית")) return 5;
        return 0;
    }

    // מחזירה את פרטי המשימה כמחרוזת להצגה ברשימה.
    @Override
    public String toString() {
        return title + " | " + subject + " | " + priority + " | " + dueDate + " | " + (done ? "בוצע" : "לא בוצע");
    }

    // פעולות שמחזירות את פרטי המשימה.
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public boolean isDone() { return done; }

    // מעדכנת את מצב המשימה.
    public void setDone(boolean done)
    {
        this.done = done;
    }

    // מעדכנת את שם המשימה.
    public void setTitle(String title) { this.title = title; }
}