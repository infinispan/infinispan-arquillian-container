/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.arquillian.model;

import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.arquillian.utils.MBeanUtils;

/**
 * Retrieve various information about a cache, either default or named one
 * cache.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class RemoteInfinispanCache
{
   private MBeanServerConnectionProvider provider;
   
   private MBeanObjectsProvider mBeans;

   private String cacheManagerName;

   private String cacheName;

   public RemoteInfinispanCache(MBeanServerConnectionProvider provider, MBeanObjectsProvider mBeans, String cacheName, String cacheManagerName)
   {
      this.cacheName = cacheName;
      this.cacheManagerName = cacheManagerName;
      this.provider = provider;
      this.mBeans = mBeans;
   }

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

   public long getAverageReadTime()
   {
      return Long.parseLong(getStatistics(CacheStatistics.AVERAGE_READ_TIME));
   }

   public long getAverageWriteTime()
   {
      return Long.parseLong(getStatistics(CacheStatistics.AVERAGE_WRITE_TIME));
   }

   public long getElapsedTime()
   {
      return Long.parseLong(getStatistics(CacheStatistics.ELAPSED_TIME));
   }

   public long getEvictions()
   {
      return Long.parseLong(getStatistics(CacheStatistics.EVICTIONS));
   }

   public double getHitRatio()
   {
      return Double.parseDouble(getStatistics(CacheStatistics.HIT_RATIO));
   }

   public long getHits()
   {
      return Long.parseLong(getStatistics(CacheStatistics.HITS));
   }

   public long getMisses()
   {
      return Long.parseLong(getStatistics(CacheStatistics.MISSES));
   }

   public long getNumberOfEntries()
   {
      return Long.parseLong(getStatistics(CacheStatistics.NUMBER_OF_ENTRIES));
   }

   public double getReadWriteRatio()
   {
      return Double.parseDouble(getStatistics(CacheStatistics.READ_WRITE_RATIO));
   }

   public long getRemoveHits()
   {
      return Long.parseLong(getStatistics(CacheStatistics.REMOVE_HITS));
   }

   public long getRemoveMisses()
   {
      return Long.parseLong(getStatistics(CacheStatistics.REMOVE_MISSES));
   }

   public long getStores()
   {
      return Long.parseLong(getStatistics(CacheStatistics.STORES));
   }

   public long getTimeSinceReset()
   {
      return Long.parseLong(getStatistics(CacheStatistics.TIME_SINCE_RESET));
   }
}
