package com.example.taskapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TaskDetailsActivity extends AppCompatActivity {

    private TaskStorage taskStorage;
    private ArrayList<Task> tasks;
    private Task task;
    private TextView tvDetails;
    private Button btnDone;
    private Button btnDelete;
    private Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // חיבור המחלקה לקובץ ה-XML של המסך
        setContentView(R.layout.activity_task_details);

        tvDetails = findViewById(R.id.tvDetails);
        btnDone = findViewById(R.id.btnDone);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);


        // יצירת SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("tasks", MODE_PRIVATE);


        // יצירת אובייקט שאחראי על שמירת המשימות
        taskStorage = new TaskStorage(sharedPreferences);


        // טעינת המשימות
        tasks = taskStorage.loadTasks();

        // קבלת ה-ID שנשלח מ-TasksActivity
        int taskId = getIntent().getIntExtra("taskId", -1);


        // חיפוש המשימה לפי ה-ID
        task = null;

        for (int i = 0; i < tasks.size(); i++) {

            if (tasks.get(i).getId() == taskId) {

                task = tasks.get(i);

                break;
            }
        }


        // אם המשימה לא נמצאה
        if (task == null) {

            Toast.makeText(this, "המשימה לא נמצאה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        // הצגת פרטי המשימה
        String details =
                "סוג: "
                        + task.getTypeName()
                        + "\nשם: "
                        + task.getTitle()
                        + "\nמקצוע: "
                        + task.getSubject()
                        + "\nעדיפות: "
                        + task.getPriority()
                        + "\nתאריך יעד: "
                        + task.getDueDate()
                        + "\nנקודות: "
                        + task.getPoints()
                        + "\nבוצע: "
                        + (task.isDone() ? "כן" : "לא");


        // אם זו משימת שיעורי בית
        if (task instanceof HomeworkTask) {

            HomeworkTask homeworkTask = (HomeworkTask) task;
            details += "\nמספר תרגילים: " + homeworkTask.getExercises();
        }


        // אם זו משימת מבחן
        if (task instanceof ExamTask) {

            ExamTask examTask = (ExamTask) task;
            details += "\nמספר נושאים: " + examTask.getTopics();
        }


        tvDetails.setText(details);

        // לחיצה על סימון כבוצע
        btnDone.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (task.isDone()) {

                            Toast.makeText(
                                    TaskDetailsActivity.this, "המשימה כבר בוצעה", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        task.setDone(true);

                        taskStorage.saveTasks(tasks);

                        Toast.makeText(TaskDetailsActivity.this, "המשימה סומנה כבוצעה", Toast.LENGTH_SHORT).show();

                        // חזרה למסך המשימות
                        finish();
                    }
                }
        );


        // לחיצה על מחיקה
        btnDelete.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        tasks.remove(task);

                        taskStorage.saveTasks(tasks);

                        Toast.makeText(TaskDetailsActivity.this, "המשימה נמחקה", Toast.LENGTH_SHORT).show();

                        // חזרה למסך המשימות
                        finish();
                    }
                }
        );


        btnBack.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        finish();
                    }
                }
        );
    }
}