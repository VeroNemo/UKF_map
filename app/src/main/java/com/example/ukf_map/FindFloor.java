package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FindFloor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_floor);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(FindFloor.this, MainActivity.class);
        startActivity(intent);
    }
}