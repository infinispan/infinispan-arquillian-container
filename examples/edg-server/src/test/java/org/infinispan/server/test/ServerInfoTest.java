/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infinispan.server.test;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.infinispan.arquillian.core.Infinispan;
import org.jboss.infinispan.arquillian.core.InfinispanInfo;
import org.jboss.infinispan.arquillian.model.CacheStatistics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * An example test that shows/uses information about Infinispan embedded in a
 * JBossAS 7+ (Enterprise Data Grid). In relation to using standalone Infinispan
 * there is also a REST endpoint available and all server endpoints are available
 * simultaneously. Furthermore, names of default caches/cache managers are
 * different from those in standalone Infinispan.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
@RunWith(Arquillian.class)
public class ServerInfoTest
{
   // container1 and container2 defined in arquillian.xml
   @Infinispan("container1")
   InfinispanInfo info1;

   @Infinispan("container2")
   InfinispanInfo info2;

   @Test
   public void shouldBeAbleToSeeStatisticsChanging()
   {
      RemoteCacheManager m = new RemoteCacheManager(info1.getHotrodEndpoint().getInetAddress().getHostName(), info1.getHotrodEndpoint().getPort());
      RemoteCache<String, Integer> cache = m.getCache();
      Assert.assertTrue(info1.getDefaultCacheManager().getDefaultCache().getStores() == 0);
      Assert.assertTrue(info1.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 0);
      cache.put("key", 10);
      Assert.assertTrue(info1.getDefaultCacheManager().getDefaultCache().getStores() == 1);
      Assert.assertTrue(info1.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 0);
      cache.remove("key");
      Assert.assertTrue(info1.getDefaultCacheManager().getDefaultCache().getStores() == 1);
      Assert.assertTrue(info1.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 1);
   }

   @Test
   public void shouldRetrieveGeneralInformation() throws Exception
   {
      System.out.println("\n----- Retrieving General Information -----\n");
      System.out.println("Memcached port: " + info1.getMemcachedEndpoint().getPort());
      System.out.println("Memcached hostname: " + info1.getMemcachedEndpoint().getInetAddress());
      System.out.println("Hotrod port: " + info1.getHotrodEndpoint().getPort());
      System.out.println("Hotrod hostname: " + info1.getHotrodEndpoint().getInetAddress());
      // REST available only for JBossAS with embedded Infinispan (Enterprise
      // Data Grid)
      System.out.println("REST hostname: " + info1.getRESTEndpoint().getInetAddress());
      System.out.println("REST context path: " + info1.getRESTEndpoint().getContextPath());
   }

   @Test
   public void shouldRetrieveCacheManagerInformation()
   {
      System.out.println("\n----- Retrieving Cache Manager Information -----\n");
      System.out.println("Created cache count:  " + info1.getCacheManager("default").getCreatedCacheCount());
      System.out.println("Cache manager status: " + info1.getDefaultCacheManager().getCacheManagerStatus());
      System.out.println("Cache name:           " + info1.getDefaultCacheManager().getCacheName());
      System.out.println("Cluster members:      " + info1.getDefaultCacheManager().getClusterMembers());
      System.out.println("Cluster size:         " + info1.getDefaultCacheManager().getClusterSize());
      System.out.println("Created cache count:  " + info1.getDefaultCacheManager().getCreatedCacheCount());
      System.out.println("Defined cache count:  " + info1.getDefaultCacheManager().getDefinedCacheCount());
      System.out.println("Defined cache names:  " + info1.getDefaultCacheManager().getDefinedCacheNames());
      System.out.println("Node address:         " + info1.getDefaultCacheManager().getNodeAddress());
      System.out.println("Physical address:     " + info1.getDefaultCacheManager().getPhysicalAddresses());
      System.out.println("Running cache count:  " + info1.getDefaultCacheManager().getRunningCacheCount());
      System.out.println("Version:              " + info1.getDefaultCacheManager().getVersion());
   }

   @Test
   public void shouldRetrieveCacheInformation()
   {
      System.out.println("\n----- Retrieving Cache Information -----\n");
      System.out.println("Default cache name:   " + info2.getCacheManager("default").getCache("default").getCacheName());
      System.out.println("Named cache name:     " + info2.getCacheManager("default").getCache("namedCache").getCacheName());
      System.out.println("Cache name:           " + info2.getDefaultCacheManager().getDefaultCache().getCacheName());
      System.out.println("Cache status:         " + info2.getDefaultCacheManager().getDefaultCache().getCacheStatus());
      System.out.println("Average read time:    " + info2.getDefaultCacheManager().getDefaultCache().getAverageReadTime());
      System.out.println("Average write time:   " + info2.getDefaultCacheManager().getDefaultCache().getAverageWriteTime());
      System.out.println("Elapsed time:         " + info2.getDefaultCacheManager().getDefaultCache().getElapsedTime());
      System.out.println("Evictions:            " + info2.getDefaultCacheManager().getDefaultCache().getEvictions());
      System.out.println("Hit ratio:            " + info2.getDefaultCacheManager().getDefaultCache().getHitRatio());
      System.out.println("Hits:                 " + info2.getDefaultCacheManager().getDefaultCache().getHits());
      System.out.println("Misses:               " + info2.getDefaultCacheManager().getDefaultCache().getMisses());
      System.out.println("Number of entries:    " + info2.getDefaultCacheManager().getDefaultCache().getNumberOfEntries());
      System.out.println("Read-write ratio:     " + info2.getDefaultCacheManager().getDefaultCache().getReadWriteRatio());
      System.out.println("Remove hits:          " + info2.getDefaultCacheManager().getDefaultCache().getRemoveHits());
      System.out.println("Remote misses:        " + info2.getDefaultCacheManager().getDefaultCache().getRemoveMisses());
      System.out.println("Stores:               " + info2.getDefaultCacheManager().getDefaultCache().getStores());
      System.out.println("Time since reset:     " + info2.getDefaultCacheManager().getDefaultCache().getTimeSinceReset());
   }

   @Test
   public void shouldRetrieveCacheInformationUsingGenericApproach(@Infinispan("container2") InfinispanInfo info3)
   {
      System.out.println("\n----- Retrieving Cache Information using semi-generic approach -----\n");
      System.out.println("Average read time:    " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.AVERAGE_READ_TIME));
      System.out.println("Average write time:   " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.AVERAGE_WRITE_TIME));
      System.out.println("Elapsed time:         " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.ELAPSED_TIME));
      System.out.println("Evictions:            " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.EVICTIONS));
      System.out.println("Hit ratio:            " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.HIT_RATIO));
      System.out.println("Hits:                 " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.HITS));
      System.out.println("Misses:               " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.MISSES));
      System.out.println("Number of entries:    " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.NUMBER_OF_ENTRIES));
      System.out.println("Read-write ratio:     " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.READ_WRITE_RATIO));
      System.out.println("Remove hits:          " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.REMOVE_HITS));
      System.out.println("Remote misses:        " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.REMOVE_MISSES));
      System.out.println("Stores:               " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.STORES));
      System.out.println("Time since reset:     " + info3.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.TIME_SINCE_RESET));
   }
}
