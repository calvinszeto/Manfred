package com.calvins.manfred;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.content.Intent;
import android.util.Log;

public class ActionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");
        final int save_id = intent.getIntExtra("_id", 0);
        final Activity activity = this;

        GridView gridview = (GridView) findViewById(R.id.actions_grid);
        // The adapter for the actions
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_action, R.id.action_wrapper, Action.getActions(category));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Action.applyAction((int) id, category, save_id, activity);
                finish();
            }
        });
        gridview.setAdapter(adapter);
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
