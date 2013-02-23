package com.octo.android.robospice.persistence.springandroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import roboguice.util.temp.Ln;
import android.app.Application;

import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;

public abstract class SpringAndroidObjectPersister<T> extends
    InFileObjectPersister<T> {

    // ============================================================================================
    // ATTRIBUTES
    // ============================================================================================

    private String mFactoryPrefix;

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public SpringAndroidObjectPersister(Application application,
        Class<T> clazz, String factoryPrefix) {
        super(application, clazz);
        this.mFactoryPrefix = factoryPrefix;
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================

    @Override
    protected String getCachePrefix() {
        return mFactoryPrefix + super.getCachePrefix();
    }

    @Override
    public T loadDataFromCache(Object cacheKey, long maxTimeInCacheBeforeExpiry)
        throws CacheLoadingException {
        T result = null;
        String resultJson = null;

        File file = getCacheFile(cacheKey);
        if (file.exists()) {
            long timeInCache = System.currentTimeMillis() - file.lastModified();
            if (maxTimeInCacheBeforeExpiry == 0
                || timeInCache <= maxTimeInCacheBeforeExpiry) {
                try {
                    resultJson = FileUtils.readFileToString(file,
                        CharEncoding.UTF_8);

                    // finally transform json in object
                    if (!StringUtils.isEmpty(resultJson)) {
                        result = deserializeData(resultJson);
                        return result;
                    }
                    throw new CacheLoadingException(
                        "Unable to restore cache content : cache file is empty");
                } catch (FileNotFoundException e) {
                    // Should not occur (we test before if file exists)
                    // Do not throw, file is not cached
                    Ln.w("file " + file.getAbsolutePath() + " does not exists",
                        e);
                    return null;
                } catch (CacheLoadingException e) {
                    throw e;
                } catch (Exception e) {
                    throw new CacheLoadingException(e);
                }
            }
            Ln.v("Cache content is expired since "
                + (maxTimeInCacheBeforeExpiry - timeInCache));
            return null;
        }
        Ln.v("file " + file.getAbsolutePath() + " does not exists");
        return null;
    }

    protected abstract T deserializeData(String json)
        throws CacheLoadingException;

    @Override
    public T saveDataToCacheAndReturnData(final T data, final Object cacheKey)
        throws CacheSavingException {

        try {
            if (isAsyncSaveEnabled()) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            saveData(data, cacheKey);
                        } catch (IOException e) {
                            Ln.e(e, "An error occured on saving request "
                                + cacheKey + " data asynchronously");
                        } catch (CacheSavingException e) {
                            Ln.e(e, "An error occured on saving request "
                                + cacheKey + " data asynchronously");
                        }
                    };
                };
                t.start();
            } else {
                saveData(data, cacheKey);
            }
        } catch (CacheSavingException e) {
            throw e;
        } catch (Exception e) {
            throw new CacheSavingException(e);
        }
        return data;
    }

    protected abstract void saveData(T data, Object cacheKey)
        throws IOException, CacheSavingException;
}
