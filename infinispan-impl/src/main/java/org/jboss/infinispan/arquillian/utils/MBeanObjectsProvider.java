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

import java.util.ArrayList;
import java.util.List;

/**
 * MBean objects for accessing cache,cache manager, cache statistics mbeans and
 * server module endpoints via JMX.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class MBeanObjectsProvider
{
   public String domain;

   public String hotRodServerMBean;

   public String memCachedServerMBean;

   public MBeanObjectsProvider(String domain)
   {
      this.domain = domain;
      this.hotRodServerMBean = domain + ":type=Server,name=HotRod,component=Transport";
      this.memCachedServerMBean = domain + ":type=Server,name=Memcached,component=Transport";
   }

   public String getCacheManagerMBean(MBeanServerConnectionProvider provider, String cacheManagerName)
   {  
      //e.g. org.infinispan:type=CacheManager,name="default",component=CacheManager
      String pattern = domain + ":" + "type=CacheManager,name=\"" + cacheManagerName + "\",component=CacheManager";
      List<String> mBeanNames = MBeanUtils.getMBeanNamesByPattern(provider, pattern);
      if (mBeanNames.size() != 1)
      {
         throw new RuntimeException("Found cache manager named " + cacheManagerName + ": " + mBeanNames.size());
      }
      else
      {
         return mBeanNames.get(0);
      }
   }

   public String getCacheMBean(MBeanServerConnectionProvider provider, String cacheName, String cacheManagerName)
   {
      // name of the cache is "*" here -> get all cache mbeans
      String pattern = domain + ":" + "type=Cache,*,manager=\"" + cacheManagerName + "\",component=Cache";
      List<String> mBeanNames = MBeanUtils.getMBeanNamesByPattern(provider, pattern);
      List<String> foundCacheManagerMBeans = new ArrayList<String>();
      for (String name : mBeanNames)
      {
         if (extractNameProperty(name).contains(cacheName))
         {
            foundCacheManagerMBeans.add(name);
         }
      }
      if (foundCacheManagerMBeans.size() != 1)
      {
         throw new RuntimeException("Found caches named " + cacheName + ": " + foundCacheManagerMBeans.size());
      }
      else
      {
         return foundCacheManagerMBeans.get(0);
      }
   }

   public String getCacheStatisticsMBean(MBeanServerConnectionProvider provider, String cacheName, String cacheManagerName)
   {
      // name of the cache is "*" here -> get all cache statistics mbeans
      String pattern = domain + ":" + "type=Cache,*,manager=\"" + cacheManagerName + "\",component=Statistics";
      List<String> mBeanNames = MBeanUtils.getMBeanNamesByPattern(provider, pattern);
      List<String> foundCacheStatisticsMBeans = new ArrayList<String>();
      for (String name : mBeanNames)
      {
         if (extractNameProperty(name).contains(cacheName))
         {
            foundCacheStatisticsMBeans.add(name);
         }
      }
      if (foundCacheStatisticsMBeans.size() != 1)
      {
         throw new RuntimeException("Found cache statistics: " + foundCacheStatisticsMBeans.size());
      }
      else
      {
         return foundCacheStatisticsMBeans.get(0);
      }
   }

   public String getHorRodServerMBean()
   {
      return hotRodServerMBean;
   }

   public String getMemCachedServerMBean()
   {
      return memCachedServerMBean;
   }

   private String extractNameProperty(String from)
   {
      /*
       * e.g when I pass "jboss.infinispan:type=Cache,name="default(dist_sync)",
       * manager="default", the result will be "default(dist_sync)"
       */
      String delimiter = "name=\"";
      int start = from.indexOf(delimiter) + delimiter.length();
      String rest = from.substring(start);
      return rest.substring(0, rest.indexOf("\""));
   }

   public String getDomain()
   {
      return domain;
   }

   public void setDomain(String domain)
   {
      this.domain = domain;
   }
   
   public class Domain
   {
      public static final String STANDALONE = "org.infinispan";

      public static final String EDG = "jboss.infinispan";
   }
}
