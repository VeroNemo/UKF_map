package com.example.ukf_map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.Image;
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
import java.text.Format;
import java.util.ArrayList;

public class FindRouteToRoom2 extends AppCompatActivity {

    final int MAX_WIDTH = 513;
    final int MAX_HEIGHT = 432;

    private DrawMap drawMap;
    private String[] filesNames;
    private float displayWidth, displayHeight, cellSize;
    private int floor;
    private Field[] files;

    protected float[][] fileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_find_route_to_room2);

        Intent intent = getIntent();
        floor = Integer.parseInt(intent.getStringExtra("floor"));
        ImageView imageView = (ImageView) findViewById(R.id.imageView3);

        files = R.raw.class.getFields();
        filesNames = new String[files.length];
        for(int i = 0; i < filesNames.length; i++) {
            filesNames[i] = files[i].getName();
        }

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);
        displayWidth = distances.x;
        displayHeight = distances.y;
        cellSize = displayWidth/MAX_WIDTH;

        //drawMap = new DrawMap(this, files, cellSize, floor, imageView, displayWidth, displayHeight);
        //drawMap.setBackgroundColor(Color.rgb(241, 241, 241));
        //setContentView(drawMap);
        DrawView drawView = new DrawView(this, files, cellSize);
        drawView.setBackgroundColor(Color.rgb(241, 241, 241));
        setContentView(drawView);
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float [][] fileSize = drawMap.getFiles();
        for(int j = 0; j < fileSize.length; j++) {
            if (x >= fileSize[j][0] && x <= fileSize[j][2] && y >= fileSize[j][1] && y <= fileSize[j][3]) {
                System.out.println(files[j].getName().charAt(0));
            }
        }

        for(int i = 0; i < filesNames.length; i++) {
            String[] helper = filesNames[i].split("_");
            float x2 = Float.parseFloat(helper[1]);
            float y2 = Float.parseFloat(helper[2]);
            float width = Float.parseFloat(helper[3]);
            float height = Float.parseFloat(helper[4]);
            if( x >= x2*cellSize && x <= (x2+width)*cellSize && y >= y2 && y <= y2+(height-1) ) {
                System.out.println(helper[0]);
            }
        }

        return true;
    } */

    private class DrawView extends View {

        private float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
        private float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
        private float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
        private float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};

        private Context context;
        private Paint paint;
        private float cellSize;
        private Field[] files;

        //public float[][] fileSize;

        public DrawView(Context context, Field[] f, float size) {
            super(context);
            this.context = context;
            this.files = f;
            this.cellSize = size;

            System.out.println(cellSize);

            paint = new Paint();
            fileSize = new float[f.length][4];
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for(int i = 0; i < files.length; i++){
                String file = files[i].getName();
                String[] helper = file.split("_");
                //if(helper[5].equals(""+floor)) {
                String line = "";
                float y = Float.parseFloat(helper[2]);

                int resID = this.getResources().getIdentifier(file, "raw", context.getPackageName());
                InputStream inputStream = this.getResources().openRawResource(resID);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    try {
                        if((line = reader.readLine()) == null) break;
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    float x = Float.parseFloat(helper[1]);
                    for(int a = 0; a < line.length(); a++) {
                        Path path = new Path();
                        RectF rectF = new RectF(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize);

                        if(y == Float.parseFloat(helper[2])) {
                            if(a == 0) fileSize[i][0] = x*cellSize;
                            if(a == (line.length()-1)) fileSize[i][1] = y;
                        }

                        switch (line.charAt(a)){
                            case 'X': //stena
                            case 'Y': //chodba
                            case 'R':
                            case 'U':
                            case 'K':
                            case 'E':
                            case 'S':
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(rectF, paint);
                                break;
                            case '_': //schodisko
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.rgb(89,89,89));
                                canvas.drawLine(x*cellSize, y, (x*cellSize)+cellSize, y, paint);
                                break;
                            case '|': //schodisko
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.rgb(89,89,89));
                                canvas.drawLine((x*cellSize)+(cellSize/2), y, (x*cellSize)+(cellSize/2), y+cellSize, paint);
                                break;
                            case 'T': //schodisko
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.rgb(89,89,89));
                                canvas.drawLine((x*cellSize), y+(cellSize/2), (x*cellSize)+cellSize, y+(cellSize/2), paint);
                                break;
                            case 'A': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerLT, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'B': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerRT, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'C': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerLB, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'D': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerRB, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'Z':
                                break;
                            default:
                                paint.setColor(Color.WHITE);
                                canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                                break;
                        }
                        x++;
                    }
                    fileSize[i][2] = (x*cellSize);
                    fileSize[i][3] = y;
                    y++;
                }
                //}
            }
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            for(int j = 0; j < fileSize.length; j++) {
                if (x >= fileSize[j][0] && x <= fileSize[j][2] && y >= fileSize[j][1] && y <= fileSize[j][3]) {
                    System.out.println(files[j].getName().charAt(0));
                    DrawViewMini drawViewMini = new DrawViewMini(context, files[j].getName());
                    drawViewMini.setBackgroundColor(Color.rgb(241, 241, 241));
                    setContentView(drawViewMini);
                    break;
                }
            }

            return true;
        }
    }

    private class DrawViewMini extends View {

        private float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
        private float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
        private float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
        private float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};

        private Context context;
        private Paint paint;
        private float cellSize2;
        private ArrayList<String> help = new ArrayList<>();
        private String[] helper;

        public DrawViewMini(Context context, String name) {
            super(context);
            this.context = context;

            helper = name.split("_");

            for(int i = 0; i < filesNames.length; i++) {
                if((""+filesNames[i].charAt(0)).equals(helper[0])) {
                    System.out.println(filesNames[i]);
                    help.add(filesNames[i]);
                }
            }

            cellSize2 = displayWidth/Float.parseFloat(helper[3]);

            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float y = 0;

            for(int i = 0; i < help.size(); i++){
                String line = "";
                switch (i) {
                    case 0:
                        y = 10;
                        break;
                    case 1:
                        y = 20 + Float.parseFloat(helper[4])*cellSize2;
                        break;
                    case 2:
                        y = 30 + 2*(Float.parseFloat(helper[4])*cellSize2);
                        break;
                }

                int resID = this.getResources().getIdentifier(help.get(i), "raw", context.getPackageName());
                InputStream inputStream = this.getResources().openRawResource(resID);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    try {
                        if((line = reader.readLine()) == null) break;
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                    for(int x = 0; x < line.length(); x++) {
                        Path path = new Path();
                        RectF rectF = new RectF(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2);

                        switch (line.charAt(x)){
                            case 'X': //stena
                            case 'Y': //chodba
                            case 'R':
                            case 'U':
                            case 'K':
                            case 'E':
                            case 'S':
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(rectF, paint);
                                break;
                            case '_': //schodisko
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.rgb(89,89,89));
                                canvas.drawLine(x*cellSize2, y, (x*cellSize2)+cellSize2, y, paint);
                                break;
                            case '|': //schodisko
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.rgb(89,89,89));
                                canvas.drawLine((x*cellSize2)+(cellSize2/2), y, (x*cellSize2)+(cellSize2/2), y+cellSize2, paint);
                                break;
                            case 'T': //schodisko
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.rgb(89,89,89));
                                canvas.drawLine((x*cellSize2), y+(cellSize2/2), (x*cellSize2)+cellSize2, y+(cellSize2/2), paint);
                                break;
                            case 'A': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerLT, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'B': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerRT, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'C': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerLB, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'D': //roh miestnosti
                                paint.setColor(Color.rgb(218,218,217));
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                paint.setColor(Color.WHITE);
                                path.addRoundRect(rectF, cornerRB, Path.Direction.CW);
                                canvas.drawPath(path, paint);
                                break;
                            case 'Z':
                                break;
                            default:
                                paint.setColor(Color.WHITE);
                                canvas.drawRect(x*cellSize2,y,(x*cellSize2)+cellSize2, y+cellSize2, paint);
                                break;
                        }
                    }
                    y+=cellSize2;
                }
            }
        }
    }
}