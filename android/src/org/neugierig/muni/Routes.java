package org.neugierig.muni;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class Routes extends ListActivity {
  private Backend.Route[] routes;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Backend backend = new Backend(this);
    routes = backend.fetchRoutes();

    ListAdapter adapter = new ArrayAdapter<Backend.Route>(
        this,
        android.R.layout.simple_list_item_1,
        routes);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Backend.Route route = routes[position];
    Intent intent = new Intent(this, Route.class);
    intent.putExtra(Route.KEY_ROUTE, route.name);
    intent.putExtra(Backend.KEY_QUERY, route.url);
    startActivity(intent);
  }
}
