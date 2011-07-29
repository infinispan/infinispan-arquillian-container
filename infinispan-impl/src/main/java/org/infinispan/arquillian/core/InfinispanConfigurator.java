/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.arquillian.core;

import org.infinispan.arquillian.container.managed.InfinispanConfiguration;
import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanObjectsProvider.Domain;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.as.arquillian.container.CommonContainerConfiguration;

/**
 * Creates {@link InfinispanContext} and stores {@link RemoteInfinispanServer} object
 * related to the particular container.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class InfinispanConfigurator
{
   private final String STANDALONE_FLAG = "protocol";  //protocol=hotrod|memcached|websocket

   @Inject
   @SuiteScoped
   private InstanceProducer<InfinispanContext> infinispanContext;

   public void configureInfinispan(@Observes SetupContainer event)
   {
      ContainerDef def = event.getContainer().getContainerConfiguration();

      if (infinispanContext.get() == null)
      {
         infinispanContext.set(new InfinispanContext());
      }

      RemoteInfinispanServer server = null;

      if (def.getContainerProperties().containsKey(STANDALONE_FLAG))
      {
         InfinispanConfiguration conf;
         try
         {
            conf = (InfinispanConfiguration) event.getContainer().createDeployableConfiguration();
            server = new RemoteInfinispanServerImpl(conf.getHost(), conf.getJmxPort(), new MBeanObjectsProvider(Domain.STANDALONE));
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not create deployable configuration", e);
         }
      }
      else
      {
         CommonContainerConfiguration conf;
         try
         {
            conf = (CommonContainerConfiguration) event.getContainer().createDeployableConfiguration();
            server = new RemoteInfinispanServerImpl(conf.getBindAddress().getHostName(), conf.getJmxPort(), new MBeanObjectsProvider(Domain.EDG));
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not create deployable configuration", e);
         }
      }
      
      infinispanContext.get().add(event.getContainer().getContainerConfiguration().getContainerName(), server);
   }
}
