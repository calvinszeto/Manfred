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
        setActionsLocked(context, save_id);
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
     * @param context
     * @param save_id
     */
    private static void setActionsLocked(Context context, int save_id) {
        final DatabaseConnector dbConnector = new DatabaseConnector(context);
        dbConnector.open();
        Cursor currentActions = dbConnector.getActions(save_id);
        currentActions.moveToFirst();
        // current values for eat, exercise & sleep, they determine what actions can & can't be seen
        int action_eat_num = currentActions.getInt(currentActions.getColumnIndex("action_eat_num"));
        int action_exercise_num = currentActions.getInt(currentActions.getColumnIndex("action_exercise_num"));
        int action_sleep_num = currentActions.getInt(currentActions.getColumnIndex("action_sleep_num"));
        for (int i = 0; i < eat_actions.size(); i++) {
            Log.d(ManfredActivity.TAG, "" + i + " " + action_eat_num + " " + (((action_eat_num >> i) & 1) == 1));
            eat_actions.get(i).setUnlocked(((action_eat_num >> i) & 1) == 0);
        }
        for (int i = 0; i < exercise_actions.size(); i++) {
            exercise_actions.get(i).setUnlocked(((action_exercise_num >> i) & 1) == 0);
        }
        for (int i = 0; i < sleep_actions.size(); i++) {
            sleep_actions.get(i).setUnlocked(((action_sleep_num >> i) & 1) == 0);
        }
    }

    /**
     * Get the List of ActionWrappers for the given category
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

        // current values for eat, exercise & sleep, they determine what actions can & can't be seen
        int action_eat_num = currentActions.getInt(currentActions.getColumnIndex("action_eat_num"));
        int action_sleep_num = currentActions.getInt(currentActions.getColumnIndex("action_sleep_num"));
        int action_exercise_num = currentActions.getInt(currentActions.getColumnIndex("action_exercise_num"));

        // current values for number of unhealthy & healthy actions
        int num_healthy_actions = currentActions.getInt(currentActions.getColumnIndex("num_healthy_actions"));
        int num_unhealthy_actions = currentActions.getInt(currentActions.getColumnIndex("num_unhealthy_actions"));

        // current stat values for this Manfred
        int weight = currentStats.getInt(currentStats.getColumnIndex("weight"));
        int cholesterol = currentStats.getInt(currentStats.getColumnIndex("cholesterol"));
        int bench_press = currentStats.getInt(currentStats.getColumnIndex("bench_press"));
        int deadlift = currentStats.getInt(currentStats.getColumnIndex("deadlift"));
        int squat = currentStats.getInt(currentStats.getColumnIndex("squat"));

        if (true) {//(action.getPath().equals("healthy")) {
            // TODO: See comment below

            /**
             * Add healthy calculations here to determine action_eat_num, action_sleep_num or action_exercise_num
             * Also, default stat changes have been made based on an action, need to be changed
             */

            num_healthy_actions += 1;
            weight -= 1;
            cholesterol -= 1;
            bench_press += 2;
            deadlift += 1;
            squat += 1;
            dbConnector.updateStatsForAction(save_id, weight, cholesterol, bench_press, deadlift, squat);
            dbConnector.addHealthyAction(save_id, action_eat_num, action_sleep_num, action_exercise_num, num_healthy_actions);
            Log.d(TAG, "num_healthy_actions = " + num_healthy_actions + ", id for Manfred = " + save_id);
        } else {
            // TODO: See comment below

            /**
             * Add unhealthy calculations here to determine action_eat_num, action_sleep_num or action_exercise_num
             * Also, default stat changes have been made based on an action, need to be changed
             */

            num_unhealthy_actions += 1;
            weight += 1;
            cholesterol += 1;
            bench_press -= 2;
            deadlift -= 1;
            squat -= 1;
            dbConnector.updateStatsForAction(save_id, weight, cholesterol, bench_press, deadlift, squat);
            dbConnector.addUnhealthyAction(save_id, action_eat_num, action_sleep_num, action_exercise_num, num_unhealthy_actions);
        }
        //enter new time modified for this Manfred instance
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dbConnector.updateCurrentGame(save_id, dateFormat.format(date));

        dbConnector.close();
        try {
            ManfredLog.writeLog(context, action.getEvent(), save_id);
        } catch (Exception e) {
            Log.d(ManfredActivity.TAG, "applyAction: " + e.getMessage());
        }
    }
}
