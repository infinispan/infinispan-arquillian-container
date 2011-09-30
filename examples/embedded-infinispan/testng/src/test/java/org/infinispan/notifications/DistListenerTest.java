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
package org.infinispan.notifications;

import java.util.List;
import org.infinispan.Cache;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.config.Configuration;
import org.infinispan.distribution.MagicKey;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryEvent;
import org.infinispan.remoting.transport.Address;
import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DistListenerTest extends Arquillian
{
   @InfinispanResource
   DatagridManager d;

   private TestListener listener;

   @BeforeMethod
   public void setUp() throws Exception
   {
      d.createCluster(Configuration.CacheMode.DIST_SYNC, 3);
      // make sure all caches are started...
      d.cache(0);
      d.cache(1);
      d.cache(2);
   }

   @Test
   public void testRemoteGet()
   {
      final String key1 = this.getClass().getName() + "K1";

      List<Address> owners = d.cache(0).getAdvancedCache().getDistributionManager().locate(key1);

      assert owners.size() == 2 : "Key should have 2 owners";

      Cache owner1 = getCacheForAddress(owners.get(0));
      Cache owner2 = getCacheForAddress(owners.get(1));
      assert owner1 != owner2;
      Cache nonOwner = null;
      for (int i = 0; i < 3; i++)
      {
         if (d.cache(i) != owner1 && d.cache(i) != owner2)
         {
            nonOwner = d.cache(i);
            break;
         }
      }
      assert nonOwner != null;

      listener = new TestListener();

      // test owner puts and listens:
      assertCreated(false);
      assertModified(false);
      owner1.addListener(listener);
      owner1.put(key1, "hello");
      assertModified(true);
      assertCreated(true);
      assertCreated(false);
      assertModified(false);
      owner1.put(key1, "hello");
      assertModified(true);
      assertCreated(false);

      // now de-register:
      owner1.removeListener(listener);
      owner1.put(key1, "hello");
      assertModified(false);
      assertCreated(false);

      // put on non-owner and listens on owner:
      owner1.addListener(listener);
      nonOwner.put(key1, "hello");
      assertModified(true);
      owner1.removeListener(listener);
      assertModified(false);

      // listen on non-owner:
      nonOwner.addListener(listener);
      nonOwner.put(key1, "hello");
      assertModified(true);

      // listen on non-owner non-putting:
      owner1.put(key1, "hello");
      assertModified(false);
   }

   private void assertCreated(boolean b)
   {
      assert listener.created == b;
      listener.created = false;
   }

   private void assertModified(boolean b)
   {
      assert listener.modified == b;
      listener.modified = false;
   }

   @SuppressWarnings("unchecked")
   private Cache<MagicKey, String> getCacheForAddress(Address a)
   {
      for (Cache<?, ?> c : d.caches())
         if (c.getAdvancedCache().getRpcManager().getAddress().equals(a))
            return (Cache<MagicKey, String>) c;
      return null;
   }

   @Listener
   public class TestListener
   {
      boolean created = false;
      boolean modified = false;

      @CacheEntryCreated
      public void create(CacheEntryEvent e)
      {
         created = true;
      }

      @CacheEntryModified
      public void modify(CacheEntryEvent e)
      {
         modified = true;
      }
   }
}