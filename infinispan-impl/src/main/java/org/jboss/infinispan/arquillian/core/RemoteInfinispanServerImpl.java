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

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.jboss.infinispan.arquillian.model.RemoteInfinispanCacheManager;
import org.jboss.infinispan.arquillian.model.HotRodEndpoint;
import org.jboss.infinispan.arquillian.model.MemCachedEndpoint;
import org.jboss.infinispan.arquillian.model.RESTEndpoint;
import org.jboss.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.jboss.infinispan.arquillian.utils.MBeanObjectsProvider.Domain;
import org.jboss.infinispan.arquillian.utils.MBeanServerConnectionProvider;

/**
 * Implementation of {@link RemoteInfinispanServer}. This class is injected into a
 * testcase and provides information about caches, cache managers and server
 * module endpoints (hotrod, memcached, REST).
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class RemoteInfinispanServerImpl implements RemoteInfinispanServer
{
   private MBeanServerConnectionProvider provider;

   private MBeanObjectsProvider mBeans;

   public RemoteInfinispanServerImpl(String host, int jmxPort, MBeanObjectsProvider mBeans)
   {
      this.provider = new MBeanServerConnectionProvider(getInetAddress(host), jmxPort);
      this.mBeans = mBeans;
   }

   @Override
   public RemoteInfinispanCacheManager getDefaultCacheManager()
   {
      if (mBeans.getDomain().equals(Domain.EDG))
      {
         return new RemoteInfinispanCacheManager(provider, mBeans, "default");
      }
      else
      {
         return new RemoteInfinispanCacheManager(provider, mBeans, "DefaultCacheManager");
      }     
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
      if (mBeans.getDomain().equals(Domain.EDG))
      {
         return new RESTEndpoint(provider, mBeans);
      }
      else
      {
         throw new RuntimeException("Could not retrieve REST endpoint -> not applicable for standalone Infinispan Server");
      }
   }

   protected static InetAddress getInetAddress(String name)
   {
      try
      {
         return InetAddress.getByName(name);
      }
      catch (UnknownHostException e)
      {
         throw new IllegalArgumentException("Unknown host: " + name);
      }
   }
}
