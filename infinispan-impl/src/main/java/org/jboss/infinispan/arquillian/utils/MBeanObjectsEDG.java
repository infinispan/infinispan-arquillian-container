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
package org.jboss.infinispan.arquillian.utils;

public class MBeanObjectsEDG implements MBeanObjects
{
   public final String hotRodServerMBean = "org.infinispan:type=Server,name=HotRod,component=Transport";

   public final String memCachedServerMBean = "org.infinispan:type=Server,name=Memcached,component=Transport";

   public final String genericCacheManagerMBean = "org.infinispan:type=CacheManager,name=\"{CACHE_MANAGER}\",component=CacheManager";

   public final String genericCacheMBean = "org.infinispan:type=Cache,name=\"{CACHE}\",manager=\"{CACHE_MANAGER}\",component=Cache";

   public final String genericCacheStatisticsMBean = "org.infinispan:type=Cache,name=\"{CACHE}\",manager=\"{CACHE_MANAGER}\",component=Statistics";

   @Override
   public String getCacheManagerMBean(String cacheManagerName)
   {
      return null;
   }

   @Override
   public String getCacheMBean(String cacheName, String cacheManagerName)
   {
      return null;
   }

   @Override
   public String getCacheStatisticsMBean(String cacheName, String cacheManagerName)
   {
      return null;
   }

   @Override
   public String getHorRodServerMBean()
   {
      return null;
   }

   @Override
   public String getMemCachedServerMBean()
   {
      return null;
   }
}
