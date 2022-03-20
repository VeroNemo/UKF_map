package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void activityFindFloor(View view) {
        Intent intent = new Intent(MainActivity.this, FindFloor.class);
        startActivity(intent);
    }

    public void activityFindRoom(View view) {
        Intent intent = new Intent(MainActivity.this, FindRoom.class);
        intent.putExtra("instructions", "classic");
        startActivity(intent);
    }

    public void activityFindRouteToRoom(View view) {
        //Intent intent = new Intent(MainActivity.this, FindRoom.class);
        Intent intent = new Intent(MainActivity.this, FindRouteToRoom2.class);
        //intent.putExtra("instructions", "from");
        intent.putExtra("floor", "0");
        startActivity(intent);
    }
}