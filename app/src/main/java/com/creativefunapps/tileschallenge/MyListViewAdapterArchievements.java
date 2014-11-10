package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

public class MyListViewAdapterArchievements extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private final Activity actividad;
    private final Vector<ArchievementWarehouse.Archievement> lista;
    private TextView textView;

    public MyListViewAdapterArchievements(Activity actividad, Vector<ArchievementWarehouse.Archievement> lista) {
        super();
        this.actividad = actividad;
        this.lista = lista;
        this.lista.add(0, new ArchievementWarehouse.Archievement(0,false,actividad.getResources().getString(R.string.easy),0,false));
        this.lista.add(8, new ArchievementWarehouse.Archievement(0,false,actividad.getResources().getString(R.string.hard),0,false));
        this.lista.add(17, new ArchievementWarehouse.Archievement(0,false,actividad.getResources().getString(R.string.all),0,false));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = actividad.getLayoutInflater();
        View view;
        if(position==0 || position==8 || position==17){
            view = inflater.inflate(R.layout.archievement_separator, null, true);
            ((TextView) view.findViewById(R.id.tvArchievementsMode)).setText(lista.elementAt(position).getName().toUpperCase());
        }else{
            view = inflater.inflate(R.layout.archievement_element, null, true);
            textView = (TextView) view.findViewById(R.id.tvArchievementsMode);
            String str = lista.get(position).getName().toUpperCase();
            if(position>8 && position<17){
                str = str.substring(0, str.length()-1);
            }
            textView.setText(str);
            textView.setSelected(true);
            textView = (TextView) view.findViewById(R.id.desc);

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            if(lista.get(position).isAcquired()){
                textView.setText(ArchievementWarehouseSQLite.ARCHIEVEMENTS_DESC_STRINGS[position]);
                textView.setSelected(true);
                icon.setVisibility(View.VISIBLE);
            }
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

    public boolean areAllItemsEnabled() { return true; }
}
