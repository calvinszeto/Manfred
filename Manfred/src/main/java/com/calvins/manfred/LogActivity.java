package com.calvins.manfred;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            // Inflate the fragment
            View rootView = inflater.inflate(R.layout.fragment_log, container, false);

            // Populate the Log ListView
            ArrayList<String> lines = ManfredLog.getLog(10);
            ListView list = (ListView) rootView.findViewById(R.id.log_list);
            FadedTextAdapter adapter = new FadedTextAdapter(getActivity(), lines);
            list.setAdapter(adapter);
            return rootView;
        }

        public class FadedTextAdapter extends ArrayAdapter {
            private final Context context;
            private final ArrayList<String> values;

            public FadedTextAdapter(Context context, ArrayList<String> values) {
                super(context, R.layout.item_log, values);
                this.context = context;
                this.values = values;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Grab the TextView for each line
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rootView = layoutInflater.inflate(R.layout.item_log, parent, false);
                TextView textView = (TextView) rootView.findViewById(R.id.log_wrapper);
                // Set the text accordingly
                textView.setText(values.get(position));
                // Set the color according to how far along the list we are
                // The shades of gray are #x0x0x0 where higher x is a lighter gray, black is #000000
                int normalizedPosition = 15 * position / values.size();
                String color = normalizedPosition == 0 ? "#000000" : "#" + Integer.toHexString(1052688 * normalizedPosition);
                textView.setTextColor(Color.parseColor(color));
                return rootView;
            }
        }
    }


}
