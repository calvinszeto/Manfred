package com.calvins.manfred;

import android.app.Activity;
import android.database.Cursor;
import android.util.Xml;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.Date;

import android.content.Context;

/**
 * Created by puppyplus on 11/3/13.
 */
public class Action {

    public static final String TAG = "Action";

    private static ArrayList<ActionWrapper> eat_actions = new ArrayList<ActionWrapper>();
    private static ArrayList<ActionWrapper> exercise_actions = new ArrayList<ActionWrapper>();
    private static ArrayList<ActionWrapper> sleep_actions = new ArrayList<ActionWrapper>();

    /**
     * Loads the actions from actions.xml into the actions object as ActionWrappers
     */
    public static void loadActions(Context context, int save_id) throws XmlPullParserException, IOException {
        InputStream in = context.getAssets().open("actions.xml");
        eat_actions = new ArrayList<ActionWrapper>();
        exercise_actions = new ArrayList<ActionWrapper>();
        sleep_actions = new ArrayList<ActionWrapper>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(in, null);
            parser.nextTag();
            parseActions(parser);
        } finally {
            in.close();
        }
        resetActionsLocked(context, save_id);
    }

    /**
     * Pull the actions from the actions.xml file and parse them into ActionWrapper objects.
     *
     * @param parser the XmlPullParser for traversing the actions.xml file
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static void parseActions(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            // All attributes are in action standalone tags
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("action")) {
                // Grab all the attributes out of the tag
                String category = parser.getAttributeValue(null, "category");
                // Create a new ActionWrapper and add it to the appropriate List
                ActionWrapper action = new ActionWrapper(parser.getAttributeValue(null, "name"),
                        parser.getAttributeValue(null, "event"),
                        Boolean.valueOf(parser.getAttributeValue(null, "major")),
                        Integer.parseInt(parser.getAttributeValue(null, "level")),
                        parser.getAttributeValue(null, "stat_changes"),
                        parser.getAttributeValue(null, "stat_requirements"),
                        category);
                if (category.equals("eat")) {
                    eat_actions.add(action);
                } else if (category.equals("exercise")) {
                    exercise_actions.add(action);
                } else {
                    sleep_actions.add(action);
                }
            }
        }
    }

    /**
     * Pull the user's lock/unlock code from the database and set the ActionWrapper accordingly
     *
     * @param context
     * @param save_id
     */
    private static void resetActionsLocked(Context context, int save_id) {
        final DatabaseConnector dbConnector = new DatabaseConnector(context);
        dbConnector.open();
        Cursor currentActions = dbConnector.getActions(save_id);
        currentActions.moveToFirst();
        // current values for eat, exercise & sleep, they determine what actions can & can't be seen
        int action_eat_num = currentActions.getInt(currentActions.getColumnIndex("action_eat_num"));
        int action_exercise_num = currentActions.getInt(currentActions.getColumnIndex("action_exercise_num"));
        int action_sleep_num = currentActions.getInt(currentActions.getColumnIndex("action_sleep_num"));
        for (int i = 0; i < eat_actions.size(); i++) {
            eat_actions.get(i).setUnlocked(((action_eat_num >> i) & 1) == 0);
        }
        for (int i = 0; i < exercise_actions.size(); i++) {
            exercise_actions.get(i).setUnlocked(((action_exercise_num >> i) & 1) == 0);
        }
        for (int i = 0; i < sleep_actions.size(); i++) {
            sleep_actions.get(i).setUnlocked(((action_sleep_num >> i) & 1) == 0);
        }
        dbConnector.close();
    }

    /**
     * Calculate the user's locked/unlocked actions and update the codes in the database accordingly
     *
     * @param context
     * @param save_id
     */
    private static void updateActionsLocked(Context context, int save_id) {
        // Database junk
        final DatabaseConnector dbConnector = new DatabaseConnector(context);
        dbConnector.open();
        Cursor currentActions = dbConnector.getActions(save_id);
        currentActions.moveToFirst();
        Cursor currentStats = dbConnector.getStats(save_id);
        currentStats.moveToFirst();

        // current values for eat, exercise & sleep
        int action_eat_num = currentActions.getInt(currentActions.getColumnIndex("action_eat_num"));
        int action_exercise_num = currentActions.getInt(currentActions.getColumnIndex("action_exercise_num"));
        int action_sleep_num = currentActions.getInt(currentActions.getColumnIndex("action_sleep_num"));

        // current stat values for this Manfred
        int weight = currentStats.getInt(currentStats.getColumnIndex("weight"));
        int vo2_max = currentStats.getInt(currentStats.getColumnIndex("vo2_max"));
        int squat = currentStats.getInt(currentStats.getColumnIndex("squat"));
        int body_fat = currentStats.getInt(currentStats.getColumnIndex("body_fat"));

        for (int i = 0; i < eat_actions.size(); i++) {
            ActionWrapper action = eat_actions.get(i);
            int[] stat_requirements = action.getStat_requirements();
            if ((weight == stat_requirements[0] || stat_requirements[0] == 0) &&
                    (vo2_max == stat_requirements[1] || stat_requirements[1] == 0) &&
                    (squat == stat_requirements[2] || stat_requirements[2] == 0) &&
                    (body_fat == stat_requirements[3] || stat_requirements[3] == 0)){
                eat_actions.get(i).setUnlocked(true);
                action_eat_num = action_eat_num | 1 << i;
            } else {
                eat_actions.get(i).setUnlocked(false);
            }
        }
        for (int i = 0; i < exercise_actions.size(); i++) {
            ActionWrapper action = exercise_actions.get(i);
            int[] stat_requirements = action.getStat_requirements();
            if ((weight == stat_requirements[0] || stat_requirements[0] == 0) &&
                    (vo2_max == stat_requirements[1] || stat_requirements[1] == 0) &&
                    (squat == stat_requirements[2] || stat_requirements[2] == 0) &&
                    (body_fat == stat_requirements[3] || stat_requirements[3] == 0)){
                exercise_actions.get(i).setUnlocked(true);
                action_exercise_num = action_exercise_num | 1 << i;
            } else {
                exercise_actions.get(i).setUnlocked(false);
            }
        }
        for (int i = 0; i < sleep_actions.size(); i++) {
            ActionWrapper action = sleep_actions.get(i);
            int[] stat_requirements = action.getStat_requirements();
            if ((weight == stat_requirements[0] || stat_requirements[0] == 0) &&
                    (vo2_max == stat_requirements[1] || stat_requirements[1] == 0) &&
                    (squat == stat_requirements[2] || stat_requirements[2] == 0) &&
                    (body_fat == stat_requirements[3] || stat_requirements[3] == 0)){
                sleep_actions.get(i).setUnlocked(true);
                action_sleep_num = action_sleep_num | 1 << i;
            } else {
                sleep_actions.get(i).setUnlocked(false);
            }
        }
        dbConnector.addAction(save_id, action_eat_num, action_sleep_num, action_exercise_num);
        dbConnector.close();
    }

    /**
     * Get the List of ActionWrappers for the given category
     *
     * @param category
     * @return
     */
    public static ArrayList<ActionWrapper> getActions(String category) {
        if (category.equals("eat")) {
            return eat_actions;
        } else if (category.equals("exercise")) {
            return exercise_actions;
        } else {
            return sleep_actions;
        }
    }

    /**
     * Apply an Action: change the stats, reapply Action locks, and add the event to the log
     *
     * @param action_id
     * @param category
     * @param save_id
     * @param dbConnector
     * @param context
     */
    public static void applyAction(int action_id, String category, int save_id, DatabaseConnector dbConnector, Context context) {
        ActionWrapper action = getActions(category).get(action_id);

        dbConnector.open();
        Cursor currentActions = dbConnector.getActions(save_id);
        currentActions.moveToFirst();
        Cursor currentStats = dbConnector.getStats(save_id);
        currentStats.moveToFirst();

        // current stat values for this Manfred
        int weight = currentStats.getInt(currentStats.getColumnIndex("weight"));
        int vo2_max = currentStats.getInt(currentStats.getColumnIndex("vo2_max"));
        int squat = currentStats.getInt(currentStats.getColumnIndex("squat"));
        int body_fat = currentStats.getInt(currentStats.getColumnIndex("body_fat"));

        int[] stat_changes = action.getStat_changes();

        weight += stat_changes[0];
        vo2_max += stat_changes[1];
        squat += stat_changes[2];
        body_fat += stat_changes[3];
        dbConnector.updateStatsForAction(save_id, weight, vo2_max, squat, body_fat);

        //enter new time modified for this Manfred instance
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dbConnector.updateCurrentGame(save_id, dateFormat.format(date));

        dbConnector.close();

        // Recalculate locked/unlocked actions
        updateActionsLocked(context, save_id);
        try {
            ManfredLog.writeLog(context, action.getEvent(), save_id);
        } catch (Exception e) {
            Log.d(ManfredActivity.TAG, "applyAction: " + e.getMessage());
        }
    }
}
