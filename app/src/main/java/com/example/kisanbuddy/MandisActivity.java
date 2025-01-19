package com.example.kisanbuddy;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MandisActivity extends AppCompatActivity {

    private RecyclerView mandisRecyclerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandis);

        mandisRecyclerView = findViewById(R.id.mandisRecyclerView);
        mandisRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setStatusBarColor();
        TextView textView = findViewById(R.id.tvWelcome);

        String fullText = "Nearby Mandis \n(Click on a Mandi to view on map)";
        SpannableString spannable = new SpannableString(fullText);

        // Set the style for the second line (the part in parentheses)
        int start = fullText.indexOf("(");
        int end = fullText.length();

        // Reduce the size of the second line
        spannable.setSpan(
                new RelativeSizeSpan(0.6f), // 60% of the original size
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Change the typeface to non-bold for the second line
        spannable.setSpan(
                new TypefaceSpan("sans-serif"), // Regular font
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textView.setText(spannable);

        Intent intent = getIntent();
        String state = intent.getStringExtra("state");
        double userCropCost = intent.getDoubleExtra("userCropCost", 0);
        String cropName = intent.getStringExtra("cropName");
        int cropQuantity = intent.getIntExtra("cropQuantity", 1); // Default to 1 if not provided

        Log.d("MandisInputValue", "State: " + state + ", CropName: " + cropName + ", Quantity: " + cropQuantity);

        // Validate input data
        if (state == null || state.isEmpty() || cropName == null || cropName.isEmpty()) {
            Toast.makeText(this, "State or Crop Name is missing", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if required data is missing
            return;
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch data from Firestore
        fetchMandisFromFirestore(state, cropName, userCropCost, cropQuantity);
    }

    private void fetchMandisFromFirestore(String state, String cropName, double userCropCost, int cropQuantity) {
        db.collection("MandisData")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Mandi> mandiList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fetchedState = document.getString("State");
                            String fetchedCropName = document.getString("CropName");

                            // Check if the fetched crop name contains the desired crop name
                            if (fetchedCropName != null &&
                                    fetchedCropName.toLowerCase().contains(cropName.toLowerCase().trim())) {
                                String district = document.getString("District");
                                String market = document.getString("Market");
                                double price = document.getDouble("Price");

                                double profitPerUnit = price - userCropCost;
                                mandiList.add(new Mandi(fetchedState, district, market, fetchedCropName, price, profitPerUnit * cropQuantity));
                            }
                        }

                        // Process data
                        processMandiList(mandiList, state);
                    } else {
                        Log.e("MandisActivity", "Error getting documents: ", task.getException());
                        Toast.makeText(MandisActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void processMandiList(List<Mandi> mandiList, String state) {
        List<Mandi> stateMandis = new ArrayList<>();
        List<Mandi> otherStateMandis = new ArrayList<>();

        for (Mandi mandi : mandiList) {
            if (mandi.getState().equalsIgnoreCase(state.trim())) {
                stateMandis.add(mandi); // Add to state-matching list
            } else {
                otherStateMandis.add(mandi); // Add to other states list
            }
        }

        // Sort both lists by profit in descending order
        Collections.sort(stateMandis, (m1, m2) -> Double.compare(m2.getProfit(), m1.getProfit()));
        Collections.sort(otherStateMandis, (m1, m2) -> Double.compare(m2.getProfit(), m1.getProfit()));

        // Combine lists, giving priority to state-matching mandis
        List<Mandi> combinedMandis = new ArrayList<>();
        combinedMandis.addAll(stateMandis);
        combinedMandis.addAll(otherStateMandis);

        if (combinedMandis.isEmpty()) {
            Toast.makeText(this, "No matching markets found.", Toast.LENGTH_SHORT).show();
        } else {
            // Save the first mandi to history
            saveFirstMandiToHistory(combinedMandis.get(0));
            // Bind to RecyclerView
            MandiAdapter adapter = new MandiAdapter(combinedMandis, this);
            mandisRecyclerView.setAdapter(adapter);
        }
    }

    private void setStatusBarColor() {
        int statusBarColor = ContextCompat.getColor(this, R.color.app_main_dark);
        getWindow().setStatusBarColor(statusBarColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void saveFirstMandiToHistory(Mandi mandi) {
        SharedPreferences sharedPreferences = getSharedPreferences("mandi_history", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Load existing history
        Gson gson = new Gson();
        String json = sharedPreferences.getString("history", null);
        Type listType = new TypeToken<List<Mandi>>() {}.getType();
        List<Mandi> mandiHistory = json != null ? gson.fromJson(json, listType) : new ArrayList<>();

        // Add the new mandi at the top
        mandiHistory.add(0, mandi);

        // Ensure history size does not exceed 5
        if (mandiHistory.size() > 5) {
            mandiHistory.remove(mandiHistory.size() - 1);
        }

        // Save updated history back to SharedPreferences
        String updatedJson = gson.toJson(mandiHistory);
        editor.putString("history", updatedJson);
        editor.apply();
    }
}
