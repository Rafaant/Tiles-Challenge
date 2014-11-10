package com.creativefunapps.tileschallenge;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;

//ESTA CLASE OBLIGA AL BOTON A SER CUADRADO, RESTRINGE SU ALTURA A SU ANCHURA
public class MyButtonClass extends Button {

    public MyButtonClass(Context context) {
        super(context);
    }

    public MyButtonClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButtonClass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
