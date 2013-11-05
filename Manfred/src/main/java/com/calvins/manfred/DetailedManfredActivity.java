package com.calvins.manfred;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class DetailedManfredActivity extends Activity {

    public static final String TAG = "ManfredActivity";

    //id of the current manfred object
    private int _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_manfred);
        Intent intent = getIntent();
        _id = (int) intent.getIntExtra("_id", 0);
        setValuesOnScreen();
    }

    private void setValuesOnScreen() {
        DatabaseConnector dbConnector = new DatabaseConnector(DetailedManfredActivity.this);
        dbConnector.open();

        Cursor statsCursor = dbConnector.getStats(_id);
        statsCursor.moveToFirst();
        Cursor nameCursor = dbConnector.getName(_id);
        nameCursor.moveToFirst();

        String name = nameCursor.getString(nameCursor.getColumnIndex("name"));
        String weight = "Weight: "+statsCursor.getString(statsCursor.getColumnIndex("weight"))+" lb";
        String cholesterol = "Cholesterol: "+statsCursor.getString(statsCursor.getColumnIndex("cholesterol"))+" mg/dL";
        String bench_press = "Bench Press: "+statsCursor.getString(statsCursor.getColumnIndex("bench_press"))+" lb";
        String deadlift = "Deadlift: "+statsCursor.getString(statsCursor.getColumnIndex("deadlift"))+" lb";
        String squat = "Squat: "+statsCursor.getString(statsCursor.getColumnIndex("squat"))+" lb";

        TextView n = (TextView)findViewById(R.id.detail_screen_header);
        TextView w = (TextView)findViewById(R.id.detail_weight);
        TextView c = (TextView)findViewById(R.id.detail_cholesterol);
        TextView b = (TextView)findViewById(R.id.detail_bench);
        TextView d = (TextView)findViewById(R.id.detail_deadlift);
        TextView s = (TextView)findViewById(R.id.detail_squat);

        n.setText(name);
        w.setText(weight);
        c.setText(cholesterol);
        b.setText(bench_press);
        d.setText(deadlift);
        s.setText(squat);

        nameCursor.close();
        statsCursor.close();
        dbConnector.close();
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

    public void manfredClicked(View view) {
       finish();
    }
}

