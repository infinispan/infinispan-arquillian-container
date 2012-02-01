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
 * MBeanUtil class contains helper methods for accessing Infinispan data via
 * JMX.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class MBeanUtils
{
   private static final long TIMEOUT = 30000; // wait at most 30 seconds

   private static final long RETRY_TIME = 1000; // retry after this time period

   /**
    * 
    * Returns an MBean attribute according to its name.
    * 
    * @param provider the MBean server connection provider
    * @param mbean the name of the MBean
    * @param attr the name of the attribute being retrieved
    * @return the value of the attribute
    * 
    */
   public static String getMBeanAttribute(MBeanServerConnectionProvider provider, String mbean, String attr)
   {
      String result = null;
      final long timeout = System.currentTimeMillis() + TIMEOUT;
      while (result == null)
      {
         try
         {
            result = provider.getConnection().getAttribute(new ObjectName(mbean), attr).toString();
         }
         catch (Exception e)
         {
            if (System.currentTimeMillis() >= timeout)
            {
               throw new IllegalArgumentException("Could not retrieve attribute " + attr + " on MBean " + mbean + " after " + TIMEOUT + " ms.", e);
            }
            try
            {
               Thread.sleep(RETRY_TIME);
            }
            catch (InterruptedException ie)
            {
               // do nothing
            }
         }
      }
      return result;
   }

   /**
    * 
    * Returns names of MBeans according to the search pattern.
    * 
    * @param provider the MBean server connection provider
    * @param pattern the string pattern
    * @return the list of MBeans found under this pattern
    * 
    */
   public static List<String> getMBeanNamesByPattern(MBeanServerConnectionProvider provider, String pattern)
   {
      List<String> mBeanNames = null;
      final long timeout = System.currentTimeMillis() + TIMEOUT;
      while (mBeanNames == null)
      {
         try
         {
            Set<ObjectInstance> mBeans = (Set<ObjectInstance>) provider.getConnection().queryMBeans(new ObjectName(pattern), null);
            mBeanNames = new ArrayList<String>();
            Iterator<ObjectInstance> iter = mBeans.iterator();
            while (iter.hasNext())
            {
               mBeanNames.add(iter.next().getObjectName().toString());
            }
         }
         catch (Exception e)
         {
            if (System.currentTimeMillis() >= timeout)
            {
               throw new IllegalArgumentException("Could not retrieve MBean objects based on domain name after " + TIMEOUT + " ms.", e);
            }
            try
            {
               Thread.sleep(RETRY_TIME);
            }
            catch (InterruptedException ie)
            {
               // do nothing
            }
         }
      }
      return mBeanNames;
   }
}
