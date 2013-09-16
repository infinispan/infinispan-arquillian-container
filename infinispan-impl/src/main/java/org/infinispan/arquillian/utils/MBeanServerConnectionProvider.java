/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 * @author Martin Gencur
 * @since 03-Dec-2010
 *
 * (imported from JBoss AS 7 and modified)
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
      setUpJmxServiceUrl(hostAddr, port);
   }
   
   private void setUpJmxServiceUrl(String hostAddr, int port)
   {
      this.hostAddr = hostAddr;
      this.port = port; 
      this.jmxServiceUrl = getRemotingJmxUrl();
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

   private String getRemotingJmxUrl() {
      if (InetAddressValidator.isValidInet6Address(this.hostAddr))
      {
         return "service:jmx:remoting-jmx://[" + this.hostAddr + "]:" + this.port;
      }
      else
      {
         return "service:jmx:remoting-jmx://" + this.hostAddr + ":" + this.port;
      }
   }
}