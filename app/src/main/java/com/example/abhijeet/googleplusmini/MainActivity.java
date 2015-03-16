package com.example.abhijeet.googleplusmini;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;



public class MainActivity extends ListActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] items = getResources().getStringArray(R.array.main_activity_items);
        this.setListAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Highly coupled with the order of contents in main_activity_items
        Intent intent = new Intent(this, HelloActivity.class);
        Log.i("item clicked: ", Integer.toString(position));  //just to check which item is clicked
        intent.putExtra(HelloActivity.TYPE_KEY, "background");
        /*if (position == 0) {
            intent.putExtra(HelloActivity.TYPE_KEY, HelloActivity.Type.FOREGROUND.name());
        } else if (position == 1) {

        } else if (position == 2) {
            intent.putExtra(HelloActivity.TYPE_KEY, HelloActivity.Type.BACKGROUND_WITH_SYNC.name());
        }*/
        startActivity(intent);
    }
}
