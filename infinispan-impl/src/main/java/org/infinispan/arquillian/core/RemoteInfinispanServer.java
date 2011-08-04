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

import org.infinispan.arquillian.model.HotRodEndpoint;
import org.infinispan.arquillian.model.MemCachedEndpoint;
import org.infinispan.arquillian.model.RESTEndpoint;
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
    * Returns an object containing information about MemCached endpoint (e.g. hostname, port to which
    * this endpoint is bound).
    * 
    * @return the MemCached endpoint
    * 
    */
   public MemCachedEndpoint getMemcachedEndpoint();

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

   // not supported - not desired
   // public WebSocketEndpoint getWebSocketEndpoint();
}
