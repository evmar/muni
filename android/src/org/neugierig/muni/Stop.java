package org.neugierig.muni;

import android.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.util.Log;

public class Stop extends Activity implements AsyncBackendHelper.Delegate {
  // Intent extra data on the stop name.
  public static final String KEY_NAME = "name";

  private MuniAPI.Stop mStop;
  private AsyncBackendHelper mBackendHelper;
  // Whether to force going out to the network for a query.
  private boolean mRefresh = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.stop);

    Bundle extras = getIntent().getExtras();
    String route = extras.getString(Route.KEY_ROUTE);
    String direction = extras.getString(Route.KEY_DIRECTION);
    mStop = new MuniAPI.Stop(extras.getString(KEY_NAME),
                             extras.getString(Backend.KEY_QUERY));

    TextView title = (TextView) findViewById(R.id.title);
    title.setText(mStop.name);
    TextView subtitle = (TextView) findViewById(R.id.subtitle);
    subtitle.setText(route + "\n" + direction);

    mBackendHelper = new AsyncBackendHelper(this, this);
    mBackendHelper.start();
  }

  @Override
  public void startAsyncQuery(AsyncBackend backend) {
    backend.fetchStop(mStop.url, mRefresh, mBackendHelper);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    return mBackendHelper.onCreateDialog(id);
  }

  @Override
  public void onAsyncResult(Object data) {
    mStop.times = (MuniAPI.Stop.Time[]) data;

    ListView list = (ListView) findViewById(R.id.list);
    ListAdapter adapter;
    if (mStop.times.length > 0) {
      adapter = new ArrayAdapter<MuniAPI.Stop.Time>(
          this,
          android.R.layout.simple_list_item_1,
          mStop.times);
    } else {
      adapter = new ArrayAdapter<String>(
          this,
          android.R.layout.simple_list_item_1,
          new String[] {"(no arrivals predicted)"});
    }
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
      mRefresh = true;
      mBackendHelper.start();
      return true;
    }
    return false;
  }
}
