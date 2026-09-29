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
    private Button btnEnter;
    private Button btnReset;

    private SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        btnEnter = findViewById(R.id.btnEnter);
        btnReset = findViewById(R.id.btnReset);

        userPreferences =
                getSharedPreferences(
                        "user_data",
                        MODE_PRIVATE
                );

        String savedName =
                userPreferences.getString(
                        "user_name",
                        ""
                );

        if (!savedName.equals("")) {

            etName.setText(savedName);

            Toast.makeText(
                    MainActivity.this,
                    "ברוך שוב, " + savedName,
                    Toast.LENGTH_SHORT
            ).show();
        }

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name =
                        etName.getText().toString();

                if (name.length() < 2) {

                    Toast.makeText(
                            MainActivity.this,
                            "יש להכניס שם של לפחות 2 תווים",
                            Toast.LENGTH_SHORT
                    ).show();

                } else {

                    SharedPreferences.Editor editor =
                            userPreferences.edit();

                    editor.putString(
                            "user_name",
                            name
                    );

                    editor.apply();

                    Toast.makeText(
                            MainActivity.this,
                            "השם נשמר",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    TasksActivity.class
                            );

                    startActivity(intent);
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showResetDialog();
            }
        });
    }

    private void showResetDialog() {

        Dialog dialog =
                new Dialog(MainActivity.this);

        dialog.setTitle("איפוס האפליקציה");

        LinearLayout layout =
                new LinearLayout(MainActivity.this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        Button btnConfirm =
                new Button(MainActivity.this);

        btnConfirm.setText("איפוס");

        Button btnCancel =
                new Button(MainActivity.this);

        btnCancel.setText("ביטול");

        layout.addView(btnConfirm);
        layout.addView(btnCancel);

        dialog.setContentView(layout);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userPreferences
                        .edit()
                        .clear()
                        .apply();

                SharedPreferences tasksPreferences =
                        getSharedPreferences(
                                "tasks",
                                MODE_PRIVATE
                        );

                tasksPreferences
                        .edit()
                        .clear()
                        .apply();

                etName.setText("");

                Toast.makeText(
                        MainActivity.this,
                        "הנתונים אופסו",
                        Toast.LENGTH_SHORT
                ).show();

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
}