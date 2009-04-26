package org.neugierig.muni;

import android.app.ListActivity;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import android.view.*;

public class Route extends ListActivity {
  public static final String KEY_ROUTE = "route";
  public static final String KEY_DIRECTION = "direction";

  private String mRoute;
  private Backend.Direction[] directions;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String query = getIntent().getExtras().getString(Backend.KEY_QUERY);
    mRoute = getIntent().getExtras().getString(KEY_ROUTE);

    Backend backend = new Backend(this);
    directions = backend.fetchRoute(query);

    ListAdapter adapter = new ArrayAdapter<Backend.Direction>(
        this,
        android.R.layout.simple_list_item_1,
        directions);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Backend.Direction direction = directions[position];
    Intent intent = new Intent(this, Stops.class);
    intent.putExtra(Route.KEY_ROUTE, mRoute);
    intent.putExtra(Route.KEY_DIRECTION, direction.name);
    intent.putExtra(Backend.KEY_QUERY, direction.url);
    startActivity(intent);
  }
}
