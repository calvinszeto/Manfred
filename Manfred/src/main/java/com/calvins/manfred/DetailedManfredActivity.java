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
import android.widget.ImageView;


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

        // Set Manfred image
        DatabaseConnector dbc = new DatabaseConnector(this);
        dbc.open();
        int level = dbc.getCurrentLevel(_id);
        ImageView imageView = (ImageView) findViewById(R.id.manfred_image);
        imageView.setImageResource(getResources().getIdentifier("manfred" + level, "drawable", getPackageName()));

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
        String vo2_max = "VO2-Max: "+statsCursor.getString(statsCursor.getColumnIndex("vo2_max"))+" ml/kg/min";
        String squat = "Squat: "+statsCursor.getString(statsCursor.getColumnIndex("squat"))+" lb";
        String body_fat = "Body Fat Percentage: "+statsCursor.getString(statsCursor.getColumnIndex("body_fat"))+" %";

        TextView n = (TextView)findViewById(R.id.detail_screen_header);
        TextView w = (TextView)findViewById(R.id.detail_weight);
        TextView v = (TextView)findViewById(R.id.detail_vo2_max);
        TextView s = (TextView)findViewById(R.id.detail_squat);
        TextView b = (TextView)findViewById(R.id.detail_body_fat);

        n.setText(name);
        w.setText(weight);
        v.setText(vo2_max);
        s.setText(squat);
        b.setText(body_fat);

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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void manfredClicked(View view) {
       finish();
    }
}

