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
package org.infinispan.arquillian.model;

import org.infinispan.arquillian.utils.MBeanObjectsProvider;
import org.infinispan.arquillian.utils.MBeanServerConnectionProvider;
import org.infinispan.arquillian.utils.MBeanUtils;

/**
 * Retrieves various information about a cache manager, either default or
 * named one. Object of this class holds a name of the cache manager that will
 * be queried via JMX.
 * 
 * It is also possible to retrieve objects containing information about
 * individual caches bound to this cache manager.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class RemoteInfinispanCacheManager
{
   private MBeanServerConnectionProvider provider;

   /**
    * Holds the name of the cache manager.
    */
   private String cacheManagerName;

   private MBeanObjectsProvider mBeans;

   /**
    * 
    * Creates a new RemoteInfinispanCacheManager.
    * 
    * @param provider an MBean server connection provider
    * @param mBeans an MBean objects provider
    * @param cacheManagerName the name of the cache manager that this object should be bound to
    */
   public RemoteInfinispanCacheManager(MBeanServerConnectionProvider provider, MBeanObjectsProvider mBeans, String cacheManagerName)
   {
      this.cacheManagerName = cacheManagerName;
      this.provider = provider;
      this.mBeans = mBeans;
   }

   /**
    * 
    * Returns a total number of created caches, including the default cache.
    * 
    * @return the total number of created caches, including the default cache
    */
   public int getCreatedCacheCount()
   {
      String createdCacheCount = null;
      try
      {
         createdCacheCount = MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.CREATED_CACHE_COUNT);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get createdCacheCount", e);
      }
      return Integer.parseInt(createdCacheCount);
   }

   /**
    * 
    * Returns a total number of defined caches, excluding the default cache.
    * 
    * @return the total number of defined caches, excluding the default cache
    */
   public int getDefinedCacheCount()
   {
      String definedCacheCount = null;
      try
      {
         definedCacheCount = MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.DEFINED_CACHE_COUNT);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get definedCacheCount", e);
      }
      return Integer.parseInt(definedCacheCount);
   }

   /**
    * 
    * Returns a total number of running caches, including the default cache.
    * 
    * @return the total number of running caches, including the default cache
    */
   public int getRunningCacheCount()
   {
      String runningCacheCount = null;
      try
      {
         runningCacheCount = MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.RUNNING_CACHE_COUNT);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get definedCacheCount", e);
      }
      return Integer.parseInt(runningCacheCount);
   }

   //TODO: remove this method - not available for a cache manager
   public String getCacheName()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.RUNNING_CACHE_COUNT);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get cacheName", e);
      }
   }

   /**
    * 
    * Returns size of the cluster in number of nodes.
    * 
    * @return the size of the cluster in number of nodes
    */
   public int getClusterSize()
   {
      String clusterSize = null;
      try
      {
         clusterSize = MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.CLUSTER_SIZE);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get definedCacheCount", e);
      }
      return Integer.parseInt(clusterSize);
   }

   /**
    * 
    * Returns a status of the cache manager instance. 
    * 
    * @return the status of the cache manager instance
    */
   public String getCacheManagerStatus()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.CACHE_MANAGER_STATUS);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get cacheManagerStatus", e);
      }
   }

   /**
    * 
    * Returns a network address associated with this instance.
    * 
    * @return the network address associated with this instance
    */
   public String getNodeAddress()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.NODE_ADDRESS);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get nodeAddress", e);
      }
   }

   /**
    * 
    * Returns the Infinispan version.
    * 
    * @return the Infinispan version
    */
   public String getVersion()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.VERSION);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get version", e);
      }
   }

   /**
    * 
    * Returns defined cache names and their statuses, the default cache is not included in this representation.
    * 
    * @return the defined cache names and their statuses
    */
   public String getDefinedCacheNames()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.DEFINED_CACHE_NAMES);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get definedCacheNames", e);
      }
   }

   /**
    * 
    * Returns a list of members in the cluster.
    * 
    * @return the list of members in the cluster
    */
   public String getClusterMembers()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.CLUSTER_MEMBERS);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get clusterMembers", e);
      }
   }

   /**
    * 
    * Returns physical network addresses associated with this instance.
    * 
    * @return the physical network addresses associated with this instance
    */
   public String getPhysicalAddresses()
   {
      try
      {
         return MBeanUtils.getMBeanAttribute(provider, mBeans.getCacheManagerMBean(provider, cacheManagerName), CacheManagerAttributes.PHYSICAL_ADDRESSES);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not get physicalAddresses", e);
      }
   }

   /**
    * 
    * Returns a cache as per its name, defined under this cache manager.
    * 
    * @param cacheName the cache name
    * @return the named cache
    */
   public RemoteInfinispanCache getCache(String cacheName)
   {
      return new RemoteInfinispanCache(provider, mBeans, cacheName, cacheManagerName);
   }

   /**
    * 
    * Returns the default cache defined under this cache manager.
    * 
    * @return the default cache defined under this cache manager
    */
   public RemoteInfinispanCache getDefaultCache()
   {
      return new RemoteInfinispanCache(provider, mBeans, "default", cacheManagerName);
   }
}
