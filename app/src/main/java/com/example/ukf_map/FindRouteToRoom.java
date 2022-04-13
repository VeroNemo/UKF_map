package com.example.ukf_map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class FindRouteToRoom extends AppCompatActivity {

    private final String[] allRooms = {"A010", "A013", "A111", "A115", "A119", "A120", "A223", "A224", "A226", "B025", "B112", "B113", "B114", "B204", "B205", "B212", "C010", "C011", "C117", "C118", "C117", "C118", "C119", "C212", "C217", "C218", "C305", "C308", "C309", "P002", "P006", "S109", "S110"};
    private final int[] positionsOfAllRooms = {0, 1, 3, 4, 5, 8, 6, 9, 7, 0, 1, 2, 3, 5, 6, 4, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 0, 1, 0, 1};
    public final int[] colors = {Color.rgb(33, 150, 243), Color.rgb(76, 175, 80), Color.rgb(255, 152, 0), Color.rgb(244, 67, 54)};
    public final int MAX_WIDTH = 508;
    public final int MAX_HEIGHT = 435;
    public final int ALPHA = 40;

    public int[][] array0 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] array1 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] array2 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] array3 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] arraySolve0 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] arraySolve1 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] arraySolve2 = new int[MAX_HEIGHT][MAX_WIDTH];
    public int[][] arraySolve3 = new int[MAX_HEIGHT][MAX_WIDTH];
    public ArrayList<int[][]> arrayList = new ArrayList<>();
    public ArrayList<int[][]> arrayList2 = new ArrayList<>();
    public ArrayList<Button> buttons = new ArrayList<>();

    public AutoCompleteTextView autoCompleteTextViewFrom, autoCompleteTextViewTo;
    public ImageView imageView;
    public Canvas cnvs;
    public Bitmap btm;
    public Paint pnt;
    public float displayWidth, displayHeight, cellSize;
    public Field[] files;
    public String coordinatesFrom, coordinatesTo, stairs = "", roomFrom = "", roomTo = "";
    public Button button0;
    public int btn_ID, btn_clicked = 0, floorFrom = 0, floorTo = 0, zoomIn = 0;
    public BottomSheetBehavior behavior;
    public TextView title;
    public LinearLayout linearLayoutRoute;
    public RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route_to_room);

        //inicializácia objektov
        LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.lin_2);
        autoCompleteTextViewFrom = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewFrom);
        autoCompleteTextViewTo = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewTo);
        title = (TextView) findViewById(R.id.txt_titleForActivity4);
        imageView = (ImageView) findViewById(R.id.imageView7);
        button0 = (Button) findViewById(R.id.btn_0);

        //inicializácia adaptéra pre hľadacie inputy
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, allRooms);
        autoCompleteTextViewFrom.setThreshold(1);
        autoCompleteTextViewTo.setThreshold(1);
        autoCompleteTextViewFrom.setAdapter(adapter);
        autoCompleteTextViewTo.setAdapter(adapter);

        //inicializácia správanie pre vysúvaci layout na spodu aktivity
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        //zistenie rozmerov obrazovky
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);
        displayWidth = distances.x;
        displayHeight = distances.y;
        cellSize = displayWidth/MAX_WIDTH;

        //inicializovanie bitmapy, ktorá sa použije pri canvase pri vykresľovaní
        btm = Bitmap.createBitmap((int)displayWidth*2, (int)displayHeight, Bitmap.Config.ARGB_8888);
        cnvs = new Canvas(btm);
        pnt = new Paint();

        files = R.raw.class.getFields(); //uloženie textových súborov s mapami poschodí
        setPrimaryDataToArrayLists(); //nastavenie prvotných údajov do arrayListov a buttonov

        //pri zapnutí aktivity sa vykreslí prízemie
        button0.setTextColor(Color.WHITE);
        btn_clicked = 0;
        drawPrimaryMap();

        //vloženie buttonov do arrayListu
        buttons.add(button0);
        buttons.add(findViewById(R.id.btn_1));
        buttons.add(findViewById(R.id.btn_2));
        buttons.add(findViewById(R.id.btn_3));
    }

    public void setPrimaryDataToArrayLists() {
        arrayList.clear();
        arrayList2.clear();

        arrayList.add(array0);
        arrayList.add(array1);
        arrayList.add(array2);
        arrayList.add(array3);

        arrayList2.add(arraySolve0);
        arrayList2.add(arraySolve1);
        arrayList2.add(arraySolve2);
        arrayList2.add(arraySolve3);

        for(int a = 0; a < array0.length; a++) {
            for(int b = 0; b < array0[a].length; b++) {
                array0[a][b] = 0;
                array1[a][b] = 0;
                array2[a][b] = 0;
                array3[a][b] = 0;
                arraySolve0[a][b] = 0;
                arraySolve1[a][b] = 0;
                arraySolve2[a][b] = 0;
                arraySolve3[a][b] = 0;
            }
        }
    }

    //po stlačení tlačidla sa začne hľadať trasa
    public void findRoute(View view) {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if(autoCompleteTextViewFrom.getText().toString().length() > 0 && autoCompleteTextViewTo.getText().toString().length() > 0) { //musia byť obidva inputy vyplnené
            if(!autoCompleteTextViewFrom.getText().toString().equals(autoCompleteTextViewTo.getText().toString())) { //nemôžu byť v inputoch rovnaké miestnosti
                zoomIn = 0;
                //základná inicializácia
                title.setText("Trasa z miestnosti " + autoCompleteTextViewFrom.getText().toString() + " do miestnosti " + autoCompleteTextViewTo.getText().toString());
                setPrimaryDataToArrayLists();
                cnvs.drawColor(Color.WHITE);
                stairs = "";
                roomFrom = autoCompleteTextViewFrom.getText().toString().toLowerCase();
                roomTo = autoCompleteTextViewTo.getText().toString().toLowerCase();
                floorFrom = Integer.parseInt(roomFrom.substring(1, 2));
                floorTo = Integer.parseInt(roomTo.substring(1, 2));
                int positionFrom = 0;
                int positionTo = 0;

                //nastavenie pozície miestností aby sa potom vedeli nájsť v mape
                for (int i = 0; i < allRooms.length; i++) {
                    if (roomFrom.equals(allRooms[i].toLowerCase())) positionFrom = positionsOfAllRooms[i];
                    if (roomTo.equals(allRooms[i].toLowerCase())) positionTo = positionsOfAllRooms[i];
                }

                //prehľadávanie v textových súboroch
                for (int i = 0; i < files.length; i++) {
                    String file = files[i].getName();
                    String[] helper = file.split("_");

                    //podľa poschodí textových súborov sa napĺňajú polia (každé poschodie má vlastné pole)
                    switch (Integer.parseInt(helper[5])) {
                        case 0:
                            fillArray(arrayList.get(0), positionFrom, positionTo, file, floorFrom, floorTo, roomFrom, roomTo);
                            break;
                        case 1:
                            fillArray(arrayList.get(1), positionFrom, positionTo, file, floorFrom, floorTo, roomFrom, roomTo);
                            break;
                        case 2:
                            fillArray(arrayList.get(2), positionFrom, positionTo, file, floorFrom, floorTo, roomFrom, roomTo);
                            break;
                        case 3:
                            fillArray(arrayList.get(3), positionFrom, positionTo, file, floorFrom, floorTo, roomFrom, roomTo);
                            break;
                    }
                }

                //kontrolné vypisovanie
                System.out.println("From: " + roomFrom + " - " + floorFrom + " - " + positionFrom);
                System.out.println("To: " + roomTo + " - " + floorTo + " - " + positionTo);
                System.out.println("From: " + coordinatesFrom + " | To: " + coordinatesTo + "| Stairs: " + stairs);

                //samotné hľadanie trasy pomocou algoritmu a koordinácií miestností a poschodí
                if (stairs.length() > 1) { //pokiaľ sa ide cez schodisko
                    String[] helperStairs = stairs.split("_");
                    for (int a = 0; a < helperStairs.length; a++) {
                        String[] helperStairs2 = helperStairs[a].split("-"); //prehľadáva sa string kde sú uložené koordinácie poschodí

                        if (helperStairs2[0].equals("" + roomFrom.charAt(0) + floorFrom)) { //keď sa schodisko rovná miestnosti odkiaľ sa chce ísť
                            String[] helperCoordinatesF = coordinatesFrom.split("_");
                            if (findRouteAlgorithm(arrayList.get(floorFrom), arrayList2.get(floorFrom), Integer.parseInt(helperCoordinatesF[0]), Integer.parseInt(helperCoordinatesF[1]), Integer.parseInt(helperStairs2[1]), Integer.parseInt(helperStairs2[2]))) {}
                        } else if (helperStairs2[0].equals("" + roomTo.charAt(0) + floorTo)) { //keď sa schodisko rovná miestnosti kam sa chce ísť
                            String[] helperCoordinatesT = coordinatesTo.split("_");
                            if (findRouteAlgorithm(arrayList.get(floorTo), arrayList2.get(floorTo), Integer.parseInt(helperStairs2[1]), Integer.parseInt(helperStairs2[2]), Integer.parseInt(helperCoordinatesT[0]), Integer.parseInt(helperCoordinatesT[1]))) {}
                        } else if (floorFrom == 0 && helperStairs2[0].charAt(1) == '0') { //keď sa ide z prízemia a cez schody
                            String[] helperCoordinatesF = coordinatesFrom.split("_");
                            if (findRouteAlgorithm(arrayList.get(0), arrayList2.get(0), Integer.parseInt(helperCoordinatesF[0]), Integer.parseInt(helperCoordinatesF[1]), Integer.parseInt(helperStairs2[1]), Integer.parseInt(helperStairs2[2]))) {}
                        } else if (floorTo == 0 && helperStairs2[0].charAt(1) == '0') { //keď sa ide do prízemia a cez schody
                            String[] helperCoordinatesT = coordinatesTo.split("_");
                            if (findRouteAlgorithm(arrayList.get(0), arrayList2.get(0), Integer.parseInt(helperStairs2[1]), Integer.parseInt(helperStairs2[2]), Integer.parseInt(helperCoordinatesT[0]), Integer.parseInt(helperCoordinatesT[1]))) {}
                        } else {
                            for (int b = 0; b < helperStairs.length; b++) { //pokiaľ sa v stringu schodiska rovnajú poschodia, t.j. pospájajú sa body na každom poschodí
                                String[] helperStairs3 = helperStairs[b].split("-");
                                if (helperStairs2[0].charAt(1) == helperStairs3[0].charAt(1)) {
                                    if (findRouteAlgorithm(arrayList.get(Integer.parseInt(helperStairs2[0].substring(1, 2))), arrayList2.get(Integer.parseInt(helperStairs2[0].substring(1, 2))), Integer.parseInt(helperStairs2[1]), Integer.parseInt(helperStairs2[2]), Integer.parseInt(helperStairs3[1]), Integer.parseInt(helperStairs3[2]))) {}
                                }
                            }
                        }
                    }
                } else {
                    //nejde sa cez žiadne schody -> ide sa z rovnakých poschodí na rovnaké bloky
                    String[] helperCoordinatesF = coordinatesFrom.split("_");
                    String[] helperCoordinatesT = coordinatesTo.split("_");
                    if (findRouteAlgorithm(arrayList.get(floorFrom), arrayList2.get(floorFrom), Integer.parseInt(helperCoordinatesF[0]), Integer.parseInt(helperCoordinatesF[1]), Integer.parseInt(helperCoordinatesT[0]), Integer.parseInt(helperCoordinatesT[1]))) {}
                }
                drawRoute(floorFrom, pnt, cnvs, btm, 2, 3); //vykreslenie mapy s trasou
            } else Toast.makeText(getApplicationContext(),"Snažíte sa ísť do tých istých miestností", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(),"Nemáte vyplnené údaje miestností", Toast.LENGTH_SHORT).show();
        autoCompleteTextViewTo.setText("");
        autoCompleteTextViewFrom.setText("");
    }

    public void fillArray(int[][] array, int positionFrom, int positionTo, String fileName, int floorFrom, int floorTo, String roomFrom, String roomTo){
        String[] helper = fileName.split("_");
        String line = "";
        int resID = this.getResources().getIdentifier(fileName, "raw", this.getPackageName());
        InputStream inputStream = this.getResources().openRawResource(resID);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        int i = Integer.parseInt(helper[2]);

        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            int j = Integer.parseInt(helper[1]);
            for (int x = 0; x < line.length(); x++) {
                if(line.charAt(x) == 'R') {
                    array[i][j] = 1;
                } else if((line.charAt(x) == (char) (positionFrom+'0')) && helper[0].equals(roomFrom.substring(0,1)) && Integer.parseInt(helper[5]) == floorFrom) {
                    if((line.charAt(x+1) == 'X' && line.charAt(x-1) == 'X') || (line.charAt(x+1) == (char) (positionFrom+'0') && line.charAt(x-1) == 'R') || (line.charAt(x-1) == (char) (positionFrom+'0') && line.charAt(x+1) == 'R')) {
                        array[i][j] = 1;
                        coordinatesFrom = "" + i + "_" + j;
                    }
                } else if((line.charAt(x) == (char) (positionTo+'0')) && helper[0].equals(roomTo.substring(0,1)) && Integer.parseInt(helper[5]) == floorTo) {
                    if((line.charAt(x+1) == 'X' && line.charAt(x-1) == 'X') || (line.charAt(x+1) == (char) (positionTo+'0') && line.charAt(x-1) == 'R') || (line.charAt(x-1) == (char) (positionTo+'0') && line.charAt(x+1) == 'R')) {
                        array[i][j] = 1;
                        coordinatesTo = "" + i + "_" + j;
                    }
                } else if(line.charAt(x) == 'S') {
                    if(roomFrom.charAt(0) == roomTo.charAt(0)) { //rovnaké bloky
                        if(floorFrom != floorTo) { //rôzne poschodia
                            if(roomFrom.substring(0,2).equals(helper[0]+helper[5]) || roomTo.substring(0,2).equals(helper[0]+helper[5])) {
                                array[i][j] = 1;
                                stairs += helper[0] + helper[5] + "-" + i + "-" + j + "_";
                            }
                        }
                    } else { //rôzne bloky
                        if((roomFrom.substring(0,2).equals(helper[0] + helper[5]) || roomTo.substring(0,2).equals(helper[0] + helper[5])) && !helper[0].equals("p") && !(helper[0]+helper[5]).equals("b0") && !(helper[0]+helper[5]).equals("a0") && !(helper[0]+helper[5]).equals("c0")) {
                            array[i][j] = 1;
                            stairs += helper[0] + helper[5] + "-" + i + "-" + j + "_";
                        }
                        if((roomFrom.charAt(0) == 's' || roomTo.charAt(0) == 's') && (helper[0] + helper[5]).equals("b0")) { //do/z S musím ísť vždy cez B0_219
                            if(i == 219) {
                                array[i][j] = 1;
                                stairs += helper[0] + helper[5] + "-" + i + "-" + j + "_";
                            }
                        }
                        if(((roomFrom.charAt(0) == 'a' && floorFrom > 0) || (roomTo.charAt(0) == 'a' && floorTo > 0)) && (helper[0] + helper[5]).equals("v0")) { //do/z A musím ísť vždy cez V0
                            array[i][j] = 1;
                            stairs += helper[0] + helper[5] + "-" + i + "-" + j + "_";
                        }
                        if(((roomFrom.charAt(0) == 'c' && floorFrom > 0) || (roomTo.charAt(0) == 'c' && floorTo > 0)) && (helper[0] + helper[5]).equals("c0")) { //do/z C musím ísť vždy cez C0
                            array[i][j] = 1;
                            stairs += helper[0] + helper[5] + "-" + i + "-" + j + "_";
                        }
                        if(((roomFrom.charAt(0) == 'b' && floorFrom > 0) || (roomTo.charAt(0) == 'b' && floorTo > 0)) && (helper[0] + helper[5]).equals("b0")) { //do/z B musím ísť vždy cez B0_199
                            if(i != 219) {
                                array[i][j] = 1;
                                stairs += helper[0] + helper[5] + "-" + i + "-" + j + "_";
                            }
                        }
                    }
                }
                j++;
            }
            i++;
        }
    }

    public boolean findRouteAlgorithm(int[][] array, int[][] arraySolved, int fromRow, int fromColumn, int toRow, int toColumn) {
        //BACKTRACKING
        if(fromRow == toRow && fromColumn == toColumn && array[fromRow][fromColumn] == 1) {
            arraySolved[fromRow][fromColumn] = 1;
            System.out.println("Našlo sa riešenie");
            return true;
        }
        if(fromRow >= 0 && fromRow < MAX_HEIGHT && fromColumn >= 0 && fromColumn < MAX_WIDTH && array[fromRow][fromColumn] == 1) {
            if(arraySolved[fromRow][fromColumn] == 1) return false;
            arraySolved[fromRow][fromColumn] = 1;
            if(findRouteAlgorithm(array, arraySolved, fromRow+1, fromColumn, toRow, toColumn)) return true;
            if(findRouteAlgorithm(array, arraySolved, fromRow, fromColumn+1, toRow, toColumn)) return true;
            if(findRouteAlgorithm(array, arraySolved, fromRow-1, fromColumn, toRow, toColumn)) return true;
            if(findRouteAlgorithm(array, arraySolved, fromRow, fromColumn-1, toRow, toColumn)) return true;
            arraySolved[fromRow][fromColumn] = 0;
            return false;
        }
        return false;
    }

    public void drawRoute(int floorFrom, Paint paint, Canvas canvas, Bitmap bitmap, int zoomX, int zoomY){
        if(floorFrom == 0) { //pokiaľ sa vykreľuje prízemie, vykreslí sa ešte prvé poschodie
            for (int count = 0; count <= 1; count++) {
                for (Field file : files) {
                    String name = file.getName();
                    String[] helper = name.split("_");
                    if (Integer.parseInt(helper[5]) == count) {
                        drawMap(name, Float.parseFloat(helper[1]), Float.parseFloat(helper[2]), count != 0, zoomX, zoomY, canvas, paint);
                    }
                }
            }
        } else { //inak sa vykresľujú všetky poschodia pod poschodím z ktorého chce používateľ ísť
            for (int count = 0; count <= floorFrom; count++) {
                for (Field file : files) {
                    String name = file.getName();
                    String[] helper = name.split("_");
                    if (Integer.parseInt(helper[5]) == count) {
                        drawMap(name, Float.parseFloat(helper[1]), Float.parseFloat(helper[2]), count != floorFrom, zoomX, zoomY, canvas, paint);
                    }
                }
            }
        }

        //vykreslenie trasy pomocou polí poschodí
        for(int count = 0; count < arrayList2.size(); count++) {
            paint.setColor(colors[count]);
            if(count != floorFrom) {
                paint.setAntiAlias(true);
                paint.setAlpha(ALPHA-20);
            }
            for (int y = 0; y < arrayList2.get(count).length; y++) {
                for (int x = 0; x < arrayList2.get(count)[y].length; x++) {
                    if (arrayList2.get(count)[y][x] == 1)
                        canvas.drawRect(x * cellSize, y*zoomY, (x * cellSize) + cellSize, y*zoomY + cellSize, paint);
                }
            }
        }
        imageView.setImageBitmap(bitmap);
        buttons.get(floorFrom).setTextColor(Color.WHITE);
        buttons.get(btn_clicked).setTextColor(Color.BLACK);
        btn_clicked = floorFrom;
    }

    public void changeFloor(View view) {
        cnvs.drawColor(getResources().getColor(R.color.white));
        buttons.get(btn_clicked).setTextColor(Color.BLACK);
        zoomIn = 0;
        switch (view.getId()) {
            case R.id.btn_0:
                btn_clicked = 0;
                drawRoute(0, pnt, cnvs, btm, 2, 3);
                break;
            case R.id.btn_1:
                btn_clicked = 1;
                drawRoute(1, pnt, cnvs, btm, 2, 3);
                break;
            case R.id.btn_2:
                btn_clicked = 2;
                drawRoute(2, pnt, cnvs, btm, 2, 3);
                break;
            case R.id.btn_3:
                btn_clicked = 3;
                drawRoute(3, pnt, cnvs, btm, 2, 3);
                break;
        }
        btn_ID = view.getId();
        Button button2 = (Button) findViewById(view.getId());
        button2.setTextColor(Color.WHITE);
    }

    public void drawPrimaryMap(){
        for(int i = 0; i < files.length; i++){
            String file = files[i].getName();
            String[] helper = file.split("_");
            if(Integer.parseInt(helper[5]) == 0) drawMap(file,Float.parseFloat(helper[1]),Float.parseFloat(helper[2]), false, 2, 3, cnvs, pnt);
            if(Integer.parseInt(helper[5]) == 1) drawMap(file,Float.parseFloat(helper[1]),Float.parseFloat(helper[2]), true, 2, 3, cnvs, pnt);
        }
        imageView.setImageBitmap(btm);
    }

    public void drawMap(String fileName, float x2, float y, boolean opacity, int zoomX, int zoomY, Canvas canvas, Paint paint) {
        String line = "";
        String stringOfChars = "";
        String stringOfCharsCoordinates = "";
        int resID = this.getResources().getIdentifier(fileName, "raw", this.getPackageName());
        InputStream inputStream = this.getResources().openRawResource(resID);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        y = y*zoomY;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            float x = x2;
            for (int a = 0; a < line.length(); a++) {
                cellSize = (displayWidth*zoomX)/(MAX_WIDTH);
                RectF rectF = new RectF(x*cellSize, y, (x*cellSize)+cellSize, y+cellSize);

                switch (line.charAt(a)) {
                    case 'X': //stena
                    case 'Y': //chodba
                    case 'R':
                    case 'S':
                        paint.setColor(getResources().getColor(R.color.bcg_color));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawRect(rectF, paint);
                        break;
                    case '_': //schodisko
                        paint.setColor(getResources().getColor(R.color.bcg_color));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawRect(x * cellSize, y, (x * cellSize) + cellSize, y + cellSize, paint);
                        paint.setColor(getResources().getColor(R.color.black));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawLine(x * cellSize, y, (x * cellSize) + cellSize, y, paint);
                        break;
                    case '|': //schodisko
                        paint.setColor(getResources().getColor(R.color.bcg_color));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawRect(x * cellSize, y, (x * cellSize) + cellSize, y + cellSize, paint);
                        paint.setColor(getResources().getColor(R.color.black));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawLine((x * cellSize) + (cellSize / 2), y, (x * cellSize) + (cellSize / 2), y + cellSize, paint);
                        break;
                    case 'T': //schodisko
                        paint.setColor(getResources().getColor(R.color.bcg_color));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawRect(x * cellSize, y, (x * cellSize) + cellSize, y + cellSize, paint);
                        paint.setColor(getResources().getColor(R.color.black));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawLine((x * cellSize), y + (cellSize / 2), (x * cellSize) + cellSize, y + (cellSize / 2), paint);
                        break;
                    case 'Z':
                        break;
                    default:
                        paint.setColor(Color.WHITE);
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawRect(x * cellSize, y, (x * cellSize) + cellSize, y + cellSize, paint);
                        break;
                }
                if(line.charAt(a) >= 48 && line.charAt(a) <= 57) {
                    if ((line.charAt(a + 1) == 'X' && line.charAt(a - 1) == 'X') || line.charAt(a - 1) == 'R' || line.charAt(a + 1) == 'R') {
                        paint.setColor(getResources().getColor(R.color.bcg_color));
                        if(opacity) {
                            paint.setAntiAlias(true);
                            paint.setAlpha(ALPHA);
                        }
                        canvas.drawRect(rectF, paint);
                    }
                }
                if(zoomX == 6 && !opacity) {
                    if (line.charAt(a) >= 48 && line.charAt(a) <= 57 && line.charAt(a+1) >= 48 && line.charAt(a+1) <= 57) {
                        for(int i = 0; i < allRooms.length; i++) {
                            if((fileName.substring(0,1).toUpperCase() + fileName.charAt(fileName.length()-1)).equals(allRooms[i].substring(0,2))) {
                                for(int j = i; j < positionsOfAllRooms.length; j++) {
                                    if(((char) positionsOfAllRooms[j]+'0' == line.charAt(a)) && !stringOfChars.contains(allRooms[j])) {
                                        stringOfChars += allRooms[j] + " ";
                                        stringOfCharsCoordinates += x * cellSize + "-" + y + "-" + allRooms[j] + "_";
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                x++;
            }
            y+=zoomY;
        }

        for(int i = 0; i < stringOfCharsCoordinates.length(); i++) {
            String[] helper = stringOfCharsCoordinates.split("_");
            for(int k = 0; k < helper.length; k++) {
                String[] helper2 = helper[k].split("-");
                paint.setColor(Color.BLACK);
                paint.setTextSize(cellSize * 2);
                canvas.drawText(helper2[2], Float.parseFloat(helper2[0]), Float.parseFloat(helper2[1]) + cellSize * 2, paint);
            }
        }
    }

    //priblíženie mapy
    public void zoomIn(View view) {
        if (zoomIn == 0) {
            zoomIn++;
            imageView.setImageBitmap(Bitmap.createScaledBitmap(btm, (int) (displayWidth * 3), (int) (displayHeight * 1.3), false));
        } else if (zoomIn == 1) {
            zoomIn++;
            Bitmap bitmap2 = Bitmap.createBitmap((int)displayWidth*6, (int)displayHeight*2, Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(bitmap2);
            Paint paint2 = new Paint();

            drawRoute(btn_clicked, paint2, canvas2, bitmap2, 6, 8);
            imageView.setImageBitmap(bitmap2);
        }
        System.out.println(zoomIn);
    }

    //oddialenie mapy
    public void zoomOut(View view) { //oddialiť mapu
        if(zoomIn == 1) {
            zoomIn--;
            imageView.setImageBitmap(btm);
        } else if(zoomIn == 2) {
            zoomIn--;
            imageView.setImageBitmap(Bitmap.createScaledBitmap(btm, (int) (displayWidth * 3), (int) (displayHeight * 1.3), false));
        }
        System.out.println(zoomIn);
    }

    //okno so slovnou navigáciou
    public void writeRoute(View view) {
        if(roomFrom.length() > 0 && roomTo.length() > 0) { //trasa sa vypíše iba pokiaľ používateľ zvolil miestnosti
            relativeLayout = (RelativeLayout) findViewById(R.id.second_rel);
            relativeLayout.setAlpha(0.5f);

            linearLayoutRoute = (LinearLayout) findViewById(R.id.linear_listView);
            linearLayoutRoute.setVisibility(View.VISIBLE);
            linearLayoutRoute.setAlpha(1f);

            ArrayList<String> writeRoute = new ArrayList<>();
            ArrayList<Integer> writeRouteImages = new ArrayList<>();

            if (stairs.length() > 0) {
                if (floorFrom == 0) writeRoute.add("Vaša trasa začína z miestnosti " + roomFrom + " na prízemí");
                else writeRoute.add("Vaša trasa začína z miestnosti " + roomFrom + " na " + floorFrom + ". poschodí");
                writeRouteImages.add(R.drawable.walk);
                if (roomFrom.charAt(0) == roomTo.charAt(0)) { //používateľ hľadá trasu v jednom bloku
                    writeRoute.add("Pokračujte v naznačenej trase na poschodí");
                    writeRouteImages.add(R.drawable.walk);
                    if (floorFrom > floorTo) {
                        if (floorFrom - floorTo == 1) writeRoute.add("Prejdite cez schodisko o 1 poschodie nižšie");
                        if (floorFrom - floorTo == 2) writeRoute.add("Prejdite cez schodisko o 2 poschodia nižšie");
                        if (floorFrom - floorTo == 3) writeRoute.add("Prejdite cez schodisko o 3 poschodia nižšie");
                        writeRouteImages.add(R.drawable.stairs);
                    } else {
                        if (floorTo - floorFrom == 1) writeRoute.add("Prejdite cez schodisko o 1 poschodie vyššie");
                        if (floorTo - floorFrom == 2) writeRoute.add("Prejdite cez schodisko o 2 poschodia vyššie");
                        if (floorTo - floorFrom == 3) writeRoute.add("Prejdite cez schodisko o 3 poschodia vyššie");
                        writeRouteImages.add(R.drawable.stairs);
                    }
                    writeRoute.add("Pokračujte v naznačenej trase na poschodí");
                    writeRouteImages.add(R.drawable.walk);
                } else { //používateľ hľadá trasu medzi viacerými blokmi
                    writeRoute.add("Pokračujte v naznačenej trase ku schodisku");
                    writeRouteImages.add(R.drawable.walk);
                    writeRoute.add("Prejdite cez schodisko až na prízemie");
                    writeRouteImages.add(R.drawable.stairs);
                    writeRoute.add("Pokračujte v naznačenej trase na prízemí");
                    writeRouteImages.add(R.drawable.walk);
                    if(floorTo != 0) {
                        writeRoute.add("Prejdite cez schodisko na " + floorTo + ". poschodie");
                        writeRouteImages.add(R.drawable.stairs);
                        writeRoute.add("Pokračujte v naznačenej trase na poschodí");
                        writeRouteImages.add(R.drawable.walk);
                    }
                }
                if (floorTo == 0) writeRoute.add("Dostali ste sa do hľadanej miestnosti " + roomTo + " na prízemí");
                else writeRoute.add("Dostali ste sa do hľadanej miestnosti " + roomTo + " na " + floorTo + ". poschodí");
                writeRouteImages.add(R.drawable.walk);
            } else { //pokiaľ sa nejde cez poschodie -> používateľ je na tom istom poschodí
                if (floorFrom == 0) writeRoute.add("Vaša trasa začína z miestnosti " + roomFrom + " na prízemí");
                else writeRoute.add("Vaša trasa začína z miestnosti " + roomFrom + " na " + floorFrom + ". poschodí");
                writeRouteImages.add(R.drawable.walk);
                writeRoute.add("Hľadaná miestnosť sa nachádza na tej istej chodbe. Pokračujte podľa trasy na mape");
                writeRouteImages.add(R.drawable.walk);
                if (floorTo == 0) writeRoute.add("Dostali ste sa do hľadanej miestnosti " + roomTo + " na prízemí");
                else writeRoute.add("Dostali ste sa do hľadanej miestnosti " + roomTo + " na " + floorTo + ". poschodí");
                writeRouteImages.add(R.drawable.walk);
            }

            ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
            for (int i = 0; i < writeRoute.size(); i++)
            {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("text",writeRoute.get(i));
                hashMap.put("image",writeRouteImages.get(i)+"");
                arrayList.add(hashMap);
            }
            String[] from = {"text","image"};
            int[] to = {R.id.label,R.id.imageViewRoute};

            SimpleAdapter adapter = new SimpleAdapter(this, arrayList, R.layout.listview_layout_route,from, to);
            ListView listView = (ListView) findViewById(R.id.listViewRoute);
            listView.setAdapter(adapter);
        } else Toast.makeText(this, "Nemáte vybranú žiadnu trasu!", Toast.LENGTH_SHORT).show();
    }

    //zatvorenie okna zo slovnou navigáciou
    public void exitLayout(View view) {
        linearLayoutRoute.setVisibility(View.INVISIBLE);
        relativeLayout.setAlpha(1f);
    }

    //pomocná trieda pri algortimu na hľadanie trasy
    class Node {
        int x, y;

        Node(int x1, int y1) {
            x = x1;
            y = y1;
        }
    }
}