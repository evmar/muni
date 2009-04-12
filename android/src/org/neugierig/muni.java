package org.neugierig;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.util.Log;

import java.net.*;
import java.io.*;
import org.json.*;

public class muni extends Activity {
  private final String TAG = "muni";

  public class Stop {
    public String direction;
    public String name;
    class Time {
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

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.stop);

    Stop stop = fetchInfo();
    TextView title = (TextView) findViewById(R.id.title);
    title.setText(stop.name);
    TextView subtitle = (TextView) findViewById(R.id.subtitle);
    subtitle.setText(stop.direction);

    ListView list = (ListView) findViewById(R.id.list);
    ListAdapter adapter = new ArrayAdapter<Stop.Time>(
        this,
        android.R.layout.simple_list_item_1,
        stop.times);
    list.setAdapter(adapter);
  }

  Stop fetchInfo() {
    try {
      String data = fetchURL(new URL("http://10.0.2.2:8080"));
      Log.i(TAG, data);

      JSONObject json = new JSONObject(data);
      Stop stop = new Stop();
      stop.direction = json.getString("direction");
      stop.name = json.getString("name");
      JSONArray times = json.getJSONArray("times");
      stop.times = new Stop.Time[times.length()];
      for (int i = 0; i < times.length(); ++i)
        stop.times[i] = stop.new Time(times.getInt(i));

      return stop;
    } catch (MalformedURLException e) {
      Log.e(TAG, "url", e);
      return null;
    } catch (IOException e) {
      Log.e(TAG, "io", e);
      return null;
    } catch (JSONException e) {
      Log.e(TAG, "json", e);
      return null;
    }
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
}
