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
package org.jboss.infinispan.arquillian.core;

import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.jboss.as.arquillian.container.CommonContainerConfiguration;

import org.jboss.infinispan.arquillian.container.managed.InfinispanConfiguration;
import org.jboss.infinispan.arquillian.utils.MBeanObjectsEDG;
import org.jboss.infinispan.arquillian.utils.MBeanObjectsStandalone;

/**
 * Creates {@link InfinispanContext} and stores {@link InfinispanInfo} object
 * related to the particular container.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class InfinispanConfigurator
{
   private static final String STANDALONE_FLAG = "ispnHome";

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

      InfinispanInfo info = null;

      if (def.getContainerProperties().containsKey(STANDALONE_FLAG))
      {
         InfinispanConfiguration conf;
         try
         {
            conf = (InfinispanConfiguration) event.getContainer().createDeployableConfiguration();
            info = new InfinispanInfoImpl(conf.getHost(), conf.getJmxPort(), new MBeanObjectsStandalone());
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not create deployable configuration");
         }
      }
      else
      {
         //throw new RuntimeException("Retrieving stastistics from a non-community Infinispan is not allowed yet");
         CommonContainerConfiguration conf;
         try
         {
            conf = (CommonContainerConfiguration) event.getContainer().createDeployableConfiguration();
            info = new InfinispanInfoImpl(conf.getBindAddress().getHostName(), conf.getJmxPort(), new MBeanObjectsEDG());
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not create deployable configuration");
         }
      }

      infinispanContext.get().add(event.getContainer().getContainerConfiguration().getContainerName(), info);
   }
}
