package com.creativefunapps.tileschallenge;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Vector;

public class ArchievementWarehouseSQLite extends SQLiteOpenHelper implements ArchievementWarehouse {

    //logros posibles
    //public static final String ARCHIEVEMENTS_STRINGS[] = Resources.getSystem().getStringArray(R.array.archievements_array);
    public static String ARCHIEVEMENTS_STRINGS[];
    public static String ARCHIEVEMENTS_DESC_STRINGS[];
    public boolean new_archievement = false;
    public int[] new_archievement_positions;

    public ArchievementWarehouseSQLite(Context context) {
        // llamada al constructor del super con los parametros:
        // SQLiteOpenHelper(Context contexto, String nombreBD,
        // SQLiteDatabase.CursorFactory cursorACrear, int version).
        super(context, "DBarchievements", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE archievements ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "mode INTEGER, hard INTEGER, name TEXT, date LONG, acquired INTEGER)");
        for(int i=0; i<ARCHIEVEMENTS_STRINGS.length; i++){
            db.execSQL("INSERT INTO archievements (mode, hard, name, date, acquired) VALUES ( 1, 0, '" + ARCHIEVEMENTS_STRINGS[i] + "', 0, 0 )");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {

    }

    @Override
    public void storeArchievement(String name, int mode, int hard) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE archievements SET acquired = 1, mode = " + mode + ", hard = " + hard + " WHERE name = '" + name + "'");
        new_archievement = true;
    }

    /*@Override
    public Vector<Archievement> archievementList(int quantity, int mode) {
        Vector<Archievement> result = new Vector<Archievement>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + "archievements WHERE mode = " + mode + " ORDER BY _id ASC LIMIT " + quantity, null);
        try{
            // o igualmente podemos hacer la consulta asi
            // String[] CAMPOS = {"puntos", "nombre"};
            // Cursor cursor=db.query("puntuaciones", CAMPOS, null, null, null,
            // null, "puntos", Integer.toString(cantidad));
            while (cursor.moveToNext()) {
                //el indice 0 es el valor _id
                int a = cursor.getInt(1);
                boolean b = (cursor.getInt(2)==1) ? true : false;
                String c = cursor.getString(3);
                Long d = cursor.getLong(4);
                boolean e = (cursor.getInt(5)==1) ? true : false;
                Archievement arch = new Archievement(a, b, c, d, e);
                result.add(arch);
            }
        }finally {
            cursor.close();
        }
        return result;
    }*/

    @Override
    public Vector<Archievement> archievementList(int quantity) {
        Vector<Archievement> result = new Vector<Archievement>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + "archievements ORDER BY _id ASC LIMIT " + quantity, null);
        try{
            // o igualmente podemos hacer la consulta asi
            // String[] CAMPOS = {"puntos", "nombre"};
            // Cursor cursor=db.query("puntuaciones", CAMPOS, null, null, null,
            // null, "puntos", Integer.toString(cantidad));
            while (cursor.moveToNext()) {
                //el indice 0 es el valor _id
                int a = cursor.getInt(1);
                boolean b = (cursor.getInt(2)==1) ? true : false;
                String c = cursor.getString(3);
                Long d = cursor.getLong(4);
                boolean e = (cursor.getInt(5)==1) ? true : false;
                Archievement arch = new Archievement(a, b, c, d, e);
                result.add(arch);
            }
        }finally {
            cursor.close();
        }
        return result;
    }

    @Override
    public boolean deleteArchievements() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE archievements");
        this.onCreate(db);
        Main.archievements_now = archievementList(50); //hay que actualizar la "copia" que tengo en main (archievements actuales de la sesion) para que si los borro y seguidamente juego no los recuerde, sino que tb borre los de la copia.
        return true;
    }

    //compara la lista de archivements anterior a la partida, con la que tiene ya la base de datos puesto que ya se han añadido los nuevos.
    public boolean isNew_archievement(Vector<Archievement> before_game, Vector<Archievement> after_game) {
        int nuevos=0;
        int db=0;
        int f=0;
        boolean a, b;
        new_archievement_positions = new int[before_game.size()];
        for(int i=0; i<before_game.size(); i++){
            a = before_game.elementAt(i).isAcquired();
            if(a){
                db++;
            }
            b = after_game.elementAt(i).isAcquired();
            if(b){
                nuevos++;
            }
            if(!a && b){//si no estaba y esta ahora...es nuevo!
                new_archievement_positions[i]=1;
            }
        }
        new_archievement = (nuevos>db) ? true : false;
        return new_archievement;
    }

    @Override
    public int[] get_newArchievementPositions() {
        return new_archievement_positions;
    }

    public boolean areAllAcquired(){
        boolean all_acquired =  true;
        for(int i=0; i<Main.archievements_after_game.size() && all_acquired; i++){
            if(!Main.archievements_after_game.get(i).isAcquired()){
                all_acquired = false;
            }
        }
        return all_acquired;
    };
}
