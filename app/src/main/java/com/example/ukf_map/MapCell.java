package com.example.ukf_map;

public class MapCell {
    private float x, y;

    public MapCell(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
