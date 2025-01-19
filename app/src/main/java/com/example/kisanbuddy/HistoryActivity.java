package com.example.kisanbuddy;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setStatusBarColor();
        TextView textView = findViewById(R.id.tvWelcome);

        String fullText = "Recent Mandis \n(Click on a Mandi to view on map)";
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

        loadHistory();
    }

    private void loadHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("mandi_history", MODE_PRIVATE);
        String json = sharedPreferences.getString("history", null);

        if (json == null) {
            Toast.makeText(this, "No history available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deserialize the JSON
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Mandi>>() {}.getType();
        List<Mandi> mandiHistory = gson.fromJson(json, listType);

        if (mandiHistory == null || mandiHistory.isEmpty()) {
            Toast.makeText(this, "No history available.", Toast.LENGTH_SHORT).show();
        } else {
            // Bind to RecyclerView
            MandiAdapter adapter = new MandiAdapter(mandiHistory, this);
            historyRecyclerView.setAdapter(adapter);
        }
    }
    private void setStatusBarColor() {
        int statusBarColor = ContextCompat.getColor(this, R.color.app_main_dark);
        getWindow().setStatusBarColor(statusBarColor);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}
