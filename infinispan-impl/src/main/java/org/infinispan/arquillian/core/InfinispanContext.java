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

   public InfinispanContext add(String containerId, RemoteInfinispanServer server)
   {
      cache.put(containerId, server);
      return this;
   }

   public InfinispanContext remove(String containerId)
   {
      cache.remove(containerId);
      return this;
   }
   
   public int size()
   {
       return cache.size();
   }
}
