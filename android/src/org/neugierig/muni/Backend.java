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

  public interface ProgressListener {
    public void onProgress(String str);
  }

  Backend(Context context) {
    mContext = context;
    mDatabase = new Database(context);
  }

  MuniAPI.Route[] fetchRoutes(ProgressListener progress)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseRoutes(queryAPI("", false, progress));
  }

  MuniAPI.Direction[] fetchRoute(String query, ProgressListener progress)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseRoute(queryAPI(query, false, progress));
  }

  MuniAPI.Stop[] fetchStops(String query, ProgressListener progress)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseStops(queryAPI(query, false, progress));
  }

  MuniAPI.Stop.Time[] fetchStop(String query, boolean force_refresh,
                                ProgressListener progress)
      throws MalformedURLException, IOException, JSONException
  {
    return MuniAPI.parseStop(queryAPI(query, force_refresh, progress));
  }

  private interface StringCallback {
    public void onString(String str);
    public void onException(Exception exn);
  }

  String queryAPI(String query, boolean reload, ProgressListener progress)
      throws MalformedURLException, IOException
  {
    String data = null;

    if (!reload)
      data = mDatabase.get(query);

    if (data == null) {
      progress.onProgress("Contacting server...");
      data = MuniAPI.queryNetwork(query);
      Log.i(TAG, "Network fetch: " + data);
      mDatabase.put(query, data);
    }

    return data;
  }

  private Context mContext;
  private Database mDatabase;
}
