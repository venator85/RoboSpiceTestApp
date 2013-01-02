package com.octo.android.robospice.request.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import roboguice.util.temp.Ln;

import com.octo.android.robospice.request.SpiceRequest;

/**
 * Abstract class used to download binaries. See {@link SmallBinaryRequest} and {@link BigBinaryRequest}
 * 
 * @author jva
 * 
 */
public abstract class BinaryRequest extends SpiceRequest<InputStream> {

	private static final int BUF_SIZE = 4096;
	protected String url;

	public BinaryRequest(String url) {
		super(InputStream.class);
		this.url = url;
	}

	@Override
	public final InputStream loadDataFromNetwork() throws Exception {
		try {
			final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
			return processStream(httpURLConnection.getContentLength(), httpURLConnection.getInputStream());
		}
		catch (MalformedURLException e) {
			Ln.e(e, "Unable to create URL");
			return null;
		}
		catch (IOException e) {
			Ln.e(e, "Unable to download binary");
			return null;
		}
	}

	protected final String getUrl() {
		return this.url;
	}

	/**
	 * Override this method to process the stream downloaded
	 * 
	 * @param contentLength
	 *            size of the download
	 * @param inputStream
	 *            stream of the download
	 * @return an inputstream containing the download
	 */
	public abstract InputStream processStream(int contentLength, InputStream inputStream) throws IOException;

	/**
	 * Inspired from Guava com.google.common.io.ByteStreams
	 */
	protected void readBytes(InputStream in, ProgressByteProcessor processor) throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		try {
			int amt;
			do {
				amt = in.read(buf);
				if (amt == -1) {
					break;
				}
			} while (processor.processBytes(buf, 0, amt));
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Inspired from Guava com.google.common.io.ByteProcessor
	 */
	public class ProgressByteProcessor {

		private OutputStream bos;
		private long progress;
		private long total;

		public ProgressByteProcessor(OutputStream bos, long total) {
			this.bos = bos;
			this.total = total;
		}

		public boolean processBytes(byte[] buffer, int offset, int length) throws IOException {
			bos.write(buffer, offset, length);
			progress += length - offset;
			publishProgress((float) progress / total);
			return !Thread.interrupted();
		}
	}

}
