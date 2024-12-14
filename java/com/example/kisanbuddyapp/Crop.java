package com.example.kisanbuddyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Crop extends AppCompatActivity {
    EditText cropname, quantity, cost;
    Button submitCropDetailsButton;
    ImageButton back;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        cropname = findViewById(R.id.inputCropName);
        quantity = findViewById(R.id.inputCropQuantity);
        cost = findViewById(R.id.inputCropCost);
        back = findViewById(R.id.backbtn);
        submitCropDetailsButton = findViewById(R.id.submitCropDetailsButton);


        submitCropDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireStoreDB fireStoreDB = new FireStoreDB();
                fireStoreDB.setCrop(user.getEmail(),cropname.getText().toString(),quantity.getText().toString(),cost.getText().toString());
                Toast.makeText(Crop.this, "details  added"+cropname.getText().toString()+quantity.getText().toString()+cost.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Crop.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}