package com.calvins.manfred;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.util.Log;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;


public class ManfredActivity extends Activity {

    public static final String TAG = "ManfredActivity";
    private int save_id;

    // variables that deal with sound
    private SoundPool mSounds;
    private int mEatSoundID;
    private int mSleepSoundID;
    private int mExerciseSoundID;

    // variables to deal with instructional popup
    private PopupWindow instruct_popup;
    private PopupWindow dim;

    // variables to deal with storage
    private SharedPreferences mPrefs;
    private DatabaseConnector dbConnector;

    // variables that deal with action delay
    private MyCountDownTimer eatDelayTimer;
    private MyCountDownTimer sleepDelayTimer;
    private MyCountDownTimer exerciseDelayTimer;
    private int eat_delay;
    private int sleep_delay;
    private int exercise_delay;
    private Button eat;
    private Button sleep;
    private Button exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manfred);

        dbConnector = new DatabaseConnector(ManfredActivity.this);
        mPrefs = this.getSharedPreferences("com.calvins.manfred", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        save_id = ((Long) intent.getLongExtra("_id", 0)).intValue();
        Log.d(ManfredActivity.TAG, "ManfredActivity - save_id is " + save_id);

        try {
            // Load the list of actions into memory
            Action.loadActions(this, save_id);
            ManfredLog.loadLog(this, save_id);
            /*
            // Temporary: testing the log write
            ManfredLog.writeLog(this, "This is a test log\nMoo\nMoo Moo\nMoo Moo Moo\nMoo Moo Moo Moo\nMoo Moo Moo Moo Moo\nMoo Moo Moo Moo Moo Moo\nHarish\nMoo Moo\nMoo", save_id);
            */
        } catch (IOException e) {
            Log.d(TAG, "ManfredActivity: " + e.toString());
        } catch (XmlPullParserException e) {
            Log.d(TAG, "ManfredActivity: " + e.toString());
        }

        eatDelayTimer = new MyCountDownTimer(3000,1000);
        sleepDelayTimer = new MyCountDownTimer(3000,1000);
        exerciseDelayTimer = new MyCountDownTimer(3000,1000);

        eat = (Button)findViewById(R.id.eat_button);
        sleep = (Button)findViewById(R.id.sleep_button);
        exercise = (Button)findViewById(R.id.exercise_button);

        // instructional overlay code
        int overlay_instructions = intent.getIntExtra("put_instructions",0);
        if(overlay_instructions==1) {
            findViewById(R.id.container).post(new Runnable() {
                public void run() {
                    dim = dimBackground();
                    initiateInstructionWindow();
                }
            });
        }
    }

    private PopupWindow dimBackground() {
        LayoutInflater inflater = (LayoutInflater) ManfredActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fadepopup,
                (ViewGroup) findViewById(R.id.fadePopup));
        PopupWindow fadePopup = new PopupWindow(layout, 0, 0, false);
        fadePopup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        fadePopup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        fadePopup.showAtLocation(layout, Gravity.NO_GRAVITY, 0, 0);
        return fadePopup;
    }

    private void initiateInstructionWindow() {
        try{
            LayoutInflater inflater = (LayoutInflater)ManfredActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.instruction_overlay,null);

            instruct_popup = new PopupWindow(layout,0,0,true);
            instruct_popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            instruct_popup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            instruct_popup.showAtLocation(this.findViewById(R.id.container), Gravity.CENTER,0,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        long millisUntilEatDone = eatDelayTimer.getMillisUntilDone();
        long millisUntilSleepDone = sleepDelayTimer.getMillisUntilDone();
        long millisUntilExerciseDone = exerciseDelayTimer.getMillisUntilDone();

        eatDelayTimer.cancel();
        sleepDelayTimer.cancel();
        exerciseDelayTimer.cancel();

        mPrefs.edit().putLong("millisUntilEatDone", millisUntilEatDone).commit();
        mPrefs.edit().putLong("millisUntilSleepDone", millisUntilSleepDone).commit();
        mPrefs.edit().putLong("millisUntilExerciseDone", millisUntilExerciseDone).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(dim!=null)
            dim.dismiss();
        if(instruct_popup!=null)
            instruct_popup.dismiss();

        // Create sound
        mSounds = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        createSound();

        eat_delay = mPrefs.getInt("eat_delay",0);
        sleep_delay = mPrefs.getInt("sleep_delay",0);
        exercise_delay = mPrefs.getInt("exercise_delay",0);
        // Set Manfred image
        DatabaseConnector dbc = new DatabaseConnector(this);
        dbc.open();
        int level = dbc.getCurrentLevel(save_id);
        ImageView imageView = (ImageView) findViewById(R.id.manfred_image);
        imageView.setImageResource(getResources().getIdentifier("manfred" + level, "drawable", getPackageName()));
        dbc.close();

        // Grab all saved delays
        eat_delay = mPrefs.getInt("eat_delay", 0);
        sleep_delay = mPrefs.getInt("sleep_delay", 0);
        exercise_delay = mPrefs.getInt("exercise_delay", 0);
        boolean easy_delays = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_delays", false);

        // Get action totals from database
        dbConnector.open();
        Cursor cursor = dbConnector.getTotalNumActions(save_id);
        cursor.moveToFirst();
        int eat_total = cursor.getInt(cursor.getColumnIndex("num_eat_total"));
        int sleep_total = cursor.getInt(cursor.getColumnIndex("num_sleep_total"));
        int exercise_total = cursor.getInt(cursor.getColumnIndex("num_exercise_total"));
        dbConnector.close();

        if(eat_delay==1) {
            Button button_to_delay = eat;
            button_to_delay.setEnabled(false);
            button_to_delay.setBackgroundColor(getResources().getColor(R.color.DarkGray));
            long millisUntilDone;
            if (easy_delays) {
                millisUntilDone = 0;
            } else {
                millisUntilDone = mPrefs.getLong("millisUntilEatDone", 3000);
                if (millisUntilDone == 0) {
                    int den = sleep_total + exercise_total;
                    if (eat_total == 0)
                        millisUntilDone = 3000;
                    else if (den == 0)
                        millisUntilDone = (long) Math.ceil((double) 2 * eat_total) * 3000;
                    else
                        millisUntilDone = (long) Math.ceil((double) 2 * eat_total / den) * 3000;
                }
            }
            eatDelayTimer = new MyCountDownTimer(millisUntilDone, 1000);
            eatDelayTimer.setButton(button_to_delay);
            eatDelayTimer.start();
        }
        if (sleep_delay == 1) {
            Button button_to_delay = sleep;
            button_to_delay.setEnabled(false);
            button_to_delay.setBackgroundColor(getResources().getColor(R.color.DarkGray));
            long millisUntilDone;
            if (easy_delays) {
                millisUntilDone = 0;
            } else {
                millisUntilDone = mPrefs.getLong("millisUntilEatDone", 3000);
                if (millisUntilDone == 0) {
                    int den = eat_total + exercise_total;
                    if (sleep_total == 0)
                        millisUntilDone = 3000;
                    else if (den == 0)
                        millisUntilDone = (long) Math.ceil((double) 2 * sleep_total) * 3000;
                    else
                        millisUntilDone = (long) Math.ceil((double) 2 * sleep_total / den) * 3000;
                }
            }
            sleepDelayTimer = new MyCountDownTimer(millisUntilDone, 1000);
            sleepDelayTimer.setButton(button_to_delay);
            sleepDelayTimer.start();
        }
        if (exercise_delay == 1) {
            Log.d(ManfredActivity.TAG, "Exercise Delay Received.");
            Button button_to_delay = exercise;
            button_to_delay.setEnabled(false);
            button_to_delay.setBackgroundColor(getResources().getColor(R.color.DarkGray));
            long millisUntilDone;
            if (easy_delays) {
                millisUntilDone = 0;
            } else {
                millisUntilDone = mPrefs.getLong("millisUntilEatDone", 3000);
                if (millisUntilDone == 0) {
                    int den = eat_total + sleep_total;
                    if (exercise_total == 0)
                        millisUntilDone = 3000;
                    else if (den == 0)
                        millisUntilDone = (long) Math.ceil((double) 2 * exercise_total) * 3000;
                    else
                        millisUntilDone = (long) Math.ceil((double) 2 * exercise_total / den) * 3000;
                    Log.d(ManfredActivity.TAG, "Exercise delay is 3000*2*" + exercise_total + "/" + den + " = " + millisUntilDone);
                }
            }
            exerciseDelayTimer = new MyCountDownTimer(millisUntilDone, 1000);
            exerciseDelayTimer.setButton(button_to_delay);
            exerciseDelayTimer.start();
        }
    }

    public void createSound() {
        int sound = mPrefs.getInt("action_sound", 0);
        if(sound==1) {
            mEatSoundID = mSounds.load(ManfredActivity.this, R.raw.eat_sound, 2);
            mSounds.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    mSounds.play(mEatSoundID, 1, 1, 1, 0, 1);
                }
            });
        }
        else if(sound==2) {
            mSleepSoundID = mSounds.load(ManfredActivity.this, R.raw.sleep_sound, 2);
            mSounds.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    mSounds.play(mSleepSoundID, 1, 1, 1, 0, 1);
                }
            });
        }
        else if(sound==3){
            mExerciseSoundID = mSounds.load(ManfredActivity.this, R.raw.exercise_sound, 2);
            mSounds.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    mSounds.play(mExerciseSoundID, 1, 1, 1, 0, 1);
                }
            });
        }
        mPrefs.edit().putInt("action_sound", 0).commit();
    }

    public class MyCountDownTimer extends CountDownTimer {
        private Button button_to_delay;
        private long millisUntilDone;

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            millisUntilDone = millisInFuture;
        }

        public void setButton(Button b) {
            button_to_delay = b;
        }

        public long getMillisUntilDone() {
            return millisUntilDone;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // display the time left in the actions buttons
            millisUntilDone = millisUntilFinished;
            button_to_delay.setText("" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            // renable buttons and their text
            button_to_delay.setEnabled(true);
            if (button_to_delay == eat) {
                button_to_delay.setText("Eat");
                mPrefs.edit().putInt("eat_delay", 0).commit();
            } else if (button_to_delay == sleep) {
                button_to_delay.setText(R.string.sleep_button);
                mPrefs.edit().putInt("sleep_delay", 0).commit();
            } else {
                button_to_delay.setText(R.string.exercise_button);
                mPrefs.edit().putInt("exercise_delay", 0).commit();
            }
            button_to_delay.setBackgroundColor(getResources().getColor(R.color.button_blue));
            millisUntilDone = 0;
        }
    }

    /**
     * The fragment for displaying the image of Manfred
     */
    public static class ManfredFragment extends Fragment {

        public ManfredFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_manfred, container, false);
            return rootView;
        }
    }

    public void finishInstruction(View view) {
        instruct_popup.dismiss();
        dim.dismiss();
    }

    public void eatButtonClicked(View view) {
        Intent intent = new Intent(this, ActionsActivity.class);
        intent.putExtra("category", "eat");
        intent.putExtra("_id", save_id);
        startActivity(intent);
    }

    public void exerciseButtonClicked(View view) {
        Intent intent = new Intent(this, ActionsActivity.class);
        intent.putExtra("category", "exercise");
        intent.putExtra("_id", save_id);
        startActivity(intent);
    }

    public void sleepButtonClicked(View view) {
        Intent intent = new Intent(this, ActionsActivity.class);
        intent.putExtra("category", "sleep");
        intent.putExtra("_id", save_id);
        startActivity(intent);
    }

    public void manfredClicked(View view) {
        Intent intent = new Intent(this, DetailedManfredActivity.class);
        intent.putExtra("_id", save_id);
        startActivity(intent);
    }

    public void logClicked(View view) {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }
}