package com.calvins.manfred;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DatabaseConnector {

    private static final String TAG = "DatabaseConnector";

    private static final String DATABASE_NAME = "Manfred";
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DatabaseConnector(Context context){
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() throws SQLException {
        //creates or opens the database
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null)
            database.close();
    }

    /**
     * Called when a new manfred is created
     * @param dateCreated
     * @param name
     * @return returns _id of manfred object
     */
    public long insertNewGame(String dateCreated, String name){
        open();

        ContentValues newManfred = new ContentValues();
        newManfred.put("dateModified", dateCreated);
        newManfred.put("dateCreated", dateCreated);
        newManfred.put("name",name);
        int id = (int)database.insert("manfred", null, newManfred);

        ContentValues newManfredActions = new ContentValues();
        newManfredActions.put("_id",id);
        newManfredActions.put("action_eat_num", 3);
        newManfredActions.put("action_sleep_num", 3);
        newManfredActions.put("action_exercise_num", 3);
        newManfredActions.put("num_healthy_actions", 0);
        newManfredActions.put("num_unhealthy_actions", 0);
        database.insert("actions",null,newManfredActions);

        ContentValues newManfredStats = new ContentValues();
        newManfredStats.put("_id",id);
        newManfredStats.put("weight",136);
        newManfredStats.put("cholesterol",140);
        newManfredStats.put("bench_press",145);
        newManfredStats.put("deadlift",120);
        newManfredStats.put("squat",95);
        database.insert("stats",null,newManfredStats);

        Log.d(TAG, "Inserted manfred instance to all tables with _id = "+id);
        close();
        return id;
    }

    //Updates an instance of the Manfred game
    public void updateCurrentGame(long id, String dateModified){
        ContentValues editManfred = new ContentValues();
        editManfred.put("dateModified", dateModified);
        open();
        database.update("manfred", editManfred, "_id="+id, null);
        close();
    }

    //Returns information for all games to display on Saves Screen
    public Cursor getAllGames(){
        return database.query("manfred", new String[]{"_id", "name", "dateModified"},
                null, null, null, null, "name");
    }

    //Returns stats for Manfred instance
    public Cursor getStats(int _id) {
        String id = _id+"";
        return database.query("stats", new String[]{"weight", "cholesterol", "bench_press", "deadlift", "squat"},
                "_id = ?", new String[]{id}, null, null, null);
    }

    //Returns name of this Manfred instance
    public Cursor getName(int _id) {
        String id = _id+"";
        return database.query("manfred", new String[]{"name"},
                "_id = ?", new String[]{id}, null, null, null);
    }

    //Returns actions of this Manfred instance
    public Cursor getActions(int _id) {
        String id = _id+"";
        return database.query("actions", new String[]{"action_eat_num", "action_sleep_num", "action_exercise_num", "num_healthy_actions", "num_unhealthy_actions"},
                "_id = ?", new String[]{id}, null, null, null);
    }

    //Updates Manfred instance to reflect a healthy action click
    public void addHealthyAction(int _id, int eat, int sleep, int exercise, int healthy) {
        ContentValues editAction = new ContentValues();
        editAction.put("action_eat_num", eat);
        editAction.put("action_sleep_num", sleep);
        editAction.put("action_exercise_num", exercise);
        editAction.put("num_healthy_actions", healthy);

        open();
        database.update("actions", editAction, "_id="+_id, null);
        close();
    }

    //Updates Manfred instance to reflect an unhealthy action click
    public void addUnhealthyAction(int _id, int eat, int sleep, int exercise, int unhealthy) {
        ContentValues editAction = new ContentValues();
        editAction.put("action_eat_num", eat);
        editAction.put("action_sleep_num", sleep);
        editAction.put("action_exercise_num", exercise);
        editAction.put("num_unhealthy_actions", unhealthy);

        open();
        database.update("actions", editAction, "_id="+_id, null);
        close();
    }

    //Updates Manfred instance stats to reflect an action choice
    public void updateStatsForAction(int _id, int w, int c, int b, int d, int s) {
        ContentValues editStats = new ContentValues();
        editStats.put("weight", w);
        editStats.put("cholesterol", c);
        editStats.put("bench_press", b);
        editStats.put("deadlift", d);
        editStats.put("squat", s);

        open();
        database.update("stats", editStats, "_id="+_id, null);
        close();
    }

    //Returns information for one Manfred game
    public Cursor getOneManfred(long id){
        return null;
    }

    //Deletes instance of Manfred game from database
    public void deleteManfred(long id){
        open();
        database.delete("stats", "_id="+id, null);
        database.delete("actions", "_id="+id, null);
        database.delete("manfred", "_id="+id, null);
        close();
        Log.d(TAG, "Deleting Manfred instance with id = "+id);
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
            Log.d(TAG, "In DatabaseOpenHelper Constructor");
        }

        //creates database tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "In onCreate for DatabaseOpenHelper");
            String createManfred = "CREATE TABLE manfred"+
                    "(_id INTEGER PRIMARY KEY autoincrement, "+
                    "dateCreated TEXT, "+
                    "dateModified TEXT, "+
                    "name TEXT);";

            String createActions = "CREATE TABLE actions" +
                    "(_id INTEGER PRIMARY KEY, "+
                    "action_eat_num INTEGER, "+
                    "action_sleep_num INTEGER, "+
                    "action_exercise_num INTEGER, "+
                    "num_healthy_actions INTEGER, "+
                    "num_unhealthy_actions INTEGER, "+
                    "FOREIGN KEY(_id) REFERENCES manfred(_id));";

            String createStats = "CREATE TABLE stats"+
                    "(_id INTEGER PRIMARY KEY, "+
                    "weight INTEGER, "+
                    "cholesterol INTEGER, "+
                    "bench_press INTEGER, "+
                    "deadlift INTEGER, "+
                    "squat INTEGER, "+
                    "FOREIGN KEY(_id) REFERENCES manfred(_id));";

            db.execSQL(createManfred);
            db.execSQL(createActions);
            db.execSQL(createStats);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // nothing to do
        }
    }
}
