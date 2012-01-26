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

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.infinispan.arquillian.container.managed.InfinispanConfiguration;
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
 * {@link RemoteInfinispanServer} instances can point either to a standalone 
 * Infinispan server or an JBoss Application Server with Infinispan embedded 
 * (i.e. Enterprise Data Grid). Container's type resolution is based on presence
 * of a <strong>protocol</strong> property which is mandatory for standalone 
 * Infinispan server configuration (in arquillian.xml) and not used for JBoss AS 
 * with Infinispan embedded.
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
            server = new StandaloneInfinispanServer(getInetAddress(conf.getHost()), conf.getJmxPort());
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
            server = new EDGServer(conf.getManagementAddress(), conf.getManagementPort());
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not create deployable configuration", e);
         }
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
      RemoteInfinispanServer server = (RemoteInfinispanServer) infinispanContext.get().get(RemoteInfinispanServer.class, event.getContainer().getContainerConfiguration().getContainerName());
      if (server instanceof EDGServer) 
      {
         ((EDGServer) server).invalidateMBeanProvider();
      }
   }
   
   protected static InetAddress getInetAddress(String name)
   {
      try
      {
         return InetAddress.getByName(name);
      }
      catch (UnknownHostException e)
      {
         throw new IllegalArgumentException("Unknown host: " + name);
      }
   }
}
