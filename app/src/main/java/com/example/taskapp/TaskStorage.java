package com.example.taskapp;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

public class TaskStorage {

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public TaskStorage(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        gson = new Gson();
    }

    public void saveTasks(ArrayList<Task> tasks) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.putInt("count", tasks.size());

        for (int i = 0; i < tasks.size(); i++) {

            Task task = tasks.get(i);

            editor.putString(
                    "type_" + i,
                    task.getTypeName()
            );

            editor.putString(
                    "task_" + i,
                    gson.toJson(task)
            );
        }

        editor.apply();
    }

    public ArrayList<Task> loadTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        int count = sharedPreferences.getInt("count", 0);

        for (int i = 0; i < count; i++) {

            String type =
                    sharedPreferences.getString(
                            "type_" + i,
                            ""
                    );

            String json =
                    sharedPreferences.getString(
                            "task_" + i,
                            ""
                    );

            if (type.equals("שיעורי בית")) {

                HomeworkTask task =
                        gson.fromJson(
                                json,
                                HomeworkTask.class
                        );

                tasks.add(task);

            } else if (type.equals("מבחן")) {

                ExamTask task =
                        gson.fromJson(
                                json,
                                ExamTask.class
                        );

                tasks.add(task);

            } else {

                Task task =
                        gson.fromJson(
                                json,
                                Task.class
                        );

                tasks.add(task);
            }
        }

        return tasks;
    }
}