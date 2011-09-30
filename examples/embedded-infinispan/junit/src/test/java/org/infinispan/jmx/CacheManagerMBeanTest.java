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
package org.infinispan.jmx;

import static org.infinispan.test.TestingUtil.getCacheManagerObjectName;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ServiceNotFoundException;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.config.Configuration;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.arquillian.DatagridManager;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CacheManagerMBeanTest
{
   @InfinispanResource
   DatagridManager dm;

   public static final String JMX_DOMAIN = CacheManagerMBeanTest.class.getSimpleName();

   private MBeanServer server;
   private ObjectName name;

   @Before
   public void setUp() throws Exception
   {
      EmbeddedCacheManager cacheManager = TestCacheManagerFactory.createCacheManagerEnforceJmxDomain(JMX_DOMAIN, true, false);
      dm.registerCacheManagers(cacheManager);
      name = getCacheManagerObjectName(JMX_DOMAIN);
      server = PerThreadMBeanServerLookup.getThreadMBeanServer();
      server.invoke(name, "startCache", new Object[] {}, new String[] {});
   }

   @Test
   public void testJmxOperations() throws Exception
   {
      assert server.getAttribute(name, "CreatedCacheCount").equals("1");
      assert server.getAttribute(name, "DefinedCacheCount").equals("0") : "Was " + server.getAttribute(name, "DefinedCacheCount");
      assert server.getAttribute(name, "DefinedCacheNames").equals("[]");
      assert server.getAttribute(name, "RunningCacheCount").equals("1");

      // now define some new caches
      dm.manager().defineConfiguration("a", new Configuration());
      dm.manager().defineConfiguration("b", new Configuration());
      dm.manager().defineConfiguration("c", new Configuration());
      assert server.getAttribute(name, "CreatedCacheCount").equals("1");
      assert server.getAttribute(name, "DefinedCacheCount").equals("3");
      assert server.getAttribute(name, "RunningCacheCount").equals("1");
      String attribute = (String) server.getAttribute(name, "DefinedCacheNames");
      assert attribute.contains("a(");
      assert attribute.contains("b(");
      assert attribute.contains("c(");

      // now start some caches
      server.invoke(name, "startCache", new Object[] { "a" }, new String[] { String.class.getName() });
      server.invoke(name, "startCache", new Object[] { "b" }, new String[] { String.class.getName() });
      assert server.getAttribute(name, "CreatedCacheCount").equals("3");
      assert server.getAttribute(name, "DefinedCacheCount").equals("3");
      assert server.getAttribute(name, "RunningCacheCount").equals("3");
      attribute = (String) server.getAttribute(name, "DefinedCacheNames");
      assert attribute.contains("a(");
      assert attribute.contains("b(");
      assert attribute.contains("c(");
   }

   @Test
   public void testInvokeJmxOperationNotExposed() throws Exception
   {
      try
      {
         server.invoke(name, "stop", new Object[] {}, new String[] {});
         assert false : "Method not exposed, invocation should have failed";
      }
      catch (MBeanException mbe)
      {
         assert mbe.getCause() instanceof ServiceNotFoundException;
      }

   }
   
   @Test
   public void testAddressInformation() throws Exception
   {
      assert server.getAttribute(name, "NodeAddress").equals("local");
      assert server.getAttribute(name, "ClusterMembers").equals("local");
      assert server.getAttribute(name, "PhysicalAddresses").equals("local");
      assert server.getAttribute(name, "ClusterSize").equals(1);
   }
}
