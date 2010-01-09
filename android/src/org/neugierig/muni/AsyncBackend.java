package org.neugierig.muni;

import android.app.*;
import android.content.*;
import android.os.*;

// The AsyncBackend provides an asynchronous interface to the
// (blocking) Backend.  Its callback always comes back on the thread
// it's queried from, but internally it's running the queries on a
// separate thread.
// It has hooks into (and expects callbacks from) an Activity and
// manages showing the throbber and the "Network Error" dialog.
class AsyncBackend {
  public interface Delegate {
    // Return the result of a query.  If there was an error, it will
    // never get called.
    public void onAsyncResult(Object obj);
  }

  // A Query is a snippet of code that is passed to the backend
  // and run.  Its resulting object comes back asynchronously via
  // the APIResultCallback interface.
  public interface Query {
    public Object runQuery(Backend backend) throws Exception;
  }

  AsyncBackend(Activity activity, Delegate delegate) {
    mActivity = activity;
    mDelegate = delegate;
    mBackend = new Backend(activity);
  }

  public void start(AsyncBackend.Query query) {
    mQuery = query;
    restart();
  }

  public void restart() {
    mActivity.setProgressBarIndeterminateVisibility(true);
    runQuery(mQuery);
  }

  public Dialog onCreateDialog(int id) {
    switch (id) {
      case ERROR_DIALOG_ID: {
        DialogInterface.OnClickListener clicker =
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                  case DialogInterface.BUTTON1:
                    restart();
                    break;
                  case DialogInterface.BUTTON2:
                    mActivity.dismissDialog(ERROR_DIALOG_ID);
                    mActivity.finish();
                    break;
                }
              }
            };

        return new AlertDialog.Builder(mActivity)
            .setTitle("Server Error")
            .setMessage(mBackendError.getLocalizedMessage())
            .setPositiveButton("Retry", clicker)
            .setNegativeButton("Cancel", clicker)
            .create();
      }
    }

    return null;
  }

  // All the thread magic happens in this single function, which
  // hopefully helps reasoning about what needs locks.  TODO: maybe
  // lock around not having two pending queries out at once, which
  // doesn't make sense from an API perspective but could race in
  // mBackend.
  private void runQuery(final Query query) {
    final int MSG_RESULT = 0;
    final int MSG_EXCEPTION = 1;

    final Handler handler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case MSG_RESULT:
            mActivity.setProgressBarIndeterminateVisibility(false);
            mDelegate.onAsyncResult(msg.obj);
            break;
          case MSG_EXCEPTION:
            mActivity.setProgressBarIndeterminateVisibility(false);
            mBackendError = (Exception)msg.obj;
            mActivity.showDialog(ERROR_DIALOG_ID);
            break;
        }
      }
    };

    Thread thread = new Thread(new Runnable() {
      public void run() {
        try {
          Object result = query.runQuery(mBackend);
          handler.sendMessage(handler.obtainMessage(MSG_RESULT, result));
        } catch (Exception exn) {
          handler.sendMessage(handler.obtainMessage(MSG_EXCEPTION,
                                                    (Object)exn));
        }
      }
    }, "Network Fetch");
    thread.start();
  }

  private static final int ERROR_DIALOG_ID = 0;     // XXX how to choose?
  private Activity mActivity;
  private Delegate mDelegate;

  private Backend mBackend;
  private Exception mBackendError;
  private Query mQuery;
}
