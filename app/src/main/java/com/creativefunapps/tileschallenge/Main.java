package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;
import java.util.Vector;

public class Main extends PortraitActivity {

    private int year;
    public static boolean hard = false;
    public static ScoreWarehouse score_warehouse;
    public static ArchievementWarehouse archievement_warehouse;
    public static Vector<ArchievementWarehouse.Archievement> archievements_now;
    public static Vector<ArchievementWarehouse.Archievement> archievements_before_game;
    public static Vector<ArchievementWarehouse.Archievement> archievements_after_game;
    public static int points_max_before_game;
    //public static int points_max_after_game;
    public static boolean deleteScores = false;
    public static boolean show_help_mode1 = true;
    public static boolean show_help_mode2 = true;
    public static final int RECORD_AND_ARCHIEVEMENT = 1;
    public static final int RECORD_ONLY = 2;
    public static final int ARCHIEVEMENT_ONLY = 3;
    public static int game_results_type = 0;
    public final static int GAME = 1234;
    public final static int SHARE_RESULTS = 5678;
    public final static int COMMENT = 9;
    public final static int RETRY_CODE = 10;

    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;
    private ImageView touch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GOOGLE ANALYTICS: Get a Tracker (should auto-report)
        //((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);


        score_warehouse = new ScoreWarehouseSQLite(this);
        ArchievementWarehouseSQLite.ARCHIEVEMENTS_STRINGS = getResources().getStringArray(R.array.archievements_array);
        ArchievementWarehouseSQLite.ARCHIEVEMENTS_DESC_STRINGS = getResources().getStringArray(R.array.archievements_desc_array);
        archievement_warehouse = new ArchievementWarehouseSQLite(this);
        /*archievements_now = archievement_warehouse.archievementList(50, 1);
        for(int i=0; i<archievements_now.size(); i++){
            Log.v("myAPP", archievements_now.elementAt(i).getName() + " " + archievements_now.elementAt(i).getMode() + " " + archievements_now.elementAt(i).isHard()+ " - acquired: " + archievements_now.elementAt(i).isAcquired());
        }*/

        Animation blink;
        blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        //blink.setStartOffset(100);


        TextView tv = (TextView) findViewById(R.id.tvSSyear);
        year = ( Calendar.getInstance().get(Calendar.YEAR) - 2014 == 0 ) ? 2014 : Calendar.getInstance().get(Calendar.YEAR);
        tv.setText(Integer.toString(year));

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new CardFrontFragment())
                    .commit();
            findViewById(R.id.container).startAnimation(blink);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameLogo, new logoBoard())
                    .commit();
        }

        touch = (ImageView) findViewById(R.id.start_touch);
        touch.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         findViewById(R.id.container).clearAnimation();
                                         flipCard();
                                     }
                                 }
        );
    };

    @Override
    protected void onResume() {
        super.onResume();
        //leer cambios en las opciones
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //hard = prefs.getBoolean("difficulty", false);
        deleteScores = prefs.getBoolean("delete_scores", false);
    }

    int mode;
    int points;
    int level;
    String name;
    int chain_max;
    int top_lives;
    long top_time;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("myAPP", "ON ACTIVITY RESULT - requestCode - resultCode:" + String.valueOf(requestCode) + " - " + String.valueOf(resultCode) + " - RESULT_OK: " + RESULT_OK);

        if (requestCode == GAME & resultCode == RESULT_OK & data != null) {
            mode = data.getExtras().getInt("mode");
            points = data.getExtras().getInt("score");
            level = data.getExtras().getInt("level");
            name = "Yo";
            chain_max = data.getExtras().getInt("chain_max");
            top_lives = data.getExtras().getInt("top_lives");
            top_time = data.getExtras().getLong("top_time");
            Log.i("myAPP", "mode, points, level, name, chain_max, top_lives, top_time " + mode + " " + points + " " + level + " " + name + " " + chain_max + " " + top_lives + " " + top_time);
            // Mejor leerlo desde un Dialog o una nueva actividad
            // AlertDialog.Builder


            score_warehouse.storeScore(mode, hard, points, level, chain_max, name, System.currentTimeMillis());
            archievements_after_game = archievement_warehouse.archievementList(50);

            testArchievements();
            showGameSummary();
            //showGameSummaryFragment();
            //lanzar fragment results
            /*getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new GameSummaryFragment())
                    .commit();*/
        }else if(requestCode == SHARE_RESULTS){
            launchRanking();
        }else if(requestCode == COMMENT){
            launchThanks();
        }else if(resultCode == RETRY_CODE){
            Log.i("myAPP", "RETRY");
            Intent i = new Intent(this, Game.class);
            i.putExtra("mode", mode);
            archievements_before_game = archievement_warehouse.archievementList(50);
            points_max_before_game = score_warehouse.highestScore(mode, hard);
            startActivityForResult(i, GAME);
        }else {
            Log.i("myAPP", "Se ha salido abruptamente, pulsando la tecla volver. O no se ha devuelto un requestCode/resultCode contemplado");
        }
    }

    public void testArchievements(){//guarda los logros conseguidos, todos.
        Log.v("myAPP", " chain_max: " + chain_max + " lives_max: " + top_lives + " main.hard: " + Main.hard );
        //DE LA FORMA EN LA QUE ESTAN PUESTOS LOS IF ELSE ANIDADOS HAY QUE HACER LAS COMPROBACIONES DE MAYOR A MENOR PORQUE SINO NUNCA ENTRA EN EL SIGUIENTE
        for(int i=0; i<Main.archievements_after_game.size(); i++) {
            if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_easy_100points)) && Main.hard==false && (points >= 100000)) {//comprobar los del modo fácil
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_easy_level150)) && Main.hard==false && (level >= 150)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_easy_level100)) && Main.hard==false && (level >= 100)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_easy_level50)) && Main.hard==false && (level >= 50)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_easy_big_chain)) && Main.hard==false && (chain_max >= 75)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_easy_chain)) && Main.hard==false && (chain_max >= 50)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements2_easy_time60)) && Main.hard==false && (top_time >= 60000)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_10lives)) && Main.hard && (top_lives >= 10)) { //comprobar los del modo difícil
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_50points)) && Main.hard && (points >= 50000)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_level150)) && Main.hard && (level >= 150)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_level100)) && Main.hard && (level >= 100)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_level50)) && Main.hard && (level >= 50)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_big_chain)) && Main.hard && (chain_max >= 50)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements1_hard_chain)) && Main.hard && (chain_max >= 25)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements2_hard_time60)) && Main.hard && (top_time >= 60000)) {
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard);
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            } else if (Main.archievements_after_game.elementAt(i).getName().equals(getString(R.string.archievements_all)) && (archievement_warehouse.areAllAcquired())) { //comprobar si se han obtenido todos los logros
                Main.archievements_after_game.elementAt(i).setAcquired(true);
                Main.archievements_after_game.elementAt(i).setHard(Main.hard); //no es relevante
                archievement_warehouse.storeArchievement(archievements_after_game.elementAt(i).getName(), mode, (hard)?1:0);
            }
        }
    }

    String str_logros;

    public void showGameSummary(){
        //arreglar el conseguir los nombres de los nuevos logros, preguntando a una funcion que devuelva los nombres, no hace un for aqui
        //de la forma que esta ahora va a devolver los logros que se tienen tras jugar, todos, no los nuevos.
        //o añadir una nueva variable que signifique logro recien adquirido, q se mantenga al poner y quitar logros.
        str_logros = "";
        boolean isNew_archievements = Main.archievement_warehouse.isNew_archievement(Main.archievements_before_game, Main.archievements_after_game);
        if(isNew_archievements){
            int pos[] = archievement_warehouse.get_newArchievementPositions();//siempre llamada a esta funcion despues de la anterior, isNew_archievement, que es quien prepara la respuesta
            for(int i=0; i<Main.archievements_after_game.size(); i++){
                Log.i("myAPP", "pos[]" + pos[i]);
                if(pos[i]==1){
                    str_logros += "- " + ((Main.hard)?Main.archievements_after_game.elementAt(i).getName().substring(0, Main.archievements_after_game.elementAt(i).getName().length()-1):Main.archievements_after_game.elementAt(i).getName()) + ":\n\t" + ArchievementWarehouseSQLite.ARCHIEVEMENTS_DESC_STRINGS[i] + "\n";
                }
            }
        }

        Intent i = new Intent(this, Results.class);
        i.putExtra("points", points);
        i.putExtra("achievements", str_logros);
        i.putExtra("difficulty", Main.hard);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.i("myAPP", "mode, Main.hard: " + mode + " " + Main.hard + "\npoints, ScoreWarehouseSQLite(this).highestScore(mode, Main.hard): " + points + " " + new ScoreWarehouseSQLite(this).highestScore(mode, Main.hard));
        if(points > points_max_before_game){
            if(isNew_archievements){
                /*builder.setMessage(getString(R.string.dialog_new_highscore_and_archievement) + "\n\n" + "- " + points + " " + getString(R.string.points).toLowerCase() + "\n" + str_logros);
                builder.setNegativeButton(getString(R.string.dialog_share).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        launchShareFromDialog(RECORD_AND_ARCHIEVEMENT);
                    }
                });*/
                i.putExtra("case", RECORD_AND_ARCHIEVEMENT);
                i.putExtra("title", getString(R.string.dialog_new_highscore_and_archievement));
            }else{
                /*builder.setMessage(getString(R.string.dialog_new_highscore) + "\n\n" + "- " + points + " " + getString(R.string.points).toLowerCase());
                builder.setNegativeButton(getString(R.string.dialog_share).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       launchShareFromDialog(RECORD_ONLY);
                    }
                });*/
                i.putExtra("case", RECORD_ONLY);
                i.putExtra("title", getString(R.string.dialog_new_highscore));
            }
            /*builder.setPositiveButton(getString(R.string.dialog_continue).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    launchRanking();
                }
            });
            builder.show();*/
        }else if(isNew_archievements){
            /*builder.setMessage(getString(R.string.dialog_new_archievement) + "\n\n" + str_logros);
            builder.setPositiveButton(getString(R.string.dialog_continue).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    launchRanking();
                }
            });
            builder.setNegativeButton(getString(R.string.dialog_share).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    launchShareFromDialog(ARCHIEVEMENT_ONLY);
                }
            });
            builder.show();*/
            i.putExtra("case", ARCHIEVEMENT_ONLY);
            i.putExtra("title", getString(R.string.dialog_new_archievement));
        }else{
            builder.setMessage(getString(R.string.unsuccessful_game));
            builder.setPositiveButton(getString(R.string.dialog_continue).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //no hacer nada, se consume el dialog y listo
                }
            });
            builder.show();
        }

        startActivityForResult(i, RETRY_CODE);
    }

    public void showGameSummaryFragment(){
        //arreglar el conseguir los nombres de los nuevos logros, preguntando a una funcion que devuelva los nombres, no hace un for aqui
        //de la forma que esta ahora va a devolver los logros que se tienen tras jugar, todos, no los nuevos.
        //o añadir una nueva variable que signifique logro recien adquirido, q se mantenga al poner y quitar logros.
        str_logros = "";
        boolean isNew_archievements = Main.archievement_warehouse.isNew_archievement(Main.archievements_before_game, Main.archievements_after_game);
        if(isNew_archievements){
            int pos[] = archievement_warehouse.get_newArchievementPositions();//siempre llamada a esta funcion despues de la anterior, isNew_archievement, que es quien prepara la respuesta
            for(int i=0; i<Main.archievements_after_game.size(); i++){
                Log.i("myAPP", "pos[]" + pos[i]);
                if(pos[i]==1){
                    str_logros += "\n- " + Main.archievements_after_game.elementAt(i).getName().substring(0, Main.archievements_after_game.elementAt(i).getName().length()-1) + ":\n\t" + ArchievementWarehouseSQLite.ARCHIEVEMENTS_DESC_STRINGS[i] + "\n";
                }
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.i("myAPP", "mode, Main.hard: " + mode + " " + Main.hard + "\npoints, ScoreWarehouseSQLite(this).highestScore(mode, Main.hard): " + points + " " + new ScoreWarehouseSQLite(this).highestScore(mode, Main.hard));
        if(points > points_max_before_game){
            if(isNew_archievements){
                game_results_type = 1;
            }else{
                game_results_type = 2;
            }
        }else if(isNew_archievements){
            game_results_type = 3;
        }else{
            game_results_type = 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_score:
                launchRanking();
                return true;
            case R.id.menu_share:
                launchShare();
                return true;
            case R.id.menu_comment:
                launchComment();
                return true;
            case R.id.menu_help:
                launchHelp();
                return true;
            /*case R.id.menu_config:
                launchPreferences();
                return true;*/
            case R.id.menu_delete_scores:
                eraseScoresDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void launchRanking(){
        Intent i = new Intent(this, Score.class);
        startActivity(i);
    }

    public void launchThanks(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.thanks_for_share));
        builder.setPositiveButton(getResources().getString(R.string.dialog_continue).toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //consumir sin hacer nada
            }
        });
        builder.show();
    }

    public void launchShare(){
        try{
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "He probado el juego Tiles Challenge y me encanta. ¡Consíguelo aquí! Es gratis.");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_app_text));
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app_via)));
        }catch (Exception E){
            Log.v("myAPP", "Error o Intent.ACTION_SEND no disponible");
        }
    }

    public void launchShareFromDialog(int i){
        try{
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String str = "";
            switch(i){
                case RECORD_AND_ARCHIEVEMENT:
                    str = getString(R.string.share_from_dialog_text_record_archievement_1) + " " + points + " " + getString(R.string.share_from_dialog_text_record_archievement_2) + "\n" + str_logros + "\n" + getString(R.string.share_from_dialog_tail);
                    break;
                case RECORD_ONLY:
                    str = getString(R.string.share_from_dialog_text_record_only_1) + " " + points + " " + getString(R.string.share_from_dialog_text_record_only_2) + "\n" + getString(R.string.share_from_dialog_tail);
                    break;
                case ARCHIEVEMENT_ONLY:
                    str = getString(R.string.share_from_dialog_text_archievement_only_1) + " " + str_logros + "\n" + getString(R.string.share_from_dialog_tail);
                    break;
            }
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, str);
            //startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_dialog_title)));
            startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.share_dialog_title)), SHARE_RESULTS);
        }catch (Exception E){
            Log.v("myAPP", "Error o Intent.ACTION_SEND no disponible");
        }
    }

    public void launchComment(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.creativefunapps.tileschallenge"));
        startActivityForResult(intent, COMMENT);
    }

    public void launchHelp(){
        Intent i = new Intent(this, Help.class);
        startActivity(i);
    }

    public void launchPreferences(){
        Intent i = new Intent(this, Preferences.class);
        startActivity(i);
    }

    public void eraseScoresDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.preferences_dialog_scores);
        builder.setPositiveButton(R.string.preferences_dialog_accept, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                //action on dialog ok
                //delete scores
                new ScoreWarehouseSQLite(Main.this).deleteScores();
                new ArchievementWarehouseSQLite(Main.this).deleteArchievements();
            }

        });
        builder.setNegativeButton(R.string.preferences_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //action on dialog cancel
                //nothing
            }
        });
        builder.show();
    }

    private void flipCard () {
        touch.setOnClickListener(null);

        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                        R.anim.card_flip_left_in, R.anim.card_flip_left_out)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.container, new CardBackFragment())

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                        //.addToBackStack(null)

                        // Commit the transaction.
                .commit();
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_card_front, container, false);

            return view;
        }
    }
    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View inputView = inflater.inflate(R.layout.fragment_card_back, container, false);
            //al usar fragments hasta este punto no se han generado los botones ni sus id correspondientes para poder apuntarlos y asignarles onClick
            inputView.findViewById(R.id.button1).setOnClickListener(showDifficultyDialog);
            inputView.findViewById(R.id.button2).setOnClickListener(showDifficultyDialog);
            //inputView.findViewById(R.id.button3).setOnClickListener(showDifficultyDialog);
            inputView.findViewById(R.id.button4).setOnClickListener(openOptions);

            inputView.findViewById(R.id.button3).setVisibility(View.GONE);
            return inputView;
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class GameSummaryFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View inputView = inflater.inflate(R.layout.fragment_game_results, container, false);
            //al usar fragments hasta este punto no se han generado los botones ni sus id correspondientes para poder apuntarlos y asignarles onClick
            inputView.findViewById(R.id.btn_share).setOnClickListener(launchShare);
            inputView.findViewById(R.id.btn_continue).setOnClickListener(launchRanking);

            switch(game_results_type){
                case 0: //no buena partida
                    break;
                case 1: //record y archievements
                    break;
                case 2: //only record
                    break;
                case 3: //only archievement
                    break;
            }

            return inputView;
        }
    }

    static View.OnClickListener launchShare = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), Game.class);
            try{
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                ((Activity)view.getContext()).startActivityForResult(Intent.createChooser(sharingIntent, view.getContext().getString(R.string.share_dialog_title)), SHARE_RESULTS);
            }catch (Exception E){
                Log.v("myAPP", "Error o Intent.ACTION_SEND no disponible");
            }
        }
    };

    static View.OnClickListener launchRanking = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), Score.class);
            ((Activity)view.getContext()).startActivity(i);
        }
    };

    static View.OnClickListener showDifficultyDialog = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(view.getResources().getString(R.string.difficulty_selection));
            builder.setNegativeButton(view.getResources().getString(R.string.easy).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    hard = false;
                    launchGame(view);
                }
            });
            builder.setPositiveButton(view.getResources().getString(R.string.hard).toUpperCase(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    hard = true;
                    launchGame(view);
                }
            });
            builder.show();
        }
    };

    static void launchGame(View view) {
        Intent i = new Intent(view.getContext(), Game.class);
        switch(view.getId()) {
            case R.id.button1:
                i.putExtra("mode", 1);
                archievements_before_game = archievement_warehouse.archievementList(50);
                points_max_before_game = score_warehouse.highestScore(1, hard);
                break;
            case R.id.button2:
                i.putExtra("mode", 2);
                archievements_before_game = archievement_warehouse.archievementList(50);
                points_max_before_game = score_warehouse.highestScore(2, hard);
                break;
            case R.id.button3:
                i.putExtra("mode", 3);
                break;
            case R.id.button4:
                ((Activity) view.getContext()).openOptionsMenu();
                break;
        }
        ((Activity)view.getContext()).startActivityForResult(i, GAME);
    };

    static View.OnClickListener startGame = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), Game.class);
            switch(view.getId()) {
                case R.id.button1:
                    i.putExtra("mode", 1);
                    archievements_before_game = archievement_warehouse.archievementList(50);
                    points_max_before_game = score_warehouse.highestScore(1, hard);
                    break;
                case R.id.button2:
                    i.putExtra("mode", 2);
                    archievements_before_game = archievement_warehouse.archievementList(50);
                    points_max_before_game = score_warehouse.highestScore(2, hard);
                    break;
                case R.id.button3:
                    i.putExtra("mode", 3);
                    break;
                case R.id.button4:
                    ((Activity) view.getContext()).openOptionsMenu();
                    break;
            }
            ((Activity)view.getContext()).startActivityForResult(i, GAME);
        }
    };

    static View.OnClickListener openOptions = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((Activity) view.getContext()).openOptionsMenu();
        }
    };

    public static class logoBoard extends Fragment {

        private View inputView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.logo_board, container, false);
            //usar el adaptador necesario para cada modo de juego, 1 MyBaseAdapter, cambiar nombre a MyBaseAdapterMode1, y sucesivos.
            //preparar gameboard inicial (creo que siempre sera de 2x2
            GridView layout = (GridView) inputView.findViewById(R.id.logoBoard);
            MyBaseAdapterLogo MyAdapter = new MyBaseAdapterLogo(inputView.getContext(), 10);
            layout.setAdapter(MyAdapter);
            layout.setNumColumns(5);

            return inputView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);

    }
}
