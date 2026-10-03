package com.example.taskapp;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class TaskStorage {

    //מגדיר קבוע בשם KEY_TASKS שערכו tasks
    private static final String KEY_TASKS = "tasks";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public TaskStorage(SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.gson = new Gson();
    }

    public ArrayList<Task> loadTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        String json =
                sharedPreferences.getString(KEY_TASKS, "");

        if (json.equals("")) {
            return tasks;
        }

        try {

            JsonArray array = new JsonParser().parse(json).getAsJsonArray();

            for (JsonElement element : array) {

                JsonObject object = element.getAsJsonObject();

                String type = object.get("type").getAsString();

                Task task;

                int id = object.get("id").getAsInt();

                String title = object.get("title").getAsString();

                String subject = object.get("subject").getAsString();

                String priority = object.get("priority").getAsString();

                String dueDate = object.get("dueDate").getAsString();

                if (type.equals("HomeworkTask")) {

                    int exercises = object.get("exercises").getAsInt();

                    task = new HomeworkTask(
                            id,
                            title,
                            subject,
                            priority,
                            dueDate,
                            exercises
                    );

                } else if (type.equals("ExamTask")) {

                    int topics = object.get("topics").getAsInt();

                    task = new ExamTask(
                            id,
                            title,
                            subject,
                            priority,
                            dueDate,
                            topics
                    );

                } else {

                    task = new Task(
                            id,
                            title,
                            subject,
                            priority,
                            dueDate
                    );
                }

                boolean done = object.get("done").getAsBoolean();

                task.setDone(done);

                tasks.add(task);
            }

        } catch (Exception e) {

            return new ArrayList<>();
        }

        return tasks;
    }

    public void saveTasks(ArrayList<Task> tasks) {

        JsonArray array = new JsonArray();

        for (Task task : tasks) {

            JsonObject object = new JsonObject();

            object.addProperty("id", task.getId());

            object.addProperty("title", task.getTitle());

            object.addProperty("subject", task.getSubject());

            object.addProperty("priority", task.getPriority());

            object.addProperty("dueDate", task.getDueDate());

            object.addProperty("done", task.isDone());

            if (task instanceof HomeworkTask) {

                HomeworkTask homeworkTask = (HomeworkTask) task;

                object.addProperty("type", "HomeworkTask");

                object.addProperty("exercises", homeworkTask.getExercises());

            }
            else if (task instanceof ExamTask) {

                ExamTask examTask = (ExamTask) task;

                object.addProperty("type", "ExamTask");

                object.addProperty("topics", examTask.getTopics());

            }
            else
            {

                object.addProperty("type", "Task");
            }

            array.add(object);
        }

        sharedPreferences.edit().putString(KEY_TASKS, gson.toJson(array)).apply();
    }
}