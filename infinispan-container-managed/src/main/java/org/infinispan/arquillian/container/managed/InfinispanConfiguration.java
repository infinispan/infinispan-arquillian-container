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
package org.infinispan.arquillian.container.managed;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.container.spi.client.deployment.Validate;

/**
 * A
 * {@link org.jboss.arquillian.container.spi.client.container.ContainerConfiguration}
 * implementation for the Infinispan container.
 * 
 * The configuration class contains attributes which can be set via properties
 * specified in an arquillian.xml configuration file. Only the
 * {@link InfinispanConfiguration#protocol} attribute is mandatory. Furthermore,
 * the {@link InfinispanConfiguration#ispnHome} attribute should be specified (either
 * in arquillian.xml file or as an environment property called ISPN_HOME)
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class InfinispanConfiguration implements ContainerConfiguration
{
   private String host = "localhost";

   private int port; // different defaults for hotrod/memcached/websocket

   private long masterThreads; // by default unlimited

   private long workerThreads; // by default unlimited

   private String cacheConfig = System.getProperty("infinispan.server.config.file.name");

   private String protocol; // mandatory, possible values are hotrod, memcached, websocket

   private int idleTimeout = -1;

   private boolean tcpNoDelay = true;

   private int sendBufSize; // by default defined by OS

   private int recvBufSize; // by default defined by OS

   private String proxyHost;

   private int proxyPort;

   private long topoLockTimeout = 10000L;

   private long topoReplTimeout = 10000L;

   private boolean topoStateTransfer = true;

   private String cacheManagerClass;

   private String ispnHome = System.getenv("ISPN_HOME");

   private String javaHome = System.getenv("JAVA_HOME");

   private String javaVmArguments = "-Xmx512m -XX:MaxPermSize=128m";

   private int startupTimeoutInSeconds = 30;

   private int shutdownTimeoutInSeconds = 20;

   private int jmxPort = 1090;
   
   private String systemProperties = "";

   public void validate() throws ConfigurationException
   {
      Validate.configurationDirectoryExists(ispnHome, "Either ISPN_HOME environment variable or ispnHome property in Arquillian configuration must be set and point to a valid directory");
      Validate.notNull(protocol, "protocol property in Arquillian configuration must be set");
      validateProtocol(protocol);
   }

   private void validateProtocol(String protocol)
   {
      if (!("hotrod".equals(protocol) || "memcached".equals(protocol) || "websocket".equals(protocol)))
      {
         throw new IllegalArgumentException("Valid protocol name must be specified (hotrod|memcached|websocket)");
      }
   }

   public void setJavaHome(String javaHome)
   {
      this.javaHome = javaHome;
   }

   public String getJavaHome()
   {
      return javaHome;
   }

   public void setJavaVmArguments(String javaVmArguments)
   {
      this.javaVmArguments = javaVmArguments;
   }

   public String getJavaVmArguments()
   {
      return javaVmArguments;
   }

   public String getHost()
   {
      return host;
   }

   public void setHost(String host)
   {
      this.host = host;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   public long getMasterThreads()
   {
      return masterThreads;
   }

   public void setMasterThreads(long masterThreads)
   {
      this.masterThreads = masterThreads;
   }

   public long getWorkerThreads()
   {
      return workerThreads;
   }

   public void setWorkerThreads(long workerThreads)
   {
      this.workerThreads = workerThreads;
   }

   public String getCacheConfig()
   {
      return cacheConfig;
   }

   public void setCacheConfig(String cacheConfig)
   {
      this.cacheConfig = cacheConfig;
   }

   public String getProtocol()
   {
      return protocol;
   }

   public void setProtocol(String protocol)
   {
      this.protocol = protocol;
   }

   public int getIdleTimeout()
   {
      return idleTimeout;
   }

   public void setIdleTimeout(int idleTimeout)
   {
      this.idleTimeout = idleTimeout;
   }

   public boolean isTcpNoDelay()
   {
      return tcpNoDelay;
   }

   public void setTcpNoDelay(boolean tcpNoDelay)
   {
      this.tcpNoDelay = tcpNoDelay;
   }

   public int getSendBufSize()
   {
      return sendBufSize;
   }

   public void setSendBufSize(int sendBufSize)
   {
      this.sendBufSize = sendBufSize;
   }

   public int getRecvBufSize()
   {
      return recvBufSize;
   }

   public void setRecvBufSize(int recvBufSize)
   {
      this.recvBufSize = recvBufSize;
   }

   public String getProxyHost()
   {
      return proxyHost;
   }

   public void setProxyHost(String proxyHost)
   {
      this.proxyHost = proxyHost;
   }

   public int getProxyPort()
   {
      return proxyPort;
   }

   public void setProxyPort(int proxyPort)
   {
      this.proxyPort = proxyPort;
   }

   public long getTopoLockTimeout()
   {
      return topoLockTimeout;
   }

   public void setTopoLockTimeout(long topoLockTimeout)
   {
      this.topoLockTimeout = topoLockTimeout;
   }

   public long getTopoReplTimeout()
   {
      return topoReplTimeout;
   }

   public void setTopoReplTimeout(long topoReplTimeout)
   {
      this.topoReplTimeout = topoReplTimeout;
   }

   public boolean isTopoStateTransfer()
   {
      return topoStateTransfer;
   }

   public void setTopoStateTransfer(boolean topoStateTransfer)
   {
      this.topoStateTransfer = topoStateTransfer;
   }

   public String getCacheManagerClass()
   {
      return cacheManagerClass;
   }

   public void setCacheManagerClass(String cacheManagerClass)
   {
      this.cacheManagerClass = cacheManagerClass;
   }

   public String getIspnHome()
   {
      return ispnHome;
   }

   public void setIspnHome(String ispnHome)
   {
      this.ispnHome = ispnHome;
   }

   public void setStartupTimeoutInSeconds(int startupTimeoutInSeconds)
   {
      this.startupTimeoutInSeconds = startupTimeoutInSeconds;
   }

   public int getStartupTimeoutInSeconds()
   {
      return startupTimeoutInSeconds;
   }

   public void setShutdownTimeoutInSeconds(int shutdownTimeoutInSeconds)
   {
      this.shutdownTimeoutInSeconds = shutdownTimeoutInSeconds;
   }

   public int getShutdownTimeoutInSeconds()
   {
      return shutdownTimeoutInSeconds;
   }

   public void setJmxPort(int jmxPort)
   {
      this.jmxPort = jmxPort;
   }

   public int getJmxPort()
   {
      return jmxPort;
   }

   public void setSystemProperties(String systemProperties)
   {
      this.systemProperties = systemProperties;
   }

   public String getSystemProperties()
   {
      return systemProperties;
   }
}
