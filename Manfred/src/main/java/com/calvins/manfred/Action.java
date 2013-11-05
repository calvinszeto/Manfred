package com.calvins.manfred;

import android.util.Xml;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;

import android.content.Context;

/**
 * Created by puppyplus on 11/3/13.
 */
public class Action {

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

    public static void applyAction(int action_id, String category, int save_id, Context context) {
        ActionWrapper action = getActions(category).get(action_id);
        if(action.getPath() == "healthy") {
            // TODO: Apply healthy changes

        } else {
            // TODO: Apply unhealthy changes

        }
        // TODO: Add event to log
        try {
            Log.d(ManfredActivity.TAG, action.getEvent());
            ManfredLog.writeLog(context, action.getEvent(), save_id);
        } catch (Exception e) {
            Log.d(ManfredActivity.TAG, e.getMessage());
        }
    }
}
