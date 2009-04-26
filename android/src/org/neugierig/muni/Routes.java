package org.neugierig.muni;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class Routes extends ListActivity
                    implements AsyncBackendHelper.Delegate {
  private MuniAPI.Route[] mRoutes;
  private AsyncBackendHelper mBackendHelper;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mBackendHelper = new AsyncBackendHelper(this, this);
    mBackendHelper.start();
  }

  @Override
  public void startAsyncQuery(AsyncBackend backend) {
    backend.fetchRoutes(mBackendHelper);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    return mBackendHelper.onCreateDialog(id);
  }

  @Override
  public void onAsyncResult(Object data) {
    mRoutes = (MuniAPI.Route[]) data;
    ListAdapter adapter = new ArrayAdapter<MuniAPI.Route>(
        this,
        android.R.layout.simple_list_item_1,
        mRoutes);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    MuniAPI.Route route = mRoutes[position];
    Intent intent = new Intent(this, Route.class);
    intent.putExtra(Route.KEY_ROUTE, route.name);
    intent.putExtra(Backend.KEY_QUERY, route.url);
    startActivity(intent);
  }
}
