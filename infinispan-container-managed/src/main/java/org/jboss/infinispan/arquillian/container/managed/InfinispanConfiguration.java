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
package org.jboss.infinispan.arquillian.container.managed;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.container.spi.client.deployment.Validate;

/**
 * A
 * {@link org.jboss.arquillian.container.spi.client.container.ContainerConfiguration}
 * implementation for the Infinispan container.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * @version $Revision: $
 * 
 *          TODO: retrieve and pass environment properties (-D<name>[=<value>])
 * 
 */
public class InfinispanConfiguration implements ContainerConfiguration
{
   private String host = "localhost";

   private int port; // different defaults for hotrod/memcached/websocket

   private long masterThreads; // by default unlimited

   private long workerThreads; // by default unlimited

   private String cacheConfig;

   private String protocol; // mandatory

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

}
