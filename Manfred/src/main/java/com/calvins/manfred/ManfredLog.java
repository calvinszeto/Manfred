package com.calvins.manfred;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.util.Log;

/**
 * Created by puppyplus on 11/4/13.
 */
public class ManfredLog {

    private static String FILE_PREFIX = "log";
    private static ArrayList<String> log;
    // Make sure the log is only being modified or read by one thread
    private final static Lock lock = new ReentrantLock();

    /**
     * Loads lines of the log file into an ArrayList of Strings
     *
     * @param context
     * @param save_id the ID of the current save file being played
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void loadLog(Context context, int save_id) throws IOException, FileNotFoundException {
        try {
            // Try opening the log file.
            FileInputStream fis = context.openFileInput(FILE_PREFIX + save_id);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String line;
            lock.lock();
            try{
                log = new ArrayList<String>();
                while ((line = in.readLine()) != null) {
                    log.add(line);
                }
            } finally {
                lock.unlock();
            }
            in.close();
            fis.close();
        } catch (FileNotFoundException e) {
            // If log file is gone, make a new one. Throws a FileNotFoundException
            // if the file is not creatable.
            FileOutputStream fos = context.openFileOutput(FILE_PREFIX + save_id, Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            out.write("Manfred wakes up.");
            out.close();
            fos.close();
        }
    }

    public static ArrayList<String> getLog(Context context, int lines, int save_id) {
        if(log == null) {
            try {
                loadLog(context, save_id);
            } catch (Exception e) {
                Log.d(ManfredActivity.TAG, e.getMessage());
            }
        }
        ArrayList<String> clone;
        lock.lock();
        try {
           if (log.size() < lines) {
                clone = new ArrayList<String>(log);
            } else {
                clone = new ArrayList<String>(log.subList(log.size() - lines, log.size()));
            }
        } finally {
            lock.unlock();
        }
        Collections.reverse(clone);
        return clone;
    }

    public static int logSize() {
        return log.size();
    }

    public static void writeLog(Context context, String lines, int save_id) throws FileNotFoundException, IOException {
        if(log == null) {
            loadLog(context, save_id);
        }
        lock.lock();
        try {
            // Write the lines to the ArrayList
            for (String line : lines.split("\n")) {
                log.add(line);
            }
        } finally {
            lock.unlock();
        }
        Log.d(ManfredActivity.TAG, log.get(log.size()-1));
        // TODO: Make this asynchronous?
        // Write the lines to the log file
        FileOutputStream fos = context.openFileOutput(FILE_PREFIX + save_id, Context.MODE_APPEND);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
        for (String line : lines.split("\n")) {
            out.write(line);
            out.newLine();
        }
        out.close();
        fos.close();
    }

}
