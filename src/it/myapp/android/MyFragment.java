package it.myapp.android;

import it.myapp.android.model.MyAppEntries;
import it.myapp.android.net.FragmentRequest;
import it.myapp.android.net.core.MySpiceService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

public class MyFragment extends Fragment {

	private static final String FRAGMENT_CACHE_KEY = "my_app_entries";

	private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);

	private TextView fragment_txt;

	private class FragmentEntriesListener implements RequestListener<MyAppEntries>, RequestProgressListener {
		@Override
		public void onRequestFailure(SpiceException arg0) {
			if (!(arg0 instanceof RequestCancelledException)) {
				Toast.makeText(getActivity(), "Failed to load data.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onRequestSuccess(MyAppEntries entries) {
			if (entries != null) {
				fragment_txt.setText(entries.toString());
			} else {
				fragment_txt.setText("onRequestSuccess - entries null");
			}
		}

		@Override
		public void onRequestProgressUpdate(RequestProgress progress) {
			Log.e("", "progress " + progress);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_fragment, container, false);
		fragment_txt = (TextView) view.findViewById(R.id.fragment_txt);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		spiceManager.start(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();

		spiceManager.addListenerIfPending(MyAppEntries.class, FRAGMENT_CACHE_KEY, new FragmentEntriesListener());
		spiceManager.getFromCache(MyAppEntries.class, FRAGMENT_CACHE_KEY, DurationInMillis.ALWAYS, new FragmentEntriesListener());
		spiceManager.execute(new FragmentRequest(), FRAGMENT_CACHE_KEY, DurationInMillis.ONE_MINUTE, new FragmentEntriesListener());
	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

}
