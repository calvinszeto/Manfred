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
        updateActionsLocked(context, save_id);
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
                if (category.equals("Eat")) {
                    eat_actions.add(action);
                } else if (category.equals("Exercise")) {
                    exercise_actions.add(action);
                } else {
                    sleep_actions.add(action);
                }
            }
        }
    }

    /**
     * Calculate the user's locked/unlocked actions
     *
     * @param context
     * @param save_id
     */
    private static void updateActionsLocked(Context context, int save_id) {
        // Database junk
        final DatabaseConnector dbConnector = new DatabaseConnector(context);
        dbConnector.open();
        Cursor currentStats = dbConnector.getStats(save_id);
        currentStats.moveToFirst();

        // current stat values for this Manfred
        int weight = currentStats.getInt(currentStats.getColumnIndex("weight"));
        int vo2_max = currentStats.getInt(currentStats.getColumnIndex("vo2_max"));
        int squat = currentStats.getInt(currentStats.getColumnIndex("squat"));
        int body_fat = currentStats.getInt(currentStats.getColumnIndex("body_fat"));

        dbConnector.close();

        for (int i = 0; i < eat_actions.size(); i++) {
            ActionWrapper action = eat_actions.get(i);
            if (action.meetsRequirements(weight, vo2_max, squat, body_fat)) {
                eat_actions.get(i).setUnlocked(true);
            } else {
                eat_actions.get(i).setUnlocked(false);
            }
        }
        for (int i = 0; i < exercise_actions.size(); i++) {
            ActionWrapper action = exercise_actions.get(i);
            if (action.meetsRequirements(weight, vo2_max, squat, body_fat)) {
                exercise_actions.get(i).setUnlocked(true);
            } else {
                exercise_actions.get(i).setUnlocked(false);
            }
        }
        for (int i = 0; i < sleep_actions.size(); i++) {
            ActionWrapper action = sleep_actions.get(i);
            if (action.meetsRequirements(weight, vo2_max, squat, body_fat)) {
                sleep_actions.get(i).setUnlocked(true);
            } else {
                sleep_actions.get(i).setUnlocked(false);
            }
        }
    }

    /**
     * Get the List of ActionWrappers for the given category
     *
     * @param category
     * @return
     */
    public static ArrayList<ActionWrapper> getActions(String category, int save_id, DatabaseConnector dbConnector) {
        // Get current level
        dbConnector.open();
        int level = dbConnector.getCurrentLevel(save_id);
        dbConnector.close();

        ArrayList<ActionWrapper> actionSet;
        ArrayList<ActionWrapper> returnSet = new ArrayList<ActionWrapper>();
        if (category.equals("eat")) {
            actionSet = eat_actions;
        } else if (category.equals("exercise")) {
            actionSet = exercise_actions;
        } else {
            actionSet = sleep_actions;
        }

        // Remove all actions that shouldn't be seen yet or are on the wrong path
        for(ActionWrapper action: actionSet) {
            if(action.isMajor()) {
                if(level < 5) {
                    if(action.getLevel() == level - 1)
                        returnSet.add(action);
                } else if(level > 5) {
                    if(action.getLevel() == level + 1)
                        returnSet.add(action);
                } else {
                    if(action.getLevel() <= 6 && action.getLevel() >= 4)
                        returnSet.add(action);
                }
            } else {
                if(level < 5) {
                   if(action.getLevel() >= level && action.getLevel() <= 5)
                       returnSet.add(action);
                } else if(level > 5) {
                    if(action.getLevel() <= level && action.getLevel() >= 5)
                        returnSet.add(action);
                } else {
                    if(action.getLevel() == 5)
                        returnSet.add(action);
                }
            }
        }
        return returnSet;
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
        Log.d(ManfredActivity.TAG, "applyAction called.");
        ActionWrapper action = getActions(category, save_id, dbConnector).get(action_id);

        dbConnector.open();
        Cursor currentStats = dbConnector.getStats(save_id);
        currentStats.moveToFirst();

        // current stat values for this Manfred
        int weight = currentStats.getInt(currentStats.getColumnIndex("weight"));
        int vo2_max = currentStats.getInt(currentStats.getColumnIndex("vo2_max"));
        int squat = currentStats.getInt(currentStats.getColumnIndex("squat"));
        int body_fat = currentStats.getInt(currentStats.getColumnIndex("body_fat"));

        int[] stat_changes = action.getStat_changes();

        // Update the stats with the stat_changes
        weight += stat_changes[0];
        vo2_max += stat_changes[1];
        squat += stat_changes[2];
        body_fat += stat_changes[3];
        // Set any stats that are below zero to zero
        weight = weight < 0 ? 0 : weight;
        vo2_max = vo2_max < 0 ? 0 : vo2_max;
        squat = squat < 0 ? 0 : squat;
        body_fat = body_fat < 0 ? 0 : body_fat;

        dbConnector.updateStatsForAction(save_id, weight, vo2_max, squat, body_fat);

        if(action.isMajor()) {
            // Change the level
            dbConnector.updateCurrentLevel(save_id, action.getLevel());
        }

        //enter new time modified for this Manfred instance
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dbConnector.updateCurrentGame(save_id, dateFormat.format(date));
        dbConnector.incrementAction(save_id,category);
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
