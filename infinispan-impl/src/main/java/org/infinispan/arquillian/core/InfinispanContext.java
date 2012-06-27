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
