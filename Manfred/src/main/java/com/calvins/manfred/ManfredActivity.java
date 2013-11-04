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

    private static final String TAG = "ManfredActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.d("ManfredActivity", "manfredClicked");
    }

    public void logClicked(View view) {
        Log.d("ManfredActivity", "logClicked");
    }
}
