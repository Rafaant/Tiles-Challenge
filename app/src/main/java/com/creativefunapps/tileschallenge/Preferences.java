package com.creativefunapps.tileschallenge;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Rafa on 08/10/2014.
 */
public class Preferences extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference button = (Preference)findPreference("deleteScores");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                builder.setMessage(R.string.preferences_dialog_scores);
                builder.setPositiveButton(R.string.preferences_dialog_accept, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        //action on dialog ok
                        //delete scores
                        new ScoreWarehouseSQLite(preference.getContext()).deleteScores();
                        new ArchievementWarehouseSQLite(preference.getContext()).deleteArchievements();
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
                return true;
            }
        });
        button = (Preference)findPreference("showHelp");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                builder.setMessage(R.string.preferences_dialog_showhelp);
                builder.setPositiveButton(R.string.preferences_dialog_accept, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        //action on dialog ok
                        Main.show_help_mode1=true;
                        Main.show_help_mode2=true;
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
                return true;
            }
        });
    }
}
