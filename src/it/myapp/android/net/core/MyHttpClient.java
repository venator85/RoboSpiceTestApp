package it.myapp.android.net.core;

import java.util.concurrent.TimeUnit;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class MyHttpClient extends DefaultHttpClient {

	private static MyHttpClient instance = new MyHttpClient();

	public static MyHttpClient get() {
		return instance;
	}
	
	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		HttpParams protocolParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(protocolParams, HttpVersion.HTTP_1_1);

		SchemeRegistry supportedRegistry = new SchemeRegistry();
		supportedRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		supportedRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connman = new ThreadSafeClientConnManager(protocolParams, supportedRegistry);
		connman.closeIdleConnections(60, TimeUnit.SECONDS);
		return connman;
	}
}
