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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.infinispan.arquillian.core.InfinispanConfigurator;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.arquillian.core.InfinispanTestEnricher;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests InfinispanTestEnricher class. It should be able to inject proper
 * InfinispanInfo objects either into object's fields or method's parameters
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
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

   static class ServerEnrichedClass
   {
      @InfinispanResource
      RemoteInfinispanServer server;
   }

   static class InfServerMethodEnrichedClass
   {
      RemoteInfinispanServer server;
      
      public void testMethodEnrichment(@InfinispanResource("container2") RemoteInfinispanServer locServer)
      {
         server = locServer;
      }
   }

   private Object getFieldValue(Object object, String fieldName)
   {
      Field[] fields = object.getClass().getDeclaredFields();
      for (Field f : fields)
      {
         if (f.getName().equals(fieldName))
         {
            if (!f.isAccessible())
            {
               f.setAccessible(true);
            }
            try
            {
               return f.get(object);
            }
            catch (Exception e)
            {
               return null;
            }
         }
      }
      return null;
   }
   
   private Object getSuperClassFieldValue(Object object, String fieldName)
   {
      Field[] fields = object.getClass().getSuperclass().getDeclaredFields();
      for (Field f : fields)
      {
         if (f.getName().equals(fieldName))
         {
            if (!f.isAccessible())
            {
               f.setAccessible(true);
            }
            try
            {
               return f.get(object);
            }
            catch (Exception e)
            {
               return null;
            }
         }
      }
      return null;
   }
   
}
