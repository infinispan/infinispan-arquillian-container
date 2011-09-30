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
 * Infinispan context holds {@link RemoteInfinispanServer} and
 * {@link DatagridManager} objects.
 * 
 * @author <a href="mgencur@redhat.com>Martin Gencur</a>
 * 
 */
public class InfinispanContext
{
   private static final String defaultQualifier = "default";

   private Map<QualifiedKey, Object> cache = new ConcurrentHashMap<QualifiedKey, Object>();

   public Object get(Class<?> key)
   {
      return cache.get(new QualifiedKey(key, defaultQualifier));
   }

   public Object get(Class<?> key, String qualifier)
   {
      return cache.get(new QualifiedKey(key, qualifier));
   }

   public InfinispanContext add(Class<?> key, Object instance)
   {
      cache.put(new QualifiedKey(key, defaultQualifier), instance);
      return this;
   }

   public InfinispanContext add(Class<?> key, String qualifier, Object instance)
   {
      cache.put(new QualifiedKey(key, qualifier), instance);
      return this;
   }

   private static class QualifiedKey
   {
      private Class<?> key;
      private String qualifier;

      public QualifiedKey(Class<?> key, String qualifier)
      {
         this.key = key;
         this.qualifier = qualifier;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
         result = prime * result + ((key == null) ? 0 : key.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         QualifiedKey other = (QualifiedKey) obj;
         if (qualifier == null)
         {
            if (other.qualifier != null)
               return false;
         }
         else if (!qualifier.equals(other.qualifier))
            return false;
         if (key == null)
         {
            if (other.key != null)
               return false;
         }
         else if (!key.equals(other.key))
            return false;
         return true;
      }
   }
}
