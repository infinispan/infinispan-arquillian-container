/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
package org.infinispan.arquillian.core.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.infinispan.arquillian.container.managed.InfinispanConfiguration;
import org.infinispan.arquillian.core.InfinispanConfigurator;
import org.infinispan.arquillian.core.InfinispanResource;
import org.infinispan.arquillian.core.InfinispanTestEnricher;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
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
   public void shouldEnrichFieldsOnObject() throws Exception
   {
      final String containerName = "container1";
      final int portNumber = 1091;
      Container container = mock(Container.class);
      InfinispanConfiguration conf = mock(InfinispanConfiguration.class);
      ContainerDef def = mock(ContainerDef.class);
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("protocol", "hotrod");
      when(def.getContainerProperties()).thenReturn(properties);
      when(def.getContainerName()).thenReturn(containerName);
      when(container.getContainerConfiguration()).thenReturn(def);
      when(conf.getJmxPort()).thenReturn(portNumber);
      when(conf.getHost()).thenReturn("localhost");
      when(container.createDeployableConfiguration()).thenReturn(conf);
      fire(new SetupContainer(container));
      
      EnrichedClass enrichedObject = new EnrichedClass();
      TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
      getManager().inject(testEnricher);
      // enrich class via InfinispanTestEnricher
      testEnricher.enrich(enrichedObject);
      Assert.assertNotNull(enrichedObject.server);
      MBeanServerConnectionProvider injectedProvider = (MBeanServerConnectionProvider) getFieldValue(enrichedObject.server, "provider");
      Integer injectedPort = (Integer) getFieldValue(injectedProvider, "port");
      // must correspond to container1 (portNumber=1091)
      Assert.assertEquals(portNumber, injectedPort.intValue());
   }

   @Test
   public void shouldEnrichParametersOnMethod() throws Exception
   {
      final String containerName = "container2";
      final int portNumber = 1092;
      Container container = mock(Container.class);
      InfinispanConfiguration conf = mock(InfinispanConfiguration.class);
      ContainerDef def = mock(ContainerDef.class);
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("protocol", "hotrod");
      when(def.getContainerProperties()).thenReturn(properties);
      when(def.getContainerName()).thenReturn(containerName);
      when(container.getContainerConfiguration()).thenReturn(def);
      when(conf.getJmxPort()).thenReturn(portNumber);
      when(conf.getHost()).thenReturn("localhost");
      when(container.createDeployableConfiguration()).thenReturn(conf);
      fire(new SetupContainer(container));

      MethodEnrichedClass enrichedObject = new MethodEnrichedClass();
      TestEnricher testEnricher = serviceLoader.onlyOne(TestEnricher.class);
      getManager().inject(testEnricher);
      Method testMethod = MethodEnrichedClass.class.getMethod("testMethodEnrichment", RemoteInfinispanServer.class);
      // enrich method parameters via InfinispanTestEnricher
      Object[] parameters = testEnricher.resolve(testMethod);
      testMethod.invoke(enrichedObject, parameters);
      MBeanServerConnectionProvider injectedProvider = (MBeanServerConnectionProvider) getFieldValue(enrichedObject.server, "provider");
      Integer injectedPort = (Integer) getFieldValue(injectedProvider, "port");
      // must correspond to container2 (portNumber=1092)
      Assert.assertEquals(portNumber, injectedPort.intValue());
   }

   static class EnrichedClass
   {
      @InfinispanResource
      RemoteInfinispanServer server;
   }

   static class MethodEnrichedClass
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
}
