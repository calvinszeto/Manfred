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
    public static void loadActions(Context context) throws XmlPullParserException, IOException {
        if(eat_actions.size() == 0 ||
                exercise_actions.size() == 0 ||
                sleep_actions.size() == 0) {
            InputStream in = context.getAssets().open("actions.xml");
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(in, null);
                parser.nextTag();
                parseActions(parser);
            } finally {
                in.close();
            }
        }
    }

    private static void parseActions(XmlPullParser parser) throws XmlPullParserException, IOException {
        while(parser.next() != XmlPullParser.END_DOCUMENT) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("action")) {
               String category = parser.getAttributeValue(null, "category");
               String path = parser.getAttributeValue(null, "path");
               String event = parser.getAttributeValue(null, "event");
               if(category.equals("eat")) {
                   eat_actions.add(new ActionWrapper(parser.getAttributeValue(null, "name"),
                           category, path, event));
               }else if(category.equals("exercise")) {
                   exercise_actions.add(new ActionWrapper(parser.getAttributeValue(null, "name"),
                           category, path, event));
               }else {
                   sleep_actions.add(new ActionWrapper(parser.getAttributeValue(null, "name"),
                           category, path, event));
               }
            }
        }
    }

    public static ArrayList<ActionWrapper> getActions(String category) {
        if(category.equals("eat")) {
            return eat_actions;
        }else if(category.equals("exercise")) {
            return exercise_actions;
        }else {
            return sleep_actions;
        }
    }

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

        if(action.getPath().equals("healthy")) {
            // TODO: See comment below

            /**
             * Add healthy calculations here to determine action_eat_num, action_sleep_num or action_exercise_num
             * Also, default stat changes have been made based on an action, need to be changed
             */

            num_healthy_actions+=1;
            weight-=1;
            cholesterol-=1;
            bench_press+=2;
            deadlift+=1;
            squat+=1;
            dbConnector.updateStatsForAction(save_id,weight,cholesterol,bench_press,deadlift,squat);
            dbConnector.addHealthyAction(save_id,action_eat_num,action_sleep_num,action_exercise_num,num_healthy_actions);
            Log.d(TAG, "num_healthy_actions = "+num_healthy_actions+", id for Manfred = "+save_id);
        } else {
            // TODO: See comment below

            /**
             * Add unhealthy calculations here to determine action_eat_num, action_sleep_num or action_exercise_num
             * Also, default stat changes have been made based on an action, need to be changed
             */

            num_unhealthy_actions+=1;
            weight+=1;
            cholesterol+=1;
            bench_press-=2;
            deadlift-=1;
            squat-=1;
            dbConnector.updateStatsForAction(save_id,weight,cholesterol,bench_press,deadlift,squat);
            dbConnector.addUnhealthyAction(save_id, action_eat_num,action_sleep_num,action_exercise_num,num_unhealthy_actions);
        }
        //enter new time modified for this Manfred instance
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dbConnector.updateCurrentGame(save_id,dateFormat.format(date));

        dbConnector.close();
        // TODO: Add event to log
        try {
            ManfredLog.writeLog(context, action.getEvent(), save_id);
        } catch (Exception e) {
            Log.d(ManfredActivity.TAG, e.getMessage());
        }
    }
}
