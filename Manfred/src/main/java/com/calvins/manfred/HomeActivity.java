package com.calvins.manfred;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.content.Intent;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

    /**
     * On click responder to when play button is clicked.
     * @param view
     */
    public void playButtonClicked(View view)
    {
        Log.d(TAG,"Play button clicked.");
        //Response to button click
        Intent intent = new Intent(this, SavesScreenActivity.class);
        startActivity(intent);
    }

    /**
     * On click responder to when settings button is clicked.
     * @param view
     */
    public void settingsButtonClicked(View view)
    {
        Log.d(TAG,"Settings button clicked.");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
