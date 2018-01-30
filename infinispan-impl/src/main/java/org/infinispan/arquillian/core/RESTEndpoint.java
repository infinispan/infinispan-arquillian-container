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

import java.net.InetAddress;

import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.arquillian.utils.MBeanUtils;

/**
 * Holds REST server module's inet address and context path. Can be retrieved inside 
 * a test to find out on which contextPath the REST server module is running. Available only for
 * the Enterprise Data Grid, not for a standalone Infinispan server.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class RESTEndpoint
{
   private final String contextPath = "/rest";

   private MBeanServerConnectionProvider provider;
   
   private MBeanObjectsProvider mBeans;

   public RESTEndpoint(MBeanServerConnectionProvider provider, MBeanObjectsProvider mBeans)
   {
      this.provider = provider;
      this.mBeans = mBeans;
   }

   /**
    * 
    * Retrieves an Internet address on which the REST server module is running.
    * 
    * @return the Internet address on which the REST server module is running
    */
   public InetAddress getInetAddress()
   {
      String hostname;
      try
      {
         hostname = MBeanUtils.getMBeanAttribute(provider, mBeans.getRestServerMBean(), ServerModuleAttributes.HOST_NAME);
         return InetAddress.getByName(hostname);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not retrieve REST host", e);
      }
   }

   /**
    * 
    * Retrieves a context path on which the REST server module is running.
    * 
    * @return the context path on which the REST server module is running
    */
   public String getContextPath()
   {
      return contextPath;
   }
}
