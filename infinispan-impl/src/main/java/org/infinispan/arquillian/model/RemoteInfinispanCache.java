/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 */
package org.infinispan.arquillian.model;

import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.arquillian.utils.MBeanUtils;

/**
 * Retrieves various information about a default or named cache
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class RemoteInfinispanCache
{
   private MBeanServerConnectionProvider provider;
   
   private MBeanObjectsProvider mBeans;

   /**
    * Holds the name of the cache manager this cache is associated with
    */
   private String cacheManagerName;

   /**
    * Holds the name of the cache
    */
   private String cacheName;

   /**
    * 
    * Creates a new RemoteInfinispanCache.
    * 
    * @param provider an MBean server connection provider
    * @param mBeans an MBean objects provider
    * @param cacheName the name of the cache that this object should be bound to
    * @param cacheManagerName the name of the cache manager that this object should be bound to
    */
   public RemoteInfinispanCache(MBeanServerConnectionProvider provider, MBeanObjectsProvider mBeans, String cacheName, String cacheManagerName)
   {
      this.cacheName = cacheName;
      this.cacheManagerName = cacheManagerName;
      this.provider = provider;
      this.mBeans = mBeans;
   }

   /**
    * 
    * Returns a cache name.
    * 
    * @return the cache name
    */
   public String getCacheName()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheMBean(provider, cacheName, cacheManagerName), CacheAttributes.CACHE_NAME);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get cacheName", e);
      }
   }

   /**
    * 
    * Returns a cache status.
    * 
    * @return the cache status
    */
   public String getCacheStatus()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheMBean(provider, cacheName, cacheManagerName), CacheAttributes.CACHE_STATUS);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get cacheStatus", e);
      }
   }

   /**
    * 
    * Returns the cache statistics specified via {@link CacheStatistics} enumeration.
    * 
    * @param stats the cache statistics to be retrieved
    * @return the cache statistics
    */
   public String getStatistics(CacheStatistics stats)
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheStatisticsMBean(provider, cacheName, cacheManagerName), stats.toString());
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get cache statistics: " + stats, e);
      }
   }

   /**
    * 
    * Returns an average number of milliseconds for a read operation on the cache.
    * 
    * @return the average number of milliseconds for a read operation on the cache
    */
   public long getAverageReadTime()
   {
      return Long.parseLong(getStatistics(CacheStatistics.AVERAGE_READ_TIME));
   }

   /**
    * 
    * Returns an average number of milliseconds for a write operation in the cache.
    * 
    * @return the average number of milliseconds for a write operation in the cache
    */
   public long getAverageWriteTime()
   {
      return Long.parseLong(getStatistics(CacheStatistics.AVERAGE_WRITE_TIME));
   }

   /**
    * 
    * Returns a number of seconds since cache started.
    * 
    * @return the number of seconds since cache started
    */
   public long getElapsedTime()
   {
      return Long.parseLong(getStatistics(CacheStatistics.ELAPSED_TIME));
   }

   /**
    * 
    * Returns a number of cache eviction operations
    * 
    * @return the number of cache eviction operations.
    */
   public long getEvictions()
   {
      return Long.parseLong(getStatistics(CacheStatistics.EVICTIONS));
   }

   /**
    * 
    * Returns the percentage hit/(hit+miss) ratio for the cache.
    * 
    * @return the percentage hit/(hit+miss) ratio for the cache
    */
   public double getHitRatio()
   {
      return Double.parseDouble(getStatistics(CacheStatistics.HIT_RATIO));
   }

   /**
    * 
    * Returns a number of cache attribute hits.
    * 
    * @return the number of cache attribute hits
    */
   public long getHits()
   {
      return Long.parseLong(getStatistics(CacheStatistics.HITS));
   }

   /**
    * 
    * Returns a number of cache attribute misses.
    * 
    * @return the number of cache attribute misses
    */
   public long getMisses()
   {
      return Long.parseLong(getStatistics(CacheStatistics.MISSES));
   }

   /**
    * 
    * Returns a number of entries currently in the cache.
    * 
    * @return the number of entries currently in the cache
    */
   public long getNumberOfEntries()
   {
      return Long.parseLong(getStatistics(CacheStatistics.NUMBER_OF_ENTRIES));
   }

   /**
    *
    * Returns a number of entries currently stored in-memory in the cache.
    *
    * @return the number of entries currently in-memory  the cache
    */
   public long getNumberOfEntriesInMemory()
   {
      return Long.parseLong(getStatistics(CacheStatistics.NUMBER_OF_ENTRIES_IN_MEMORY));
   }
   
   /**
    * 
    * Returns the read/writes ratio for the cache.
    * 
    * @return the read/writes ratio for the cache
    */
   public double getReadWriteRatio()
   {
      return Double.parseDouble(getStatistics(CacheStatistics.READ_WRITE_RATIO));
   }

   /**
    * 
    * Returns a number of cache removal hits.
    * 
    * @return the number of cache removal hits
    */
   public long getRemoveHits()
   {
      return Long.parseLong(getStatistics(CacheStatistics.REMOVE_HITS));
   }

   /**
    * 
    * Returns a number of cache removals where keys were not found.
    * 
    * @return the number of cache removals where keys were not found
    */
   public long getRemoveMisses()
   {
      return Long.parseLong(getStatistics(CacheStatistics.REMOVE_MISSES));
   }

   /**
    * 
    * Returns a number of cache attribute put operations.
    * 
    * @return the number of cache attribute put operations
    */
   public long getStores()
   {
      return Long.parseLong(getStatistics(CacheStatistics.STORES));
   }

   /**
    * 
    * Returns a number of seconds since the cache statistics were last reset.
    * 
    * @return the number of seconds since the cache statistics were last reset
    */
   public long getTimeSinceReset()
   {
      return Long.parseLong(getStatistics(CacheStatistics.TIME_SINCE_RESET));
   }
}
