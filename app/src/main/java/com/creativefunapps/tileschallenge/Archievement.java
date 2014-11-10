package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


public class Archievement extends PortraitActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_archievement);
        ListView layout = (ListView) findViewById(android.R.id.list);
        layout.setAdapter(new MyListViewAdapterArchievements(this, Main.archievement_warehouse.archievementList(50)));
    }
}
