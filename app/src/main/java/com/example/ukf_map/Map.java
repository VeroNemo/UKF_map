package com.example.ukf_map;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Map extends View {

    private String room, blok;
    public Context context;
    private float y, cellSize, displayWidth, displayHeight, viewHeight;
    private int position;
    private Canvas bitmapCanvas, canvas;
    private Bitmap bitmap;
    private Paint paint;
    private float[] cornerLT = new float[]{5,5,0,0,0,0,0,0};
    private float[] cornerRT = new float[]{0,0,5,5,0,0,0,0};
    private float[] cornerLB = new float[]{0,0,0,0,0,0,5,5};
    private float[] cornerRB = new float[]{0,0,0,0,5,5,0,0};


    public Map(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
    }

    public void setParameters(String room, String blok, float w, float h, int position, int layoutTopH) {
        this.room = room;
        this.blok = blok;
        this.displayWidth = w;
        this.displayHeight = h;
        this.position = position;
        this.viewHeight = h - (layoutTopH+200);

        if(blok.equals("P")) {
            cellSize = displayWidth/359;
            y = ((viewHeight-getNavigationBarHeight())/2) - (56*cellSize);
        } else if(blok.equals("S")) {
            cellSize = displayWidth/92;
            y = ((viewHeight-getNavigationBarHeight())/2) - (23*cellSize);
        } else {
            cellSize = displayWidth/149;
            y = ((viewHeight-getNavigationBarHeight())/2) - (20*cellSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bitmap = Bitmap.createBitmap((int)displayWidth, (int)displayHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        createMap(bitmapCanvas);
       // canvas.save();
      //  canvas.rotate(50);
       // canvas.translate(-40, -40);
       // canvas.scale(3.f, 3.f);
        canvas.drawBitmap(bitmap, 0, 0, paint);
      //  canvas.restore();
    }

    private void createMap(Canvas canvas) {
        String line = "";
        int resID = context.getResources().getIdentifier("floor_"+blok.toLowerCase() + room.charAt(0), "raw", context.getPackageName());
        InputStream inputStream = this.getResources().openRawResource(resID);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            try {
                if((line = reader.readLine()) == null) break;
            } catch (IOException e){
                e.printStackTrace();
            }
            for(int x=0; x<line.length(); x++){
                Path path = new Path();
                RectF rectF = new RectF(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize);
                switch (line.charAt(x)){
                    case 'X': //stena
                    case 'Y': //chodba
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
                        if(line.charAt(x+1) == (char)(position+'0')){
                            paint.setColor(Color.rgb(91, 195, 235));
                        }else paint.setColor(Color.WHITE);
                        path.addRoundRect(rectF, cornerLT, Path.Direction.CW);
                        canvas.drawPath(path, paint);
                        break;
                    case 'B': //roh miestnosti
                        paint.setColor(Color.rgb(218,218,217));
                        canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                        if(line.charAt(x-1) == (char)(position+'0')){
                            paint.setColor(Color.rgb(91, 195, 235));
                        }else paint.setColor(Color.WHITE);
                        path.addRoundRect(rectF, cornerRT, Path.Direction.CW);
                        canvas.drawPath(path, paint);
                        break;
                    case 'C': //roh miestnosti
                        paint.setColor(Color.rgb(218,218,217));
                        canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                        if(line.charAt(x+1) == (char)(position+'0')){
                            paint.setColor(Color.rgb(91, 195, 235));
                        }else paint.setColor(Color.WHITE);
                        path.addRoundRect(rectF, cornerLB, Path.Direction.CW);
                        canvas.drawPath(path, paint);
                        break;
                    case 'D': //roh miestnosti
                        paint.setColor(Color.rgb(218,218,217));
                        canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                        if(line.charAt(x-1) == (char)(position+'0')){
                            paint.setColor(Color.rgb(91, 195, 235));
                        }else paint.setColor(Color.WHITE);
                        path.addRoundRect(rectF, cornerRB, Path.Direction.CW);
                        canvas.drawPath(path, paint);
                        break;
                    case 'Z':
                        paint.setColor(Color.rgb(241, 241, 241));
                        canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                        break;
                    default:
                        paint.setColor(Color.WHITE);
                        canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                        break;
                }

                if(((char) (position+'0')) == line.charAt(x)) {
                    paint.setColor(Color.rgb(91, 195, 235));
                    canvas.drawRect(x*cellSize,y,(x*cellSize)+cellSize, y+cellSize, paint);
                }
            }
            y+=cellSize;
        }
    }

    private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height","dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }
}
