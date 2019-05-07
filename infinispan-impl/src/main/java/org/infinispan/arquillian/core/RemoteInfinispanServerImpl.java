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

import org.infinispan.arquillian.model.RemoteInfinispanCacheManager;
import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;

/**
 * The implementation of {@link RemoteInfinispanServer}. An instance of this
 * class can be injected into a testcase and provide information about caches,
 * cache managers and server module endpoints (hotrod, memcached, REST).
 *
 * @see RemoteInfinispanServer
 *
 *
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 *
 */
public class RemoteInfinispanServerImpl extends AbstractRemoteInfinispanServer
{
   private MBeanObjectsProvider mBeans;
   private String managementAddress;
   private int managementPort;
   private String jmxProtocol;

   public RemoteInfinispanServerImpl(String managementAddress, int managementPort, boolean isDomainMode, String jmxDomain)
   {
      this(managementAddress, managementPort, isDomainMode == true ? "remote" : "http-remoting-jmx", jmxDomain);
   }

   public RemoteInfinispanServerImpl(String managementAddress, int managementPort, String jmxProtocol, String jmxDomain)
   {
      this.managementAddress = managementAddress;
      this.managementPort = managementPort;
      this.jmxProtocol = jmxProtocol;
      this.provider = createOrGetProvider();
      this.mBeans = new MBeanObjectsProvider(jmxDomain);
   }

   @Override
   protected MBeanServerConnectionProvider createOrGetProvider()
   {
      if (provider == null)
      {
         provider = new MBeanServerConnectionProvider(managementAddress, managementPort, jmxProtocol);
      }
      return provider;
   }

   @Override
   public RemoteInfinispanCacheManager getDefaultCacheManager()
   {
      return new RemoteInfinispanCacheManager(createOrGetProvider(), mBeans, "default");
   }

   @Override
   public RemoteInfinispanCacheManager getCacheManager(String cacheManagerName)
   {
      return new RemoteInfinispanCacheManager(createOrGetProvider(), mBeans, cacheManagerName);
   }

   @Override
   public HotRodEndpoint getHotrodEndpoint()
   {
      return new HotRodEndpoint(createOrGetProvider(), mBeans);
   }

   @Override
   public HotRodEndpoint getHotrodEndpoint(String name)
   {
      return new HotRodEndpoint(name, createOrGetProvider(), mBeans);
   }

   @Override
   public MemCachedEndpoint getMemcachedEndpoint()
   {
      return new MemCachedEndpoint(createOrGetProvider(), mBeans);
   }

   @Override
   public MemCachedEndpoint getMemcachedEndpoint(String name)
   {
      return new MemCachedEndpoint(name, createOrGetProvider(), mBeans);
   }

   @Override
   public RESTEndpoint getRESTEndpoint()
   {
      return new RESTEndpoint(createOrGetProvider(), mBeans);
   }
}
