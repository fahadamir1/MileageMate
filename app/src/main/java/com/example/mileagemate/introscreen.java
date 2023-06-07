package com.example.mileagemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class introscreen extends AppCompatActivity {

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introscreen);
        button=findViewById(R.id.getstarted);
        button.setOnClickListener(view -> {
            sendusertodashboard();

        });
    }
    public void sendusertodashboard(){
        Intent I = new Intent(this, Dashboard.class);
        I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(I);
    }
}