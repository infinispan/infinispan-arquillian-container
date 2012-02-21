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
package org.infinispan.arquillian.utils;

import java.util.List;

/**
 * MBean objects for accessing cache, cache manager, cache statistics MBeans and
 * server module endpoints via JMX.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class MBeanObjectsProvider
{
   /**
    * Holds domain name for JMX
    */
   public String domain;

   public String hotRodServerMBean;

   public String memCachedServerMBean;

   /**
    * 
    * Creates a new MBeanObjectsProvider. 
    * 
    * @param domain specifies which JMX domain should be used, some predefined values can be found in {@link Domain}
    */
   public MBeanObjectsProvider(String domain)
   {
      this.domain = domain;
      this.hotRodServerMBean = domain + ":type=Server,name=HotRod,component=Transport";
      this.memCachedServerMBean = domain + ":type=Server,name=Memcached,component=Transport";
   }

   /**
    * 
    * Returns an MBean for the cache manager.
    * 
    * @param provider the MBean server connection provider
    * @param cacheManagerName the cache manager name
    * @return the cache manager MBean
    */
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

   /**
    * 
    * Returns an MBean for the cache.
    * 
    * @param provider the MBean server connection provider
    * @param cacheName the cache name
    * @param cacheManagerName the cache manager name
    * @return the cache MBean
    */
   public String getCacheMBean(MBeanServerConnectionProvider provider, String cacheName, String cacheManagerName)
   {
      // name of the cache is "*" here -> get all cache mbeans
      String pattern = domain + ":" + "type=Cache,*,manager=\"" + cacheManagerName + "\",component=Cache";
      List<String> mBeanNames = MBeanUtils.getMBeanNamesByKeyValuePattern(provider, pattern, "name", cacheName);
      if (mBeanNames.size() != 1)
      {
         throw new RuntimeException("More than one matching cache MBean found: " + mBeanNames.size());
      }
      else
      {
         return mBeanNames.get(0);
      }
   }

   /**
    * 
    * Returns an MBean for the cache statistics.
    * 
    * @param provider the MBean server connection provider
    * @param cacheName the cache name
    * @param cacheManagerName the cache manager name
    * @return the cache statistics MBean
    */
   public String getCacheStatisticsMBean(MBeanServerConnectionProvider provider, String cacheName, String cacheManagerName)
   {
      // name of the cache is "*" here -> get all cache statistics mbeans
      String pattern = domain + ":" + "type=Cache,*,manager=\"" + cacheManagerName + "\",component=Statistics";
      List<String> mBeanNames = MBeanUtils.getMBeanNamesByKeyValuePattern(provider, pattern, "name", cacheName);
      if (mBeanNames.size() != 1)
      {
         throw new RuntimeException("More than one matching cache statistics MBean found: " + mBeanNames.size());
      }
      else
      {
         return mBeanNames.get(0);
      }
   }

   /**
    * 
    * Returns a HotRod server MBean.
    * 
    * @return the HotRod server MBean
    */
   public String getHorRodServerMBean()
   {
      return hotRodServerMBean;
   }

   /**
    * 
    * Returns a MemCached server MBean.
    * 
    * @return the MemCached server MBean
    */
   public String getMemCachedServerMBean()
   {
      return memCachedServerMBean;
   }

   /**
    * 
    * Returns a domain name for JMX.
    * 
    * @return the domain name for JMX
    */
   public String getDomain()
   {
      return domain;
   }

   /**
    * 
    * Sets a domain name for JMX.
    * 
    * @param domain the name of the domain
    */
   public void setDomain(String domain)
   {
      this.domain = domain;
   }
   
   /**
    * A helper class containing domain name constants for standalone Infinispan server and 
    * Infinispan embedded in JBoss Application Server
    *  
    * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
    * 
    */
   public class Domain
   {
      public static final String STANDALONE = "org.infinispan";

      public static final String EDG = "jboss.infinispan";
   }
}
