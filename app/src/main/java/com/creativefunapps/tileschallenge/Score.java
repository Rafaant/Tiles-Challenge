package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import java.util.Vector;


public class Score extends BaseGameActivity {

    public static Fragment archievements_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        getGameHelper().setMaxAutoSignInAttempts(0);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    //.add(R.id.containerTitle, new topbar()) //de momento se añade pero se esconden los botones para elegir entre puntuaciones locales y internet
                    .add(R.id.container, new scoreTable())
                    .add(R.id.containerBottom, new bottomBar())
                    .commit();
        }

        ((Button)findViewById(R.id.ranking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSignedIn()){
                    if(((Button)view).getText()==getString(R.string.score_ranking)){
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                                        getApiClient(), getString(R.string.leaderboard_scoreboard)),
                                2);
                    }else if (((Button)view).getText()==getString(R.string.score_achievements_online)){
                        startActivityForResult(Games.Achievements.getAchievementsIntent(
                                getApiClient()), 1);
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Score.this);
                    builder.setMessage(getString(R.string.not_connected));
                    builder.setPositiveButton(getString(R.string.dialog_continue).toUpperCase(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //no hacer nada, se consume el dialog y listo
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }

    public static class topbar extends Fragment {
        private View inputView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.score_topbar, container, false);
            //inputView.findViewById(R.id.score).setOnClickListener(changeFragment);
            //inputView.findViewById(R.id.ranking).setOnClickListener(changeFragment);
            //((LinearLayout)inputView.findViewById(R.id.linearlayout_buttons)).setVisibility(View.GONE);
            return inputView;
        }
        public void refresh(){

        }
        /*View.OnClickListener changeFragment = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.score:
                        Toast.makeText(view.getContext(), "button score", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ranking:
                        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                                        getApiClient(), getString(R.string.number_guesses_leaderboard)),
                                2);
                        break;
                }
            }
        };*/
    }

    public static class scoreTable extends Fragment {
        private View inputView;
        private LayoutInflater inflater;
        private ViewGroup container;
        private Bundle savedInstanceState;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            this.inflater = inflater;
            this.container = container;
            this.savedInstanceState = savedInstanceState;
            inputView = inflater.inflate(R.layout.scores, container, false);
            ListView layout = (ListView) inputView.findViewById(android.R.id.list);
            Vector<ScoreRepresentationClass> list = new Vector<ScoreRepresentationClass>();
            //se añaden por duplicado porque del 0 y del 2 se coge el nombre para el saparador solamente
            list.add(new ScoreRepresentationClass(inputView.getContext(), Main.score_warehouse, 1));
            list.add(new ScoreRepresentationClass(inputView.getContext(), Main.score_warehouse, 1));
            list.add(new ScoreRepresentationClass(inputView.getContext(), Main.score_warehouse, 2));
            list.add(new ScoreRepresentationClass(inputView.getContext(), Main.score_warehouse, 2));
            //list.add(new ScoreRepresentationClass(inputView.getContext(), Main.score_warehouse, 3));

            layout.setAdapter(new MyListViewAdapterScores((Activity) inputView.getContext(), list));
            return inputView;
        }
        public void refresh(){

        }
        /*View.OnClickListener changeFragment = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.score:
                        Toast.makeText(view.getContext(), "button score", Toast.LENGTH_SHORT).show();
                        onCreateView(inflater, container, savedInstanceState);
                        break;
                    case R.id.ranking:
                        Toast.makeText(view.getContext(), "button ranking", Toast.LENGTH_SHORT).show();
                        ListView layout = (ListView) inputView.findViewById(android.R.id.list);

                        layout.setAdapter(new MyListViewAdapterArchievements((Activity) inputView.getContext(), Main.archievement_warehouse.archievementList(50))); //10 del modo 1
                        break;
                }
            }
        };*/
    }

    private static boolean front;

    public static class bottomBar extends Fragment {
        private View inputView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.score_bottombar, container, false);
            inputView.findViewById(R.id.archievements).setOnClickListener(launchArchievements);
            front = true;
            return inputView;
        }
        View.OnClickListener launchArchievements = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(front) {
                    getFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                            R.anim.card_flip_left_in, R.anim.card_flip_left_out)
                            .replace(R.id.container, archievements_fragment = new archievementsFragment()).commit();
                    ((TextView)inputView.getRootView().findViewById(R.id.tvScoreTopboardHeader)).setText(inputView.getResources().getString(R.string.archievements_header));
                    ((Button)inputView.findViewById(R.id.archievements)).setText(inputView.getResources().getString(R.string.score_archievements2));
                    ((Button)inputView.getRootView().findViewById(R.id.ranking)).setText(inputView.getResources().getString(R.string.score_achievements_online));
                    ((Button)inputView.getRootView().findViewById(R.id.ranking)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.games_achievements_green_64,0,0,0);
                    front=false;
                }else{
                    getFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.card_flip_left_in, R.anim.card_flip_left_out,
                            R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                            .replace(R.id.container, new scoreTable()).commit();
                    ((TextView)inputView.getRootView().findViewById(R.id.tvScoreTopboardHeader)).setText(inputView.getResources().getString(R.string.score_header));
                    ((Button)inputView.findViewById(R.id.archievements)).setText(inputView.getResources().getString(R.string.score_archievements));
                    ((Button)inputView.getRootView().findViewById(R.id.ranking)).setText(inputView.getResources().getString(R.string.score_ranking));
                    ((Button)inputView.getRootView().findViewById(R.id.ranking)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.games_leaderboards_green_64,0,0,0);
                    front=true;
                }
            }
        };
    }

    public static class archievementsFragment extends ListFragment {
        private View inputView;
        private TextView text;
        private static CheckBox cb;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.archievements, container, false);
            ListView layout = (ListView) inputView.findViewById(android.R.id.list);
            Vector<ScoreRepresentationClass> list = new Vector<ScoreRepresentationClass>();
            layout.setAdapter(new MyListViewAdapterArchievements((Activity) inputView.getContext(), Main.archievement_warehouse.archievementList(50)));
            //este on click funciona en los elementos vacíos, es decir, donde pone facil, dificil y todos. En los elementos buenos no hace nada
            /*layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(inputView.getContext(), "prueba", Toast.LENGTH_SHORT).show();
                }
            });*/


            return inputView;
        }
        /*@Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            // put your handler here
            Toast.makeText(inputView.getContext(), "prueba 2", Toast.LENGTH_SHORT).show();
        }*/
    }
}
