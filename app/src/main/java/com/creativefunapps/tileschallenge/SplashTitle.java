package com.creativefunapps.tileschallenge;

import com.creativefunapps.tileschallenge.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashTitle extends PortraitActivity implements Animation.AnimationListener {

    private ImageView image;
    private Animation animFadeIn;
    private Animation animFadeOut;
    private int year;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_title);

        TextView tv = (TextView) findViewById(R.id.tvSSyear);
        year = ( Calendar.getInstance().get(Calendar.YEAR) - 2014 == 0 ) ? 2014 : Calendar.getInstance().get(Calendar.YEAR);
        tv.setText(Integer.toString(year));

        image = (ImageView) findViewById(R.id.imgSStitle);
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.anim_fade_in);
        animFadeIn.setAnimationListener(this);
        animFadeIn.setStartOffset(1000);
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.anim_fade_out);
        animFadeOut.setStartOffset(1000);
        animFadeIn.setRepeatMode(Animation.REVERSE);
        image.startAnimation(animFadeIn);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animFadeIn) {
            image.startAnimation(animFadeOut);
            //image.startAnimation(animFadeIn);
            //image.setVisibility(ImageView.INVISIBLE);


            handler.postDelayed(runnable = new Runnable() {
                public void run() {
                    // acciones que se ejecutan tras los milisegundos
                    Intent i = new Intent(SplashTitle.this,
                            Main.class);
                    startActivity(i);
                    SplashTitle.this.finish();
                }
            }, 2000);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        SplashTitle.this.finish();
    }
}
