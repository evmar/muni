package org.neugierig;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ListAdapter;
import android.widget.ArrayAdapter;

public class muni extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop);

        ListView list = (ListView) findViewById(R.id.list);
        String[] entries = { "foo", "bar", "x", "x", "y", "z", "g" };
        ListAdapter adapter = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            entries);
        list.setAdapter(adapter);
    }
}
