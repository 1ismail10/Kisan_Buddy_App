package com.example.kisanbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {
    private static final String TAG = "BudgetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        EditText cropName = findViewById(R.id.cropName);
        EditText cropQuantity = findViewById(R.id.cropQuantity);
        EditText cropCost = findViewById(R.id.cropCost);
        Button findMandisButton = findViewById(R.id.findMandisButton);
        Spinner stateDropdown = findViewById(R.id.stateDropdown);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cropsRef = db.collection("crops");
        setStatusBarColor();
        // Setting OnItemSelectedListener to change text color
        stateDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        findMandisButton.setOnClickListener(v -> {
            // Navigate to MandisActivity to fetch nearby mandis
            String name = cropName.getText().toString();
            String quantity = cropQuantity.getText().toString();
            String cost = cropCost.getText().toString();

            if (name.isEmpty() || quantity.isEmpty() || cost.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to Firestore
            Map<String, Object> crop = new HashMap<>();
            crop.put("name", name);
            crop.put("quantity", Integer.parseInt(quantity));
            crop.put("cost", Double.parseDouble(cost));

            cropsRef.add(crop).addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Crop details saved successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error saving crop details", e);
                Toast.makeText(this, "Failed to save crop details", Toast.LENGTH_SHORT).show();
            });
            String selectedState = stateDropdown.getSelectedItem().toString();
            String costInput = cropCost.getText().toString();
            String enteredCropName = cropName.getText().toString().trim();

            if (costInput.isEmpty() || enteredCropName.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            double userCropCost = Double.parseDouble(costInput);

            Intent intent = new Intent(BudgetActivity.this, MandisActivity.class);
            intent.putExtra("state", selectedState);
            intent.putExtra("userCropCost", userCropCost);
            intent.putExtra("cropName", enteredCropName);
            intent.putExtra("cropQuantity", Integer.parseInt(quantity));
            startActivity(intent);
        });
    }
    private void setStatusBarColor() {
        int statusBarColor = ContextCompat.getColor(this, R.color.app_main_dark);
        getWindow().setStatusBarColor(statusBarColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
