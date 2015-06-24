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
package org.infinispan.arquillian.core;

import java.util.Map;

import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.container.spi.event.StartContainer;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.as.arquillian.container.CommonContainerConfiguration;

/**
 * A creator of {@link RemoteInfinispanServer} objects. Creates a single instance of
 * {@link InfinispanContext} and stores {@link RemoteInfinispanServer} objects
 * related to particular containers into the context.
 *
 * {@link RemoteInfinispanServer} instances point to a standalone
 * Infinispan server.
 *
 * Servers can be found in the context through container's name/identifier matching
 * an attribute called <strong>qualifier</strong> on a container tag in Arquillian
 * configuration file.
 *
 * <p>
 * Observes:
 * </p>
 * <ul>
 * <li>{@link SetupContainer}</li>
 * </ul>
 *
 * <p>
 * Produces:
 * </p>
 * <ul>
 * <li>{@link InfinispanContext}</li>
 * </ul>
 *
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 *
 */
public class InfinispanConfigurator
{
   private final String STANDALONE_FLAG = "protocol";  //protocol=hotrod|memcached|websocket
   private final String SKIP_ISPN_CONTEXT_FLAG = "skipIspnContext";  //if the flag is present, container creation and in injection to context is skipped
   private final String JMX_DOMAIN = "jmxDomain";

   @Inject
   @SuiteScoped
   private InstanceProducer<InfinispanContext> infinispanContext;

   /**
    * Creates an {@link InfinispanContext} and stores a {@link RemoteInfinispanServer} in it.
    *
    * @param event an event being observed
    */
   public void configureInfinispan(@Observes SetupContainer event)
   {
      ContainerDef def = event.getContainer().getContainerConfiguration();
      Map<String, String> props = def.getContainerProperties();
      if (props != null && props.containsKey(SKIP_ISPN_CONTEXT_FLAG)) {
         return;
      }

      if (infinispanContext.get() == null)
      {
         infinispanContext.set(new InfinispanContext());
      }

      RemoteInfinispanServer server = null;


      CommonContainerConfiguration conf;
      try
      {
         conf = (CommonContainerConfiguration) event.getContainer().createDeployableConfiguration();
         server = (RemoteInfinispanServer) infinispanContext.get().get(RemoteInfinispanServer.class, event.getContainer().getContainerConfiguration().getContainerName());
         if (server != null)
         {
            if (server instanceof InfinispanServer)
            {
               InfinispanServer orig = (InfinispanServer) server;
               orig.setManagementAddress(conf.getManagementAddress());
               orig.setManagementPort(conf.getManagementPort());
               return;
            }
            else
            {
               throw new RuntimeException("Cannot override properties of a server of different type");
            }
         }
         else
         {
            String jmxDomain = props.get(JMX_DOMAIN);
            server = new InfinispanServer(conf.getManagementAddress(), conf.getManagementPort(), jmxDomain);
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not create deployable configuration", e);
      }

      infinispanContext.get().add(RemoteInfinispanServer.class, event.getContainer().getContainerConfiguration().getContainerName(), server);
   }

   /**
    * Reconfigures server instances when needed.
    *
    * @param event an event being observed
    */
   public void reconfigureInfinispan(@Observes StartContainer event)
   {
      if (infinispanContext.get() != null) {
         AbstractRemoteInfinispanServer server = (AbstractRemoteInfinispanServer) infinispanContext.get().get(RemoteInfinispanServer.class, event.getContainer().getContainerConfiguration().getContainerName());
         server.invalidateMBeanProvider();
      }
   }
}
