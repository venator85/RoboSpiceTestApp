package it.myapp.android.net;

import it.myapp.android.model.MyAppEntries;
import it.myapp.android.net.core.RawRequest;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.google.gson.Gson;

public class FragmentRequest extends RawRequest<MyAppEntries> {

	public FragmentRequest() {
		super(MyAppEntries.class, new HttpGet("http://venator.ath.cx/myapp_stubs/details.php"));
	}

	@Override
	protected MyAppEntries processResponse(byte[] response) throws Exception {
		final String json = new String(response, "UTF-8");
		Log.e("FragmentRequest", "MyAppEntries " + json);
		return new Gson().fromJson(json, MyAppEntries.class);
	}
}