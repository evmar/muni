package org.neugierig.muni;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

public class Routes extends ListActivity {
  private Backend.Route[] mRoutes;
  private Exception mBackendError;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startFetch();
  }

  void startFetch() {
    Backend backend = new Backend(this);
    backend.fetchRoutes(new Backend.APIResultCallback() {
        public void onAPIResult(Object obj) {
          refresh((Backend.Route[]) obj);
        }
        public void onException(Exception exn) {
          mBackendError = exn;
          showDialog(0);
        }
      });
  }

  private void refresh(Backend.Route[] routes) {
    mRoutes = routes;
    ListAdapter adapter = new ArrayAdapter<Backend.Route>(
        this,
        android.R.layout.simple_list_item_1,
        routes);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Backend.Route route = mRoutes[position];
    Intent intent = new Intent(this, Route.class);
    intent.putExtra(Route.KEY_ROUTE, route.name);
    intent.putExtra(Backend.KEY_QUERY, route.url);
    startActivity(intent);
  }

  @Override
  protected Dialog onCreateDialog(final int id) {
    DialogInterface.OnClickListener clicker =
      new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          switch (which) {
          case DialogInterface.BUTTON1:
            startFetch();
            break;
          case DialogInterface.BUTTON2:
            dismissDialog(id);
            finish();
            break;
          }
        }
      };

    AlertDialog dialog = (new AlertDialog.Builder(this))
      .setTitle("Server Error")
      .setMessage(mBackendError.getLocalizedMessage())
      .setPositiveButton("Retry", clicker)
      .setNegativeButton("Cancel", clicker)
      .create();

    return dialog;
  }
}
