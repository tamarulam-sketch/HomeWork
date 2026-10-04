
package com.example.taskapp;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class TaskStorage {

    // מגדיר קבוע שמכיל את השם שבו נשמרות המשימות
    private static final String KEY_TASKS = "tasks";

    private SharedPreferences sharedPreferences;

    private Gson gson;

    public TaskStorage(SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.gson = new Gson();
    }

    // פעולה שטוענת את המשימות השמורות ומחזירה אותן ברשימה
    public ArrayList<Task> loadTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        // קוראת את הנתונים השמורים תחת המפתח KEY_TASKS
        String json = sharedPreferences.getString(KEY_TASKS, "");

        // אם אין נתונים שמורים, מחזירה רשימה ריקה
        if (json.equals("")) {
            return tasks;
        }

        try {

            // ממירה את מחרוזת ה-JSON למערך של נתונים
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();

            // עוברת על כל משימה שנמצאת במערך ה-JSON
            for (JsonElement element : array) {

                // הופכת את הנתונים של המשימה לאובייקט JSON
                JsonObject object = element.getAsJsonObject();

                String type = object.get("type").getAsString();

                Task task;

                // קוראת את מזהה המשימה
                int id = object.get("id").getAsInt();

                String title = object.get("title").getAsString();

                String subject = object.get("subject").getAsString();

                String priority = object.get("priority").getAsString();

                String dueDate = object.get("dueDate").getAsString();

                if (type.equals("HomeworkTask")) {

                    //שומר את מספר התרגילים כאינט
                    int exercises = object.get("exercises").getAsInt();

                    // יוצרת אובייקט מסוג HomeworkTask עם הנתונים שנקראו
                    task = new HomeworkTask(
                            id,
                            title,
                            subject,
                            priority,
                            dueDate,
                            exercises
                    );

                }
                else if (type.equals("ExamTask")) {

                    int topics = object.get("topics").getAsInt();

                    task = new ExamTask(
                            id,
                            title,
                            subject,
                            priority,
                            dueDate,
                            topics
                    );

                }
                else {

                    task = new Task(
                            id,
                            title,
                            subject,
                            priority,
                            dueDate
                    );
                }

                // קוראת האם המשימה כבר הושלמה
                boolean done = object.get("done").getAsBoolean();

                task.setDone(done);
                tasks.add(task);
            }

        }
        catch (Exception e) {

            // אם מתרחשת שגיאה בקריאת הנתונים, מחזירה רשימה ריקה
            return new ArrayList<>();
        }

        return tasks;
    }

    // פעולה שמקבלת רשימת משימות ושומרת אותה במכשיר
    public void saveTasks(ArrayList<Task> tasks) {

        // יוצרת מערך JSON שאליו יוכנסו נתוני המשימות
        JsonArray array = new JsonArray();

        // עוברת על כל המשימות ברשימה
        for (Task task : tasks) {

            // יוצרת אובייקט JSON עבור המשימה הנוכחית
            JsonObject object = new JsonObject();

            // מוסיפה את מזהה המשימה לנתונים
            object.addProperty("id", task.getId());

            object.addProperty("title", task.getTitle());

            object.addProperty("subject", task.getSubject());

            object.addProperty("priority", task.getPriority());

            object.addProperty("dueDate", task.getDueDate());

            object.addProperty("done", task.isDone());

            if (task instanceof HomeworkTask) {

                HomeworkTask homeworkTask = (HomeworkTask) task;

                // שומרת את סוג המשימה
                object.addProperty("type", "HomeworkTask");

                object.addProperty("exercises", homeworkTask.getExercises());

            }
            else if (task instanceof ExamTask) {

                ExamTask examTask = (ExamTask) task;

                // שומרת את סוג המשימה
                object.addProperty("type", "ExamTask");

                object.addProperty("topics", examTask.getTopics());

            }
            else {

                object.addProperty("type", "Task");
            }

            // מוסיפה את אובייקט המשימה למערך ה-JSON
            array.add(object);
        }

        // ממירה את מערך ה JSON למחרוזת ושומרת אותה במכשיר
        // זה שומר את השינוי באופן אסינכרוני (במקביל)
        sharedPreferences.edit().putString(KEY_TASKS, gson.toJson(array)).apply();
    }
}
