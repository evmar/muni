package org.neugierig.muni;

import android.content.*;
import android.os.*;
import android.widget.Toast;
import org.json.*;

class AsyncBackend {
  public interface APIResultCallback {
    public void onAPIResult(Object obj);
    public void onException(Exception exn);

    // Notification that a network fetch is beginning.
    public void onNetworkFetch();
  }

  public interface BackendQuery {
    public Object runQuery(Backend backend) throws Exception;
  }

  Context mContext;
  Backend mBackend;

  AsyncBackend(Context context) {
    mContext = context;
    mBackend = new Backend(context);
  }

  public void fetchRoutes(APIResultCallback callback) {
    queryBackend(new BackendQuery() {
        public Object runQuery(Backend backend) throws Exception {
          return backend.fetchRoutes();
        }
      },
      callback);
  }

  public void fetchRoute(final String query, APIResultCallback callback) {
    queryBackend(new BackendQuery() {
        public Object runQuery(Backend backend) throws Exception {
          return backend.fetchRoute(query);
        }
      },
      callback);
  }

  public void fetchStops(final String query, APIResultCallback callback) {
    queryBackend(new BackendQuery() {
        public Object runQuery(Backend backend) throws Exception {
          return backend.fetchStops(query);
        }
      },
      callback);
  }

  public void fetchStop(final String query, final boolean reload,
                        APIResultCallback callback) {
    queryBackend(new BackendQuery() {
        public Object runQuery(Backend backend) throws Exception {
          return backend.fetchStop(query, reload);
        }
      },
      callback);
  }

  synchronized private void queryBackend(final BackendQuery query,
                                         final APIResultCallback callback) {
    final int MSG_NETWORK_FETCH = 0;
    final int MSG_RESULT = 1;
    final int MSG_EXCEPTION = 2;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
          switch (msg.what) {
          case MSG_NETWORK_FETCH:
            callback.onNetworkFetch();
            break;
          case MSG_RESULT:
            callback.onAPIResult(msg.obj);
            break;
          case MSG_EXCEPTION:
            callback.onException((Exception)msg.obj);
            break;
          }
        }
      };

    mBackend.setNetworkFetchListener(new Backend.NetworkFetchListener() {
        public void onNetworkFetch() {
          handler.sendMessage(handler.obtainMessage(MSG_NETWORK_FETCH));
        }
      });

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


}
