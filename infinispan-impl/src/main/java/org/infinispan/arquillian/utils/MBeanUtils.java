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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * MBeanUtil class contains helper methods for accessing Infinispan data via JMX.
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
      try
      {
         return provider.getConnection().getAttribute(new ObjectName(mbean), attr).toString();
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Could not retrieve attribute " + attr + " on MBean " + mbean, e);
      }
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
   
   /**
    * 
    * Returns names of MBeans according to the search pattern. Only MBean names matching the specified key-value pair
    * are returned.
    * 
    * @param provider the MBean server connection provider
    * @param pattern the string pattern
    * @param the returned MBean names must contain this key
    * @param the returned MBean names must contain this value for the previous parameter 
    * @return the list of MBeans found under this pattern
    * 
    */
   public static List<String> getMBeanNamesByKeyValuePattern(MBeanServerConnectionProvider provider, String pattern, String key, String value)
   {
      List<String> mBeanNames = null;
      List<String> matchingMBeans = new ArrayList<String>();
      final long timeout = System.currentTimeMillis() + TIMEOUT;
      
      while (matchingMBeans.size() == 0)
      {
         if (System.currentTimeMillis() >= timeout)
         {
            throw new IllegalArgumentException("Could not retrieve matching MBean objects based a pattern in " + TIMEOUT + " ms.");
         }
         mBeanNames = getMBeanNamesByPattern(provider, pattern);
         for (String mBeanName : mBeanNames)
         {
            if (extractKey(key, mBeanName).contains(value))
            {
               matchingMBeans.add(mBeanName);
            }
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
      return matchingMBeans;
   }
   
   private static String extractKey(String key, String from)
   {
      /*
       * e.g when I look for a key "name" in the following string: "jboss.infinispan:type=Cache,name="default(dist_sync)",
       * manager="default", the result will be default(dist_sync)
       */
      Pattern namePattern = Pattern.compile(".*" + key + "=\"(.*?)\".*", Pattern.DOTALL);
      return namePattern.matcher(from).replaceFirst("$1");
   }
}
