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
import org.infinispan.arquillian.utils.MBeanObjectsProvider.Domain;

/**
 * The implementation of {@link RemoteInfinispanServer}. An instance of this
 * class can be injected into a testcase and provide information about caches,
 * cache managers and server module endpoints (hotrod, memcached, REST).
 * 
 * There are always all endpoints available simultaneously:
 * 
 * <ul>
 * <li>hotrod</li>
 * <li>memcached</li>
 * <li>REST</li>
 * </ul>
 * 
 * @see RemoteInfinispanServer
 * 
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class JDGServer extends AbstractRemoteInfinispanServer
{
   private MBeanObjectsProvider mBeans;
   
   private String managementAddress;
   
   private int managementPort;

   public JDGServer(String managementAddress, int managementPort)
   {
      this.managementAddress = managementAddress;
      this.managementPort = managementPort;
      this.provider = createOrGetProvider();
      this.mBeans = new MBeanObjectsProvider(Domain.JDG);
   }
   
   @Override
   protected MBeanServerConnectionProvider createOrGetProvider() 
   {
      if (provider == null)
      {
         provider = new MBeanServerConnectionProvider(managementAddress, managementPort); 
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
   public MemCachedEndpoint getMemcachedEndpoint()
   {
      return new MemCachedEndpoint(createOrGetProvider(), mBeans);
   }

   @Override
   public RESTEndpoint getRESTEndpoint()
   {
      return new RESTEndpoint(createOrGetProvider(), mBeans);
   }
   
   void setManagementAddress(String managementAddress) 
   {
      this.managementAddress = managementAddress;
   }

   void setManagementPort(int managementPort) 
   {
      this.managementPort = managementPort;
   }
}
