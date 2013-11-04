package com.calvins.manfred;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SavesScreenActivity extends ListActivity {

    private static final String TAG = "SavesScreenActivity";
    private ListView manfredListView;
    private CursorAdapter manfredAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves_screen);

        DatabaseConnector databaseConnector = new DatabaseConnector(SavesScreenActivity.this);
        databaseConnector.insertNewGame("","","manfred1");

        manfredListView = (ListView) findViewById(android.R.id.list);
        manfredListView.setOnItemClickListener(viewManfredListener);

        //map each manfred instance name and date modified to a TextView in the ListView layout
        String[] from = new String[] {"name"};
        int[] to = new int[] {R.id.firstLine};
        manfredAdapter = new SimpleCursorAdapter(
            SavesScreenActivity.this,
            R.layout.manfred_list_item, null,
                from, to);
        setListAdapter(manfredAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetManfredsTask().execute((Object[]) null);
    }

    @Override
    protected void onStop() {
        Cursor cursor = manfredAdapter.getCursor();
        if(cursor != null)
            cursor.deactivate();

        manfredAdapter.changeCursor(null);
        super.onStop();
    }

    //performs database query outside GUI thread
    private class GetManfredsTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(SavesScreenActivity.this);

        //database access
        @Override
        protected Cursor doInBackground(Object... params) {
            databaseConnector.open();
            return databaseConnector.getAllGames();
        }

        //use the cursor returned from the doInBackground method
        @Override
        protected void onPostExecute(Cursor result) {
            manfredAdapter.changeCursor(result);
            databaseConnector.close();
        }
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

    //listener that responds to the user touching a manfred instance in the ListView
    OnItemClickListener viewManfredListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Manfred instance clicked on Saves Screen with id = "+id);

            //create intent to main manfred screen
            Intent startManfredGame = new Intent(SavesScreenActivity.this, ManfredActivity.class);
            //pass the id of the manfred game with the intent
            startManfredGame.putExtra("row_id", id);
            startActivity(startManfredGame);
        }
    };

    /**
     * On click responder to when new game button is clicked.
     * @param view
     */
    public void newGameClicked(View view)
    {
        Log.d(TAG,"New game button clicked.");
        //Respond to the new game click
        Intent intent = new Intent(this, ManfredActivity.class);
        startActivity(intent);
    }
}
