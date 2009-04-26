package org.neugierig.muni;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

public class Route extends ListActivity implements AsyncBackendHelper.Delegate {
  public static final String KEY_ROUTE = "route";
  public static final String KEY_DIRECTION = "direction";

  private String mRoute;
  private String mQuery;
  private MuniAPI.Direction[] mDirections;
  private AsyncBackendHelper mBackendHelper;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mQuery = getIntent().getExtras().getString(Backend.KEY_QUERY);
    mRoute = getIntent().getExtras().getString(KEY_ROUTE);

    mBackendHelper = new AsyncBackendHelper(this, this);
    mBackendHelper.start();
  }

  @Override
  public void startAsyncQuery(AsyncBackend backend) {
    backend.fetchRoute(mQuery, mBackendHelper);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    return mBackendHelper.onCreateDialog(id);
  }

  @Override
  public void onAsyncResult(Object data) {
    mDirections = (MuniAPI.Direction[]) data;
    ListAdapter adapter = new ArrayAdapter<MuniAPI.Direction>(
        this,
        android.R.layout.simple_list_item_1,
        mDirections);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    MuniAPI.Direction direction = mDirections[position];
    Intent intent = new Intent(this, Stops.class);
    intent.putExtra(Route.KEY_ROUTE, mRoute);
    intent.putExtra(Route.KEY_DIRECTION, direction.name);
    intent.putExtra(Backend.KEY_QUERY, direction.url);
    startActivity(intent);
  }
}
