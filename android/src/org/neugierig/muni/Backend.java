package org.neugierig.muni;

import android.content.*;
import android.os.*;
import android.util.Log;

import java.net.*;
import java.io.*;
import org.json.*;

public class Backend {
  // Tag for logging.
  private static final String TAG = "muni";
  // Intent extra indicating the backend query.
  public static final String KEY_QUERY = "query";

  public static interface NetworkFetchListener {
    public void onNetworkFetch();
  }

  Backend(Context context) {
    mContext = context;
    mDatabase = new Database(context);
  }

  void setNetworkFetchListener(NetworkFetchListener l) {
    mNetworkFetchListener = l;
  }

  MuniAPI.Route[] fetchRoutes()
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseRoutes(queryAPI("", false));
  }

  MuniAPI.Direction[] fetchRoute(String query)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseRoute(queryAPI(query, false));
  }

  MuniAPI.Stop[] fetchStops(String query)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseStops(queryAPI(query, false));
  }

  MuniAPI.Stop.Time[] fetchStop(String query, boolean force_refresh)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseStop(queryAPI(query, force_refresh));
  }

  String queryAPI(String query, boolean reload)
      throws MalformedURLException, IOException
  {
    String data = null;

    if (!reload)
      data = mDatabase.get(query);

    if (data == null) {
      if (mNetworkFetchListener != null)
        mNetworkFetchListener.onNetworkFetch();
      data = MuniAPI.queryNetwork(query);
      Log.i(TAG, "Network fetch: " + data);
      mDatabase.put(query, data);
    }

    return data;
  }

  private Context mContext;
  private Database mDatabase;
  private NetworkFetchListener mNetworkFetchListener;
}
