package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FindRoom extends AppCompatActivity {

    ArrayAdapter<String> adapterA, adapterB, adapterC, adapterP, adapterS;
    ListView listViewA, listViewB, listViewC, listViewP, listViewS;

    String[] blokA = {"226"};
    String[] blokB = {"025", "112", "113", "114", "212"};
    String[] blokC = {"117", "118", "119", "212", "217", "305"};
    String[] blokS = {"109", "110"};
    String[] blokP = {"002", "006"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_room);

        adapterA = new ArrayAdapter<>(this, R.layout.listview_layout, blokA);
        listViewA = findViewById(R.id.listViewA);
        listViewA.setAdapter(adapterA);
        listViewA.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                intent.putExtra("blok", "A");
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });

        adapterB = new ArrayAdapter<>(this, R.layout.listview_layout, blokB);
        listViewB = findViewById(R.id.listViewB);
        listViewB.setAdapter(adapterB);
        listViewB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                intent.putExtra("blok", "B");
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });

        adapterC = new ArrayAdapter<>(this, R.layout.listview_layout, blokC);
        listViewC = findViewById(R.id.listViewC);
        listViewC.setAdapter(adapterC);
        listViewC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                intent.putExtra("blok", "C");
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });

        adapterP = new ArrayAdapter<>(this, R.layout.listview_layout, blokP);
        listViewP = findViewById(R.id.listViewP);
        listViewP.setAdapter(adapterP);
        listViewP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                intent.putExtra("blok", "P");
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });

        adapterS = new ArrayAdapter<>(this, R.layout.listview_layout, blokS);
        listViewS = findViewById(R.id.listViewS);
        listViewS.setAdapter(adapterS);
        listViewS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                intent.putExtra("blok", "S");
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        });
    }

    public void backToMain(View view) {
        Intent intent = new Intent(FindRoom.this, MainActivity.class);
        startActivity(intent);
    }
}