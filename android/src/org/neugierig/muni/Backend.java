package org.neugierig.muni;

import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import java.net.*;
import java.io.*;
import org.json.*;

public class Backend {
  // Tag for logging.
  private static final String TAG = "muni";
  // Intent extra indicating the backend query.
  public static final String KEY_QUERY = "query";

  private static final String API_URL = "http://muni-api.appspot.com/api/";
  // When testing:
  // private static final String API_URL = "http://10.0.2.2:8080/api/";

  public static class QueryEntry {
    public String name;
    public String url;
    public QueryEntry(String name, String url) {
      this.name = name;
      this.url = url;
    }
    public QueryEntry() {}
    public String toString() { return this.name; }
  }

  public static class Stop extends QueryEntry {
    Stop(String name, String url) { super(name, url); }
    public static class Time {
      public Time(int minutes) {
        this.minutes = minutes;
      }
      public int minutes;
      public String toString() {
        return "" + minutes + " minutes";
      }
    }
    public Time[] times;
  }

  public class Route extends QueryEntry {
    Route(String name, String url) { super(name, url); }
  }

  public class Direction extends QueryEntry {
    Direction(String name, String url) { super(name, url); }
  }

  Backend(Context context) {
    mContext = context;
    mDatabase = new Database(context);
  }

  public interface APIResultCallback {
    public void onAPIResult(Object obj);
    public void onException(Exception exn);
  }

  void fetchRoutes(final APIResultCallback callback) {
    queryAPI("", false, new StringCallback() {
        public void onString(String data) {
          try {
            JSONArray array = new JSONArray(data);
            Route[] routes = new Route[array.length()];
            for (int i = 0; i < array.length(); ++i) {
              JSONObject entry = array.getJSONObject(i);
              routes[i] = new Route(entry.getString("name"),
                                    entry.getString("url"));
            }
            callback.onAPIResult(routes);
          } catch (JSONException exn) {
            callback.onException(exn);
          }
        }
        public void onException(Exception exn) {
          callback.onException(exn);
        }
      });
  }

  Direction[] fetchRoute(String query) {
    try {
      JSONArray json = new JSONArray(queryAPIBlocking(query, false));
      Direction[] directions = new Direction[2];
      for (int i = 0; i < 2; ++i) {
        JSONObject json_dir = json.getJSONObject(i);
        directions[i] = new Direction(json_dir.getString("name"),
                                      json_dir.getString("url"));
      }
      return directions;
    } catch (JSONException e) {
      Log.e(TAG, "json", e);
      return null;
    }
  }

  Stop[] fetchStops(String query) {
    try {
      JSONArray json = new JSONArray(queryAPIBlocking(query, false));
      Stop[] stops = new Stop[json.length()];
      for (int i = 0; i < json.length(); ++i) {
        JSONObject json_stop = json.getJSONObject(i);
        stops[i] = new Stop(json_stop.getString("name"),
                            json_stop.getString("url"));
      }
      return stops;
    } catch (JSONException e) {
      Log.e(TAG, "json", e);
      return null;
    }
  }

  Stop.Time[] fetchStop(String query, boolean force_refresh) {
    try {
      JSONArray json = new JSONArray(queryAPIBlocking(query, force_refresh));

      Stop.Time[] times = new Stop.Time[json.length()];
      for (int i = 0; i < json.length(); ++i)
        times[i] = new Stop.Time(json.getInt(i));

      return times;
    } catch (JSONException e) {
      Log.e(TAG, "json", e);
      return null;
    }
  }

  private interface StringCallback {
    public void onString(String str);
    public void onException(Exception exn);
  }

  private interface ProgressCallback {
    public void onProgress(String str);
  }

  synchronized void queryAPI(final String query,
                             final boolean bypass_cache,
                             final StringCallback callback) {
    final int MSG_TOAST = 0;
    final int MSG_RESULT = 1;
    final int MSG_EXCEPTION = 2;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
          switch (msg.what) {
          case MSG_TOAST:
            Toast.makeText(mContext, (String)msg.obj,
                           Toast.LENGTH_SHORT).show();
            break;
          case MSG_RESULT:
            callback.onString((String)msg.obj);
            break;
          case MSG_EXCEPTION:
            callback.onException((Exception)msg.obj);
            break;
          }
        }
      };

    final ProgressCallback progress = new ProgressCallback() {
        public void onProgress(String message) {
          handler.sendMessage(handler.obtainMessage(MSG_TOAST,
                                                    (Object)message));
        }
      };

    new Thread(new Runnable() {
      public void run() {
        try {
          String data = Backend.this.queryAPIBlocking(query, bypass_cache,
                                                      progress);
          handler.sendMessage(handler.obtainMessage(MSG_RESULT, (Object)data));
        } catch (Exception exn) {
          handler.sendMessage(handler.obtainMessage(MSG_EXCEPTION,
                                                    (Object)exn));
        }
      }
    }, "Network Fetch").start();
  }

  synchronized String queryAPIBlocking(String query, boolean bypass_cache) {
    try {
      return queryAPIBlocking(query, bypass_cache, null);
    } catch (Exception e) {
      Log.e(TAG, e.toString());
      return null;
    }
  }

  synchronized String queryAPIBlocking(String query, boolean bypass_cache,
                                       ProgressCallback progress)
      throws MalformedURLException, IOException
  {
    String data = null;
    if (!bypass_cache)
      mDatabase.get(query);
    if (data == null) {
      progress.onProgress("Contacting server...");
      data = queryAPINetworkBlocking(query);
    }
    return data;
  }

  synchronized String queryAPINetworkBlocking(String query)
      throws MalformedURLException, IOException
  {
    String data = fetchURL(new URL(API_URL + query));
    mDatabase.put(query, data);
    Log.i(TAG, "Network fetch: " + data);
    return data;
  }

  // It's pretty unbelievable there's no simpler way to do this.
  String fetchURL(URL url) throws IOException {
    InputStream input = url.openStream();
    StringBuffer buffer = new StringBuffer(8 << 10);

    int byte_read;
    while ((byte_read = input.read()) != -1) {
      // This is incorrect for non-ASCII, but we don't have any of that.
      buffer.appendCodePoint(byte_read);
    }

    return buffer.toString();
  }

  private Context mContext;
  private Database mDatabase;
}
