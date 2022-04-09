package com.example.ukf_map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class FindRoom extends AppCompatActivity {

    private final float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
    private final float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
    private final float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
    private final float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};
    private final String[] allRooms = {"A010", "A013", "A111", "A115", "A119", "A223", "A226", "B025", "B112", "B113", "B114", "B212", "C011", "C117", "C118", "C119", "C212", "C217", "C218", "C305", "P002", "P006", "S109", "S110"};
    private final int[] positionsOfAllRooms = {0, 1, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 0, 1};

    public float displayWidth, displayHeight;
    public Bitmap bitmap;
    public ImageView imageView;
    public boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_room);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);
        displayWidth = distances.x;
        displayHeight = distances.y;

        isOpen = false;
    }

    public void chooseRoom(View view) {
        String btn_name = getResources().getResourceEntryName(view.getId());
        String room = btn_name.substring(4);

        setContentView(R.layout.activity_find_room2);
        isOpen = true;

        TextView textView = (TextView) findViewById(R.id.txt_titleForActivity4);
        textView.setText("Učebňa "+room);

        imageView = (ImageView) findViewById(R.id.imageView4);

        drawMap(room);
    }

    public void drawMap(String room) {
        Paint paint = new Paint();

        int position = 0;
        float cellSize;
        for(int j = 0; j < allRooms.length; j++) {
            if(room.equals(allRooms[j])) {
                position = positionsOfAllRooms[j];
            }
        }
        Field[] files = R.raw.class.getFields();
        float y = 0;

        for(int i = 0; i < files.length; i++){
            String file = files[i].getName();
            String[] helper = file.split("_");
            if(helper[0].equals((room.substring(0,1)).toLowerCase()) && helper[5].equals(room.substring(1,2))) {
                if(helper[0].equals("p")) cellSize = displayWidth / Float.parseFloat(helper[4]);
                else cellSize = displayWidth / Float.parseFloat(helper[3]);

                bitmap = Bitmap.createBitmap(((int)cellSize*Integer.parseInt(helper[3]))+75, ((int)cellSize*Integer.parseInt(helper[4]))+50, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                String line = "";

                int resID = this.getResources().getIdentifier(file, "raw", this.getPackageName());
                InputStream inputStream = this.getResources().openRawResource(resID);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    while (true) {
                        try {
                            if ((line = reader.readLine()) == null) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (int x = 0; x < line.length(); x++) {
                            Path path = new Path();
                            RectF rectF = new RectF(x* cellSize, y, (x* cellSize)+ cellSize, y+ cellSize);

                            switch (line.charAt(x)) {
                                case 'X':
                                case 'Y':
                                case 'R':
                                case 'U':
                                case 'K':
                                case 'E':
                                case 'S':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    break;
                                case '_':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    paint.setColor(getResources().getColor(R.color.black));
                                    canvas.drawLine(x * cellSize, y, (x * cellSize) + cellSize, y, paint);
                                    break;
                                case '|':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    paint.setColor(getResources().getColor(R.color.black));
                                    canvas.drawLine((x * cellSize) + (cellSize / 2), y, (x * cellSize) + (cellSize / 2), y + cellSize, paint);
                                    break;
                                case 'T':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    paint.setColor(getResources().getColor(R.color.black));
                                    canvas.drawLine((x * cellSize), y + (cellSize / 2), (x * cellSize) + cellSize, y + (cellSize / 2), paint);
                                    break;
                                case 'Z':
                                    break;
                                case 'A':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    if(line.charAt(x+1) == (char)(position+'0')){
                                        paint.setColor(Color.rgb(91, 195, 235));
                                    }else paint.setColor(Color.WHITE);
                                    path.addRoundRect(rectF, cornerLT, Path.Direction.CW);
                                    canvas.drawPath(path, paint);
                                    break;
                                case 'B':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    if(line.charAt(x-1) == (char)(position+'0')){
                                        paint.setColor(Color.rgb(91, 195, 235));
                                    }else paint.setColor(Color.WHITE);
                                    path.addRoundRect(rectF, cornerRT, Path.Direction.CW);
                                    canvas.drawPath(path, paint);
                                    break;
                                case 'C':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    if(line.charAt(x+1) == (char)(position+'0')){
                                        paint.setColor(Color.rgb(91, 195, 235));
                                    }else paint.setColor(Color.WHITE);
                                    path.addRoundRect(rectF, cornerLB, Path.Direction.CW);
                                    canvas.drawPath(path, paint);
                                    break;
                                case 'D':
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                    if(line.charAt(x-1) == (char)(position+'0')){
                                        paint.setColor(Color.rgb(91, 195, 235));
                                    }else paint.setColor(Color.WHITE);
                                    path.addRoundRect(rectF, cornerRB, Path.Direction.CW);
                                    canvas.drawPath(path, paint);
                                    break;
                                default:
                                    paint.setColor(Color.WHITE);
                                    canvas.drawRect(rectF, paint);
                                    break;
                            }
                            if(((char) (position+'0')) == line.charAt(x)) {
                                paint.setColor(Color.rgb(91, 195, 235));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                            }
                            if(line.charAt(x) >= 48 && line.charAt(x) <= 55) {
                                if ((line.charAt(x + 1) == 'X' && line.charAt(x - 1) == 'X') || line.charAt(x - 1) == 'R' || line.charAt(x + 1) == 'R') {
                                    paint.setColor(getResources().getColor(R.color.bcg_color));
                                    canvas.drawRect(rectF, paint);
                                }
                            }
                        }
                        y+= cellSize;
                    }
            }
        }
        imageView.setImageBitmap(bitmap);
    }

    public void chooseDepartment(View view) {
        switch (view.getId()) {
            case R.id.btn_INF:
                setContentView(R.layout.layout_katedra_informatiky);
                break;
            case R.id.btn_MAT:
                setContentView(R.layout.layout_katedra_matematiky);
                break;
            case R.id.btn_FYZ:
                setContentView(R.layout.layout_katedra_fyziky);
                break;
            case R.id.btn_CHEM:
                break;
            case R.id.btn_GEO:
                break;
            case R.id.btn_EKO:
                break;
        }
    }

   /* @Override
    public void onBackPressed() {
        if (isOpen) {
            setContentView(R.layout.activity_find_room);
        } else super.onBackPressed();
    } */
}