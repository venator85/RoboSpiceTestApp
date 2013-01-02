package it.myapp.android.net.core;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.ObjectPersisterFactory;
import com.octo.android.robospice.persistence.springandroid.json.gson.GsonObjectPersisterFactory;

public class MySpiceService extends SpiceService {

	@Override
	public int getThreadCount() {
		return 3;
	}

	@Override
	public CacheManager createCacheManager(Application application) {
		CacheManager cacheManager = new CacheManager();

		ObjectPersisterFactory gsonPersister = new GsonObjectPersisterFactory(application);
		gsonPersister.setAsyncSaveEnabled(true);
		cacheManager.addPersister(gsonPersister);
		
		return cacheManager;
	}

}