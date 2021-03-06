package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;


public class Game extends BaseGameActivity {

    public static final int STARTING_TILES = 2;
    public static final int MAX_TILES = 9;
    public static final int MAX_TILES_HARD = 12;
    public static final int TILES_EACH_X_LEVELS_EASY = 10;
    public static final int TILES_EACH_X_LEVELS_HARD = 5;
    public static int tiles;
    public static int max_tiles;
    public static final int STARTING_LEVEL = 1;
    public static int level;
    public static final int LIVES = 3;
    public static int lives;
    public static int top_lives;
    public static final int STARTING_TIME = 30000; //ALL TIME IS EXPRESSED IN MILLISECONDS
    public static long time;
    public static long top_time;
    public static final int POINTS = 0;
    public static int points;
    public static final int CORRECT_POINTS = 100;
    public static final int FAILURE_POINTS = 20;
    public static boolean combo;
    public static final int MULTIPLIER_MAX = 10;
    public static int multiplier;
    public static int chain;
    public static int chain_max;
    public static final int LIVES_EACH_X_LEVELS_CHAINED = 9; //PONER 1 MENOS DE LOS QUE QUIERAS (funciona así)
    public static final int TIME_EACH_X_LEVELS_EASY = 5;
    public static final int TIME_EACH_X_LEVELS_HARD = 10;
    public static final int TIME_ADDED_EASY = 10000;
    public static final int TIME_ADDED_HARD = 15000;
    public static int mode;
    public static final int SHOW_INTERSTITIAL_EACH_X_LEVELS_MODE_1 = 5;

    private static MyBaseAdapterMode12 MyAdapter;
    public static GridView layout;
    public Fragment topbar;
    public bottomBar bottombar;
    public static Fragment help_fragment;

    private static int myProgress=0;
    private static ProgressBar progressBar;
    private int progressStatus=0;
    private Handler myHandler=new Handler();
    private Thread thread;

    private CountDownTimer mCountDownTimer;
    //private MyCountDownTimer timer;
    private com.creativefunapps.tileschallenge.CountDownTimer timer;
    public static boolean start = true;
    boolean pause_because_add = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*Intent j = new Intent(this, Advertisement.class);
        startActivity(j);*/

        getGameHelper().setMaxAutoSignInAttempts(0);

        tiles=STARTING_TILES;
        level=STARTING_LEVEL;
        lives=LIVES;
        top_lives=lives;
        points=POINTS;
        combo=false;
        multiplier=1;
        chain=0;
        chain_max=chain;
        time=STARTING_TIME;
        top_time=time;

        setContentView(R.layout.activity_game);
        Intent i = getIntent();
        mode = i.getIntExtra("mode", 1); //en caso de error -> modo normal 1

        //lanzar publicidad
        show_ad();
        show_banner_ad();

        //layout inferior comun a todos
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerBottomBar, bottombar = new bottomBar())
                    .commit();
        }

        //config de los layouts mediante funciones
        switch(mode){
            case 1:
                mode1(savedInstanceState);
                break;
            case 2:
                mode2(savedInstanceState);
                break;
            case 3:
                mode3();
                break;
        }
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void mode1(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            if(Main.show_help_mode1){
                getFragmentManager().beginTransaction().add(R.id.containerFullScreen, help_fragment = new helpFragment()).commit();
            }
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerTopBar, topbar = new topBarMode1())
                    .commit();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerGameBoard, new gameBoard())
                    .commit();
        }
    }

    private void mode2(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            if(Main.show_help_mode2){
                getFragmentManager().beginTransaction().add(R.id.containerFullScreen, help_fragment = new helpFragment()).commit();
            }
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerTopBar, topbar = new topBarMode2())
                    .commit();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerGameBoard, new gameBoard())
                    .commit();
        }
    }

    private void mode3(){

    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {
        Games.setGravityForPopups(getApiClient(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    public static class helpFragment extends Fragment {
        private View inputView;
        private TextView text;
        private static CheckBox cb;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.help_overlay, container, false);
            switch(mode){
                case 1:
                    ((TextView)inputView.findViewById(R.id.help_title)).setText(inputView.getResources().getString(R.string.help_mode1_title));
                    ((TextView)inputView.findViewById(R.id.help)).setText(inputView.getResources().getString(R.string.help_mode1));
                    break;
                case 2:
                    ((TextView)inputView.findViewById(R.id.help_title)).setText(inputView.getResources().getString(R.string.help_mode2_title));
                    ((TextView)inputView.findViewById(R.id.help)).setText(inputView.getResources().getString(R.string.help_mode2));
                    break;
            }
            //cb = (CheckBox) inputView.findViewById(R.id.help_checkBox);
            //Button button = (Button) inputView.findViewById(R.id.help_button);

            /*if(Main.hard){
                ((TextView)inputView.findViewById(R.id.help)).append(inputView.getResources().getString(R.string.help_hard));
            }*/
            RelativeLayout touch = (RelativeLayout) inputView.findViewById(R.id.help_touch);
            touch.setOnClickListener(close_help);
            return inputView;
        }

        static View.OnClickListener close_help = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myAPP", "click help button");
                ((Activity)view.getContext()).getFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.fade_out_help, R.anim.fade_out_help)
                        .remove(help_fragment).commit();
                /*if(cb.isChecked()){
                    if(mode==1){
                        Main.show_help_mode1=false;
                    }
                    if(mode==2){
                        Main.show_help_mode2=false;
                    }
                }*/
            }
        };
    }

    public static class bottomBar extends Fragment {
        private View inputView;
        private TextView text;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.bottom_bar, container, false);
            text = (TextView) inputView.findViewById(R.id.bottomBarExtra);
            text.setText("- - -");
            text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            text = (TextView) inputView.findViewById(R.id.bottomBarBonus);
            //text.setVisibility(View.INVISIBLE);
            text = (TextView) inputView.findViewById(R.id.points);
            text.setText(""+points);
            return inputView;
        }
        public void refresh(){
            ((TextView)(inputView.findViewById(R.id.points))).setText(""+points);
            if (combo){
                if(multiplier==MULTIPLIER_MAX){
                    ((TextView)(inputView.findViewById(R.id.bottomBarExtra))).setText("MAX");
                    ((TextView)(inputView.findViewById(R.id.bottomBarExtra))).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                }else{
                    ((TextView)(inputView.findViewById(R.id.bottomBarExtra))).setText("x" + Integer.toString(multiplier));
                    ((TextView)(inputView.findViewById(R.id.bottomBarExtra))).setTextColor(getResources().getColor(android.R.color.black));
                }
                //((TextView)(inputView.findViewById(R.id.bottomBarBonus))).setVisibility(View.VISIBLE);
            } else {
                ((TextView)(inputView.findViewById(R.id.bottomBarExtra))).setText("- - -");
                ((TextView)(inputView.findViewById(R.id.bottomBarExtra))).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                //((TextView)(inputView.findViewById(R.id.bottomBarBonus))).setVisibility(View.INVISIBLE);
            }
        }
        public void refresh_message(String s){
            ((TextView)(inputView.findViewById(R.id.bottomBarMessage))).setText(s);
        }
        public void add_message(String s){
            Animation fade_in_out_info = AnimationUtils.loadAnimation(inputView.getContext(),
                    R.anim.fade_in_out_info);
            ((TextView)(inputView.findViewById(R.id.bottomBarMessage))).setText(s);
            inputView.findViewById(R.id.bottomBarMessage).startAnimation(fade_in_out_info);
        }
    }

    public static class topBarMode1 extends Fragment {

        private View inputView;
        private TextView text;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.top_bar_mode1, container, false);
            text = (TextView) inputView.findViewById(R.id.level);
            text.setText(""+level);
            text = (TextView) inputView.findViewById(R.id.topBarMessage);
            text.setText((Main.hard==true)?getResources().getString(R.string.hard)+" ":getResources().getString(R.string.easy)+" "); //espacios añadidos porque al ser italic se come un trozo de la ultima letra
            text.setTextColor((Main.hard == true) ? getResources().getColor(android.R.color.holo_red_dark) : getResources().getColor(android.R.color.holo_blue_dark));
            text = (TextView) inputView.findViewById(R.id.lives);
            text.setText(String.valueOf(lives));
            return inputView;
        }
        public void refresh(){
            ((TextView)(inputView.findViewById(R.id.level))).setText(""+level);
            ((TextView)(inputView.findViewById(R.id.lives))).setText(Integer.toString(lives));
            if(lives<=1) {
                ((TextView) (inputView.findViewById(R.id.lives))).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }else{
                ((TextView) (inputView.findViewById(R.id.lives))).setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    public static class topBarMode2 extends Fragment {

        private View inputView;
        private TextView text;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.top_bar_mode2, container, false);
            text = (TextView) inputView.findViewById(R.id.level);
            text.setText(""+level);
            text = (TextView) inputView.findViewById(R.id.lives);
            text.setText(String.valueOf(lives));
            text = (TextView) inputView.findViewById(R.id.tvDifficulty);
            text.setText((Main.hard==true)?getResources().getString(R.string.hard)+" ":getResources().getString(R.string.easy)+" "); //espacios añadidos porque al ser italic se come un trozo de la ultima letra
            text.setTextColor((Main.hard == true) ? getResources().getColor(android.R.color.holo_red_dark) : getResources().getColor(android.R.color.holo_blue_dark));
            progressBar = (ProgressBar) inputView.findViewById(R.id.progressBar);
            text = (TextView) inputView.findViewById(R.id.time);
            text.setText(Long.toString(time/1000) + " s.");
            return inputView;
        }
        public void refresh(){
            ((TextView)(inputView.findViewById(R.id.level))).setText(""+level);
            ((TextView)(inputView.findViewById(R.id.lives))).setText(Integer.toString(lives));
            if(lives<=1) {
                ((TextView) (inputView.findViewById(R.id.lives))).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }else{
                ((TextView) (inputView.findViewById(R.id.lives))).setTextColor(getResources().getColor(android.R.color.black));
            }
        }
        public void refresh_time(int t){
            ((TextView)(inputView.findViewById(R.id.time))).setText(t + " s.");
            if(t<=10) {
                ((TextView) (inputView.findViewById(R.id.time))).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_red));
            }else{
                ((TextView) (inputView.findViewById(R.id.time))).setTextColor(getResources().getColor(android.R.color.black));
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_blue));
            }
        }
    }

    public static class gameBoard extends Fragment {

        private View inputView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.game_board, container, false);
            //usar el adaptador necesario para cada modo de juego, 1 MyBaseAdapter, cambiar nombre a MyBaseAdapterMode1, y sucesivos.
            //preparar gameboard inicial (creo que siempre sera de 2x2
            layout = (GridView) inputView.findViewById(R.id.gameBoard);
            max_tiles = (Main.hard) ? MAX_TILES_HARD : MAX_TILES ;
            //tiles = (tiles<max_tiles) ? tiles++ : max_tiles;
            //incrementar tiles cada x niveles segun dificultad y hasta un maximo
            if(tiles < max_tiles){
                if(!Main.hard && level%TILES_EACH_X_LEVELS_EASY==0){
                    tiles++;
                }
                if(Main.hard && level%TILES_EACH_X_LEVELS_HARD==0){
                    tiles++;
                }
            }else{
                tiles = max_tiles;
            }
            MyAdapter = new MyBaseAdapterMode12(inputView.getContext(), tiles, Main.hard);
            layout.setAdapter(MyAdapter);
            layout.setNumColumns(tiles);

            return inputView;
        }
    }

    //comprueba pulsaciones para todos los modos, puesto que es un callback de unClick de un xml y no se pueden dar parametros
    public void testCorrect(View v){
        if(Main.online){
            switch(((Main.hard)?1:0)){
                case 0:
                    if(points>=100000){
                        send_achievement_to_google_play_games(getString(R.string.achievement_100_000_points__easy));
                    }
                    if(level>=150){
                        send_achievement_to_google_play_games(getString(R.string.achievement_level_150__easy));
                    }else if(level>=100){
                        send_achievement_to_google_play_games(getString(R.string.achievement_level_100__easy));
                    }else if(level>=50){
                        send_achievement_to_google_play_games(getString(R.string.achievement_level_50__easy));
                    }
                    if(chain_max>=75){
                        send_achievement_to_google_play_games(getString(R.string.achievement_75_consecutive_hits__easy));
                    }else if(chain_max>=50){
                        send_achievement_to_google_play_games(getString(R.string.achievement_50_consecutive_hits__easy));
                    }
                    if(top_time>=60000){
                        send_achievement_to_google_play_games(getString(R.string.achievement_earn_60_seconds__easy));
                    }
                    break;
                case 1:
                    if(top_lives>=10){
                        send_achievement_to_google_play_games(getString(R.string.achievement_get_10_lives__hard));
                    }
                    if(level>=150){
                        send_achievement_to_google_play_games(getString(R.string.achievement_level_150__hard));
                    }else if(level>=100){
                        send_achievement_to_google_play_games(getString(R.string.achievement_level_100__hard));
                    }else if(level>=50){
                        send_achievement_to_google_play_games(getString(R.string.achievement_level_50__hard));
                    }
                    if(chain_max>=50){
                        send_achievement_to_google_play_games(getString(R.string.achievement_50_consecutive_hits__hard));
                    }else if(chain_max>=25){
                        send_achievement_to_google_play_games(getString(R.string.achievement_25_consecutive_hits__hard));
                    }
                    if(top_time>=60000){
                        send_achievement_to_google_play_games(getString(R.string.achievement_earn_60_seconds__hard));
                    }
                    break;
            }
        }
        switch(mode){
            case 1:
                /*if(level%SHOW_INTERSTITIAL_EACH_X_LEVELS_MODE_1==0){//mostrar publicidad cada x niveles en modo práctica
                    show_ad();
                }*/
                //la primera vez que se ejecuta ya podemos poner start = false para que el algoritmo de deviation del color funcione,
                start = false;
                if(lives>=1) {
                    if (Integer.parseInt(String.valueOf(v.getTag())) == MyAdapter.getDifferentIndex()) {//acierto
                        //Toast.makeText(getBaseContext(), "CORRECTO", Toast.LENGTH_SHORT).show();
                        //gira el tablero y sale uno nuevo más completo hasta un numero maximo de tiles
                        //tiles++;
                        level++;
                        //conceder vidas por combos largos (dependiendo de la dificultad):
                        if(Main.hard && chain!=0){
                            if (chain%LIVES_EACH_X_LEVELS_CHAINED==0){
                                lives++;
                                bottombar.add_message("+1 " + getResources().getString(R.string.life).toUpperCase());
                            }
                        }
                        //actualizar el valor de top_lives, representa el maximo de vidas que has llegado a conseguir, no el que tienes ahora ni al terminar...
                        top_lives = (lives>top_lives) ? lives : top_lives;
                        //points += CORRECT_POINTS + ((combo==true) ? CORRECT_POINTS * multiplier : 0);
                        points += (combo==true) ? CORRECT_POINTS * multiplier : CORRECT_POINTS;
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                                        R.anim.card_flip_left_in, R.anim.card_flip_left_out)
                                .replace(R.id.containerGameBoard, new gameBoard())
                                .commit();
                        combo = true;
                        multiplier = (multiplier<MULTIPLIER_MAX) ? ++multiplier : MULTIPLIER_MAX;
                        chain++;
                    }else{ //fallo, se pierde una vida y se pierden puntos, mas a mejor nivel. Indicar cual es el tile fallado
                        MyAdapter.hideTile(v);
                        lives--;
                        points = (points>=FAILURE_POINTS*tiles) ? points - FAILURE_POINTS*tiles : points;
                        Log.v("myAPP", "FAILURE_POINTS*tiles:" + FAILURE_POINTS + " " + tiles + " " + FAILURE_POINTS*tiles);
                        //guardar el combo maximo = cadena de aciertos mas larga, chain
                        chain_max = (chain>chain_max) ? chain : chain_max;
                        combo = false;
                        multiplier = 1;
                        chain = 0;
                        //forzamos la salida ya que de momento algo ocurre que hace que siga esperando un toque cuando hay 0 vidas en el topbar, sera debido al onclick que hasta que no se hace otro click no se vuelve a ejecutar esta funcion de comprobación, por eso la fuerzo en ese caso.
                        if(lives==0) testCorrect(v);
                    }
                }else{
                    //poner a cero las vidas
                    lives=0;
                    //ocultar el último fallo ya que se sale del if antes de hacerlo
                    //hideTile(v);
                    //mostrar cual era el tile correcto
                    MyAdapter.highlightTile(MyAdapter.getItem(MyAdapter.getDifferentIndex()));
                    //comprobar si hay logros conseguidos
                    //testArchievements();
                    /*new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //showGameSummary();
                            gameEnd();
                        }
                    }, 2000);*/
                    //deshabilitar ya los clicks en el tablero de juego
                    disableClicks();
                }
                ((topBarMode1)topbar).refresh();
                bottombar.refresh();
                break;

            case 2: //mismo funcionamiento básico que en el modo 1, pero ahora se añade tiempo cada x niveles superados, sea o no en cadena.
                //lo primero lanzar el temporizador sólo después del primer toque, es decir, cuando hay 2x2 y le das, aciertes o falles empiezas a jugar y cuenta tiempo hacia atras
                if(start && time==STARTING_TIME){

                    timer = new com.creativefunapps.tileschallenge.CountDownTimer(STARTING_TIME, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if(lives>=0) {
                                time = millisUntilFinished;
                                top_time = (time>top_time) ? time : top_time;
                                Log.v("myAPP", "----- 1 : actualizacion de la barra");
                                progressBar.setProgress((int) millisUntilFinished / 1000);
                                ((topBarMode2) topbar).refresh_time((int) millisUntilFinished / 1000);

                            }else{//NO LO TENGO CLARO creo que aqui nunca entra cuando se te acaban las vidas por que eso lo contempla testCorrect, se termina alli la partida y no por el tick del temporizador
                                onFinish();
                            }
                        }

                        @Override
                        public void onFinish() {
                            timer.cancel();
                            //time-=1000;
                            points += lives * CORRECT_POINTS * 10;
                            ((topBarMode2)topbar).refresh_time(0);
                            progressBar.setProgress(0);
                            bottombar.refresh();
                            MyAdapter.highlightTile(MyAdapter.getItem(MyAdapter.getDifferentIndex()));
                            disableClicks();
                        }
                    }.start();
                    start=false;
                }
                if(lives>=1 && time>0) {
                    if (Integer.parseInt(String.valueOf(v.getTag())) == MyAdapter.getDifferentIndex()) {//acierto
                        //Toast.makeText(getBaseContext(), "CORRECTO", Toast.LENGTH_SHORT).show();
                        //gira el tablero y sale uno nuevo más completo hasta un numero maximo de tiles
                        //tiles++;
                        level++;
                        //conceder vidas por combos largos (dependiendo de la dificultad) SI ES FÁCIL SE DA MAS TIEMPO, SI ES DIFICIL SE DA TIEMPO Y VIDAS PARA PODER FALLAR ALGO XK YA EL TIEMPO LO ES TO_DO:
                        if(Main.hard && chain!=0){
                            if (chain%LIVES_EACH_X_LEVELS_CHAINED==0){
                                lives++;
                                bottombar.add_message("+1 " + getResources().getString(R.string.life).toUpperCase());
                            }
                        }
                        //conceder tiempo por niveles superados (dependiendo de la dificultad, facil cada menos niveles, dificil cada mas, o facil mas tiempo, dificil menos):
                        if(!Main.hard && level%TIME_EACH_X_LEVELS_EASY==1){ //1 para que sea en el siguiente
                            //time+=TIME_ADDED_EASY;
                            addTime(TIME_ADDED_EASY);
                            bottombar.add_message("+" + String.valueOf(TIME_ADDED_EASY/1000) + " s.");
                            Log.v("myAPP", "esta en if 1, time: " + time);
                        }else if(Main.hard && level%TIME_EACH_X_LEVELS_HARD==1){
                            //time+=TIME_ADDED_HARD;
                            addTime(TIME_ADDED_HARD);
                            bottombar.add_message("+" + String.valueOf(TIME_ADDED_HARD/1000) + " s.");
                            Log.v("myAPP", "esta en el else, time: " + time);
                        }
                        //guardar el tiempo maximo conseguido
                        //top_time = (time>top_time) ? time : top_time;
                        //actualizar el valor de top_lives, representa el maximo de vidas que has llegado a conseguir, no el que tienes ahora ni al terminar...
                        top_lives = (lives>top_lives) ? lives : top_lives;
                        points += (combo==true) ? CORRECT_POINTS * multiplier : CORRECT_POINTS;
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                                        R.anim.card_flip_left_in, R.anim.card_flip_left_out)
                                .replace(R.id.containerGameBoard, new gameBoard())
                                .commit();
                        combo = true;
                        multiplier = (multiplier<MULTIPLIER_MAX) ? ++multiplier : MULTIPLIER_MAX;
                        chain++;
                        chain_max = (chain>chain_max) ? chain : chain_max;
                    }else{ //fallo, se pierde una vida y se pierden puntos, mas a mejor nivel. Indicar cual es el tile fallado
                        MyAdapter.hideTile(v);
                        lives--;
                        points = (points>=FAILURE_POINTS*tiles) ? points - FAILURE_POINTS*tiles : points;
                        Log.v("myAPP", "FAILURE_POINTS*tiles:" + FAILURE_POINTS + " " + tiles + " " + FAILURE_POINTS*tiles);
                        //guardar el combo maximo = cadena de aciertos mas larga, chain
                        chain_max = (chain>chain_max) ? chain : chain_max;
                        combo = false;
                        multiplier = 1;
                        chain = 0;
                        //forzamos la salida ya que de momento algo ocurre que hace que siga esperando un toque cuando hay 0 vidas en el topbar, sera debido al onclick que hasta que no se hace otro click no se vuelve a ejecutar esta funcion de comprobación, por eso la fuerzo en ese caso.
                        if(lives==0) testCorrect(v);
                    }
                }else{
                    //poner a cero las vidas
                    lives=0;
                    //parar el tiempo y dejarlo a 0
                    timer.cancel();
                    //mostrar cual era el tile correcto
                    MyAdapter.highlightTile(MyAdapter.getItem(MyAdapter.getDifferentIndex()));
                    //deshabilitar ya los clicks en el tablero de juego
                    disableClicks();
                }
                ((topBarMode2)topbar).refresh();
                bottombar.refresh();
                break;

            case 3:

                break;
        }
    }

    public void send_achievement_to_google_play_games(String ach){
        Games.Achievements.unlock(getApiClient(), ach);
    }

    public void addTime(final long time_to_add){
        timer.cancel();
        if(time+time_to_add>progressBar.getMax()){
            progressBar.setMax((int)(time+time_to_add)/1000);
        }
        timer = new com.creativefunapps.tileschallenge.CountDownTimer(time + time_to_add, 1000) {
            boolean add_time = false;
            //long time_to_add;
            int t;

            @Override
            public void onTick(long millisUntilFinished) {
                if(lives>=0) {
                    time = millisUntilFinished;
                    top_time = (time>top_time) ? time : top_time;
                    Log.v("myAPP", "----- 2 : actualizacion de la barra");
                    progressBar.setProgress((int) millisUntilFinished / 1000);
                    ((topBarMode2) topbar).refresh_time((int) millisUntilFinished / 1000);


                }else{//NO LO TENGO CLARO creo que aqui nunca entra cuando se te acaban las vidas por que eso lo contempla testCorrect, se termina alli la partida y no por el tick del temporizador
                    onFinish();
                }
            }

            @Override
            public void onFinish() {
                timer.cancel();
                //time-=1000;
                points += lives * CORRECT_POINTS * 10;
                ((topBarMode2)topbar).refresh_time(0);
                progressBar.setProgress(0);
                bottombar.refresh();
                MyAdapter.highlightTile(MyAdapter.getItem(MyAdapter.getDifferentIndex()));
                disableClicks();
            }
        }.start();
    }

    public void disableClicks(){ //y desactivar onclick
        TextView tv = (TextView) findViewById(R.id.topBarMessage);
        tv.setText(getString(R.string.click_to_continue)+" "); //espacio añadido porque al ser italic se come un trozo de la ultima letra
        tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        tv.setVisibility(View.VISIBLE);
        Animation blink_tile = AnimationUtils.loadAnimation(this, R.anim.blink_tile);
        tv.startAnimation(blink_tile);
        for(int i=0; i<MyAdapter.getCount(); i++){
            MyAdapter.getItem(i).setClickable(true);
            MyAdapter.getItem(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gameEnd();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //para el tiempo y ocultar el tablero
        layout.setVisibility(View.INVISIBLE);
        if(timer!=null) timer.pause();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.pause));
        builder.setMessage(getString(R.string.dialog_exit_game));
        builder.setPositiveButton(getString(R.string.preferences_dialog_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(timer!=null) timer.cancel();
                start=true;
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.preferences_dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                layout.setVisibility(View.VISIBLE);
                if(timer!=null) timer.resume();
            }
        });
        builder.show();
    }



    //solo devuelve el result con la puntuacion esperada por el Main y cierra el juego
    public void gameEnd(){

        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);
        bundle.putInt("score", points);
        bundle.putInt("level", level);
        bundle.putInt("chain_max", chain_max);
        bundle.putInt("top_lives", top_lives);
        bundle.putLong("top_time", top_time);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        start=true;
        finish();
    }

    ////////////////////////
    /*     AD METHODS     */
    ////////////////////////

    /** The log tag. */
    private static final String LOG_TAG = "Advertisement";

    /** Your ad unit id. Replace with your actual ad unit id. */
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9128082053165311/3395872781";

    /** The interstitial ad. */
    private InterstitialAd interstitialAd;

    public void show_ad(){
        // Create an ad.
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);

        // Check the logcat output for your hashed device ID to get test ads on a physical device.
        // ELIMINAR CUANDO YA FUNCIONE
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("ABB18ACF9860B7B99B386733FDF7FF18")
                .build();

        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                Log.d(LOG_TAG, "Interstitial ad shown.");
                //show the ad
                interstitialAd.show();
                /*Toast.makeText(Advertisement.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

                // Change the button text and enable the button.
                showButton.setText("Show Interstitial");
                showButton.setEnabled(true);*/
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
                Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
                Log.d(LOG_TAG, message);
                //close activity
                //finish();
                /*Toast.makeText(Advertisement.this, message, Toast.LENGTH_SHORT).show();

                // Change the button text and disable the button.
                showButton.setText("Ad Failed to Load");
                showButton.setEnabled(false);*/
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
    }

    /** Gets a string error reason from an error code. */
    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch(errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

    private AdView adView;
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-9128082053165311/9632379583";

    public void show_banner_ad(){
        adView = new AdView(this);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);
        adView.setAdSize(AdSize.BANNER);

        LinearLayout l = (LinearLayout)findViewById(R.id.banner_bottom);
        l.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("ABB18ACF9860B7B99B386733FDF7FF18")
                .build();

        adView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        adView.pause();
        if(timer!=null){
            timer.pause();
        }
        if(!start) { //para que no aparezca cuando estamos en el help_overlay
            pause_because_add = true;
            layout.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
        if(pause_because_add==true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.pause));
            builder.setPositiveButton(getString(R.string.dialog_continue), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (timer != null) {
                        timer.resume();
                    }
                    layout.setVisibility(View.VISIBLE);
                }
            }).show();
            pause_because_add=false;
        }
    }

    @Override
    public void onDestroy() {
        if(timer!=null){
            timer.cancel();
        }
        adView.destroy();
        super.onDestroy();
    }
}
