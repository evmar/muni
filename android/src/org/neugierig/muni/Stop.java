package org.neugierig.muni;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.util.Log;

public class Stop extends Activity {
  // Intent extra data on the stop name.
  public static final String KEY_NAME = "name";

  private Backend.Stop stop;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.stop);

    Bundle extras = getIntent().getExtras();
    stop = new Backend.Stop(extras.getString(KEY_NAME),
                            extras.getString(Backend.KEY_QUERY));

    TextView title = (TextView) findViewById(R.id.title);
    title.setText(stop.name);
    TextView subtitle = (TextView) findViewById(R.id.subtitle);
    subtitle.setText("bar");

    refresh(false);
  }

  private void refresh(boolean force_reload) {
    Backend backend = new Backend(this);
    stop.times = backend.fetchStop(stop.url, force_reload);

    ListView list = (ListView) findViewById(R.id.list);
    ListAdapter adapter = new ArrayAdapter<Backend.Stop.Time>(
        this,
        android.R.layout.simple_list_item_1,
        stop.times);
    list.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stop_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.refresh:
      refresh(true);
      return true;
    }
    return false;
  }
}
