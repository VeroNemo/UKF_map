package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class FindRouteToRoom extends AppCompatActivity {

    private Spinner spinner;
    private String[] rooms = {"Vyberte miestnosť:", "A226", "B025", "B112", "B113", "B114", "B212", "C117", "C118", "C119", "C212", "C217", "C305", "P002", "P006", "S109", "S110"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route_to_room);

        spinner = (Spinner) findViewById(R.id.spinnerTo);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listview_layout, rooms);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position > 0){
                    Toast.makeText(getApplicationContext(), rooms[position], Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void backToMain(View view) {
        Intent intent = new Intent(FindRouteToRoom.this, MainActivity.class);
        startActivity(intent);
    }
}