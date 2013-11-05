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
    private static int save_id;

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
            save_id = save_id;
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
            fos.close();
        }
    }

    public static ArrayList<String> getLog(int lines) {
        lock.lock();
        try {
            if (log.size() < lines) {
                return new ArrayList<String>(log);
            } else {
                return new ArrayList<String>(log.subList(0, lines));
            }
        } finally {
            lock.unlock();
        }
    }

    public static void writeLog(Context context, String lines) throws FileNotFoundException, IOException {
        lock.lock();
        try {
            // Write the lines to the ArrayList
            for (String line : lines.split("\n")) {
                log.add(line);
            }
        } finally {
            lock.unlock();
        }
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
