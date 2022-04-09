package com.example.ukf_map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DateFormat;

public class FindFloor extends AppCompatActivity {

    private final int MAX_WIDTH = 508;
    final int MAX_HEIGHT = 432;
    final int ALPHA = 40;

    private ImageView imageViewFloor;
    public Button button0;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;
    private float displayWidth, displayHeight, cellSize;
    public int btn_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_floor);

        imageViewFloor = (ImageView) findViewById(R.id.imageViewFloors);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);
        displayWidth = distances.x;
        displayHeight = distances.y;
        cellSize = displayWidth/MAX_WIDTH;

        bitmap = Bitmap.createBitmap((int)displayWidth, (int)MAX_HEIGHT+20, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();

        drawFloors(0);
        button0 = (Button) findViewById(R.id.btn_0);
        button0.setBackgroundColor(getResources().getColor(R.color.bcg_color2));
    }

    public void changeFloor(View view) {
        canvas.drawColor(getResources().getColor(R.color.white));
        if(btn_ID != 0) {
            Button button = (Button) findViewById(btn_ID);
            button.setBackgroundColor(getResources().getColor(R.color.btn_color));
        } else button0.setBackgroundColor(getResources().getColor(R.color.btn_color));
        switch (view.getId()) {
            case R.id.btn_0:
                drawFloors(0);
                break;
            case R.id.btn_1:
                drawFloors(1);
                break;
            case R.id.btn_2:
                drawFloors(2);
                break;
            case R.id.btn_3:
                drawFloors(3);
                break;
        }
        btn_ID = view.getId();
        Button button2 = (Button) findViewById(view.getId());
        button2.setBackgroundColor(getResources().getColor(R.color.bcg_color2));
    }

    public void drawFloors(int floor){
        Field [] files = R.raw.class.getFields();
        boolean opacity = false;

        for(int i = 0; i < files.length; i++){
            String file = files[i].getName();
            String[] helper = file.split("_");

            String line = "";
            float y = Float.parseFloat(helper[2]);

            int resID = this.getResources().getIdentifier(file, "raw", this.getPackageName());
            InputStream inputStream = this.getResources().openRawResource(resID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            if((helper[0].equals("p") || helper[0].equals("v")) && floor != 0) opacity = true;
            else opacity = false;

            if(helper[5].equals(""+floor) || helper[0].equals("p") || helper[0].equals("v")) {
                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    float x = Float.parseFloat(helper[1]);
                    for (int a = 0; a < line.length(); a++) {
                        RectF rectF = new RectF(x*cellSize, y, (x*cellSize)+cellSize, y+cellSize);

                        switch (line.charAt(a)) {
                            case 'X': //stena
                            case 'Y': //chodba
                            case 'R':
                            case 'U':
                            case 'K':
                            case 'E':
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
                        if(line.charAt(a) >= 48 && line.charAt(a) <= 55) {
                            if ((line.charAt(a + 1) == 'X' && line.charAt(a - 1) == 'X') || line.charAt(a - 1) == 'R' || line.charAt(a + 1) == 'R') {
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                if(opacity) {
                                    paint.setAntiAlias(true);
                                    paint.setAlpha(ALPHA);
                                }
                                canvas.drawRect(rectF, paint);
                            }
                        }
                        x++;
                    }
                    y++;
                }
            }
        }
        imageViewFloor.setImageBitmap(Bitmap.createScaledBitmap(bitmap, (int)(displayWidth*1.2), (int)(displayHeight*0.5), false));
    }
}