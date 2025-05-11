package com.example.hisaabkitaab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsernameRegister;
    private EditText editTextPasswordRegister;
    private EditText editTextConfirmPasswordRegister;
    private Button buttonRegister;
    private TextView textViewLoginLink;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // The 'main' ID in activity_register.xml is mainRegister
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);

        editTextUsernameRegister = findViewById(R.id.editTextUsernameRegister);
        editTextPasswordRegister = findViewById(R.id.editTextPasswordRegister);
        editTextConfirmPasswordRegister = findViewById(R.id.editTextConfirmPasswordRegister);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity, for now navigating to Login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Optional: finish RegisterActivity so user can't go back
            }
        });
    }

    private void registerUser() {
        String username = editTextUsernameRegister.getText().toString().trim();
        String password = editTextPasswordRegister.getText().toString().trim();
        String confirmPassword = editTextConfirmPasswordRegister.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkUser(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // In a real app, hash the password before storing

            databaseHelper.addUser(user);
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            // Optionally, navigate to login screen or main activity
            // For example, navigate to MainActivity after successful registration
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}