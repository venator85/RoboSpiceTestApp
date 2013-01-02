package com.octo.android.robospice.request.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Downloads small images in size. All data is passed to the listener using memory. This class is meant to help download small images (like thumbnails). If you wish to download bigger documents (or if
 * you don't know the size of your documents), you would be better using {@link BigBinaryRequest}.
 * 
 * @author sni & jva
 * 
 */
public class SmallBinaryRequest extends BinaryRequest {

	public SmallBinaryRequest(String url) {
		super(url);
	}

	@Override
	public InputStream processStream(int contentLength, InputStream inputStream) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		readBytes(inputStream, new ProgressByteProcessor(bos, contentLength));

		byte[] bytes = bos.toByteArray();
		return new ByteArrayInputStream(bytes);
	}

}
