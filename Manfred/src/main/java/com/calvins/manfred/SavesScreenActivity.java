package com.calvins.manfred;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SavesScreenActivity extends ListActivity {

    private static final String TAG = "SavesScreenActivity";
    private ListView manfredListView;
    private CursorAdapter manfredAdapter;

    RelativeLayout relCreatePopup;
    Button btnClosePopup;
    Button btnPlayManfred;
    private PopupWindow popupWindow;
    private PopupWindow dim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves_screen);

        manfredListView = (ListView) findViewById(android.R.id.list);
        manfredListView.setOnItemClickListener(viewManfredListener);

        //map each manfred instance name and date modified to a TextView in the ListView layout
        String[] from = new String[] {"name", "dateModified"};
        int[] to = new int[] {R.id.firstLine, R.id.secondLine};
        manfredAdapter = new SimpleCursorAdapter(
            SavesScreenActivity.this,
            R.layout.manfred_list_item, null,
                from, to);
        setListAdapter(manfredAdapter);

        //popup related stuff
        relCreatePopup = (RelativeLayout)findViewById(R.id.new_game);
        relCreatePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dim = dimBackground();
                initiatePopupWindow();
            }
        });
    }

    private PopupWindow dimBackground() {
        LayoutInflater inflater = (LayoutInflater) SavesScreenActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.fadepopup,
                (ViewGroup) findViewById(R.id.fadePopup));
        PopupWindow fadePopup = new PopupWindow(layout, 0, 0, false);
        fadePopup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        fadePopup.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        fadePopup.showAtLocation(layout, Gravity.NO_GRAVITY, 0, 0);
        return fadePopup;
    }

    private void initiatePopupWindow() {
        try{
            LayoutInflater inflater = (LayoutInflater)SavesScreenActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_new_game,null);

            popupWindow = new PopupWindow(layout,0,0,true);
            popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.showAtLocation(this.findViewById(R.id.saves_screen), Gravity.CENTER,0,0);

            btnClosePopup = (Button)layout.findViewById(R.id.popup_back);
            btnClosePopup.setOnClickListener(back_button_click_listener);
            btnPlayManfred = (Button)layout.findViewById(R.id.popup_play);
            btnPlayManfred.setOnClickListener(play_button_click_listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(popupWindow!=null)
            popupWindow.dismiss();
        if(dim!=null)
            dim.dismiss();
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //listener that responds to the user touching a manfred instance in the ListView
    OnItemClickListener viewManfredListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long _id) {
            Log.d(TAG, "Manfred instance clicked on Saves Screen with _id = "+_id);

            //create intent to main manfred screen
            Intent startManfredGame = new Intent(SavesScreenActivity.this, ManfredActivity.class);
            //pass the id of the manfred game with the intent
            startManfredGame.putExtra("_id", _id);
            startActivity(startManfredGame);
        }
    };

    //listener that responds to back button press on popup window
    private OnClickListener back_button_click_listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            dim.dismiss();
            popupWindow.dismiss();
        }
    };

    //listener that responds to play button press on popup window
    private OnClickListener play_button_click_listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //inputs the new manfred instance into the database
            View popupView = popupWindow.getContentView();
            EditText editText = (EditText) popupView.findViewById(R.id.edit_message);
            String name = editText.getText().toString();
            if(name!=null&&!name.equals(""))
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                DatabaseConnector dbConnector = new DatabaseConnector(SavesScreenActivity.this);
                long _id = dbConnector.insertNewGame(dateFormat.format(date), name);

                Log.d(TAG,"Created new Manfred instance with _id = " + _id);
                //now start the manfred activity with this new instance
                Intent startManfredGame = new Intent(SavesScreenActivity.this, ManfredActivity.class);
                startManfredGame.putExtra("_id",_id);
                startActivity(startManfredGame);
            }
        }
    };
}
