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
package org.infinispan.arquillian.utils;

import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.logging.Logger;

/**
 * A provider for the JSR160 connection.
 * 
 * @author Thomas.Diesler@jboss.com
 * @since 03-Dec-2010
 */
public final class MBeanServerConnectionProvider
{

   private static final Logger log = Logger.getLogger(MBeanServerConnectionProvider.class.getName());
   private String jmxServiceUrl;
   private String hostAddr;
   private int port;

   private JMXConnector jmxConnector;
   
   public MBeanServerConnectionProvider(String hostAddr, int port)
   {
       setUpJmxServiceUrl(hostAddr, port, true);
   }
   
   public MBeanServerConnectionProvider(String hostAddr, int port, boolean useRemotingJmx)
   {
       setUpJmxServiceUrl(hostAddr, port, useRemotingJmx);
   }
   
   private void setUpJmxServiceUrl(String hostAddr, int port, boolean useRemotingJmx) 
   {
      this.hostAddr = hostAddr;
      this.port = port; 
      if (useRemotingJmx) 
      {
         this.jmxServiceUrl = getRemotingJmxUrl();
      } 
      else 
      {
         this.jmxServiceUrl = getRmiJmxUrl();
      }
   }

   public MBeanServerConnection getConnection()
   {
      String urlString = System.getProperty("jmx.service.url", this.jmxServiceUrl);
      try
      {
         if (jmxConnector == null)
         {
            log.fine("Connecting JMXConnector to: " + urlString);
            JMXServiceURL serviceURL = new JMXServiceURL(urlString);
            jmxConnector = JMXConnectorFactory.connect(serviceURL, null);
         }
         return jmxConnector.getMBeanServerConnection();
      }
      catch (IOException ex)
      {
         throw new IllegalStateException("Cannot obtain MBeanServerConnection to: " + urlString, ex);
      }
   }
   
   private String getRemotingJmxUrl() 
   {
      return "service:jmx:remoting-jmx://" + this.hostAddr + ":" + this.port;
   }
   
   private String getRmiJmxUrl() 
   {
      return "service:jmx:rmi:///jndi/rmi://" + this.hostAddr + ":" + this.port + "/jmxrmi";
   }
}