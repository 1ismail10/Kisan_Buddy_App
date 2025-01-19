package com.example.kisanbuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // Tag for logging
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setStatusBarColor();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // No user is signed in, redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // User is logged in, fetch user details
            fetchUserData(currentUser);
        }

        // Click listener for ivLogout
        Button ivLogout = findViewById(R.id.ivLogout);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmation();
            }
        });

        // Click listener for ivProfile
        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileActivity();
            }
        });

        // Click listener for ivBudget
        ImageView ivBudget = findViewById(R.id.ivBudget);
        ivBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBudgetActivity();
            }
        });
        // Click listener for ivHistory
        ImageView ivHistory = findViewById(R.id.ivHistory);
        ivHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistoryActivity();
            }
        });

        // Click listener for aboutUsText
        ImageView aboutUsText = findViewById(R.id.tvAboutUs);
        aboutUsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the AboutUsActivity
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setStatusBarColor() {
        int statusBarColor = ContextCompat.getColor(this, R.color.app_main_dark);
        getWindow().setStatusBarColor(statusBarColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void fetchUserData(FirebaseUser currentUser) {
        String userId = currentUser.getUid(); // Get user ID
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getName() != null) {
                    updateWelcomeText(user.getName());
                } else {
                    Log.d(TAG, "User or user name is null");
                    updateWelcomeText("user data not available");
                }
                if (user != null && user.getCity() != null) {
                    updateLocation(user.getCity());
                } else {
                    Log.d(TAG, "User or user city is null");
                    updateWelcomeText("user data not available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                updateWelcomeText("Failed to load user data");
            }
        });
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                }
                // Not supported language selected
                if (!selectedLanguage.equals("English")) {
                    Toast.makeText(parent.getContext(), selectedLanguage + " is not supported now", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateWelcomeText(String text) {
        TextView welcomeText = findViewById(R.id.tvWelcome);
        welcomeText.setText("Welcome, " + text);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.akaya_telivigala);
        welcomeText.setTypeface(typeface);
    }

    private void updateLocation(String text) {
        TextView locationText = findViewById(R.id.tvLocation);
        locationText.setText("  " + text);
    }

    // Show confirmation popup before logging out
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void logoutUser() {
        // Sign out from Firebase
        mAuth.signOut();

        // Clear SharedPreferences to remove previous user's data
        SharedPreferences sharedPreferences = getSharedPreferences("mandi_history", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openBudgetActivity() {
        Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
        startActivity(intent);
    }
    private void openHistoryActivity() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }


    // User class
    public static class User {
        private String name;
        private String email;
        private String city;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
