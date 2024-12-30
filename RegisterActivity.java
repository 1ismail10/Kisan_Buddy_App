package com.example.kisanbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(this::registerUser);
        setStatusBarColor();
    }

    public void registerUser(View view) {
        EditText nameField = findViewById(R.id.nameEditText);
        EditText numberField = findViewById(R.id.numberEditText);
        EditText emailField = findViewById(R.id.emailEditText);
        EditText addressField = findViewById(R.id.addressEditText);
        EditText cityField = findViewById(R.id.cityEditText);
        EditText passwordField = findViewById(R.id.passwordEditText);

        String name = nameField.getText().toString().trim();
        String number = numberField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String city = cityField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(number) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(city)) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "SignIn successful", Toast.LENGTH_SHORT).show();
                        String userId = task.getResult().getUser().getUid();
                        saveUserData(userId, name, number, email, address, city, password);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "This email is already registered", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserData(String userId, String name, String number, String email, String address, String city, String password) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
        User user = new User(name, number, email, address, city, password);
        database.child(userId).setValue(user);
    }

    private void setStatusBarColor() {
        int statusBarColor = ContextCompat.getColor(this, R.color.app_main);
        getWindow().setStatusBarColor(statusBarColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    static class User {
        public String name, number, email, address, city, password;

        public User(String name, String number, String email, String address, String city, String password) {
            this.name = name;
            this.number = number;
            this.email = email;
            this.address = address;
            this.city = city;
            this.password = password;
        }
    }
}
