package it.myapp.android.net;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.octo.android.robospice.request.listener.RequestStatus;

public abstract class AbstractLoadingRequestListener<T> implements RequestListener<T>, RequestProgressListener {

	public AbstractLoadingRequestListener() {
		super();
	}

	@Override
	public final void onRequestProgressUpdate(RequestProgress progress) {
		Log.e("AbstractLoadingRequestListener", "onRequestProgressUpdate " + progress.getStatus() + ", " + progress.getProgress());
		if (progress.getStatus() == RequestStatus.LOADING_FROM_NETWORK) {
			showLoading();

		} else if (progress.getStatus() == RequestStatus.COMPLETE) {
			hideLoading();
		}
		onRequestProgressUpdate_(progress);
	}

	public void onRequestProgressUpdate_(RequestProgress progress) {
	}
	
	@Override
	public final void onRequestFailure(SpiceException e) {
		hideLoading();
		onRequestFailure_(e);
	}
	
	public void onRequestFailure_(SpiceException e) {
	}
	
	public abstract void showLoading();
	
	public abstract void hideLoading();

}
