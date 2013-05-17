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
 * An example test that uses information exposed by Infinispan Server via JMX.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
@RunWith(Arquillian.class)
public class ServerStatisticsTest
{
   // container1 and container2 defined in arquillian.xml
   @InfinispanResource("container1")
   RemoteInfinispanServer server1;

   @InfinispanResource("container2")
   RemoteInfinispanServer server2;

   private static final String DEFAULT_CM = "clustered";

   @Test
   public void shouldBeAbleToSeeStatisticsChanging()
   {
      RemoteCacheManager m = new RemoteCacheManager(server1.getHotrodEndpoint().getInetAddress().getHostName(), server1.getHotrodEndpoint().getPort());
      RemoteCache<String, Integer> cache = m.getCache();
      Assert.assertTrue(server1.getCacheManager(DEFAULT_CM).getDefaultCache().getStores() == 0 &&
                        server2.getCacheManager(DEFAULT_CM).getDefaultCache().getStores() == 0);
      Assert.assertTrue(server1.getCacheManager(DEFAULT_CM).getDefaultCache().getRemoveHits() == 0 &&
                        server1.getCacheManager(DEFAULT_CM).getDefaultCache().getRemoveHits() == 0);
      cache.put("key", 10);
      Assert.assertTrue(server1.getCacheManager(DEFAULT_CM).getDefaultCache().getStores() == 1 ||
                        server2.getCacheManager(DEFAULT_CM).getDefaultCache().getStores() == 1);
      cache.remove("key");
      Assert.assertTrue(server1.getCacheManager(DEFAULT_CM).getDefaultCache().getRemoveHits() == 1 ||
                        server2.getCacheManager(DEFAULT_CM).getDefaultCache().getRemoveHits() == 1);
   }

   @Test
   public void shouldRetrieveGeneralInformation() throws Exception
   {
      System.out.println("\n----- Retrieving General Information -----\n");
      System.out.println("Memcached port: " + server1.getMemcachedEndpoint().getPort());
      System.out.println("Memcached hostname: " + server1.getMemcachedEndpoint().getInetAddress());
      System.out.println("Hotrod port: " + server1.getHotrodEndpoint().getPort());
      System.out.println("Hotrod hostname: " + server1.getHotrodEndpoint().getInetAddress());
      System.out.println("REST hostname: " + server1.getRESTEndpoint().getInetAddress());
      System.out.println("REST context path: " + server1.getRESTEndpoint().getContextPath());
   }

   @Test
   public void shouldRetrieveCacheManagerInformation()
   {
      System.out.println("\n----- Retrieving Cache Manager Information -----\n");
      System.out.println("Created cache count:  " + server1.getCacheManager(DEFAULT_CM).getCreatedCacheCount());
      System.out.println("Cache manager status: " + server1.getCacheManager(DEFAULT_CM).getCacheManagerStatus());
      System.out.println("Cluster members:      " + server1.getCacheManager(DEFAULT_CM).getClusterMembers());
      System.out.println("Cluster size:         " + server1.getCacheManager(DEFAULT_CM).getClusterSize());
      System.out.println("Created cache count:  " + server1.getCacheManager(DEFAULT_CM).getCreatedCacheCount());
      System.out.println("Defined cache count:  " + server1.getCacheManager(DEFAULT_CM).getDefinedCacheCount());
      System.out.println("Defined cache names:  " + server1.getCacheManager(DEFAULT_CM).getDefinedCacheNames());
      System.out.println("Node address:         " + server1.getCacheManager(DEFAULT_CM).getNodeAddress());
      System.out.println("Physical address:     " + server1.getCacheManager(DEFAULT_CM).getPhysicalAddresses());
      System.out.println("Running cache count:  " + server1.getCacheManager(DEFAULT_CM).getRunningCacheCount());
      System.out.println("Version:              " + server1.getCacheManager(DEFAULT_CM).getVersion());
   }

   @Test
   public void shouldRetrieveCacheInformation()
   {
      System.out.println("\n----- Retrieving Cache Information -----\n");
      System.out.println("Default cache name:   " + server2.getCacheManager(DEFAULT_CM).getCache("default").getCacheName());
      System.out.println("Cache name:           " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getCacheName());
      System.out.println("Cache status:         " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getCacheStatus());
      System.out.println("Average read time:    " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getAverageReadTime());
      System.out.println("Average write time:   " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getAverageWriteTime());
      System.out.println("Elapsed time:         " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getElapsedTime());
      System.out.println("Evictions:            " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getEvictions());
      System.out.println("Hit ratio:            " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getHitRatio());
      System.out.println("Hits:                 " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getHits());
      System.out.println("Misses:               " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getMisses());
      System.out.println("Number of entries:    " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getNumberOfEntries());
      System.out.println("Read-write ratio:     " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getReadWriteRatio());
      System.out.println("Remove hits:          " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getRemoveHits());
      System.out.println("Remote misses:        " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getRemoveMisses());
      System.out.println("Stores:               " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getStores());
      System.out.println("Time since reset:     " + server2.getCacheManager(DEFAULT_CM).getDefaultCache().getTimeSinceReset());
   }

   @Test
   public void shouldRetrieveCacheInformationUsingGenericApproach(@InfinispanResource("container2") RemoteInfinispanServer server)
   {
      System.out.println("\n----- Retrieving Cache Information using semi-generic approach -----\n");
      System.out.println("Average read time:    " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.AVERAGE_READ_TIME));
      System.out.println("Average write time:   " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.AVERAGE_WRITE_TIME));
      System.out.println("Elapsed time:         " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.ELAPSED_TIME));
      System.out.println("Evictions:            " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.EVICTIONS));
      System.out.println("Hit ratio:            " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.HIT_RATIO));
      System.out.println("Hits:                 " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.HITS));
      System.out.println("Misses:               " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.MISSES));
      System.out.println("Number of entries:    " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.NUMBER_OF_ENTRIES));
      System.out.println("Read-write ratio:     " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.READ_WRITE_RATIO));
      System.out.println("Remove hits:          " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.REMOVE_HITS));
      System.out.println("Remote misses:        " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.REMOVE_MISSES));
      System.out.println("Stores:               " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.STORES));
      System.out.println("Time since reset:     " + server.getCacheManager(DEFAULT_CM).getDefaultCache().getStatistics(CacheStatistics.TIME_SINCE_RESET));
   }
}
