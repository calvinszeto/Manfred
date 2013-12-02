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
        newManfredActions.put("num_eat_total", 0);
        newManfredActions.put("num_sleep_total", 0);
        newManfredActions.put("num_exercise_total", 0);
        newManfredActions.put("current_action_level", 5);
        database.insert("actions",null,newManfredActions);

        ContentValues newManfredStats = new ContentValues();
        newManfredStats.put("_id",id);
        newManfredStats.put("weight",160);
        newManfredStats.put("vo2_max",42);
        newManfredStats.put("squat",95);
        newManfredStats.put("body_fat",15);
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
        return database.query("stats", new String[]{"weight", "vo2_max", "squat", "body_fat"},
                "_id = ?", new String[]{id}, null, null, null);
    }

    //Returns name of this Manfred instance
    public Cursor getName(int _id) {
        String id = _id+"";
        return database.query("manfred", new String[]{"name"},
                "_id = ?", new String[]{id}, null, null, null);
    }

    //Returns current level user is on
    public int getCurrentLevel(int _id) {
        String id = _id+"";
        Cursor cursor = database.query("actions", new String[]{"current_action_level"},
                "_id = ?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("current_action_level"));
    }

    //Update current level user is on
    public void updateCurrentLevel(int _id, int new_level) {
        ContentValues editLevel = new ContentValues();
        editLevel.put("current_action_level", new_level);
        open();
        database.update("actions", editLevel, "_id="+_id, null);
        close();
    }

    //Returns total number of actions done in each action category
    public Cursor getTotalNumActions(int _id) {
        String id = _id+"";
        return database.query("actions", new String[]{"num_eat_total", "num_sleep_total", "num_exercise_total"},
                "_id = ?", new String[]{id}, null, null, null);
    }

    //Returns total number of actions for a specific category where the String argument is the column name
    public int getTotalNumAction(int _id, String colName) {
        String id = _id+"";
        Cursor cursor = database.query("actions", new String[]{colName},
                "_id = ?", new String[]{id}, null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(colName));
    }

    //Increments num_*category*_total by 1 depending on what category the action that was just executed was in
    public void incrementAction(int _id, String category) {
        String col = "";
        if(category.toLowerCase().equals("eat"))
            col = "num_eat_total";
        else if(category.toLowerCase().equals("sleep"))
            col = "num_sleep_total";
        else
            col = "num_exercise_total";
        open();
        int new_val = getTotalNumAction(_id,col)+1;
        Log.d(ManfredActivity.TAG, "Updating " + col + " to " + new_val);
        ContentValues editTotalAction = new ContentValues();
        editTotalAction.put(col, new_val);
        database.update("actions",editTotalAction,"_id="+_id,null);
        close();
    }

    //Updates Manfred instance stats to reflect an action choice
    public void updateStatsForAction(int _id, int w, int v, int s, int b) {
        ContentValues editStats = new ContentValues();
        editStats.put("weight", w);
        editStats.put("vo2_max", v);
        editStats.put("squat", s);
        editStats.put("body_fat", b);

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
                    "num_eat_total INTEGER, "+
                    "num_sleep_total INTEGER, "+
                    "num_exercise_total INTEGER, "+
                    "current_action_level INTEGER, "+
                    "FOREIGN KEY(_id) REFERENCES manfred(_id));";

            String createStats = "CREATE TABLE stats"+
                    "(_id INTEGER PRIMARY KEY, "+
                    "weight INTEGER, "+
                    "vo2_max INTEGER, "+
                    "squat INTEGER, "+
                    "body_fat INTEGER, "+
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
