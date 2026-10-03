package com.example.taskapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnLogin;
    private Button btnReset;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);

        //קובע שהריכיבים יסודרו אחד אחרי השני
        layout.setOrientation(LinearLayout.VERTICAL);

        //קובע את הרווח בין התוכן לצדדים של המסך
        layout.setPadding(
                30,
                30,
                30,
                30
        );

        etName = new EditText(this);

        etName.setHint("שם משתמש");

        btnLogin = new Button(this);

        btnLogin.setText("כניסה");

        btnReset = new Button(this);

        btnReset.setText("איפוס");

        //הוספת הרכיבים לlayout
        layout.addView(etName);
        layout.addView(btnLogin);
        layout.addView(btnReset);

        //מציג את מה שהכנסו לlayout כך שהמתמש יוכל לראות
        setContentView(layout);

        // טעינת שם המשתמש שנשמר בעבר
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        //קריאת שםהמתמש שנשמק תחת username
        String savedName = sharedPreferences.getString("username", "");

        //כל עוד השדה של השם לא ריק תשמור את השם שהוכנס
        if (!savedName.equals("")) {
            etName.setText(savedName);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String name = etName.getText().toString().trim();

                        if (name.length() < 2) {

                            Toast.makeText(MainActivity.this, "יש להכניס שם של 2 תווים לפחות", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //שומרת את הערך name תחת המפתח username
                        sharedPreferences.edit().putString("username", name).apply();

                        Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                        startActivity(intent);
                    }
                }
        );

        btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showResetDialog();
                    }
                }
        );
    }

    private void showResetDialog() {

        Dialog dialog = new Dialog(MainActivity.this);

        dialog.setTitle("איפוס");

        LinearLayout layout = new LinearLayout(MainActivity.this);

        layout.setOrientation(LinearLayout.VERTICAL);

        EditText message = new EditText(MainActivity.this);

        message.setText("האם אתה בטוח שברצונך לאפס את הנתונים?");

        message.setEnabled(false);

        Button btnYes = new Button(MainActivity.this);
        btnYes.setText("כן");

        Button btnNo = new Button(MainActivity.this);
        btnNo.setText("לא");

        //הוספת הרכיבים לlayout
        layout.addView(message);
        layout.addView(btnYes);
        layout.addView(btnNo);

        dialog.setContentView(layout);

        btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //מוחק את כל הנתונים השמורים בקובץ
                        sharedPreferences.edit().clear().apply();

                        //שומר את הגישה לקובץ במשתנה tasksPreferences
                        SharedPreferences tasksPreferences = getSharedPreferences("tasks", MODE_PRIVATE);

                        tasksPreferences.edit().clear().apply();

                        etName.setText("");

                        Toast.makeText(MainActivity.this, "הנתונים אופסו", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                }
        );

        btnNo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                }
        );
        dialog.show();
    }
}