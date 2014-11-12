package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class MyListViewAdapterScores extends BaseAdapter {

    private final Activity actividad;
    private final Vector<ScoreRepresentationClass> lista;
    private TextView textView;

    public MyListViewAdapterScores(Activity actividad, Vector<ScoreRepresentationClass> lista) {
        super();
        this.actividad = actividad;
        this.lista = lista;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater inflater = actividad.getLayoutInflater();
        if(position==0 || position==2){
            view = inflater.inflate(R.layout.score_separator, null, true);
            ((TextView) view.findViewById(R.id.tvScoreMode)).setText(lista.elementAt(position).getMode_text().toUpperCase());
            /*if(position==0){
                ((ImageView) view.findViewById(R.id.icon)).setBackground(view.getResources().getDrawable(R.drawable.pointer_64));
            }
            if(position==2){
                ((ImageView) view.findViewById(R.id.icon)).setBackground(view.getResources().getDrawable(R.drawable.chronometer_64));
            }*/
        }else {
            view = inflater.inflate(R.layout.score_element, null, true);
            //textView = (TextView) view.findViewById(R.id.mode);
            //textView.setText(lista.elementAt(position).getMode_text());
            textView = (TextView) view.findViewById(R.id.easyPoints);
            textView.setText("" + lista.elementAt(position).getPoints_easy());
            textView = (TextView) view.findViewById(R.id.easyLevel);
            textView.setText("" + lista.elementAt(position).getLevel_easy());
            textView = (TextView) view.findViewById(R.id.hardPoints);
            textView.setText("" + lista.get(position).getPoints_hard());
            textView = (TextView) view.findViewById(R.id.hardLevel);
            textView.setText("" + lista.get(position).getLevel_hard());
        }
        return view;
    }

    public int getCount() {
        return lista.size();
    }

    public Object getItem(int arg0) {
        return lista.elementAt(arg0);
    }

    public long getItemId(int position) {
        return position;
    }
}
