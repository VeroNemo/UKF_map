package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FindRoom extends AppCompatActivity {

    private ArrayAdapter<String> adapterB, adapterC, adapterP, adapterS;
    private ListView listViewB, listViewC, listViewP, listViewS;

    private String[] blokB = {"025", "112", "113", "114", "212"};
    private String[] blokC = {"117", "118", "119", "212", "217", "305"};
    private String[] blokS = {"109", "110"};
    private String[] blokP = {"002", "006"};

    private String from = "";
    public String instructions = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_room);

        Intent intent = getIntent();
        instructions = intent.getStringExtra("instructions");
        TextView textView = (TextView) findViewById(R.id.txt_miniTitle6);
        TextView textView2 = (TextView) findViewById(R.id.txt_titleForActivity);
        switch (instructions) {
            case "classic":
                textView2.setText("Nájsť učebňu");
                textView.setVisibility(View.INVISIBLE);
                break;
            case "from":
                textView2.setText("Vyberte, kde sa nachádzate");
                break;
            case "to":
                textView2.setText("Vyberte, kde chcete ísť");
                from = intent.getStringExtra("from");
                break;
        }

        adapterB = new ArrayAdapter<>(this, R.layout.listview_layout, blokB);
        listViewB = findViewById(R.id.listViewB);
        listViewB.setAdapter(adapterB);
        listViewB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(instructions.equals("classic")) {
                    Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                    intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                    intent.putExtra("blok", "B");
                    intent.putExtra("position", String.valueOf(position));
                    startActivity(intent);
                } else if(instructions.equals("from")) {
                    Intent intent = new Intent(FindRoom.this, FindRoom.class);
                    intent.putExtra("instructions", "to");
                    intent.putExtra("from", "B" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FindRoom.this, FindRouteToRoom.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", "B" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                }
            }
        });

        adapterC = new ArrayAdapter<>(this, R.layout.listview_layout, blokC);
        listViewC = findViewById(R.id.listViewC);
        listViewC.setAdapter(adapterC);
        listViewC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(instructions.equals("classic")) {
                    Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                    intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                    intent.putExtra("blok", "C");
                    intent.putExtra("position", String.valueOf(position));
                    startActivity(intent);
                } else if(instructions.equals("from")) {
                    Intent intent = new Intent(FindRoom.this, FindRoom.class);
                    intent.putExtra("instructions", "to");
                    intent.putExtra("from", "C" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FindRoom.this, FindRouteToRoom.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", "C" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                }
            }
        });

        adapterP = new ArrayAdapter<>(this, R.layout.listview_layout, blokP);
        listViewP = findViewById(R.id.listViewP);
        listViewP.setAdapter(adapterP);
        listViewP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(instructions.equals("classic")) {
                    Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                    intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                    intent.putExtra("blok", "P");
                    intent.putExtra("position", String.valueOf(position));
                    startActivity(intent);
                } else if(instructions.equals("from")) {
                    Intent intent = new Intent(FindRoom.this, FindRoom.class);
                    intent.putExtra("instructions", "to");
                    intent.putExtra("from", "P" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FindRoom.this, FindRouteToRoom.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", "P" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                }
            }
        });

        adapterS = new ArrayAdapter<>(this, R.layout.listview_layout, blokS);
        listViewS = findViewById(R.id.listViewS);
        listViewS.setAdapter(adapterS);
        listViewS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(instructions.equals("classic")) {
                    Intent intent = new Intent(FindRoom.this, MapForRooms.class);
                    intent.putExtra("room_number", (String) parent.getItemAtPosition(position));
                    intent.putExtra("blok", "S");
                    intent.putExtra("position", String.valueOf(position));
                    startActivity(intent);
                } else if(instructions.equals("from")) {
                    Intent intent = new Intent(FindRoom.this, FindRoom.class);
                    intent.putExtra("instructions", "to");
                    intent.putExtra("from", "S" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FindRoom.this, FindRouteToRoom.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", "S" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);
                }
            }
        });
    }

    public void clickFloorV(View view) {
        Intent intent;
        if(instructions.equals("from")) {
            intent = new Intent(FindRoom.this, FindRoom.class);
            intent.putExtra("instructions", "to");
            intent.putExtra("from", "Hlavný vchod");
        } else {
            intent = new Intent(FindRoom.this, FindRouteToRoom.class);
            intent.putExtra("from", from);
            intent.putExtra("to", "Hlavný vchod");
        }
        startActivity(intent);
    }

    public void clickFloorA(View view) {
        Intent intent;
        if(instructions.equals("classic")) {
            intent = new Intent(FindRoom.this, MapForRooms.class);
            intent.putExtra("room_number", "226");
            intent.putExtra("blok", "A");
            intent.putExtra("position", "0");
        } else if(instructions.equals("from")) {
            intent = new Intent(FindRoom.this, FindRoom.class);
            intent.putExtra("instructions", "to");
            intent.putExtra("from", "A226");
        } else {
            intent = new Intent(FindRoom.this, FindRouteToRoom.class);
            intent.putExtra("from", from);
            intent.putExtra("to", "A226");
        }
        startActivity(intent);
    }

    public void backToMain(View view) {
        Intent intent = new Intent(FindRoom.this, MainActivity.class);
        startActivity(intent);
    }
}