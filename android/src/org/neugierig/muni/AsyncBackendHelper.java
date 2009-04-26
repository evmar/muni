package org.neugierig.muni;

import android.app.*;
import android.content.*;

class AsyncBackendHelper implements AsyncBackend.APIResultCallback {
  public interface Delegate {
    public void startAsyncQuery(AsyncBackend backend);
    public void onAsyncResult(Object obj);
  }

  AsyncBackendHelper(Activity activity, Delegate delegate) {
    mActivity = activity;
    mDelegate = delegate;
    mBackend = new AsyncBackend(activity);
  }

  public void start() {
    mDelegate.startAsyncQuery(mBackend);
  }

  @Override
  public void onAPIResult(Object obj) {
    mDelegate.onAsyncResult(obj);
  }

  @Override
  public void onException(Exception exn) {
    mBackendError = exn;
    mActivity.showDialog(ERROR_DIALOG_ID);
  }

  public Dialog onCreateDialog(int id) {
    // XXX Handle the case where id != ERROR_DIALOG_ID.
    DialogInterface.OnClickListener clicker =
      new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          switch (which) {
          case DialogInterface.BUTTON1:
            mDelegate.startAsyncQuery(mBackend);
            break;
          case DialogInterface.BUTTON2:
            mActivity.dismissDialog(ERROR_DIALOG_ID);
            mActivity.finish();
            break;
          }
        }
      };

    AlertDialog dialog = (new AlertDialog.Builder(mActivity))
      .setTitle("Server Error")
      .setMessage(mBackendError.getLocalizedMessage())
      .setPositiveButton("Retry", clicker)
      .setNegativeButton("Cancel", clicker)
      .create();

    return dialog;
  }

  private static final int ERROR_DIALOG_ID = 0;  // XXX how to choose?
  private Activity mActivity;
  private Delegate mDelegate;
  private AsyncBackend mBackend;
  private Exception mBackendError;
}
