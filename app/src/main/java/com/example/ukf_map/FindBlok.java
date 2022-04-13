package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class FindBlok extends AppCompatActivity {

    private final int MAX_WIDTH = 508;
    final int MAX_HEIGHT = 432;
    private final float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
    private final float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
    private final float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
    private final float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};

    private ImageView imageViewBlok;
    public Button buttonS;
    public TextView textView;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;
    private float displayWidth;
    private float displayHeight;
    public int btn_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_blok);

        imageViewBlok = (ImageView) findViewById(R.id.imageViewBlok);
        textView = (TextView) findViewById(R.id.textViewDesc);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);
        displayWidth = distances.x;
        displayHeight = distances.y;

        bitmap = Bitmap.createBitmap((int) displayWidth, (int) displayHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();

        drawBlok("S");
        buttonS = (Button) findViewById(R.id.btn_S);
        buttonS.setBackgroundColor(getResources().getColor(R.color.bcg_color2));
        textView.setText("Blok S sa nachádza na 1. poschodí");
    }

    public void changeBlok(View view) {
        canvas.drawColor(getResources().getColor(R.color.white));
        if(btn_ID != 0) {
            Button button = (Button) findViewById(btn_ID);
            button.setBackgroundColor(getResources().getColor(R.color.btn_color));
        } else buttonS.setBackgroundColor(getResources().getColor(R.color.btn_color));
        switch (view.getId()) {
            case R.id.btn_A:
                textView.setText("Blok A sa nachádza na prízemí, 1., 2. a 3. poschodí");
                drawBlok("A");
                break;
            case R.id.btn_B:
                textView.setText("Blok B sa nachádza na prízemí, 1. a 2. poschodí");
                drawBlok("B");
                break;
            case R.id.btn_C:
                textView.setText("Blok C sa nachádza na prízemí, 1., 2. a 3. poschodí");
                drawBlok("C");
                break;
            case R.id.btn_P:
                textView.setText("Blok P sa nachádza na prízemí");
                drawBlok("P");
                break;
            case R.id.btn_S:
                textView.setText("Blok S sa nachádza na 1. poschodí");
                drawBlok("S");
                break;
        }
        btn_ID = view.getId();
        Button button2 = (Button) findViewById(view.getId());
        button2.setBackgroundColor(getResources().getColor(R.color.bcg_color2));
    }

    public void drawBlok(String blok) {
        Field[] files = R.raw.class.getFields();
        float y = 0;

        for(int i = 0; i < files.length-1; i++){
            String file = files[i].getName();
            String[] helper = file.split("_");
            float cellSize;
            if(helper[0].equals("p")) {
                cellSize = displayWidth / Float.parseFloat(helper[4]);
                y = 60;
            } else if(helper[0].equals("b")){
                cellSize = displayWidth / Float.parseFloat(helper[3]);
                if(Integer.parseInt(helper[5]) == 1) y = 60 + Float.parseFloat(helper[4])*cellSize;
                else if(Integer.parseInt(helper[5]) == 2) y = 120 + 2*(Float.parseFloat(helper[4])*cellSize);
                else y = 60;
            } else if(helper[0].equals("c")){
                cellSize = displayWidth / Float.parseFloat(helper[3]);
                if(Integer.parseInt(helper[5]) == 1) y = 90 + Float.parseFloat(helper[4])*cellSize;
                else if(Integer.parseInt(helper[5]) == 2) y = 130 + 2*(Float.parseFloat(helper[4])*cellSize);
                else if(Integer.parseInt(helper[5]) == 3) y = 170 + 3*(Float.parseFloat(helper[4])*cellSize);
                else y = 60;
            }  else if(helper[0].equals("a")) {
                cellSize = displayWidth / Float.parseFloat(helper[3]);
                if (Integer.parseInt(helper[5]) == 1) y = 90 + Float.parseFloat(helper[4]) * cellSize;
                else if (Integer.parseInt(helper[5]) == 2) y = 130 + 2*Float.parseFloat(helper[4]) * cellSize;
                else if (Integer.parseInt(helper[5]) == 3) y = 170 + 3*(Float.parseFloat(helper[4]) * cellSize);
                else y = 60;
            }else {
                cellSize = displayWidth / Float.parseFloat(helper[3]);
                y = 60;
            }

            String line = "";

            int resID = this.getResources().getIdentifier(file, "raw", this.getPackageName());
            InputStream inputStream = this.getResources().openRawResource(resID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            if(helper[0].equals(blok.toLowerCase())) {
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
                            case 'X': //stena
                            case 'Y': //chodba
                            case 'R':
                            case 'S':
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                break;
                            case '_': //schodisko
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(getResources().getColor(R.color.black));
                                canvas.drawLine(x * cellSize, y, (x * cellSize) + cellSize, y, paint);
                                break;
                            case '|': //schodisko
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(getResources().getColor(R.color.black));
                                canvas.drawLine((x * cellSize) + (cellSize / 2), y, (x * cellSize) + (cellSize / 2), y + cellSize, paint);
                                break;
                            case 'T': //schodisko
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(getResources().getColor(R.color.black));
                                canvas.drawLine((x * cellSize), y + (cellSize / 2), (x * cellSize) + cellSize, y + (cellSize / 2), paint);
                                break;
                            case 'Z':
                                break;
                            case 'A': //roh miestnosti
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerLT, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'B': //roh miestnosti
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerRT, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'C': //roh miestnosti
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerLB, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'D': //roh miestnosti
                                paint.setColor(getResources().getColor(R.color.bcg_color));
                                canvas.drawRect(rectF, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerRB, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            default:
                                paint.setColor(Color.WHITE);
                                canvas.drawRect(rectF, paint);
                                break;
                        }
                        if(line.charAt(x) >= 48 && line.charAt(x) <= 57) {
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
        imageViewBlok.setImageBitmap(bitmap);
    }
}