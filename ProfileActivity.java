package com.example.kisanbuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextAddress, editTextCity, editPhoneNumber;
    private Button buttonUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeFields();
        loadUserProfile();
        setStatusBarColor();

        buttonUpdate.setOnClickListener(view -> updateUserProfile());
    }
    private void setStatusBarColor() {
        int statusBarColor = ContextCompat.getColor(this, R.color.app_main_dark);
        getWindow().setStatusBarColor(statusBarColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }


    private void initializeFields() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextCity = findViewById(R.id.editTextCity);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        editPhoneNumber = findViewById(R.id.editTextPhoneNumber);
    }

    private void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (userData != null) {
                        editTextName.setText(userData.name);
                        editTextEmail.setText(userData.email);
                        editTextAddress.setText(userData.address);
                        editTextCity.setText(userData.city);
                        editPhoneNumber.setText(userData.number);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Update other user details
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            if(editTextName.getText().toString().isEmpty() || editTextEmail.getText().toString().isEmpty() || editTextAddress.getText().toString().isEmpty()|| editPhoneNumber.getText().toString().isEmpty() || editTextCity.getText().toString().isEmpty()){
                Toast.makeText(ProfileActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            userRef.child("name").setValue(editTextName.getText().toString());
            userRef.child("email").setValue(editTextEmail.getText().toString());
            userRef.child("address").setValue(editTextAddress.getText().toString());
            userRef.child(("number")).setValue(editPhoneNumber.getText().toString());
            userRef.child("city").setValue(editTextCity.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "User details updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update user details: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    public static class User {
        public String name, email, address, city, number;

        public User() {
            // Default constructor required for Firebase
        }
    }
}
