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
package org.infinispan.expiry;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.infinispan.Cache;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.container.entries.MortalCacheEntry;
import org.infinispan.container.entries.TransientCacheEntry;
import org.infinispan.container.entries.TransientMortalCacheEntry;
import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class ReplicatedExpiryTest extends Arquillian
{
   @InfinispanResource
   DatagridManager dm;

   @BeforeMethod
   public void setUp() throws Exception
   {
      ConfigurationBuilder bld = new ConfigurationBuilder();
      bld.clustering().cacheMode(CacheMode.REPL_SYNC);
      dm.createClusteredCaches(2, "cache", bld);
   }

   @Test
   public void testLifespanExpiryReplicates()
   {
      Cache c1 = dm.cache(0, "cache");
      Cache c2 = dm.cache(1, "cache");
      long lifespan = 3000;
      c1.put("k", "v", lifespan, MILLISECONDS);
      InternalCacheEntry ice = c2.getAdvancedCache().getDataContainer().get("k");

      assert ice instanceof MortalCacheEntry;
      assert ice.getLifespan() == lifespan;
      assert ice.getMaxIdle() == -1;
   }

   @Test
   public void testIdleExpiryReplicates()
   {
      Cache c1 = dm.cache(0, "cache");
      Cache c2 = dm.cache(1, "cache");
      long idle = 3000;
      c1.put("k", "v", -1, MILLISECONDS, idle, MILLISECONDS);
      InternalCacheEntry ice = c2.getAdvancedCache().getDataContainer().get("k");

      assert ice instanceof TransientCacheEntry;
      assert ice.getMaxIdle() == idle;
      assert ice.getLifespan() == -1;
   }

   @Test
   public void testBothExpiryReplicates()
   {
      Cache c1 = dm.cache(0, "cache");
      Cache c2 = dm.cache(1, "cache");
      long lifespan = 10000;
      long idle = 3000;
      c1.put("k", "v", lifespan, MILLISECONDS, idle, MILLISECONDS);
      InternalCacheEntry ice = c2.getAdvancedCache().getDataContainer().get("k");
      assert ice instanceof TransientMortalCacheEntry;
      assert ice.getLifespan() == lifespan : "Expected " + lifespan + " but was " + ice.getLifespan();
      assert ice.getMaxIdle() == idle : "Expected " + idle + " but was " + ice.getMaxIdle();
   }
}
