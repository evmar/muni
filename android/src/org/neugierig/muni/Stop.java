package org.neugierig.muni;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.util.Log;

public class Stop extends Activity {
  private Backend.Stop stop;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.stop);

    String query = getIntent().getExtras().getString(Backend.KEY_QUERY);

    stop = new Backend.Stop("FIXME", query);

    Backend backend = new Backend(this);
    stop.times = backend.fetchStop(stop.url);

    TextView title = (TextView) findViewById(R.id.title);
    title.setText(stop.name);
    TextView subtitle = (TextView) findViewById(R.id.subtitle);
    subtitle.setText("bar");

    ListView list = (ListView) findViewById(R.id.list);
    ListAdapter adapter = new ArrayAdapter<Backend.Stop.Time>(
        this,
        android.R.layout.simple_list_item_1,
        stop.times);
    list.setAdapter(adapter);
  }
}
