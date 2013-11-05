package com.calvins.manfred;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.util.Log;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;


public class ManfredActivity extends Activity {

    public static final String TAG = "ManfredActivity";
    private int save_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        save_id = (int) intent.getLongExtra("_id", 0);

        try {
            ManfredLog.loadLog(this, save_id);
            // Temporary: testing the log write
            ManfredLog.writeLog(this, "This is a test log\nMoo\nMoo Moo\nMoo Moo Moo\nMoo Moo Moo Moo\nMoo Moo Moo Moo Moo\nMoo Moo Moo Moo Moo Moo\nHarish\nMoo Moo\nMoo", save_id);
        } catch(Exception e) {
            Log.d(TAG, e.toString());
        }

        setContentView(R.layout.activity_manfred);
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
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void eatButtonClicked(View view) {
        Intent intent = new Intent(this, ActionsActivity.class);
        intent.putExtra("category", "eat");
        startActivity(intent);
    }

    public void exerciseButtonClicked(View view) {
        Intent intent = new Intent(this, ActionsActivity.class);
        intent.putExtra("category", "exercise");
        startActivity(intent);
    }

    public void sleepButtonClicked(View view) {
        Intent intent = new Intent(this, ActionsActivity.class);
        intent.putExtra("category", "sleep");
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
