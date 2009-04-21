package org.neugierig.muni;

import android.app.ListActivity;
import android.os.Bundle;
import android.content.*;
import android.widget.*;
import android.view.*;

public class Stops extends ListActivity {
  private Backend.Direction[] directions;

  private class StopsAdapter extends BaseAdapter {
    private Context mContext;
    private Backend.Direction[] mDirections;

    StopsAdapter(Context context, Backend.Direction[] directions) {
      mContext = context;
      mDirections = directions;
    }

    @Override
    public boolean areAllItemsEnabled() {
      return false;
    }

    @Override
    public boolean isEnabled(int position) {
      if (position < 1)
        return false;
      else if (position < 1 + mDirections[0].stops.length)
        return true;
      else if (position < 1 + mDirections[0].stops.length + 1)
        return false;
      else
        return true;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public Object getItem(int position) {
      if (position < 1)
        return mDirections[0].name;
      else if (position < 1 + mDirections[0].stops.length)
        return mDirections[0].stops[position - 1];
      else if (position < 1 + mDirections[0].stops.length + 1)
        return mDirections[1].name;
      else
        return mDirections[1].stops[position - 1 -
                                    mDirections[0].stops.length - 1];
    }

    @Override
    public int getCount() {
      return 1 + mDirections[0].stops.length +
             1 + mDirections[1].stops.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView tv;
      if (convertView == null) {
        tv = (TextView) LayoutInflater.from(mContext).inflate(
            android.R.layout.simple_list_item_1, parent, false);
      } else {
        tv = (TextView) convertView;
      }
      tv.setText(getItem(position).toString());
      return tv;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String query = getIntent().getExtras().getString(Backend.KEY_QUERY);

    Backend backend = new Backend();
    directions = backend.fetchStops(query);

    setListAdapter(new StopsAdapter(this, directions));
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    /*Backend.Route route = routes[position];
    Intent intent = new Intent(this, Stops.class);
    intent.putExtra("org.neugierig.muni.query", route.url);
    startActivity(intent);*/
  }
}
