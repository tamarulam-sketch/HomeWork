
package com.example.taskapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {

    private Spinner spinnerType;
    private Spinner spinnerPriority;

    private EditText etTitle;
    private EditText etSubject;
    private EditText etDueDate;
    private EditText etExtra;

    private Button btnSave;
    private Button btnCancel;

    private TaskStorage taskStorage;
    private ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        spinnerType = findViewById(R.id.spinnerType);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        etTitle = findViewById(R.id.etTitle);
        etSubject = findViewById(R.id.etSubject);
        etDueDate = findViewById(R.id.etDueDate);
        etExtra = findViewById(R.id.etExtra);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        //טעינת המשימות השמורות
        SharedPreferences prefs = getSharedPreferences("tasks", MODE_PRIVATE);

        taskStorage = new TaskStorage(prefs);
        tasks = taskStorage.loadTasks();

        //הגדרת אפשרויות סוג המשימה (נמצא במערך)
        String[] types = {
                "משימה",
                "שיעורי בית",
                "מבחן"
        };

        //יצירת ArrayAdapter שמחבר בין המערך type לבין הSpinner, כדי שהמשתמש יוכל לבחור סוג משימה מהרשימה
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);


        //הגדרת רמת עדיפות
        String[] priorities = {
                "נמוכה",
                "בינונית",
                "גבוהה"
        };

        //מחבר בין המערך לספינר כדי שהמשתמש יוכל לבחור רמת עדיפות
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        //הפעולה בודקת איזה משימה המשתמש בחר ומתאימה את שדה המידע בהתאם
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {
                            etExtra.setVisibility(View.GONE);
                        }
                        else {
                            etExtra.setVisibility(View.VISIBLE);

                            if (position == 1)
                            {
                                etExtra.setHint("מספר תרגילים");
                            }
                            else {
                                etExtra.setHint("מספר נושאים");
                            }
                        }
                    }

                    //פעולה שמתבצעת כשלא נבחרה אפשרות ב־Spinner. במקרה הזה לא קורה כלום (חובת שימוש בגלל ממשק)
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                    }
                }
        );

        //מה שיקרה אם אלחץ על הכפתורים
        btnSave.setOnClickListener(v -> saveTask());

        btnCancel.setOnClickListener(v -> finish());
    }

    //הפעולה קוראת את הנתונים שהמשתמש הזין ושומרת אותם במשתנים
    private void saveTask() {

        String type = spinnerType.getSelectedItem().toString();
        String title = etTitle.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String dueDate = etDueDate.getText().toString().trim();
        String extraText = etExtra.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("יש להכניס שם משימה");
            return;
        }

        if (subject.isEmpty()) {
            etSubject.setError("יש להכניס מקצוע");
            return;
        }

        if (dueDate.isEmpty()) {
            etDueDate.setError("יש להכניס תאריך יעד");
            return;
        }

        //יצירת ID לכל משתנה למתרת זיהוי
        int id = getNextId();
        Task newTask;

        if (type.equals("שיעורי בית")) {

            if (extraText.isEmpty()) {
                etExtra.setError("יש להכניס מספר תרגילים");
                return;
            }

            int exercises;

            //אם הכנסתי משהו שלא יכול להיות מומר לINT תציג שגיאה-
            try {
                exercises = Integer.parseInt(extraText);
            } catch (NumberFormatException e) {
                etExtra.setError("יש להכניס מספר תרגילים תקין");
                return;
            }

            if (exercises <= 0) {
                etExtra.setError("יש להכניס מספר חיובי");
                return;
            }

            newTask = new HomeworkTask(
                    id,
                    title,
                    subject,
                    priority,
                    dueDate,
                    exercises
            );

        } else if (type.equals("מבחן")) {

            if (extraText.isEmpty()) {
                etExtra.setError("יש להכניס מספר נושאים");
                return;
            }

            int topics;

            try {
                topics = Integer.parseInt(extraText);
            } catch (NumberFormatException e) {
                etExtra.setError("יש להכניס מספר נושאים תקין");
                return;
            }

            if (topics <= 0) {
                etExtra.setError("יש להכניס מספר חיובי");
                return;
            }

            newTask = new ExamTask(
                    id,
                    title,
                    subject,
                    priority,
                    dueDate,
                    topics
            );

        }
        else {

            newTask = new Task(
                    id,
                    title,
                    subject,
                    priority,
                    dueDate
            );
        }

        tasks.add(newTask);
        taskStorage.saveTasks(tasks);

        Toast.makeText(this, "נשמר! המשימה שווה " + newTask.getPoints() + " נקודות!!", Toast.LENGTH_LONG).show();

        finish();
    }

    //קביעת ID לטאסק
    private int getNextId() {

        int biggestId = 0;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() > biggestId) {
                biggestId = tasks.get(i).getId();
            }
        }

        return biggestId + 1;
    }
}
