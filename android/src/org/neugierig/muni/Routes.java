package org.neugierig.muni;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class Routes extends ListActivity
                    implements AsyncBackendHelper.Delegate {
  private MuniAPI.Route[] mRoutes;
  private SplitListAdapter mSplitListAdapter;
  private AsyncBackendHelper mBackendHelper;
  private StarDBAdapter mStarDB;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    super.onCreate(savedInstanceState);

    mStarDB = new StarDBAdapter(this);

    mSplitListAdapter = new SplitListAdapter(this, "All Routes");
    fillStarred();
    setListAdapter(mSplitListAdapter);

    mBackendHelper = new AsyncBackendHelper(this, this);
    mBackendHelper.start();
  }

  private void fillStarred() {
    Cursor cursor = mStarDB.fetchAll();
    startManagingCursor(cursor);

    String[] from = new String[]{"query"};
    int[] to = new int[]{android.R.id.text1};
    SimpleCursorAdapter notes = new SimpleCursorAdapter(
        this,
        android.R.layout.simple_list_item_1,
        cursor, from, to);
    mSplitListAdapter.setAdapter1(notes);
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
