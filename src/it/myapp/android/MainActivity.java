package it.myapp.android;

import it.myapp.android.model.Notifications;
import it.myapp.android.net.ActivityRequest;
import it.myapp.android.net.core.MySpiceService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public class MainActivity extends FragmentActivity {

	private static final String ACTIVITY_CACHE_KEY = "activity_data";
	private ViewPager pager;
	private PagerAdapter pagerAdapter;
	
	private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);
	private TextView activity_txt;

	private class PagerAdapter extends FragmentPagerAdapter {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return new MyFragment();
			} else {
				return new ListFragment();
			}
		}
	}
	
	private class ActivityNotificationsListener implements RequestListener<Notifications> {
		@Override
		public void onRequestFailure(SpiceException arg0) {
			if (!(arg0 instanceof RequestCancelledException)) {
				Toast.makeText(MainActivity.this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onRequestSuccess(Notifications notifications) {
			if (notifications != null) {
				activity_txt.setText(notifications.toString());
			} else {
				activity_txt.setText("onRequestSuccess - notifications null");
			}
		}
	}

	public Fragment getPagerFragmentAtPosition(int position) {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + position);
		if (fragment != null) { // could be null if not instantiated yet
			if (fragment.getView() != null) {
				// no need to call if fragment's onDestroyView() has since been called
				return fragment;
			}
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		activity_txt = (TextView) findViewById(R.id.activity_txt);

		pager = (ViewPager) findViewById(R.id.pager);
		pagerAdapter = new PagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOffscreenPageLimit(pagerAdapter.getCount());
	}
	
	@Override
	public void onStart() {
		super.onStart();
		spiceManager.start(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		spiceManager.addListenerIfPending(Notifications.class, ACTIVITY_CACHE_KEY, new ActivityNotificationsListener());
		spiceManager.getFromCache(Notifications.class, ACTIVITY_CACHE_KEY, DurationInMillis.ALWAYS, new ActivityNotificationsListener());
		spiceManager.execute(new ActivityRequest(), ACTIVITY_CACHE_KEY, DurationInMillis.ONE_MINUTE, new ActivityNotificationsListener());
	}

	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

}
