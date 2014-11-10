package com.creativefunapps.tileschallenge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.Random;

//CLASE QUE GESTIONA EL TABLERO DE DETRAS DEL LOGO EN EL MAIN
public class MyBaseAdapterLogo extends BaseAdapter {
    private Context context;
    private int[] tilesValues;
    private static int deviation = 20;
    View gridView;
    Button button;
    Button button_array [];
    int[][] colores =  {{255, 0, 155, 219},
                        {255, 163, 189, 49},
                        {255, 219, 150, 0},
                        {255, 207, 115, 79}
                       };

    public MyBaseAdapterLogo(Context context, int total){
        //deviation = DEVIATION_MAX;
        this.context = context;
        tilesValues = new int[total];
        button_array = new Button[total];
        Random rand = new Random();
        int index = rand.nextInt(4);
        //int a = colores[index][0];
        //int r = colores[index][1];
        //int g = colores[index][2];
        //int b = colores[index][3];
        //int r = rand.nextInt(256-100)+100;
        //int g = rand.nextInt(256-100)+100;
        //int b = rand.nextInt(256-100)+100;
        int a = 200;
        int r = 0;
        int g = 155;
        int b = 219;

        int chosen_one = rand.nextInt(total);
        for(int i=0; i<total; i++){
            if(i!=chosen_one) {
                tilesValues[i] = Color.argb(a, r, g, b);
            }else{
                int a_ = 200;
                //int r_ = (r+deviation>255) ? r-deviation: r+deviation;
                //int g_ = (g+deviation>255) ? g-deviation: g+deviation;
                //int b_ = (b+deviation>255) ? b-deviation: b+deviation;
                int r_ = 119;
                int g_ = 186;
                int b_ = 232;
                Log.w("myApp", "COLORES LOGO - r_:" + r_ + " g_:" + g_ + " b_:" + b_);
                tilesValues[i] = Color.argb(a_, r_, g_, b_); //es el diferente
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridView = new View(context);
            // get layout from tiles.xml
            gridView = inflater.inflate(R.layout.tile_logo, null);

            // set background color to the button
            button = (Button) gridView.findViewById(R.id.button);
            GradientDrawable gd = (GradientDrawable) button.getBackground();
            gd.setColor(tilesValues[position]);

            button_array[position] = button;
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }


    @Override
    public int getCount() {
        return tilesValues.length;
    }

    @Override
    public View getItem(int i) {
        return button_array[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
