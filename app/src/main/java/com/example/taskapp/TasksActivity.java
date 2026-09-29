package com.example.taskapp;

import android.app.Dialog;
import android.content.SharedPreferences;
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

    // שלב 8 - כפתור הצגת ניקוד
    private Button btnPoints;

    // שלב 9 - סינון לפי מקצוע
    private Spinner spinnerFilter;
    private Button btnFilter;

    private ArrayList<Task> displayedTasks;
    private ArrayList<Task> tasks;

    private TaskStorage taskStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        listTasks = findViewById(R.id.listTasks);
        btnAddTask = findViewById(R.id.btnAddTask);

        // שלב 8
        btnPoints = findViewById(R.id.btnPoints);
        // שלב 9
        spinnerFilter = findViewById(R.id.spinnerFilter);
        btnFilter = findViewById(R.id.btnFilter);

        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "tasks",
                        MODE_PRIVATE
                );

        taskStorage =
                new TaskStorage(sharedPreferences);

        tasks =
                taskStorage.loadTasks();


        // שלב 9 - הכנסת המקצועות ל-Spinner
        setupFilter();


        // הצגת המשימות
        showTasks();

        // לחיצה על הוספת משימה
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddTaskDialog();
            }
        });

        // שלב 7 - לחיצה על משימה ברשימה
        listTasks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        Task selectedTask =
                                displayedTasks.get(position);

                        showTaskDetailsDialog(
                                selectedTask
                        );
                    }
                }
        );


        // שלב 8 - לחיצה על הצגת ניקוד
        btnPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPoints();
            }
        });


        // שלב 9 - לחיצה על סינון

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTasks();
            }
        });
    }

    // ------------------------------------------------
    // הוספת משימה
    // ------------------------------------------------
    private void showAddTaskDialog() {

        Dialog dialog =
                new Dialog(TasksActivity.this);

        dialog.setTitle("הוספת משימה");

        LinearLayout layout =
                new LinearLayout(TasksActivity.this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );


        // סוג המשימה
        Spinner spinnerType =
                new Spinner(TasksActivity.this);

        ArrayList<String> types =
                new ArrayList<>();

        types.add("משימה");
        types.add("שיעורי בית");
        types.add("מבחן");

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_spinner_item,
                        types
                );

        spinnerType.setAdapter(typeAdapter);

        // שם המשימה
        EditText etTitle =
                new EditText(TasksActivity.this);

        etTitle.setHint("שם המשימה");

        // מקצוע
        EditText etSubject =
                new EditText(TasksActivity.this);

        etSubject.setHint("מקצוע");

        // עדיפות
        Spinner spinnerPriority =
                new Spinner(TasksActivity.this);

        ArrayList<String> priorities =
                new ArrayList<>();

        priorities.add("נמוכה");
        priorities.add("בינונית");
        priorities.add("גבוהה");

        ArrayAdapter<String> priorityAdapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_spinner_item,
                        priorities
                );

        spinnerPriority.setAdapter(priorityAdapter);

        // תאריך
        EditText etDueDate =
                new EditText(TasksActivity.this);

        etDueDate.setHint("תאריך יעד");

        // מספר תרגילים או מספר נושאים
        EditText etExtra =
                new EditText(TasksActivity.this);

        etExtra.setHint(
                "מספר תרגילים / מספר נושאים"
        );

        // כפתור שמירה
        Button btnSave =
                new Button(TasksActivity.this);

        btnSave.setText("שמירה");

        // כפתור ביטול
        Button btnCancel =
                new Button(TasksActivity.this);

        btnCancel.setText("ביטול");

        // הכנסת הכל ל-Dialog
        layout.addView(spinnerType);
        layout.addView(etTitle);
        layout.addView(etSubject);
        layout.addView(spinnerPriority);
        layout.addView(etDueDate);
        layout.addView(etExtra);
        layout.addView(btnSave);
        layout.addView(btnCancel);

        dialog.setContentView(layout);

        // לחיצה על שמירה
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String type =
                        spinnerType
                                .getSelectedItem()
                                .toString();

                String title =
                        etTitle
                                .getText()
                                .toString();

                String subject =
                        etSubject
                                .getText()
                                .toString();

                String priority =
                        spinnerPriority
                                .getSelectedItem()
                                .toString();

                String dueDate =
                        etDueDate
                                .getText()
                                .toString();

                String extraText =
                        etExtra
                                .getText()
                                .toString();


                // בדיקה שהשדות לא ריקים
                if (title.equals("")
                        || subject.equals("")
                        || dueDate.equals("")) {

                    Toast.makeText(
                            TasksActivity.this,
                            "יש למלא את כל הפרטים",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                int id =
                        getNextId();


                if (type.equals("שיעורי בית")) {

                    if (extraText.equals("")) {

                        Toast.makeText(
                                TasksActivity.this,
                                "יש להכניס מספר תרגילים",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    int exercises =
                            Integer.parseInt(extraText);

                    HomeworkTask homeworkTask =
                            new HomeworkTask(
                                    id,
                                    title,
                                    subject,
                                    priority,
                                    dueDate,
                                    exercises
                            );

                    tasks.add(homeworkTask);

                } else if (type.equals("מבחן")) {

                    if (extraText.equals("")) {

                        Toast.makeText(
                                TasksActivity.this,
                                "יש להכניס מספר נושאים",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    int topics =
                            Integer.parseInt(extraText);

                    ExamTask examTask =
                            new ExamTask(
                                    id,
                                    title,
                                    subject,
                                    priority,
                                    dueDate,
                                    topics
                            );

                    tasks.add(examTask);

                } else {

                    Task task =
                            new Task(
                                    id,
                                    title,
                                    subject,
                                    priority,
                                    dueDate
                            );

                    tasks.add(task);
                }

                // שמירת הרשימה
                taskStorage.saveTasks(tasks);

                // עדכון רשימת המקצועות
                setupFilter();

                // הצגה מחדש של הרשימה
                showTasks();

                Toast.makeText(
                        TasksActivity.this,
                        "המשימה נוספה",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();
            }
        });


        // לחיצה על ביטול
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // ------------------------------------------------
    // שלב 7 - הצגת פרטי משימה
    // ------------------------------------------------
    private void showTaskDetailsDialog(Task task) {

        Dialog dialog =
                new Dialog(TasksActivity.this);

        dialog.setTitle("פרטי משימה");

        LinearLayout layout =
                new LinearLayout(TasksActivity.this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        // הצגת פרטי המשימה
        EditText etDetails =
                new EditText(TasksActivity.this);

        String details =
                "סוג: " + task.getTypeName()
                        + "\nשם: " + task.getTitle()
                        + "\nמקצוע: " + task.getSubject()
                        + "\nעדיפות: " + task.getPriority()
                        + "\nתאריך יעד: " + task.getDueDate()
                        + "\nנקודות: " + task.getPoints()
                        + "\nבוצע: " + task.isDone();

        etDetails.setText(details);

        etDetails.setEnabled(false);

        // כפתור סימון כבוצע
        Button btnDone =
                new Button(TasksActivity.this);

        btnDone.setText("סימון כבוצע");

        // כפתור מחיקה
        Button btnDelete =
                new Button(TasksActivity.this);

        btnDelete.setText("מחיקה");

        // כפתור סגירה
        Button btnClose =
                new Button(TasksActivity.this);

        btnClose.setText("סגירה");

        layout.addView(etDetails);
        layout.addView(btnDone);
        layout.addView(btnDelete);
        layout.addView(btnClose);

        dialog.setContentView(layout);

        // סימון המשימה כבוצעה
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (task.isDone()) {

                    Toast.makeText(
                            TasksActivity.this,
                            "המשימה כבר בוצעה",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                task.setDone(true);

                // שמירת השינוי
                taskStorage.saveTasks(tasks);

                Toast.makeText(
                        TasksActivity.this,
                        "המשימה סומנה כבוצעה",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();

                // עדכון הרשימה
                showTasks();
            }
        });

        // מחיקת משימה
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tasks.remove(task);

                // שמירת הרשימה אחרי המחיקה
                taskStorage.saveTasks(tasks);

                Toast.makeText(
                        TasksActivity.this,
                        "המשימה נמחקה",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();

                // עדכון המקצועות
                setupFilter();

                // עדכון הרשימה
                showTasks();
            }
        });

        // סגירת החלון
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // ------------------------------------------------
    // שלב 8 - הצגת הניקוד
    // ------------------------------------------------
    private void showPoints() {

        int totalPoints = 0;

        int completedTasks = 0;

        for (int i = 0; i < tasks.size(); i++) {

            Task task =
                    tasks.get(i);


            // רק משימות שבוצעו נותנות נקודות
            if (task.isDone()) {

                totalPoints =
                        totalPoints
                                + task.getPoints();

                completedTasks++;
            }
        }

        Toast.makeText(
                TasksActivity.this,
                "משימות שבוצעו: "
                        + completedTasks
                        + "\nנקודות: "
                        + totalPoints,
                Toast.LENGTH_LONG
        ).show();
    }

    // ------------------------------------------------
    // שלב 9 - הכנסת המקצועות ל-Spinner
    // ------------------------------------------------
    private void setupFilter() {

        ArrayList<String> subjects =
                new ArrayList<>();

        // האפשרות הראשונה מציגה הכל
        subjects.add("כל המקצועות");

        // מעבר על כל המשימות
        for (int i = 0; i < tasks.size(); i++) {

            Task task =
                    tasks.get(i);

            String subject =
                    task.getSubject();


            // מוסיפים מקצוע רק אם הוא עדיין לא נמצא ברשימה
            if (!subjects.contains(subject)) {

                subjects.add(subject);
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_spinner_item,
                        subjects
                );

        spinnerFilter.setAdapter(adapter);
    }

    // ------------------------------------------------
    // יצירת ID למשימה חדשה
    // ------------------------------------------------
    private int getNextId() {

        int biggestId = 0;

        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i).getId() > biggestId) {

                biggestId =
                        tasks.get(i).getId();
            }
        }

        return biggestId + 1;
    }

    // ------------------------------------------------
    // שלב 9 - הצגת המשימות + סינון
    // ------------------------------------------------
    private void showTasks() {

        displayedTasks =
                new ArrayList<>();


        ArrayList<String> taskTexts =
                new ArrayList<>();


        String selectedSubject =
                "כל המקצועות";

        // בדיקה שיש ערך שנבחר ב-Spinner
        if (spinnerFilter.getSelectedItem() != null) {

            selectedSubject =
                    spinnerFilter
                            .getSelectedItem()
                            .toString();
        }

        // מעבר על כל המשימות
        for (int i = 0; i < tasks.size(); i++) {

            Task task =
                    tasks.get(i);

            // אם נבחר "כל המקצועות"
            // או שהמקצוע של המשימה מתאים לסינון
            if (selectedSubject.equals("כל המקצועות")
                    || task.getSubject().equals(selectedSubject)) {


                displayedTasks.add(task);

                taskTexts.add(
                        task.getTypeName()
                                + " | "
                                + task.toString()
                );
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_list_item_1,
                        taskTexts
                );

        listTasks.setAdapter(adapter);
    }
}