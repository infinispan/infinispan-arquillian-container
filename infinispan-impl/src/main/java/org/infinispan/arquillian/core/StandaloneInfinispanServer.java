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

import org.infinispan.arquillian.model.HotRodEndpoint;
import org.infinispan.arquillian.model.MemCachedEndpoint;
import org.infinispan.arquillian.model.RESTEndpoint;
import org.infinispan.arquillian.model.RemoteInfinispanCacheManager;
import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.arquillian.utils.MBeanObjectsProvider.Domain;

/**
 * The implementation of {@link RemoteInfinispanServer}. An instance of this class can
 * be injected into a testcase and provide information about caches, cache managers and server
 * module endpoints (hotrod, memcached).
 * 
 * There's always only one of the following server modules 
 * available at any time:
 * 
 * <ul>
 *    <li>hotrod</li>
 *    <li>memcached</li>
 * </ul>
 * 
 * @see RemoteInfinispanServer
 * 
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class StandaloneInfinispanServer implements RemoteInfinispanServer
{
   private MBeanServerConnectionProvider provider;

   private MBeanObjectsProvider mBeans;

   public StandaloneInfinispanServer(InetAddress address, int jmxPort)
   {
      this.provider = new MBeanServerConnectionProvider(address, jmxPort);
      this.mBeans = new MBeanObjectsProvider(Domain.STANDALONE);
   }

   @Override
   public RemoteInfinispanCacheManager getDefaultCacheManager()
   {
      return new RemoteInfinispanCacheManager(provider, mBeans, "DefaultCacheManager");
   }

   @Override
   public RemoteInfinispanCacheManager getCacheManager(String cacheManagerName)
   {
      return new RemoteInfinispanCacheManager(provider, mBeans, cacheManagerName);
   }

   @Override
   public HotRodEndpoint getHotrodEndpoint()
   {
      return new HotRodEndpoint(provider, mBeans);
   }

   @Override
   public MemCachedEndpoint getMemcachedEndpoint()
   {
      return new MemCachedEndpoint(provider, mBeans);
   }

   @Override
   public RESTEndpoint getRESTEndpoint()
   {
      throw new RuntimeException("Could not retrieve REST endpoint -> not applicable for standalone Infinispan Server");
   }
}
