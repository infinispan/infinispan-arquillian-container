/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infinispan.arquillian.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Infinispan context holds {@link RemoteInfinispanServer} objects for all containers
 * defined in arquillian.xml
 * 
 * @author <a href="mgencur@redhat.com>Martin Gencur</a>
 * 
 */
public class InfinispanContext
{
   private Map<String, RemoteInfinispanServer> cache = new ConcurrentHashMap<String, RemoteInfinispanServer>();

   public RemoteInfinispanServer get(String containerId)
   {
      return cache.get(containerId);
   }
   
   /**
    * 
    * Retrieves the only {@link RemoteInfinispanServer} stored in this context. If there are 
    * more than one server in the context, throw a {@link RuntimeException}.
    * 
    * @return the only server instance
    */
   public RemoteInfinispanServer getOnlyServer()
   {
       if (cache.size() == 1)
       {
           return cache.values().iterator().next();
       }
       else
       {
           throw new RuntimeException("The InfinispanContext contains more than one container");
       }
   }

   /**
    * 
    * Stores a {@link RemoteInfinispanServer} under container's identifier in the context.
    * 
    * @param containerId container identifier being used for lookup of the container
    * @param server server instance
    * @return this context
    */
   public InfinispanContext add(String containerId, RemoteInfinispanServer server)
   {
      cache.put(containerId, server);
      return this;
   }

   /**
    * 
    * Removes a container from the context based on its identifier.
    * 
    * @param containerId container's identifier
    * @return this context
    */
   public InfinispanContext remove(String containerId)
   {
      cache.remove(containerId);
      return this;
   }
   
   /**
    * 
    * Returns the number of servers stored in this context.
    * 
    * @return the number of servers stored in this context
    */
   public int size()
   {
       return cache.size();
   }
}
