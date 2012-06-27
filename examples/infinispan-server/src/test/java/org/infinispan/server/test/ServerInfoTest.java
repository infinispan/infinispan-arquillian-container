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
package org.infinispan.server.test;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.jboss.arquillian.junit.Arquillian;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.infinispan.arquillian.model.CacheStatistics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * An example test that shows/uses information about standalone Infinispan
 * server. In relation to using Infinispan embedded in JBossAS 7+ (JBoss
 * Data Grid) the REST endpoint is not available and only one server endpoint
 * is available at any time (the one configured in arquillian.xml). Furthermore,
 * names of default caches/cache managers are different from those in JBoss
 * Data Grid.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
@RunWith(Arquillian.class)
public class ServerInfoTest
{
   // container1 and container2 defined in arquillian.xml
   @InfinispanResource("container1")
   RemoteInfinispanServer server1;

   @InfinispanResource("container2")
   RemoteInfinispanServer server2;

   @Test
   public void shouldBeAbleToSeeStatisticsChanging()
   {
      RemoteCacheManager m = new RemoteCacheManager(server1.getHotrodEndpoint().getInetAddress().getHostName(), server1.getHotrodEndpoint().getPort());
      RemoteCache<String, Integer> cache = m.getCache();
      Assert.assertTrue(server1.getDefaultCacheManager().getDefaultCache().getStores() == 0 &&
                        server2.getDefaultCacheManager().getDefaultCache().getStores() == 0);
      Assert.assertTrue(server1.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 0 &&
                        server1.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 0);
      cache.put("key", 10);
      Assert.assertTrue(server1.getDefaultCacheManager().getDefaultCache().getStores() == 1 ||
                        server2.getDefaultCacheManager().getDefaultCache().getStores() == 1);
      cache.remove("key");
      Assert.assertTrue(server1.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 1 ||
                        server2.getDefaultCacheManager().getDefaultCache().getRemoveHits() == 1);
   }

   @Test
   public void shouldRetrieveCacheManagerInformation()
   {
      System.out.println("\n----- Retrieving Cache Manager Information -----\n");
      System.out.println("Created cache count:  " + server1.getCacheManager("DefaultCacheManager").getCreatedCacheCount());
      System.out.println("Cache manager status: " + server1.getDefaultCacheManager().getCacheManagerStatus());
      System.out.println("Cluster members:      " + server1.getDefaultCacheManager().getClusterMembers());
      System.out.println("Cluster size:         " + server1.getDefaultCacheManager().getClusterSize());
      System.out.println("Created cache count:  " + server1.getDefaultCacheManager().getCreatedCacheCount());
      System.out.println("Defined cache count:  " + server1.getDefaultCacheManager().getDefinedCacheCount());
      System.out.println("Defined cache names:  " + server1.getDefaultCacheManager().getDefinedCacheNames());
      System.out.println("Node address:         " + server1.getDefaultCacheManager().getNodeAddress());
      System.out.println("Physical address:     " + server1.getDefaultCacheManager().getPhysicalAddresses());
      System.out.println("Running cache count:  " + server1.getDefaultCacheManager().getRunningCacheCount());
      System.out.println("Version:              " + server1.getDefaultCacheManager().getVersion());
   }

   @Test
   public void shouldRetrieveCacheInformation()
   {
      System.out.println("\n----- Retrieving Cache Information -----\n");
      System.out.println("Cache name:           " + server2.getDefaultCacheManager().getDefaultCache().getCacheName());
      System.out.println("Cache status:         " + server2.getDefaultCacheManager().getDefaultCache().getCacheStatus());
      System.out.println("Average read time:    " + server2.getDefaultCacheManager().getDefaultCache().getAverageReadTime());
      System.out.println("Average write time:   " + server2.getDefaultCacheManager().getDefaultCache().getAverageWriteTime());
      System.out.println("Elapsed time:         " + server2.getDefaultCacheManager().getDefaultCache().getElapsedTime());
      System.out.println("Evictions:            " + server2.getDefaultCacheManager().getDefaultCache().getEvictions());
      System.out.println("Hit ratio:            " + server2.getDefaultCacheManager().getDefaultCache().getHitRatio());
      System.out.println("Hits:                 " + server2.getDefaultCacheManager().getDefaultCache().getHits());
      System.out.println("Misses:               " + server2.getDefaultCacheManager().getDefaultCache().getMisses());
      System.out.println("Number of entries:    " + server2.getDefaultCacheManager().getDefaultCache().getNumberOfEntries());
      System.out.println("Read-write ratio:     " + server2.getDefaultCacheManager().getDefaultCache().getReadWriteRatio());
      System.out.println("Remove hits:          " + server2.getDefaultCacheManager().getDefaultCache().getRemoveHits());
      System.out.println("Remote misses:        " + server2.getDefaultCacheManager().getDefaultCache().getRemoveMisses());
      System.out.println("Stores:               " + server2.getDefaultCacheManager().getDefaultCache().getStores());
      System.out.println("Time since reset:     " + server2.getDefaultCacheManager().getDefaultCache().getTimeSinceReset());
   }

   @Test
   public void shouldRetrieveCacheInformationUsingGenericApproach(@InfinispanResource("container2") RemoteInfinispanServer info3)
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
