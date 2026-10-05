package com.example.taskapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    private ListView listTasks;
    private Button btnAddTask;
    private Button btnFilter;
    private Button btnPoints;
    private Spinner spinnerFilter;
    private ArrayList<Task> tasks;
    private ArrayList<Task> displayedTasks;
    private TaskStorage taskStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        listTasks = findViewById(R.id.listTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnFilter = findViewById(R.id.btnFilter);
        btnPoints = findViewById(R.id.btnPoints);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        // יצירת SharedPreferences לשמירת המשימות
        SharedPreferences sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE);

        taskStorage = new TaskStorage(sharedPreferences);

        tasks = taskStorage.loadTasks();

        // הכנת הסינון לפי מקצוע
        setupFilter();

        // הצגת המשימות
        showTasks();

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TasksActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });



        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTasks();
            }
        });

        btnPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPoints();
            }
        });

        listTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // קבלת המשימה שעליה המשתמש לחץ
                Task task = displayedTasks.get(position);

                Intent intent = new Intent(TasksActivity.this, TaskDetailsActivity.class);

                // שליחת ה-ID של המשימה למסך החדש
                intent.putExtra("taskId", task.getId());

                startActivity(intent);
            }
        });

        listTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Task task = displayedTasks.get(position);

                showDeleteDialog(task);

                return true;
            }
        });
    }

    // פעולה שמתבצעת כאשר חוזרים למסך המשימות
    @Override
    protected void onResume() {
        super.onResume();

        // טוענים מחדש את המשימות כדי לראות שינויים שנעשו במסך פרטי המשימה
        if (taskStorage != null) {
            tasks = taskStorage.loadTasks();
            setupFilter();
            showTasks();
        }
    }

    // פעולה שמחזירה ID חדש למשימה
    private int getNextId() {
        int biggestId = 0;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() > biggestId) {
                biggestId = tasks.get(i).getId();
            }
        }

        return biggestId + 1;
    }

    // הצגת הניקוד
    private void showPoints() {
        int totalPoints = 0;
        int completedTasks = 0;

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (task.isDone()) {
                totalPoints += task.getPoints();
                completedTasks++;
            }
        }

        Toast.makeText(TasksActivity.this,
                "משימות שבוצעו: " + completedTasks + "\nנקודות: " + totalPoints,
                Toast.LENGTH_LONG).show();
    }

    // פעולה שפותחת חלון למחיקת משימה
    private void showDeleteDialog(Task task) {
        Dialog dialog = new Dialog(TasksActivity.this);

        dialog.setTitle("מחיקת משימה");

        LinearLayout layout = new LinearLayout(TasksActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Button btnDelete = new Button(TasksActivity.this);
        btnDelete.setText("מחיקה");

        Button btnCancel = new Button(TasksActivity.this);
        btnCancel.setText("ביטול");

        layout.addView(btnDelete);
        layout.addView(btnCancel);

        dialog.setContentView(layout);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tasks.remove(task);
                taskStorage.saveTasks(tasks);
                setupFilter();
                showTasks();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // הכנת הסינון לפי מקצוע
    private void setupFilter() {
        ArrayList<String> subjects = new ArrayList<>();

        subjects.add("כל המקצועות");

        for (int i = 0; i < tasks.size(); i++) {
            String subject = tasks.get(i).getSubject();

            // הוספת מקצוע רק אם הוא עדיין לא קיים
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        //לוקח את הנתונים שנמצאים ב subjects ומחבר אותם לספינר כדי להציג אותם
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                TasksActivity.this, android.R.layout.simple_spinner_item, subjects);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // חיבור ה-Adapter ל-Spinner
        spinnerFilter.setAdapter(adapter);
    }

    // הצגת המשימות ברשימה
    private void showTasks() {

        displayedTasks = new ArrayList<>();

        // רשימת הטקסטים שיוצגו ב-ListView
        ArrayList<String> taskTexts = new ArrayList<>();

        String selectedSubject = "כל המקצועות";

        //לבדוק איזה מקצוע המשתמש בחר באופציות של המקצועות
        if (spinnerFilter.getSelectedItem() != null) {
            selectedSubject = spinnerFilter.getSelectedItem().toString();
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (selectedSubject.equals("כל המקצועות") ||
                    task.getSubject().equals(selectedSubject)) {

                displayedTasks.add(task);

                // הוספת הטקסט שיוצג ברשימה
                taskTexts.add(task.getTypeName() + " | " + task.toString());
            }
        }

        //תשתמש ב Adapter כדי לקחת את הנתונים מה ListView ולהציג את המשימות
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                TasksActivity.this, android.R.layout.simple_list_item_1, taskTexts);

        // חיבור ה-Adapter ל-ListView
        listTasks.setAdapter(adapter);
    }
}