package it.myapp.android.net;

import it.myapp.android.model.Notifications;
import it.myapp.android.net.core.RawRequest;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.google.gson.Gson;

public class ActivityRequest extends RawRequest<Notifications> {

	public ActivityRequest() {
		super(Notifications.class, new HttpGet("http://venator.ath.cx/myapp_stubs/notifications.php"));
	}

	@Override
	protected Notifications processResponse(byte[] response) throws Exception {
		final String json = new String(response, "UTF-8");
		Log.e("ActivityRequest", "Notifications " + json);
		return new Gson().fromJson(json, Notifications.class);
	}
}