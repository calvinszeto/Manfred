package com.calvins.manfred;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;

public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        try {
            // Temporary: testing the log write
            this.deleteFile("log1");
            ManfredLog.writeLog(this, "This is a test log\nMoo\nMoo Moo\nMoo Moo Moo", 1);
        } catch(Exception e) {
            Log.d(ManfredActivity.TAG, e.getMessage());
        }
        */

        setContentView(R.layout.activity_log);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container, new LogFragment());
            fragmentTransaction.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log, menu);
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
     * The fragment for displaying the log/log history.
     */
    public static class LogFragment extends Fragment {

        public LogFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_log, container, false);
            String text = "";
            TextView log_text = (TextView) rootView.findViewById(R.id.log_text);
            for(String line:ManfredLog.getLog(3)) {
               text = text.concat(line + "\n");
            }
            log_text.setText(text);
            return rootView;
        }
    }

}
