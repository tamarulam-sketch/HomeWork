package com.example.taskapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

        SharedPreferences sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE);

        //מכניסה לtaskStorge את אובייקט השמירה
        taskStorage = new TaskStorage(sharedPreferences);

        //טוען את המשימות השמורות ומניס אותם לtasks
        tasks = taskStorage.loadTasks();

        //יוצר את הרשימה של המקצועות
        setupFilter();
        showTasks();

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
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

        listTasks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Task task = displayedTasks.get(position);
                        showTaskDetailsDialog(task);
                    }
                }
        );
    }

    // מעבר למסך יצירת משימה
    private void showAddTaskDialog() {

        Intent intent = new Intent(TasksActivity.this, AddTaskActivity.class);

        startActivity(intent);
    }

    // עדכון רשימת המשימות כשחוזרים למסך
    @Override
    protected void onResume() {
        super.onResume();

        if (taskStorage != null) {
            tasks = taskStorage.loadTasks();
            setupFilter();
            showTasks();
        }
    }

    private void showTaskDetailsDialog(Task task) {

        Dialog dialog = new Dialog(TasksActivity.this);
        dialog.setTitle("פרטי משימה");

        //הגדרת מבנה התצוגה
        LinearLayout layout = new LinearLayout(TasksActivity.this);

        //מגדירה שהתצוגה תהיה אנכית
        layout.setOrientation(LinearLayout.VERTICAL);

        // יצירת שדה טקסט להקלדת פרטים
        EditText etDetails = new EditText(TasksActivity.this);

        String details =
                "סוג: " + task.getTypeName()
                        + "\nשם: " + task.getTitle()
                        + "\nמקצוע: " + task.getSubject()
                        + "\nעדיפות: " + task.getPriority()
                        + "\nתאריך יעד: " + task.getDueDate()
                        + "\nנקודות: " + task.getPoints()
                        + "\nבוצע: " + (task.isDone() ? "כן" : "לא");

        if (task instanceof HomeworkTask)
        {
            HomeworkTask homeworkTask = (HomeworkTask) task;

            details += "\nמספר תרגילים: "
                    + homeworkTask.getExercises();

        }
        else if (task instanceof ExamTask)
        {

            ExamTask examTask = (ExamTask) task;

            details += "\nמספר נושאים: "
                    + examTask.getTopics();
        }

        etDetails.setText(details);

        //שהמשתמש לא יוכל לערוך את התוכן בחלון הקטן שקופץ
        etDetails.setEnabled(false);

        Button btnDone = new Button(TasksActivity.this);
        btnDone.setText("סימון כבוצע");

        Button btnDelete = new Button(TasksActivity.this);
        btnDelete.setText("מחיקה");

        Button btnClose = new Button(TasksActivity.this);
        btnClose.setText("סגירה");

      //הוספת הכפתורים ושדה הטקסט למסך
        layout.addView(etDetails);
        layout.addView(btnDone);
        layout.addView(btnDelete);
        layout.addView(btnClose);

        //הצגת העיצוב בתוך החלון הקופץ
        dialog.setContentView(layout);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (task.isDone()) {
                    Toast.makeText(
                            TasksActivity.this, "המשימה כבר בוצעה", Toast.LENGTH_SHORT).show();

                    return;
                }

                task.setDone(true);
                taskStorage.saveTasks(tasks);

                Toast.makeText(
                        TasksActivity.this, "המשימה סומנה כבוצעה", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                showTasks();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tasks.remove(task);
                taskStorage.saveTasks(tasks);

                setupFilter();
                showTasks();

                Toast.makeText(
                        TasksActivity.this,
                        "המשימה נמחקה",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //הצגת נקודות (חובת מימוש בגלל הממשק)
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

        Toast.makeText(
                TasksActivity.this,
                "משימות שבוצעו: " + completedTasks
                        + "\nנקודות: " + totalPoints,
                Toast.LENGTH_LONG
        ).show();
    }

    //בניית המסנן לרשימת המקצועות
    private void setupFilter() {

        ArrayList<String> subjects = new ArrayList<>();
        subjects.add("כל המקצועות");

        for (int i = 0; i < tasks.size(); i++) {

            String subject = tasks.get(i).getSubject();

            // הוספת המקצוע לרשימה רק אם הוא עדיין לא קיים בה
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        // הצגת רשימת המקצועות בתיבת הבחירה
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TasksActivity.this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
    }

    //הצגת המשימות המתאימות
    private void showTasks() {

        displayedTasks = new ArrayList<>();

        ArrayList<String> taskTexts = new ArrayList<>();

        String selectedSubject = "כל המקצועות";

        //ברגע שמישהו בחר נושא זה הנושא שיהיה
        if (spinnerFilter.getSelectedItem() != null) {
            selectedSubject = spinnerFilter.getSelectedItem().toString();
        }

        for (int i = 0; i < tasks.size(); i++)
        {

            Task task = tasks.get(i);

            if (selectedSubject.equals("כל המקצועות") || task.getSubject().equals(selectedSubject))
            {
                displayedTasks.add(task);

                //יוצא את הטקסט שיוצג עבור המשימה
                taskTexts.add(task.getTypeName() + " | " + task.toString());
            }
        }

        //יוצרים ArrayAdapter שתפקידו ללבר בין רשימת הטקסטים לבין רכיב התצוגה
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TasksActivity.this,
                android.R.layout.simple_list_item_1, taskTexts);

        listTasks.setAdapter(adapter);
    }
}
