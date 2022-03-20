package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FindRouteToRoom extends AppCompatActivity {

    private final String[] rooms = {"0", "A226", "B025", "B112", "B113", "B114", "B212", "C117", "C118", "C119", "C212", "C217", "C305", "P002", "P006", "S109", "S110", "Hlavný vchod"};
    private final String[] floors = {"floor_a2", "floor_b0", "floor_b1", "floor_b2", "floor_c1", "floor_c2", "floor_c3", "floor_p0", "floor_s1", "floor_vchod"};
    private final int[] nodesOfRooms = {0, 2, 3, 13, 12, 10, 15, 19, 18, 17, 24, 23, 27, 34, 28, 36, 35, 42};
    private final int[] positionsOfRoomsOnFloor = {0, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 5, 0, 1, 0, 1, -1};

    private String[] pathFloorsArray;
    private String pathFloors = "", from = "", to = "";
    private int[][] coordinatesOfNodes = new int[43][2];
    private float[][] dimensionsOfFiles = new float[10][3];
    private float cellSize;
    private int[] paths;
    private int[] finalPathFloor;
    private int roomFrom = 0, roomTo = 0, positionFrom = 0, positionTo = 0;
    private ArrayList<Map> maps = new ArrayList<>();
    private ImageView imageView;
    private Canvas canvas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route_to_room);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");

        imageView = new ImageView(this);

        for(int i = 0; i < rooms.length; i++) { //podľa miestností zistiť pozíciu a vyhľadať, ktorý je to uzol
            if(from.equals(rooms[i])) {
                roomFrom = nodesOfRooms[i];
                positionFrom = positionsOfRoomsOnFloor[i-1];
            }
            if(to.equals(rooms[i])) {
                roomTo = nodesOfRooms[i];
                positionTo = positionsOfRoomsOnFloor[i-1];
            }
        }

        getCoordinatesOfNodes();
        findRoute();
        //drawRoute();
    }

    public void getCoordinatesOfNodes() {
        int countCoordinates = 0;
        int countFileWidth = 0;
        int countFileHeight = 0;

        for (int i = 0; i < floors.length; i++) {
            int y = 0;
            String line = "";
            countFileWidth = 0;
            countFileHeight = 0;

            int resID = getResources().getIdentifier(floors[i], "raw", getPackageName());
            InputStream inputStream = getResources().openRawResource(resID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                try {
                    if((line = reader.readLine()) == null) break;
                } catch (IOException e){
                    e.printStackTrace();
                }
                if(y == 0) { //zistenie veľkosti bunky podľa rozmeru obrazovky a počtu X v riadku v txt súbore (stačí to zisťovať raz)
                    cellSize = getDisplayWidth()/line.length();
                    countFileWidth = line.length();
                }
                for(int x = 0; x < line.length(); x++) {
                    if(line.charAt(x) == 'U' || line.charAt(x) == 'E' || line.charAt(x) == 'S') {
                        coordinatesOfNodes[countCoordinates][0] = x; //x súradnica uzla
                        coordinatesOfNodes[countCoordinates][1] = y; //y súradnica uzla
                        countCoordinates++;
                    }
                }
                y++;
                countFileHeight++;
            }
            dimensionsOfFiles[i][0] = cellSize; //veľkosť jednej bunky
            dimensionsOfFiles[i][1] = countFileWidth; //počet buniek v šírke
            dimensionsOfFiles[i][2] = countFileHeight; //počet buniek vo výške
        }

        for(int i=0; i<coordinatesOfNodes.length; i++){
            System.out.println(i + " - [" + coordinatesOfNodes[i][0] + "," + coordinatesOfNodes[i][1] + "]");
        }
    }

    public void findRoute() {
        DijkstraAlgorithm dijkstraAlgorithm;
        int[][] graphNodes = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,51,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10}, //A {
                {0,51,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //A226 }
                {0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //B025 {
                {0,0,0,0,0,1,0,0,0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,5,1,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,86,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,8,86,0,98,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,98,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,0,0,0,0,0},
                {0,0,0,0,10,0,0,0,0,0,0,33,0,0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //B114
                {0,0,0,0,0,0,0,0,0,33,8,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //B113
                {0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //B112
                {0,0,0,0,0,0,0,0,0,10,0,0,0,0,0,45,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,45,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //B212 }
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,29,0,10,0,0,0,0,0,0,0,0,0,0,10,0,0,0,0,0,0,0,0,0,0}, //C {
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //C119
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //C118
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,21,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //C117
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,29,2,0,0,0,16,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,21,16,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,0,0,0,0,0,0,0,0,40,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, ///C217
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //C212
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,40,2,79,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,0,0,0,0,125,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,125,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //C305 }
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19,0,0,0,0,0,0,0,0,0,0,0,0}, //P6 {
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,73,0,0,0,0,0,0,0,0,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,73,0,106,0,0,21,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19,0,106,0,6,180,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,180,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,21,0,0,0,0,0,0,0,0,0,0,0,0,0}, //P2 }
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0}, //S110 {
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,12,0,0,0,0,0,0}, //S109
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,12,0,52,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,52,0,0,0,0,0,0}, //S }
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0}, //vchod {
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,19,0,10},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,19,0,68,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,68,0,0},
                {0,10,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,0,0,0}}; //vchod }


        if(roomTo < roomFrom) { //vždy musí byť prvé číslo menšie a druhé väčšie
            dijkstraAlgorithm = new DijkstraAlgorithm(graphNodes, roomTo, roomFrom);
        } else {
            dijkstraAlgorithm = new DijkstraAlgorithm(graphNodes, roomFrom, roomTo);
        }

        paths = dijkstraAlgorithm.returnPath(); //uzly, cez ktoré ide trasa

        finalPathFloor = new int[paths.length];
        String helper = "";
        int[][] finalPathCoordinates = new int[10][16]; //súradnice uzlov, cez ktoré ide trasa podľa textových súborov ->
        // [10]-počet súborov, [14]-max. počet uzlov na poschodí je 8 ale x2 pretože sú 2 súradnice
        int countA2 = 0, countB0 = 0, countB1 = 0, countB2 = 0, countC1 = 0, countC2 = 0, countC3 = 0, countP0 = 0, countS1 = 0, countV = 0;

        for(int i = 0; i < paths.length; i++) { //zistenie, cez ktoré bloky a poschodia trasa ide
            int node = paths[i] - 1;
            if(node == 0 || node == 1) { //A
                pathFloors += "floor_a2,";
                helper += "0";
                finalPathCoordinates[0][countA2] = coordinatesOfNodes[node][0];
                finalPathCoordinates[0][countA2+1] = coordinatesOfNodes[node][1];
                countA2 += 2;
            } else if(node >= 2 && node <= 14) { //B
                if(node <= 7) { //B0
                    pathFloors += "floor_b0,";
                    helper += "1";
                    finalPathCoordinates[1][countB0] = coordinatesOfNodes[node][0];
                    finalPathCoordinates[1][countB0+1] = coordinatesOfNodes[node][1];
                    countB0 += 2;
                } else if(node >= 8 && node <= 12) { //B1
                    pathFloors += "floor_b1,";
                    helper += "2";
                    finalPathCoordinates[2][countB1] = coordinatesOfNodes[node][0];
                    finalPathCoordinates[2][countB1+1] = coordinatesOfNodes[node][1];
                    countB1 += 2;
                } else { //B2
                    pathFloors += "floor_b2,";
                    helper += "3";
                    finalPathCoordinates[3][countB2] = coordinatesOfNodes[node][0];
                    finalPathCoordinates[3][countB2+1] = coordinatesOfNodes[node][1];
                    countB2 += 2;
                }
            } else if(node >= 15 && node <= 26) { //C
                if(node <= 20) { //C1
                    pathFloors += "floor_c1,";
                    helper += "4";
                    finalPathCoordinates[4][countC1] = coordinatesOfNodes[node][0];
                    finalPathCoordinates[4][countC1+1] = coordinatesOfNodes[node][1];
                    countC1 += 2;
                } else if(node >= 21 && node <= 24) { //C2
                    pathFloors += "floor_c2,";
                    helper += "5";
                    finalPathCoordinates[5][countC2] = coordinatesOfNodes[node][0];
                    finalPathCoordinates[5][countC2+1] = coordinatesOfNodes[node][1];
                    countC2 += 2;
                } else { //C3
                    pathFloors += "floor_c3,";
                    helper += "6";
                    finalPathCoordinates[6][countC3] = coordinatesOfNodes[node][0];
                    finalPathCoordinates[6][countC3+1] = coordinatesOfNodes[node][1];
                    countC3 += 2;
                }
            } else if(node >= 27 && node <= 33) { //P
                pathFloors += "floor_p0,";
                helper += "7";
                finalPathCoordinates[7][countP0] = coordinatesOfNodes[node][0];
                finalPathCoordinates[7][countP0+1] = coordinatesOfNodes[node][1];
                countP0 += 2;
            } else if(node >= 34 && node <= 37) { //S
                pathFloors += "floor_s1,";
                helper += "8";
                finalPathCoordinates[8][countS1] = coordinatesOfNodes[node][0];
                finalPathCoordinates[8][countS1+1] = coordinatesOfNodes[node][1];
                countS1 += 2;
            } else if(node == 43){

            } else {
                pathFloors += "floor_vchod,"; //vchod
                helper += "9";
                finalPathCoordinates[9][countV] = coordinatesOfNodes[node][0];
                finalPathCoordinates[9][countV+1] = coordinatesOfNodes[node][1];
                countV += 2;
            }
        }

        pathFloorsArray = pathFloors.split(",");
        for(int i = 0; i < pathFloorsArray.length; i++) {
            int h = Character.getNumericValue(helper.charAt(i));
            if(i == 0) {
                System.out.println("Index mapy: "+h);
                maps.add(new Map(getApplicationContext(), from, from.charAt(0) + "", dimensionsOfFiles[h][1], dimensionsOfFiles[h][2], positionFrom, dimensionsOfFiles[h][0], finalPathCoordinates[h], new Canvas(), coordinatesOfNodes[roomFrom-1][0], coordinatesOfNodes[roomFrom-1][1]));
            } else if(i == pathFloorsArray.length-1) {
                System.out.println("Index mapy: "+h);
                maps.add(new Map(getApplicationContext(), to, to.charAt(0) + "", dimensionsOfFiles[h][1], dimensionsOfFiles[h][2], positionTo, dimensionsOfFiles[h][0], finalPathCoordinates[h], new Canvas(), coordinatesOfNodes[roomTo-1][0], coordinatesOfNodes[roomTo-1][1]));
            } else if((!(pathFloorsArray[i].equals(pathFloorsArray[i-1]))) && (!(pathFloorsArray[i].equals(pathFloorsArray[pathFloorsArray.length-1])))){
                System.out.println("Index mapy: "+h);
                maps.add(new Map(getApplicationContext(), pathFloorsArray[i], "X", dimensionsOfFiles[h][1], dimensionsOfFiles[h][2], -1, dimensionsOfFiles[h][0], finalPathCoordinates[h], new Canvas(), 0, 0));
            }
        }

        for(int o = 0; o < maps.size(); o++) {
            //Bitmap bitmap = Bitmap.createBitmap((int)getDisplayWidth(), (int)getDisplayHeight(), Bitmap.Config.ARGB_8888);
            //Canvas c = new Canvas(maps.get(o).getBitmap());
            //c.drawColor(Color.RED);
            imageView.setImageBitmap(maps.get(o).getBitmap());
            setContentView(imageView);
        }

    }

    private float getDisplayWidth() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);

        return distances.x;
    }

    private float getDisplayHeight() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);

        return distances.y;
    }

    public void backToMain(View view) {
        Intent intent = new Intent(FindRouteToRoom.this, MainActivity.class);
        startActivity(intent);
    }
}