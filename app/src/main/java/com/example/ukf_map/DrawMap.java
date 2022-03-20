package com.example.ukf_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class DrawMap extends View {

    private float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
    private float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
    private float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
    private float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};

    private Context context;
    private Paint paint;
    private float cellSize, dW, dH;
    private Field[] files;
    private int floor;
    private ImageView image;

    public float[][] fileSize;

    public DrawMap(Context context, Field[] f, float size, int floor, ImageView image, float dW, float dH) {
        super(context);

        this.context = context;
        this.files = f;
        this.cellSize = size;
        this.floor = floor;
        this.image = image;
        this.dH = dH;
        this.dW = dW;

        System.out.println(cellSize);

        paint = new Paint();
        fileSize = new float[f.length][4];

        //draw(new Canvas());
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Bitmap bitmap = Bitmap.createBitmap((int) dW,434, Bitmap.Config.ARGB_8888);
        //Canvas canvas1 = new Canvas(bitmap);
        drawMap(canvas);
        //image.setImageBitmap(bitmap);

        //canvas.drawBitmap(bitmap, 0, 0, paint);
        for(int j=0; j<fileSize.length; j++) {
            //paint.setColor(Color.RED);
            //canvas1.drawRect(fileSize[j][0], fileSize[j][1], fileSize[j][0]+cellSize, fileSize[j][1]+cellSize, paint);
            //canvas1.drawRect(fileSize[j][2], fileSize[j][3], fileSize[j][2]+cellSize, fileSize[j][3]+cellSize, paint);
            System.out.println(j + ". [ " + fileSize[j][0] + " - "+ fileSize[j][1] + " - "+ fileSize[j][2] + " - "+ fileSize[j][3] + " ]");
        }
    }

    private void drawMap(Canvas canvas) {
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

    public float[][] getFiles() {
        return fileSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        for(int j = 0; j < fileSize.length; j++) {
            if (x >= fileSize[j][0] && x <= fileSize[j][2] && y >= fileSize[j][1] && y <= fileSize[j][3]) {
                System.out.println(files[j].getName().charAt(0));
                DrawMap2 drawMap2 = new DrawMap2(context, files[j].getName().charAt(0), cellSize);
                drawMap2.setBackgroundColor(Color.rgb(241, 241, 241));
            }
        }

        return true;
    }
}
