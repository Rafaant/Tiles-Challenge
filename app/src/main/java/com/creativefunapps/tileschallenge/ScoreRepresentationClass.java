package com.creativefunapps.tileschallenge;

import android.content.Context;

public class ScoreRepresentationClass {

    int mode;
    String mode_text;
    int level_easy;
    int points_easy;
    int level_hard;
    int points_hard;

    public ScoreRepresentationClass( Context context, ScoreWarehouse score, int mode) {
        this.mode = mode;
        switch (mode){
            case 1:
                mode_text = context.getString(R.string.button1); break;
            case 2:
                mode_text = context.getString(R.string.button2); break;
            case 3:
                mode_text = context.getString(R.string.button3); break;
        }
        this.level_easy = score.getTopScoreEasy(mode).getLevel();
        this.points_easy = score.getTopScoreEasy(mode).getPoints();
        this.level_hard = score.getTopScoreHard(mode).getLevel();
        this.points_hard = score.getTopScoreHard(mode).getPoints();
    }

    public int getLevel_easy() {
        return level_easy;
    }

    public void setLevel_easy(int level_easy) {
        this.level_easy = level_easy;
    }

    public int getPoints_easy() {
        return points_easy;
    }

    public void setPoints_easy(int points_easy) {
        this.points_easy = points_easy;
    }

    public int getLevel_hard() {
        return level_hard;
    }

    public void setLevel_hard(int level_hard) {
        this.level_hard = level_hard;
    }

    public int getPoints_hard() {
        return points_hard;
    }

    public void setPoints_hard(int points_hard) {
        this.points_hard = points_hard;
    }

    public String getMode_text() {
        return mode_text;
    }
}
