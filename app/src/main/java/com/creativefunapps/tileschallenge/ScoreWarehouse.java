package com.creativefunapps.tileschallenge;

import java.util.Vector;

public interface ScoreWarehouse {

    public void storeScore(int mode, boolean hard, int points, int level, int combo_max, String name, long date);

    public Vector<ScoreWarehouseSQLite.Score> scoreList(int quantity, int mode, boolean hard);

    public Score getTopScoreEasy(int mode);
    public Score getTopScoreHard(int mode);

    public boolean deleteScores();
    public int highestScore(int mode, boolean hard);

    //inner class, usada para almacenar la puntuacion a devolver en el vector<> para poder tratarlas mejor
    class Score {
        int mode;
        boolean hard;
        int points;
        int level;
        int combo_max;
        String name;
        long date;

        Score(int mode, boolean hard, int points, int level, int combo_max, String name, long date) {
            this.mode = mode;
            this.hard = hard;
            this.points = points;
            this.level = level;
            this.combo_max = combo_max;
            this.name = name;
            this.date = date;
        }

        public int getMode() {
            return mode;
        }

        public boolean isHard() {
            return hard;
        }

        public int getPoints() {
            return points;
        }

        public int getLevel() { return level; }

        public int getCombo_max() {
            return combo_max;
        }

        public String getName() {
            return name;
        }

        public long getDate() {
            return date;
        }
    }
}
