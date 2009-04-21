package org.neugierig.muni;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.util.Log;

public class Stop extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.stop);
    /*
    Backend.Stop stop = fetchInfo();
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(stop.name);
    TextView subtitle = (TextView) findViewById(R.id.subtitle);
    subtitle.setText(stop.direction);

    ListView list = (ListView) findViewById(R.id.list);
    ListAdapter adapter = new ArrayAdapter<Backend.Stop.Time>(
        this,
        android.R.layout.simple_list_item_1,
        stop.times);
        list.setAdapter(adapter);*/
  }

  Backend.Stop fetchInfo() {
    Backend backend = new Backend();
    // etc.
    return null;
  }

}
