package it.myapp.android.net.core;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import com.octo.android.robospice.request.SpiceRequest;

public abstract class RawRequest<T> extends SpiceRequest<T> {

	private HttpUriRequest request;

	public RawRequest(Class<T> clazz, HttpUriRequest request) {
		super(clazz);
		this.request = request;
	}

	@Override
	public final T loadDataFromNetwork() throws Exception {
		HttpResponse hr = MyHttpClient.get().execute(request);
		HttpEntity entity = hr.getEntity();
		byte[] response = EntityUtils.toByteArray(entity);
		return processResponse(response);
	}

	protected abstract T processResponse(byte[] response) throws Exception;

}