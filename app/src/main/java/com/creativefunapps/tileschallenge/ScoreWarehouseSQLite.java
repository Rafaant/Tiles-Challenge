package com.creativefunapps.tileschallenge;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Vector;

public class ScoreWarehouseSQLite extends SQLiteOpenHelper implements ScoreWarehouse {

    public ScoreWarehouseSQLite(Context context) {
        // llamada al constructor del super con los parametros:
        // SQLiteOpenHelper(Context contexto, String nombreBD,
        // SQLiteDatabase.CursorFactory cursorACrear, int version).
        super(context, "DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scores ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "mode INTEGER, hard INTEGER, points INTEGER, level INTEGER, combo_max INTEGER, name TEXT, date LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {

    }

    @Override
    public void storeScore(int mode, boolean hard, int points, int level, int combo_max, String name, long date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO scores (mode, hard, points, level, combo_max, name, date) VALUES ( " + mode + ", " + ((hard)? 1 : 0) + ", " + points + ", " + level + ", " + combo_max + ", '" + name + "', " + date + " )");
        db.close();
    }

    @Override
    public Vector<Score> scoreList(int quantity, int mode, boolean hard) {
        Vector<Score> result = new Vector<Score>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + "scores WHERE mode = " + mode + " AND hard = " + ((hard)?1:0) + " ORDER BY points DESC LIMIT " + quantity, null);
        try{
            // o igualmente podemos hacer la consulta asi
            // String[] CAMPOS = {"puntos", "nombre"};
            // Cursor cursor=db.query("puntuaciones", CAMPOS, null, null, null,
            // null, "puntos", Integer.toString(cantidad));
            while (cursor.moveToNext()) {
                //el indice 0 es el valor _id
                int a = cursor.getInt(1);
                boolean b = hard;
                int c = cursor.getInt(3);
                int d = cursor.getInt(4);
                int e = cursor.getInt(5);
                String f = cursor.getString(6);
                long g = cursor.getLong(7);
                Score sc = new Score(a, b, c, d, e, f, g);
                result.add(sc);
            }
        }finally {
            cursor.close();
        }
        return result;
    }

    public Score getTopScoreEasy(int mode){
        Vector<Score> list = scoreList(1, mode, false);
        Score score;
        if(list.isEmpty()){
            score = new Score(mode, false, 0, 0, 0, "", 0); //devolvemos un score vacio para este caso de mode y hard
        }else{
            score = list.elementAt(0);
        }
        return score;
    }

    public Score getTopScoreHard(int mode){
        Vector<Score> list = scoreList(1, mode, true);
        Score score;
        if(list.isEmpty()){
            score = new Score(mode, true, 0, 0, 0, "", 0); //devolvemos un score vacio para este caso de mode y hard
        }else{
            score = list.elementAt(0);
        }
        return score;
    }

    @Override
    public boolean deleteScores() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE scores");
        this.onCreate(db);
        db.close();
        return true;
    }

    public int highestScore(int mode, boolean hard){
        int highest = 0;
        Vector<Score> result = scoreList(10, mode, hard);
        if(!result.isEmpty()){
            highest = result.elementAt(0).getPoints();
        }
        return highest;
    }

}
