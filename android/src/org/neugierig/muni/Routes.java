package org.neugierig.muni;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class Routes extends ListActivity
                    implements AsyncBackendHelper.Delegate {
  private MuniAPI.Route[] mRoutes;
  private SplitListAdapter mSplitListAdapter;
  private AsyncBackendHelper mBackendHelper;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ListAdapter foo = new ArrayAdapter<String>(
        this,
        android.R.layout.simple_list_item_1,
        new String[] { "(Bookmarks will", "go here)" });
    mSplitListAdapter = new SplitListAdapter(this, "All Routes");
    mSplitListAdapter.setAdapter1(foo);
    setListAdapter(mSplitListAdapter);

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
    mSplitListAdapter.setAdapter2(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    if (mSplitListAdapter.isInList1(position)) {
      // TODO: implement me.
    } else {
      MuniAPI.Route route =
          mRoutes[mSplitListAdapter.translateList2Position(position)];
      Intent intent = new Intent(this, Route.class);
      intent.putExtra(Route.KEY_ROUTE, route.name);
      intent.putExtra(Backend.KEY_QUERY, route.url);
      startActivity(intent);
    }
  }
}
