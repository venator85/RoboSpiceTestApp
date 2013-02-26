package it.myapp.android;

import it.myapp.android.model.MyAppEntries;
import it.myapp.android.net.AbstractLoadingRequestListener;
import it.myapp.android.net.FragmentRequest;
import it.myapp.android.net.core.MySpiceService;

import java.util.Date;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;

public class MainActivity extends FragmentActivity {
	
	private class FragmentEntriesListener extends AbstractLoadingRequestListener<MyAppEntries> {
		private Dialog progressDialog;

		@Override
		public void onRequestFailure_(SpiceException arg0) {
			fragment_txt.setText(new Date() + " - " + arg0);
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
		public void showLoading() {
			Log.e("FragmentEntriesListener", "showLoading()", new Exception("-- showLoading() trace --"));
			progressDialog = MyApplication.showLoading(MainActivity.this);
		}
		
		@Override
		public void hideLoading() {
			Log.e("FragmentEntriesListener", "hideLoading()", new Exception("-- hideLoading() trace --"));
			MyApplication.dismissLoading(progressDialog);
			progressDialog = null;
		}
	}

	private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);

	private TextView fragment_txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_fragment);

		fragment_txt = (TextView) findViewById(R.id.fragment_txt);
		fragment_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spiceManager.execute(new FragmentRequest(), new FragmentEntriesListener());
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		spiceManager.start(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Immediate execution, RequestStatus.LOADING_FROM_NETWORK after RequestStatus.COMPLETE
		spiceManager.execute(new FragmentRequest(), new FragmentEntriesListener());
		
		//Delayed execution, no problems
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				spiceManager.execute(new FragmentRequest(), new FragmentEntriesListener());
//			}
//		}, 2000);
	}
	
	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

}
