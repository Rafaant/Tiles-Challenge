package com.creativefunapps.tileschallenge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.Random;

//CLASE QUE GESTIONA EL JUEGO EN MODOS 1 Y 2 EN SÍ, EL FRAGMENT, LOS TILES Y SUS COLORES, VIDAS, LEVEL, Y DEMÁS PARÁMETROS
public class MyBaseAdapterMode12 extends BaseAdapter {
    private Context context;
    private int[] tilesValues;
    private int different = 0;
    private int side;
    public static int DEVIATION_MAX = 25; //inicial. 50 para forma 1, 20 para forma 2
    public static int DEVIATION_MIN;      //         15 para forma 1, 10 para forma 2
    public static int DECREASE_EACH_LEVEL;
    private static final int HARD = 3;
    private static final int EASY = 10;
    private static int deviation = DEVIATION_MAX;
    private boolean hard;
    View gridView;
    Button button;
    Button button_array [];

    public MyBaseAdapterMode12(Context context, int side, boolean hard){
        //deviation = DEVIATION_MAX;
        this.context = context;
        this.hard = hard;
        this.side = side;
        int total = side*side;
        tilesValues = new int[total];
        button_array = new Button[total];
        Random rand = new Random();
        int r = rand.nextInt(256-50)+50;
        int g = rand.nextInt(256-50)+50;
        int b = rand.nextInt(256-50)+50;
        /*//evitar colores blancos, grises muy claros
        if (r<=50 && g<=50 && b<=50){
            r+=50;
            g+=50;
            b+=50;
        }*/
        //si la partida acaba de empezar, resetear la desviacion actual al máximo, es la inicial, ya que por el static se mantiene el valor en que se quedase en la partida anterior
        deviation = (Game.start) ? DEVIATION_MAX : deviation;
        int chosen_one = rand.nextInt(total);
        Log.w("myApp", "r:" + r + " g:" + g + " b:" + b + " total:" + total + " chosen_one:" + chosen_one);
        for(int i=0; i<total; i++){
            if(i!=chosen_one) {
                tilesValues[i] = Color.argb(255, r, g, b);
            }else{
                if(!hard){
                    DEVIATION_MIN = EASY;
                    DECREASE_EACH_LEVEL = 5;
                }else{
                    DEVIATION_MIN = HARD;
                    DECREASE_EACH_LEVEL = 5;
                }
                //preparar desviación del color en caso de jugar en modo dificil, leer del menu de config.
                DEVIATION_MIN = (hard) ? HARD : EASY;
                Log.v("myAPP", "hard: " + hard + " - DEVIATION_MIN: " + DEVIATION_MIN + " - deviation:" + deviation + " - Game.level%10:" + Game.level%10 + " deviation - Game.level%10:" + (deviation - Game.level%10));
                //deviation = (deviation - Game.level/1 > DEVIATION_MIN) ? ( (Game.level%Game.level==0) ? deviation - Game.level/1 : deviation ) : DEVIATION_MIN;

                //si toca cambiar desviacion (estamos en un nivel multiplo de X (5) niveles
                deviation = (Game.level%DECREASE_EACH_LEVEL==0) ? deviation - 1 : deviation;
                //si hemos reducido demasiado la desviacion, ponemos la minima
                deviation = (deviation < DEVIATION_MIN) ? DEVIATION_MIN : deviation;

                Log.v("myAPP", "deviation: " + deviation);
                //crear color con esa desviacion (forma 1)
                /*int rgb_selector = rand.nextInt(3);
                int r_ = (rgb_selector == 0) ? ( (r+deviation>255) ? r-deviation : r+deviation ) : r;
                int g_ = (rgb_selector == 1) ? ( (g+deviation>255) ? g-deviation : g+deviation ) : g;
                int b_ = (rgb_selector == 2) ? ( (b+deviation>255) ? b-deviation : b+deviation ) : b;*/
                //forma 2. esta forma parece ser mejor
                int r_ = (r+deviation>255) ? r-deviation: r+deviation;
                int g_ = (g+deviation>255) ? g-deviation: g+deviation;
                int b_ = (b+deviation>255) ? b-deviation: b+deviation;
                Log.w("myApp", "r_:" + r_ + " g_:" + g_ + " b_:" + b_);
                tilesValues[i] = Color.argb(255, r_, g_, b_); //es el diferente
                different = i;
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
            gridView = inflater.inflate(R.layout.tile, null);

            // set background color to the button
            button = (Button) gridView.findViewById(R.id.button);
            button.setTag(position);
            GradientDrawable gd = (GradientDrawable) button.getBackground();
            gd.setColor(tilesValues[position]);

            button_array[position] = button;
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }

    public void hideTile(View v){
        v.setBackgroundColor(Color.argb(0, 255, 255, 255)); //transparente
        //v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.wrong_cross_32));
        v.setClickable(false);
    }

    public void highlightTile(final View v){
        //final int prev_color = ((ColorDrawable)v.getBackground()).getColor();
        /*final int prev_color = tilesValues[Integer.parseInt(String.valueOf(v.getTag()))];
        //v.setBackgroundColor(Color.argb(255, 255, 255, 255)); //blanco
        final GradientDrawable gd = (GradientDrawable) v.getBackground();
        //final GradientDrawable prev_background = gd;
        gd.setColor(Color.WHITE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //v.setBackgroundColor(prev_color); //anterior
                gd.setColor(prev_color);
                //v.setBackground(prev_background);
            }
        }, 1000);*/
        Animation blink_tile = AnimationUtils.loadAnimation(v.getContext(),
                R.anim.blink_tile);
        v.startAnimation(blink_tile);
    }

    /*public void hideGameBoard() {
        for(int i=0; i<tilesValues.length; i++){
            getItem(i).setVisibility(View.INVISIBLE);
        }
    }

    public void showGameBoard() {
        for(int i=0; i<getCount(); i++){
            getItem(i).setVisibility(View.VISIBLE);
        }
    }*/

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

    public int getDifferentIndex() {
        return different;
    }
}
