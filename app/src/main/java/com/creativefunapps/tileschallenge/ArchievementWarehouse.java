package com.creativefunapps.tileschallenge;

import android.content.res.Resources;

import java.util.Vector;

public interface ArchievementWarehouse {

    public void storeArchievement(String name, int mode, int hard);

    public Vector<Archievement> archievementList(int quantity);

    public boolean deleteArchievements();

    public boolean isNew_archievement(Vector<Archievement> before_game, Vector<Archievement> after_game);

    public int[] get_newArchievementPositions();

    public boolean areAllAcquired();

    //inner class, usada para almacenar la puntuacion a devolver en el vector<> para poder tratarlas mejor
    class Archievement {
        int mode;
        boolean hard;
        String name;
        long date;
        boolean acquired = false;

        Archievement(int mode, boolean hard, String name, long date, boolean acquired) {
            this.mode = mode;
            this.hard = hard;
            this.name = name;
            this.date = date;
            this.acquired = acquired;
        }

        public String getName() {
            return name;
        }

        public int getMode() {
            return mode;
        }

        public boolean isHard() {
            return hard;
        }

        public boolean isAcquired() {
            return acquired;
        }

        public void setHard(boolean hard) {
            this.hard = hard;
        }

        public void setAcquired(boolean acquired) {
            this.acquired = acquired;
        }
    }
}
