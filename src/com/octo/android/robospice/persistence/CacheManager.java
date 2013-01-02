package com.octo.android.robospice.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;

/**
 * An entity responsible for loading/saving data from/to cache. It implements a Chain of Responsability pattern,
 * delegating loading and saving operations to {@link ObjectPersister} or {@link ObjectPersisterFactory} elements.
 * 
 * The chain of responsibility is ordered. This means that the order used to register elements matters. All elements in
 * the chain of responsibility are questioned in order. The first element that can handle a given class for persistence
 * will be used to persist data of this class.
 * 
 * @author sni
 * 
 */
public class CacheManager implements ICacheManager {

    /** The Chain of Responsability list of all {@link Persister}. */
    private Collection< Persister > listPersister = new ArrayList< Persister >();
    private Map< ObjectPersisterFactory, List< ObjectPersister< ? >>> mapFactoryToPersister = new HashMap< ObjectPersisterFactory, List< ObjectPersister< ? >>>();

    /** {@inheritDoc} */
    @Override
    public void addPersister( Persister persister ) {
        listPersister.add( persister );
        if ( persister instanceof ObjectPersisterFactory ) {
            mapFactoryToPersister.put( (ObjectPersisterFactory) persister, new ArrayList< ObjectPersister< ? >>() );
        } else if ( !( persister instanceof ObjectPersister ) ) {
            throw new RuntimeException( getClass().getSimpleName() + " only supports " + ObjectPersister.class.getSimpleName() + " or "
                    + ObjectPersisterFactory.class.getSimpleName() + " instances." );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removePersister( Persister persister ) {
        listPersister.remove( persister );
        if ( persister instanceof ObjectPersisterFactory ) {
            mapFactoryToPersister.remove( persister );
        }
    }

    /** {@inheritDoc} */
    @Override
    public < T > T loadDataFromCache( Class< T > clazz, Object cacheKey, long maxTimeInCacheBeforeExpiry ) throws CacheLoadingException {
        return getObjectPersister( clazz ).loadDataFromCache( cacheKey, maxTimeInCacheBeforeExpiry );
    }

    @Override
    @SuppressWarnings("unchecked")
    /** {@inheritDoc}*/
    public < T > T saveDataToCacheAndReturnData( T data, Object cacheKey ) throws CacheSavingException {
        // http://stackoverflow.com/questions/4460580/java-generics-why-someobject-getclass-doesnt-return-class-extends-t
        ObjectPersister< T > classCacheManager = getObjectPersister( (Class< T >) data.getClass() );
        return classCacheManager.saveDataToCacheAndReturnData( data, cacheKey );
    }

    /** {@inheritDoc} */
    @Override
    public boolean removeDataFromCache( Class< ? > clazz, Object cacheKey ) {
        return getObjectPersister( clazz ).removeDataFromCache( cacheKey );
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllDataFromCache( Class< ? > clazz ) {
        getObjectPersister( clazz ).removeAllDataFromCache();
    }

    /** {@inheritDoc} */
    @Override
    public < T > List< Object > getAllCacheKeys( final Class< T > clazz ) {
        return getObjectPersister( clazz ).getAllCacheKeys();
    }

    /** {@inheritDoc} */
    @Override
    public < T > List< T > loadAllDataFromCache( final Class< T > clazz ) throws CacheLoadingException {
        return getObjectPersister( clazz ).loadAllDataFromCache();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllDataFromCache() {
        for ( Persister persister : this.listPersister ) {
            if ( persister instanceof ObjectPersister ) {
                ( (ObjectPersister< ? >) persister ).removeAllDataFromCache();
            } else if ( persister instanceof ObjectPersisterFactory ) {
                ObjectPersisterFactory factory = (ObjectPersisterFactory) persister;
                List< ObjectPersister< ? >> listPersister = mapFactoryToPersister.get( factory );
                for ( ObjectPersister< ? > objectPersister : listPersister ) {
                    objectPersister.removeAllDataFromCache();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected < T > ObjectPersister< T > getObjectPersister( Class< T > clazz ) {
        for ( Persister persister : this.listPersister ) {
            if ( persister.canHandleClass( clazz ) ) {
                if ( persister instanceof ObjectPersister ) {
                    return (ObjectPersister< T >) persister;
                } else if ( persister instanceof ObjectPersisterFactory ) {
                    ObjectPersisterFactory factory = (ObjectPersisterFactory) persister;
                    List< ObjectPersister< ? >> listPersister = null;

                    if ( factory.canHandleClass( clazz ) ) {
                        listPersister = mapFactoryToPersister.get( factory );
                        for ( ObjectPersister< ? > objectPersister : listPersister ) {
                            if ( objectPersister.canHandleClass( clazz ) ) {
                                return (ObjectPersister< T >) objectPersister;
                            }
                        }
                        ObjectPersister< T > newPersister = factory.createObjectPersister( clazz );
                        listPersister.add( newPersister );
                        return newPersister;
                    }
                }
            }
        }
        throw new RuntimeException( "Class " + clazz.getName() + " is not handled by any registered factoryList" );
    }
}