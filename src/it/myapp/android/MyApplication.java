package it.myapp.android;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {

	private static Context instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static Context getContext() {
		return instance;
	}

	public static Dialog showLoading(Context c) {
		try {
			return FullScreenProgressDialog.show(c);
		} catch (Exception e) {
			Log.e("MyApplication", "Can't instantiate dialog", e);
		}
		return null;
	}

	public static void dismissLoading(Dialog dialog) {
		if (dialog != null) {
			try {
				dialog.dismiss();
			} catch (Exception e) {
				Log.e("MyApplication", "Can't dismiss dialog", e);
			}
		}
	}

}
