package com.creativefunapps.tileschallenge;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;


public class Score extends PortraitActivity {

    public static Fragment archievements_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.containerTitle, new topbar()) //de momento se añade pero se esconden los botones para elegir entre puntuaciones locales y internet
                    .add(R.id.container, new scoreTable())
                    .add(R.id.containerBottom, new bottomBar())
                    .commit();
        }
    }

    public static class topbar extends Fragment {
        private View inputView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            inputView = inflater.inflate(R.layout.score_topbar, container, false);
            //inputView.findViewById(R.id.score).setOnClickListener(changeFragment);
            //inputView.findViewById(R.id.ranking).setOnClickListener(changeFragment);
            ((LinearLayout)inputView.findViewById(R.id.linearlayout_buttons)).setVisibility(View.GONE);
            return inputView;
        }
        public void refresh(){

        }
        View.OnClickListener changeFragment = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()) {
                    case R.id.score:
                        Toast.makeText(view.getContext(), "button score", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ranking:
                        Toast.makeText(view.getContext(), "button ranking", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
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
        View.OnClickListener changeFragment = new View.OnClickListener() {
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
        };
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
                    front=false;
                }else{
                    getFragmentManager().beginTransaction().setCustomAnimations(
                            R.anim.card_flip_left_in, R.anim.card_flip_left_out,
                            R.anim.card_flip_right_in, R.anim.card_flip_right_out)
                            .replace(R.id.container, new scoreTable()).commit();
                    ((TextView)inputView.getRootView().findViewById(R.id.tvScoreTopboardHeader)).setText(inputView.getResources().getString(R.string.score_header));
                    ((Button)inputView.findViewById(R.id.archievements)).setText(inputView.getResources().getString(R.string.score_archievements));
                    front=true;
                }
            }
        };
    }

    public static class archievementsFragment extends Fragment {
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
            return inputView;
        }
    }
}
