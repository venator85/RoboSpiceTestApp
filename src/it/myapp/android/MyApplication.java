package it.myapp.android;

import android.app.Application;
import android.content.Context;

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

}
