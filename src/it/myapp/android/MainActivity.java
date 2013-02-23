package it.myapp.android;

import it.myapp.android.net.core.MySpiceService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;

import com.octo.android.robospice.SpiceManager;

public class MainActivity extends FragmentActivity {

	private ViewPager pager;
	private PagerAdapter pagerAdapter;
	
	private SpiceManager spiceManager = new SpiceManager(MySpiceService.class);

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
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}

}
