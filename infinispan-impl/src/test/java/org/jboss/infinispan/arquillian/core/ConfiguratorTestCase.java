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
package org.jboss.infinispan.arquillian.core;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.test.spi.context.SuiteContext;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.jboss.as.arquillian.container.CommonContainerConfiguration;
import org.jboss.infinispan.arquillian.container.managed.InfinispanConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests InfinispanConfigurator class. It should store InfinispanInfo objects in
 * InfinispanContext based on container configuration.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfiguratorTestCase extends AbstractTestTestBase
{
   @Override
   protected void addExtensions(List<Class<?>> extensions)
   {
      extensions.add(InfinispanConfigurator.class);
      extensions.add(InfinispanTestEnricher.class);
   }

   @Test
   public void shouldFindStandaloneInfinispanInfoInContext() throws Exception
   {
      final String containerName = "container1";
      Container container = mock(Container.class);
      InfinispanConfiguration conf = mock(InfinispanConfiguration.class);
      ContainerDef def = mock(ContainerDef.class);
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("protocol", "hotrod");
      when(def.getContainerProperties()).thenReturn(properties);
      when(def.getContainerName()).thenReturn(containerName);
      when(container.getContainerConfiguration()).thenReturn(def);
      when(conf.getJmxPort()).thenReturn(1091);
      when(conf.getHost()).thenReturn("localhost");
      when(container.createDeployableConfiguration()).thenReturn(conf);

      fire(new SetupContainer(container));
      InfinispanContext ctx = getManager().getContext(SuiteContext.class).getObjectStore().get(InfinispanContext.class);
      Assert.assertNotNull(ctx.get(containerName));
      String expectedMessage = "Could not retrieve REST endpoint -> not applicable for standalone Infinispan Server";
      try
      {
         ctx.get(containerName).getRESTEndpoint();
      }
      catch (Exception e)
      {
         Assert.assertEquals("Expected different exception message", expectedMessage, e.getMessage());
      }
   }

   @Test
   public void shouldFindEDGInfinispanInfoInContext() throws Exception
   {
      final String containerName = "container1";
      Container container = mock(Container.class);
      CommonContainerConfiguration conf = mock(CommonContainerConfiguration.class);
      ContainerDef def = mock(ContainerDef.class);
      Map<String, String> properties = new HashMap<String, String>();
      when(def.getContainerProperties()).thenReturn(properties);
      when(def.getContainerName()).thenReturn(containerName);
      when(container.getContainerConfiguration()).thenReturn(def);
      when(conf.getJmxPort()).thenReturn(1091);
      InetAddress addr = InetAddress.getByName("localhost");
      when(conf.getBindAddress()).thenReturn(addr);
      when(container.createDeployableConfiguration()).thenReturn(conf);

      fire(new SetupContainer(container));
      InfinispanContext ctx = getManager().getContext(SuiteContext.class).getObjectStore().get(InfinispanContext.class);

      Assert.assertNotNull(ctx.get(containerName));
      Assert.assertEquals("Expected context path configured", "/datagrid", ctx.get(containerName).getRESTEndpoint().getContextPath());
   }
}
