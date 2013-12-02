package com.calvins.manfred;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.Html;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ActionsActivity extends Activity {

    public static final String TAG = "ActionsActivity";
    public SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        mPrefs = this.getSharedPreferences("com.calvins.manfred", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");
        final int save_id = (int) intent.getIntExtra("_id", 0);
        final Activity activity = this;

        GridView gridview = (GridView) findViewById(R.id.actions_grid);
        final DatabaseConnector dbConnector = new DatabaseConnector(ActionsActivity.this);
        // The adapter for the actions
        ActionsAdapter adapter = new ActionsAdapter(this, Action.getActions(category, save_id, dbConnector));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Action.applyAction((int) id, category, save_id, dbConnector, activity);
                if (category.equals("eat"))
                    mPrefs.edit().putInt("eat_delay", 1).commit();
                else if (category.equals("sleep"))
                    mPrefs.edit().putInt("sleep_delay", 1).commit();
                else
                    mPrefs.edit().putInt("exercise_delay", 1).commit();
                finish();
            }
        });
        gridview.setAdapter(adapter);
    }

    public class ActionsAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<ActionWrapper> values;

        public ActionsAdapter(Context context, ArrayList<ActionWrapper> values) {
            super(context, R.layout.item_action, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public boolean isEnabled(int position) {
            return values.get(position).isUnlocked();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Grab the TextView for each line
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = layoutInflater.inflate(R.layout.item_action, parent, false);
            TextView name = (TextView) rootView.findViewById(R.id.action_name);
            // Set the text accordingly
            name.setText(values.get(position).toString());
            // Locked actions are gray and unclickable
            if (values.get(position).isUnlocked()) {
                rootView.setBackgroundColor(getResources().getColor(R.color.button_blue));
            } else {
                rootView.setBackgroundColor(getResources().getColor(R.color.Gray));
                int[] stat_requirements = values.get(position).getStat_requirements();
                // Add the stat requirements to the button
                TextView weight = (TextView) rootView.findViewById(R.id.required_weight);
                TextView vo2_max = (TextView) rootView.findViewById(R.id.required_vo2_max);
                TextView squat = (TextView) rootView.findViewById(R.id.required_squat);
                TextView body_fat = (TextView) rootView.findViewById(R.id.required_body_fat);

                weight.setText("Weight: " + (stat_requirements[0] == 0 ? "*" : stat_requirements[0]));
                vo2_max.setText("VO2-Max: " + (stat_requirements[1] == 0 ? "*" : stat_requirements[1]));
                squat.setText("Squat: " + (stat_requirements[2] == 0 ? "*" : stat_requirements[2]));
                body_fat.setText("Body Fat %: " + (stat_requirements[3] == 0 ? "*" : stat_requirements[3]));
            }
            return rootView;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions, menu);
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
     * The view for selecting action categories
     */
    public static class ActionCategoryFragment extends Fragment {

        public ActionCategoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_action_category, container, false);
            return rootView;
        }
    }
}
