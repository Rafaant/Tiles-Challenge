package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Results extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameLogo, new logoBoard())
                    .commit();
        }

        Intent i = getIntent();
        ((TextView)findViewById(R.id.points)).setText(i.getStringExtra("title"));

        switch (i.getIntExtra("case",-1)){
            case -1:
                finish();
                break;
            case Main.RECORD_AND_ARCHIEVEMENT:
                ((TextView)findViewById(R.id.points)).setText(Integer.toString(i.getIntExtra("points", -1)));
                ((TextView)findViewById(R.id.achievements_text)).setText(i.getStringExtra("achievements"));
                ((TextView)findViewById(R.id.results_title)).setText(i.getStringExtra("title"));
                break;
            case Main.RECORD_ONLY:
                findViewById(R.id.row2).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.points)).setText(Integer.toString(i.getIntExtra("points", -1)));
                ((TextView)findViewById(R.id.results_title)).setText(i.getStringExtra("title"));
                break;
            case Main.ARCHIEVEMENT_ONLY:
                findViewById(R.id.row1).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.achievements_text)).setText(i.getStringExtra("achievements"));
                ((TextView)findViewById(R.id.results_title)).setText(i.getStringExtra("title"));
                break;
        }

        ((TextView)findViewById(R.id.mode)).setText(i.getStringExtra("mode").toUpperCase());

        if(i.getBooleanExtra("difficulty", false)==false){
            ((TextView)findViewById(R.id.difficulty)).setText(getResources().getString(R.string.easy));
            ((TextView)findViewById(R.id.difficulty)).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }else{
            ((TextView)findViewById(R.id.difficulty)).setText(getResources().getString(R.string.hard));
            ((TextView)findViewById(R.id.difficulty)).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }


        ((Button)findViewById(R.id.share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TOMAR CAPTURA Y COMPARTIRLA
                //final RelativeLayout v1 = (RelativeLayout) findViewById(R.id.capture);
                View v1 = getWindow().getDecorView().getRootView();
                // View v1 = iv.getRootView(); //even this works
                // View v1 = findViewById(android.R.id.content); //this works too
                // but gives only content
                v1.setDrawingCacheEnabled(true);
                v1.buildDrawingCache(true);
                Bitmap myBitmap = v1.getDrawingCache();
                saveBitmap(myBitmap);
            }
        });
        ((Button)findViewById(R.id.cont)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SALIR DE RESULTS SIN MÁS
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ((Button)findViewById(R.id.retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DEVOLVER CONTROL AL MAIN CON UN COÓDIGO QUE HAGA QUE ALLI SE EJECUTE EL JUEGO DE NUEVO CON LOS MISMOS PARÁMETROS
                Intent intent = new Intent();
                setResult(Main.RETRY_CODE, intent);
                finish();
            }
        });
    }

    public void saveBitmap(Bitmap bitmap) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + "TilesChallengeScreenshot.png";
        File imagePath = new File(filePath);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            share(filePath);
        } catch (FileNotFoundException e) {
            Log.e("MyAPP", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("MyAPP", e.getMessage(), e);
        }
    }

    public void share(String path) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        String str = "";
        Intent i = getIntent();
        switch(i.getIntExtra("case",-1)){
            case Main.RECORD_AND_ARCHIEVEMENT:
                str = getString(R.string.share_results_record_and_archievement) + " " + getString(R.string.share_from_dialog_tail);
                break;
            case Main.RECORD_ONLY:
                str = getString(R.string.share_results_record) + " " + getString(R.string.share_from_dialog_tail);
                break;
            case Main.ARCHIEVEMENT_ONLY:
                str = getString(R.string.share_results_archievement) + " " + getString(R.string.share_from_dialog_tail);
                break;
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, str);
        sharingIntent.setType("image/png");
        Uri myUri = Uri.parse("file://" + path);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, myUri);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_dialog_title)));
    }

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
}
