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
package org.infinispan.arquillian.core.test;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.infinispan.arquillian.core.InfinispanConfigurator;
import org.infinispan.arquillian.core.InfinispanContext;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.arquillian.core.InfinispanTestEnricher;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.infinispan.arquillian.core.RemoteInfinispanServers;
import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests InfinispanTestEnricher class. It should be able to inject proper
 * InfinispanInfo objects either into object's fields or method's parameters
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class EnricherTestCase extends AbstractTestTestBase
{
   @Mock
   private ServiceLoader serviceLoader;

   @Override
   protected void addExtensions(List<Class<?>> extensions)
   {
      extensions.add(InfinispanConfigurator.class);
      extensions.add(InfinispanTestEnricher.class);
   }

   @Before
   public void setMocks()
   {
      TestEnricher testEnricher = new InfinispanTestEnricher();
      bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);
      Mockito.when(serviceLoader.onlyOne(TestEnricher.class)).thenReturn(testEnricher);
   }

   @Test
   public void shouldEnrichFieldWithDatagridManager()
   {
      DatagridManagerEnrichedClass enrichedObject = new DatagridManagerEnrichedClass();
      TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
      getManager().inject(testEnricher);
      // enrich class via InfinispanTestEnricher
      testEnricher.enrich(enrichedObject);
      Assert.assertNotNull(enrichedObject.dm);
      //regardless of the number of InfinispanResource annotations on a test class or their parameters there's only
      //one datagrid manager and it is injected to all such fields
      Assert.assertTrue((enrichedObject.dm == enrichedObject.dm2) &&  (enrichedObject.dm2 == enrichedObject.dm3));
   }

   @Test
   public void shouldNOTEnrichParametersWithDatagridManager() throws Exception
   {
      DatagridManagerMethodEnrichedClass enrichedObject = new DatagridManagerMethodEnrichedClass();
      TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
      getManager().inject(testEnricher);
      Method testMethod = DatagridManagerMethodEnrichedClass.class.getMethod("testMethodEnrichment", DatagridManager.class);
      // enrich method parameters via InfinispanTestEnricher
      Object[] parameters = testEnricher.resolve(testMethod);
      testMethod.invoke(enrichedObject, parameters);
      //DatagridManager object should NOT be injected into method parameters
      Assert.assertNull(enrichedObject.dm);
   }

   @Test
   public void shouldEnrichRemoteInfinispanServer() throws Exception
   {
       RemoteInfinispanServer server1 = mock(RemoteInfinispanServer.class);
       RemoteInfinispanServer server2 = mock(RemoteInfinispanServer.class);
       RemoteInfinispanServer server3 = mock(RemoteInfinispanServer.class);
       TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
       getManager().inject(testEnricher);
       InfinispanContext ctx = injectContext();
       ctx.add(RemoteInfinispanServer.class, "server1", server1);
       ctx.add(RemoteInfinispanServer.class, "server2", server2);
       ctx.add(RemoteInfinispanServer.class, "server3", server3);
       RemoteInfinispanServerEnrichedClass enriched = new RemoteInfinispanServerEnrichedClass();
       testEnricher.enrich(enriched);
       Assert.assertSame(server1, enriched.server1);
       Assert.assertSame(server2, enriched.server2);
       Assert.assertNull(enriched.server3);
       Assert.assertNull(enriched.server4);
   }

   @Test
   public void shouldEnrichRemoteInfinispanServerOnMethod() throws Exception
   {
       RemoteInfinispanServer container2 = mock(RemoteInfinispanServer.class);
       TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
       getManager().inject(testEnricher);
       InfinispanContext ctx = injectContext();
       ctx.add(RemoteInfinispanServer.class, "container2", container2);
       InfServerMethodEnrichedClass enriched = new InfServerMethodEnrichedClass();
       testEnricher.enrich(enriched);
       Method testMethod = InfServerMethodEnrichedClass.class.getMethod("testMethodEnrichment", RemoteInfinispanServer.class);
       testMethod.invoke(enriched, testEnricher.resolve(testMethod));
       Assert.assertSame(container2, enriched.server);
   }
   
   @Test
   public void shouldEnrichRemoteInfinispanServers() throws Exception
   {
       RemoteInfinispanServer server1 = mock(RemoteInfinispanServer.class);
       TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
       getManager().inject(testEnricher);
       InfinispanContext ctx = injectContext();
       ctx.add(RemoteInfinispanServer.class, "server1", server1);
       RemoteInfinispanServersEnrichedClass enriched = new RemoteInfinispanServersEnrichedClass();
       testEnricher.enrich(enriched);
       Assert.assertNotNull(enriched.servers);
       Assert.assertSame(enriched.servers.getServer("server1"), server1);
   }


   static class ContextHolder {
       @Inject
       @SuiteScoped
       InstanceProducer<InfinispanContext> infinispanContext;
   }

   static class DatagridManagerEnrichedClass 
   {
      @InfinispanResource
      DatagridManager dm;
      
      @InfinispanResource
      DatagridManager dm2;
      
      @InfinispanResource("xx")
      DatagridManager dm3;
   }
   
   static class DatagridManagerMethodEnrichedClass
   {
      DatagridManager dm;
      
      //this injection is not supposed to work
      public void testMethodEnrichment(@InfinispanResource DatagridManager dmLoc)
      {
         dm = dmLoc;
      }
   }

   static class InfServerMethodEnrichedClass
   {
      RemoteInfinispanServer server;
      
      public void testMethodEnrichment(@InfinispanResource("container2") RemoteInfinispanServer locServer)
      {
         server = locServer;
      }
   }

   static class RemoteInfinispanServersEnrichedClass
   {
      @InfinispanResource
      RemoteInfinispanServers servers;
   }

   static class RemoteInfinispanServerEnrichedClass
   {
       @InfinispanResource("server1")
       RemoteInfinispanServer server1;

       @InfinispanResource("server2")
       RemoteInfinispanServer server2;

       RemoteInfinispanServer server3;

       RemoteInfinispanServer server4;
   }

   private InfinispanContext injectContext()
   {
       ContextHolder holder = new ContextHolder();
       getManager().inject(holder);
       InfinispanContext ctx = new InfinispanContext();
       holder.infinispanContext.set(ctx);
       return ctx;
   }
}
