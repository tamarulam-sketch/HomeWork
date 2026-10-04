package com.example.taskapp;

import android.app.Dialog;
import android.content.Intent;
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
        SharedPreferences sharedPreferences =
                getSharedPreferences(
                        "tasks",
                        MODE_PRIVATE
                );

        // יצירת אובייקט שאחראי על שמירת וטעינת המשימות
        taskStorage =
                new TaskStorage(sharedPreferences);

        // טעינת כל המשימות שנשמרו
        tasks =
                taskStorage.loadTasks();

        // הכנת הסינון לפי מקצוע
        setupFilter();

        // הצגת המשימות
        showTasks();


        // לחיצה על הוספת משימה
        btnAddTask.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        showAddTaskDialog();
                    }
                }
        );


        // לחיצה על כפתור הסינון
        btnFilter.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        showTasks();
                    }
                }
        );


        // לחיצה על כפתור הניקוד
        btnPoints.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        showPoints();
                    }
                }
        );


        // לחיצה על משימה ברשימה
        listTasks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        // קבלת המשימה שעליה המשתמש לחץ
                        Task task =
                                displayedTasks.get(position);

                        // מעבר למסך פרטי המשימה
                        Intent intent =
                                new Intent(
                                        TasksActivity.this,
                                        TaskDetailsActivity.class
                                );

                        // שליחת ה-ID של המשימה למסך החדש
                        intent.putExtra(
                                "taskId",
                                task.getId()
                        );

                        // פתיחת המסך החדש
                        startActivity(intent);
                    }
                }
        );
    }


    // פעולה שמתבצעת כאשר חוזרים למסך המשימות
    @Override
    protected void onResume() {
        super.onResume();

        // טוענים מחדש את המשימות,
        // כדי לראות שינויים שנעשו במסך פרטי המשימה
        if (taskStorage != null) {

            tasks =
                    taskStorage.loadTasks();

            setupFilter();

            showTasks();
        }
    }


    // ------------------------------------------------
    // פעולה שפותחת חלון להוספת משימה חדשה
    // ------------------------------------------------

    private void showAddTaskDialog() {

        // יצירת חלון להוספת משימה
        Dialog dialog =
                new Dialog(TasksActivity.this);

        dialog.setTitle("הוספת משימה");


        // יצירת פריסה אנכית שתכיל את רכיבי החלון
        LinearLayout layout =
                new LinearLayout(
                        TasksActivity.this
                );

        layout.setOrientation(
                LinearLayout.VERTICAL
        );


        // יצירת רשימה נפתחת לבחירת סוג המשימה
        Spinner spinnerType =
                new Spinner(
                        TasksActivity.this
                );


        // יצירת רשימת סוגי המשימות
        ArrayList<String> types =
                new ArrayList<>();

        types.add("משימה");
        types.add("שיעורי בית");
        types.add("מבחן");


        // חיבור רשימת סוגי המשימות ל-Spinner
        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_spinner_item,
                        types
                );

        spinnerType.setAdapter(typeAdapter);


        // יצירת שדה להזנת שם המשימה
        EditText etTitle =
                new EditText(
                        TasksActivity.this
                );

        etTitle.setHint("שם המשימה");


        // יצירת שדה להזנת המקצוע
        EditText etSubject =
                new EditText(
                        TasksActivity.this
                );

        etSubject.setHint("מקצוע");


        // יצירת רשימה נפתחת לבחירת רמת העדיפות
        Spinner spinnerPriority =
                new Spinner(
                        TasksActivity.this
                );


        // יצירת רשימת רמות העדיפות
        ArrayList<String> priorities =
                new ArrayList<>();

        priorities.add("נמוכה");
        priorities.add("בינונית");
        priorities.add("גבוהה");


        // חיבור רשימת העדיפויות ל-Spinner
        ArrayAdapter<String> priorityAdapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_spinner_item,
                        priorities
                );

        spinnerPriority.setAdapter(
                priorityAdapter
        );


        // יצירת שדה להזנת תאריך היעד
        EditText etDueDate =
                new EditText(
                        TasksActivity.this
                );

        etDueDate.setHint("תאריך יעד");


        // יצירת שדה להזנת פרטים נוספים
        EditText etExtra =
                new EditText(
                        TasksActivity.this
                );

        etExtra.setHint(
                "מספר תרגילים / מספר נושאים"
        );


        // יצירת כפתור לשמירת המשימה
        Button btnSave =
                new Button(
                        TasksActivity.this
                );

        btnSave.setText("שמירה");


        // יצירת כפתור לביטול
        Button btnCancel =
                new Button(
                        TasksActivity.this
                );

        btnCancel.setText("ביטול");


        // הוספת כל רכיבי הממשק לחלון
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
        btnSave.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String type =
                                spinnerType
                                        .getSelectedItem()
                                        .toString();


                        String title =
                                etTitle
                                        .getText()
                                        .toString()
                                        .trim();


                        String subject =
                                etSubject
                                        .getText()
                                        .toString()
                                        .trim();


                        String priority =
                                spinnerPriority
                                        .getSelectedItem()
                                        .toString();


                        String dueDate =
                                etDueDate
                                        .getText()
                                        .toString()
                                        .trim();


                        String extraText =
                                etExtra
                                        .getText()
                                        .toString()
                                        .trim();


                        // בדיקה שהשם לא ריק
                        if (title.equals("")) {

                            Toast.makeText(
                                    TasksActivity.this,
                                    "יש להכניס שם משימה",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        // בדיקה שהמקצוע לא ריק
                        if (subject.equals("")) {

                            Toast.makeText(
                                    TasksActivity.this,
                                    "יש להכניס מקצוע",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        // בדיקה שהתאריך לא ריק
                        if (dueDate.equals("")) {

                            Toast.makeText(
                                    TasksActivity.this,
                                    "יש להכניס תאריך יעד",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        // קבלת ID חדש למשימה
                        int id =
                                getNextId();

                        Task newTask;


                        // אם מדובר בשיעורי בית
                        if (type.equals("שיעורי בית")) {

                            // בדיקה שהוכנס מספר תרגילים
                            if (extraText.equals("")) {

                                Toast.makeText(
                                        TasksActivity.this,
                                        "יש להכניס מספר תרגילים",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            int exercises;

                            try {

                                exercises =
                                        Integer.parseInt(
                                                extraText
                                        );

                            } catch (NumberFormatException e) {

                                Toast.makeText(
                                        TasksActivity.this,
                                        "יש להכניס מספר תרגילים תקין",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // בדיקה שמספר התרגילים חיובי
                            if (exercises <= 0) {

                                Toast.makeText(
                                        TasksActivity.this,
                                        "יש להכניס מספר תרגילים תקין",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // יצירת משימת שיעורי בית
                            newTask =
                                    new HomeworkTask(
                                            id,
                                            title,
                                            subject,
                                            priority,
                                            dueDate,
                                            exercises
                                    );


                            // אם מדובר במבחן
                        } else if (type.equals("מבחן")) {

                            // בדיקה שהוכנס מספר נושאים
                            if (extraText.equals("")) {

                                Toast.makeText(
                                        TasksActivity.this,
                                        "יש להכניס מספר נושאים",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            int topics;

                            try {

                                topics =
                                        Integer.parseInt(
                                                extraText
                                        );

                            } catch (NumberFormatException e) {

                                Toast.makeText(
                                        TasksActivity.this,
                                        "יש להכניס מספר נושאים תקין",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // בדיקה שמספר הנושאים חיובי
                            if (topics <= 0) {

                                Toast.makeText(
                                        TasksActivity.this,
                                        "יש להכניס מספר נושאים תקין",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // יצירת משימת מבחן
                            newTask =
                                    new ExamTask(
                                            id,
                                            title,
                                            subject,
                                            priority,
                                            dueDate,
                                            topics
                                    );


                            // אם מדובר במשימה רגילה
                        } else {

                            newTask =
                                    new Task(
                                            id,
                                            title,
                                            subject,
                                            priority,
                                            dueDate
                                    );
                        }


                        // הוספת המשימה לרשימה
                        tasks.add(newTask);


                        // שמירת המשימות
                        taskStorage.saveTasks(tasks);


                        // עדכון רשימת המקצועות
                        setupFilter();


                        // הצגת המשימות מחדש
                        showTasks();


                        // הצגת הודעה עם מספר הנקודות
                        Toast.makeText(
                                TasksActivity.this,
                                "נשמר! המשימה שווה "
                                        + newTask.getPoints()
                                        + " נקודות!!",
                                Toast.LENGTH_LONG
                        ).show();


                        // סגירת חלון ההוספה
                        dialog.dismiss();
                    }
                }
        );


        // לחיצה על ביטול
        btnCancel.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                }
        );


        dialog.show();
    }


    // ------------------------------------------------
    // פעולה שמחזירה ID חדש למשימה
    // ------------------------------------------------

    private int getNextId() {

        int biggestId = 0;


        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i).getId()
                    > biggestId) {

                biggestId =
                        tasks.get(i).getId();
            }
        }


        return biggestId + 1;
    }


    // ------------------------------------------------
    // הצגת הניקוד
    // ------------------------------------------------

    private void showPoints() {

        int totalPoints = 0;

        int completedTasks = 0;


        for (int i = 0; i < tasks.size(); i++) {

            Task task =
                    tasks.get(i);


            // רק משימות שבוצעו נותנות נקודות
            if (task.isDone()) {

                totalPoints +=
                        task.getPoints();

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
    // הכנת הסינון לפי מקצוע
    // ------------------------------------------------

    private void setupFilter() {

        ArrayList<String> subjects =
                new ArrayList<>();


        // אפשרות להציג את כל המקצועות
        subjects.add("כל המקצועות");


        // מעבר על כל המשימות
        for (int i = 0; i < tasks.size(); i++) {

            String subject =
                    tasks.get(i).getSubject();


            // הוספת מקצוע רק אם הוא עדיין לא קיים
            if (!subjects.contains(subject)) {

                subjects.add(subject);
            }
        }


        // יצירת Adapter לסינון
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_spinner_item,
                        subjects
                );


        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        // חיבור ה-Adapter ל-Spinner
        spinnerFilter.setAdapter(adapter);
    }


    // ------------------------------------------------
    // הצגת המשימות ברשימה
    // ------------------------------------------------

    private void showTasks() {

        // יצירת רשימה חדשה של המשימות שיוצגו
        displayedTasks =
                new ArrayList<>();


        // רשימת הטקסטים שיוצגו ב-ListView
        ArrayList<String> taskTexts =
                new ArrayList<>();


        String selectedSubject =
                "כל המקצועות";


        // בדיקה שנבחר מקצוע
        if (spinnerFilter.getSelectedItem()
                != null) {

            selectedSubject =
                    spinnerFilter
                            .getSelectedItem()
                            .toString();
        }


        // מעבר על כל המשימות
        for (int i = 0; i < tasks.size(); i++) {

            Task task =
                    tasks.get(i);


            // בדיקה האם המשימה מתאימה לסינון
            if (selectedSubject.equals(
                    "כל המקצועות")
                    || task.getSubject()
                    .equals(selectedSubject)) {


                // הוספת המשימה לרשימת המשימות המוצגות
                displayedTasks.add(task);


                // הוספת הטקסט שיוצג ברשימה
                taskTexts.add(task.getTypeName() + " | " + task.toString());
            }
        }


        // יצירת Adapter לרשימת המשימות
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        TasksActivity.this,
                        android.R.layout.simple_list_item_1,
                        taskTexts
                );


        // חיבור ה-Adapter ל-ListView
        listTasks.setAdapter(adapter);
    }
}