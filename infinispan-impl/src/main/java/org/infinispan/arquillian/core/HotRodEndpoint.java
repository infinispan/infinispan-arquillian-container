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

import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.arquillian.utils.MBeanUtils;

/**
 * Holds HotRod server module's Internet address and port number. Can be retrieved inside a test to
 * find out on which address/port the HotRod server module is running.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class HotRodEndpoint
{
   private MBeanServerConnectionProvider provider;
   
   private MBeanObjectsProvider mBeans;

   public HotRodEndpoint(MBeanServerConnectionProvider provider, MBeanObjectsProvider mBeans)
   {
      this.provider = provider;
      this.mBeans = mBeans;
   }

   /**
    * 
    * Retrieves an Internet address on which the HotRod server module is running.
    * 
    * @return the Internet address on which the HotRod server module is running
    */
   public InetAddress getInetAddress()
   {
      String hostname;
      try
      {
         hostname = MBeanUtils.getMBeanAttribute(provider, mBeans.getHorRodServerMBean(), ServerModuleAttributes.HOST_NAME);
         return InetAddress.getByName(hostname);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not retrieve HotRod host", e);
      }
   }

   /**
    * 
    * Retrieves a port on which the HotRod server module is running.
    * 
    * @return the port on which the HotRod server module is running
    */
   public int getPort()
   {
      String port;
      try
      {
         port = MBeanUtils.getMBeanAttribute(provider, mBeans.getHorRodServerMBean(), ServerModuleAttributes.PORT);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not retrieve HotRod port", e);
      }
      return Integer.parseInt(port);
   }
}
