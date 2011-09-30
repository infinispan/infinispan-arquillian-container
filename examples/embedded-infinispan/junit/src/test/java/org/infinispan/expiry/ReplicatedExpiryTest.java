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
package org.infinispan.expiry;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import org.infinispan.Cache;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.config.Configuration;
import org.infinispan.container.entries.InternalCacheEntry;
import org.infinispan.container.entries.MortalCacheEntry;
import org.infinispan.container.entries.TransientCacheEntry;
import org.infinispan.container.entries.TransientMortalCacheEntry;
import org.infinispan.test.arquillian.DatagridManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;

@RunWith(Arquillian.class)
public class ReplicatedExpiryTest
{
   @InfinispanResource
   DatagridManager dm;

   @Before
   public void setUp() throws Exception
   {
      Configuration cfg = dm.getDefaultClusteredConfig(Configuration.CacheMode.REPL_SYNC);
      dm.createClusteredCaches(2, "cache", cfg);
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
