package com.example.ukf_map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapForRooms extends AppCompatActivity{

    public TextView txt_room, txt_floor, txt_blok;
    public LinearLayout linearLayoutTop, linearLayoutBottom;
    public float x, y;
    public FloatingActionButton btn_info, btn_zoomIn, btn_zoomOut;
    public Map map;
    public String room, blok;
    public ScaleAnimation animation;
    public boolean animationZoomIn = false, animationZoomOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_for_rooms);

        Intent intent = getIntent();
        room = intent.getStringExtra("room_number");
        blok = intent.getStringExtra("blok");

        linearLayoutBottom = (LinearLayout) findViewById(R.id.linearLayout_blok);
        btn_info = (FloatingActionButton) findViewById(R.id.btn_info);
        btn_zoomIn = (FloatingActionButton) findViewById(R.id.btn_zoomIn);
        btn_zoomOut = (FloatingActionButton) findViewById(R.id.btn_zoomOut);
        map = (Map) findViewById(R.id.layout_map);
        linearLayoutTop = (LinearLayout) findViewById(R.id.linearLayout_title);
        
        setTextFields();
        getDisplayParameters();

        linearLayoutTop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //linearLayoutTop.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                map.setParameters(room, blok, x, y, Integer.parseInt(intent.getStringExtra("position")), linearLayoutTop.getHeight());
            }
        });

        setAnimationToMap();
        setFloatingButton();
    }

    public void setFloatingButton(){
        linearLayoutBottom.setVisibility(View.INVISIBLE);

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_info.setVisibility(View.INVISIBLE);
                linearLayoutBottom.setVisibility(View.VISIBLE);
            }
        });
    }

    public void zoomIn(View view){
       if(!animationZoomIn){
           setAnimationToMap();
       }
    }

    public void zoomOut(View view){
        if(!animationZoomOut) {
            animation = new ScaleAnimation(4f, 1f, 4f, 1f, Animation.RESTART, 0.6f,Animation.RESTART, 0.53f);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            map.clearAnimation();
            map.startAnimation(animation);
            animationZoomOut = true;
            animationZoomIn = false;
        }
    }

    public void setAnimationToMap(){
        animation = new ScaleAnimation(4f, 0.95f, 4f, 0.95f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        map.startAnimation(animation);
        animationZoomIn = true;
        animationZoomOut = false;
    }

    public void setTextFields(){
        txt_room = (TextView) findViewById(R.id.txt_map_room);
        txt_room.setText("Miestnosť: " + room);

        txt_blok = (TextView) findViewById(R.id.txt_map_blok);
        if(blok.equals("S")) txt_blok.setText("Blok: S (Katedra informatiky)");
        else txt_blok.setText("Blok: " + blok);

        txt_floor = (TextView) findViewById(R.id.txt_map_floor);
        char number = room.charAt(0);
        if(number == '0') txt_floor.setText("-> prízemie");
        else txt_floor.setText("-> " + number + ". poschodie");
    }

    public void getDisplayParameters(){
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point distances = new Point();
        display.getSize(distances);
        x = distances.x;
        y = distances.y;
    }

    public void closeLayout(View view){
        btn_info.setVisibility(View.VISIBLE);
        linearLayoutBottom.setVisibility(View.INVISIBLE);
    }

    public void exitMap(View view) {
        Intent intent = new Intent(MapForRooms.this, FindRoom.class);
        startActivity(intent);
    }
}