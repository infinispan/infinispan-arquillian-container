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

/**
 * An entry point to getting various information about remote Infinispan server.
 * Implementations of this interface are neither supposed to be able to change any
 * configuration of remote Infinispan server nor control its lifecycle.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public interface RemoteInfinispanServer
{
   /**
    * 
    * Returns an object containing all information available for a default cache manager.
    * 
    * @return the object for querying the default cache manager
    */
   public RemoteInfinispanCacheManager getDefaultCacheManager();
   
   /**
    * 
    * Returns an object containing all information available for a named cache manager.
    * 
    * @param cacheManagerName the name of the cache manager
    * @return the object for querying the named cache manager
    */
   public RemoteInfinispanCacheManager getCacheManager(String cacheManagerName);

   /**
    * Returns an object containing information about HotRod endpoint (e.g. hostname, port to which
    * this endpoint is bound).
    * 
    * @return the HotRod endpoint
    * 
    */
   public HotRodEndpoint getHotrodEndpoint();

   /**
    * Returns an object containing information about a specifically named HotRod endpoint (e.g. hostname, port to which
    * this endpoint is bound).
    *
    * @param the name of the HotRod endpoint as specified in the server's configuration file
    * @return the HotRod endpoint
    *
    */
   public HotRodEndpoint getHotrodEndpoint(String name);

   /**
    * Returns an object containing information about MemCached endpoint (e.g. hostname, port to which
    * this endpoint is bound).
    * 
    * @return the MemCached endpoint
    * 
    */
   public MemCachedEndpoint getMemcachedEndpoint();

   /**
    * Returns an object containing information about a specifically named MemCached endpoint (e.g. hostname, port to which
    * this endpoint is bound).
    *
    * @param the name of the Memcached endpoint as specified in the server's configuration file
    * @return the MemCached endpoint
    *
    */
   public MemCachedEndpoint getMemcachedEndpoint(String name);

   /**
    * Returns an object containing information about REST endpoint (e.g. hostname, REST server context path to which
    * this endpoint is bound).
    * 
    * The REST endpoint is only available for an JBoss AS with Infinispan embedded, it is not available for 
    * a standalone Infinispan server.
    * 
    * @return the REST endpoint
    * 
    */
   public RESTEndpoint getRESTEndpoint();

   //public WebSocketEndpoint getWebSocketEndpoint();
}
