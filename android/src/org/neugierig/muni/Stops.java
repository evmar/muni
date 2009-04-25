package org.neugierig.muni;

import android.app.ListActivity;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import android.view.*;

public class Stops extends ListActivity {
  private Backend.Stop[] stops;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String query = getIntent().getExtras().getString(Backend.KEY_QUERY);

    Backend backend = new Backend(this);
    stops = backend.fetchStops(query);

    ListAdapter adapter = new ArrayAdapter<Backend.Stop>(
        this,
        android.R.layout.simple_list_item_1,
        stops);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Backend.Stop stop = stops[position];
    Intent intent = new Intent(this, Stop.class);
    intent.putExtra(Stop.KEY_NAME, stop.name);
    intent.putExtra(Backend.KEY_QUERY, stop.url);
    startActivity(intent);
  }
}
