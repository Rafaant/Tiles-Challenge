package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;


public class Help extends PortraitActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help_overlay);

        Button button = (Button) findViewById(R.id.help_button);
        button.setVisibility(View.GONE);
        CheckBox cb = (CheckBox) findViewById(R.id.help_checkBox);
        cb.setVisibility(View.GONE);
        TextView tv = (TextView) findViewById(R.id.help);
        tv.setText(getString(R.string.help_howto) + "\n\n" + getString(R.string.help_extras) + "\n\n" + getString(R.string.help_mode1_title) + ":\n" + getString(R.string.help_mode1) + "\n\n" + getString(R.string.help_mode2_title) + ":\n" + getString(R.string.help_mode2));
    }

}
