package com.calvins.manfred;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import java.util.ArrayList;

public class ActionsActivity extends Activity {

    public static final String TAG = "ActionsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");
        final int save_id = (int) intent.getIntExtra("_id", 0);
        final Activity activity = this;

        GridView gridview = (GridView) findViewById(R.id.actions_grid);
        // The adapter for the actions
        ActionsAdapter adapter = new ActionsAdapter(this, Action.getActions(category));
        final DatabaseConnector dbConnector = new DatabaseConnector(ActionsActivity.this);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Action.applyAction((int) id, category, save_id, dbConnector, activity);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            // Grab the TextView for each line
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = layoutInflater.inflate(R.layout.item_action, parent, false);
            Button button = (Button) rootView.findViewById(R.id.action_wrapper);
            // Set the text accordingly
            button.setText(values.get(position).toString());
            // Locked actions are gray and unclickable
            if(values.get(position).getUnlocked()) {
                button.setBackgroundColor(getResources().getColor(R.color.Gray));
                // Ironically, we set the button as clickable to make it unclickable
                // The Button click overrides the GridView onItemClickListener
                button.setClickable(true);
            } else {
                button.setBackgroundColor(getResources().getColor(R.color.button_blue));
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
