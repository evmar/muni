package org.neugierig.muni;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;

public class StarDBAdapter {
  private SQLiteDatabase mDb;
  private static final String TAG = "StarDBAdapter";

  private static class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stops";
    private static final int DATABASE_VERSION = 3;

    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("create table stars (_id integer primary key autoincrement, " +
                 "query text, json text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
            newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS stars");
      onCreate(db);
    }
  }

  public StarDBAdapter(Context context) throws SQLException {
    mDb = (new DatabaseHelper(context)).getWritableDatabase();
  }

  public boolean getStarred(String query) {
    Cursor cursor = mDb.rawQuery("select * from stars where query=?",
                                 new String[] {query});
    boolean starred = cursor.getCount() > 0;
    cursor.close();
    return starred;
  }

  public void setStarred(String query, String json, boolean starred) {
    if (starred) {
      mDb.execSQL("insert or replace into stars (query, json) values (?, ?)",
                  new Object[] {query, json});
    } else {
      mDb.execSQL("delete from stars where query=?",
                  new Object[] {query});
    }
  }

  public Cursor fetchAll() {
    return mDb.query("stars", new String[] {"_id", "query", "json"},
                     null, null, null, null, null);
  }
}
