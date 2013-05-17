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

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.infinispan.arquillian.core.InfinispanConfigurator;
import org.infinispan.arquillian.core.InfinispanContext;
import org.infinispan.arquillian.core.InfinispanTestEnricher;
import org.infinispan.arquillian.core.RemoteInfinispanServer;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.test.spi.context.SuiteContext;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.jboss.as.arquillian.container.CommonContainerConfiguration;
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
   public void shouldFindInfinispanServerInContext() throws Exception
   {
      final String containerName = "container1";
      Container container = mock(Container.class);
      CommonContainerConfiguration conf = mock(CommonContainerConfiguration.class);
      ContainerDef def = mock(ContainerDef.class);
      Map<String, String> properties = new HashMap<String, String>();
      when(def.getContainerProperties()).thenReturn(properties);
      when(def.getContainerName()).thenReturn(containerName);
      when(container.getContainerConfiguration()).thenReturn(def);
      when(conf.getManagementPort()).thenReturn(9999);
      when(conf.getManagementAddress()).thenReturn("localhost");
      when(container.createDeployableConfiguration()).thenReturn(conf);

      fire(new SetupContainer(container));
      InfinispanContext ctx = getManager().getContext(SuiteContext.class).getObjectStore().get(InfinispanContext.class);

      Assert.assertNotNull(ctx.get(RemoteInfinispanServer.class, containerName));
   }
}
