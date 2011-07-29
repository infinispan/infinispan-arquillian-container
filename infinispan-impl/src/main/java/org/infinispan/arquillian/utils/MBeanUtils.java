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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * Helper methods for accessing Infinispan data via JMX.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class MBeanUtils
{
   public static String getMBeanAttribute(MBeanServerConnectionProvider provider, String mbean, String attr) throws Exception
   {
      try
      {
         return provider.getConnection().getAttribute(new ObjectName(mbean), attr).toString();
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Could not retrieve attribute " + attr + " on MBean " + mbean, e);
      }
   }

   public static List<String> getMBeanNamesByPattern(MBeanServerConnectionProvider provider, String pattern)
   {
      try
      {
         Set<ObjectInstance> mBeans = (Set<ObjectInstance>) provider.getConnection().queryMBeans(new ObjectName(pattern), null);
         List<String> mBeanNames = new ArrayList<String>();
         Iterator<ObjectInstance> iter = mBeans.iterator();
         while (iter.hasNext())
         {
            mBeanNames.add(iter.next().getObjectName().toString());
         }
         return mBeanNames;
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Could not retrieve MBean objects based on domain name", e);
      }
   }
}
