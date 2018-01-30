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
   private static final String DEFAULT_JMX_DOMAIN = "jboss.infinispan-core";

   /**
    * Holds domain name for JMX
    */
   public final String domain;

   public String hotRodServerMBean;

   public String memCachedServerMBean;

   public MBeanObjectsProvider(String jmxDomain) {
      this.domain = jmxDomain != null ? jmxDomain : DEFAULT_JMX_DOMAIN;
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
    * Returns a HotRod server MBean with a default name.
    *
    * @return the HotRod server MBean
    */
   public String getHorRodServerMBean()
   {
      return getHorRodServerMBean("");
   }

   /**
    *
    * Returns a REST server MBean.
    *
    * @param endpointName the name of the endpoint as specified in the server's configuration file
    * @return the REST server MBean
    */
   public String getRestServerMBean(String endpointName)
   {
      return domain + ":type=Server,name=REST" + getEndpointSuffix(endpointName) + ",component=Transport";
   }

   /**
    *
    * Returns a REST server MBean with a default name.
    *
    * @return the REST server MBean
    */
   public String getRestServerMBean()
   {
      return getRestServerMBean("");
   }

   /**
    *
    * Returns a HotRod server MBean.
    *
    * @param endpointName the name of the endpoint as specified in the server's configuration file
    * @return the HotRod server MBean
    */
   public String getHorRodServerMBean(String endpointName)
   {
      return domain + ":type=Server,name=HotRod" + getEndpointSuffix(endpointName) + ",component=Transport";
   }

   /**
    *
    * Returns a MemCached server MBean.
    *
    * @param endpointName name of the endpoint as specified in the server's configuration file
    * @return the MemCached server MBean
    */
   public String getMemcachedServerMBean(String endpointName)
   {
      return domain + ":type=Server,name=Memcached" + getEndpointSuffix(endpointName) + ",component=Transport";
   }

   private String getEndpointSuffix(String endpointName)
   {
      return endpointName.trim().isEmpty() ? "" : "-" + endpointName;
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
}
