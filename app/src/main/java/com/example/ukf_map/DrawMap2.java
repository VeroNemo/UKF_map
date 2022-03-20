package com.example.ukf_map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DrawMap2 extends View {

    private float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
    private float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
    private float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
    private float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};

    private Paint paint;
    private Context context;
    private float cellSize;
    private ArrayList<String> files = new ArrayList();

    public DrawMap2(Context context, char floor, float cell) {
        super(context);
        this.context = context;
        this.cellSize = cell;

        paint = new Paint();

        String helper = "";
        Field[] fields = R.raw.class.getFields();
        for(int i = 0; i < fields.length; i++) {
            if(fields[i].getName().charAt(0) == floor) {
                files.add(fields[i].getName());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int i = 0; i < files.size(); i++){
            String file = files.get(i);
            String[] helper = file.split("_");
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
                y++;
            }
        }
    }
}
