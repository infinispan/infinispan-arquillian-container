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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;
import org.infinispan.arquillian.core.SecurityActions;

/**
 * InfinispanTestEnricher enriches tests with {@link RemoteInfinispanServer} or
 * {@link DatagridManager} objects, storing them either into fields or method parameters.
 * 
 * @author <a href="mgencur@redhat.com>Martin Gencur</a>
 * 
 */
public class InfinispanTestEnricher implements TestEnricher
{
   @Inject
   @SuiteScoped
   private InstanceProducer<InfinispanContext> infinispanContext;

   /**
    * Enriches fields on a test class object with {@link RemoteInfinispanServer} and 
    * {@link DatagridManager} instances.
    * 
    * @param testCase the test class object being enriched
    * 
    */
   public void enrich(Object testCase)
   {
      Object value = null;

      for (Field field : SecurityActions.getFieldsWithAnnotation(testCase.getClass(), InfinispanResource.class))
      {
         value = lookup(field.getType(), field.getAnnotation(InfinispanResource.class));
         
         if (value == null)
         {
            throw new IllegalArgumentException("Cannot find specified object in InfinispanContext, type: " +
               field.getType().getName() + ", qualifier: " + field.getAnnotation(InfinispanResource.class).value());
         }
         try
         {
            if (!field.isAccessible())
            {
               field.setAccessible(true);
            }
            field.set(testCase, value);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not set value on field " + field + " using " + value, e);
         }
      }
   }
   
   private Object lookup(Class<?> type, InfinispanResource resource)
   {
      Object value = null;
      
      if (type.equals(DatagridManager.class))
      {
         if (infinispanContext.get() == null)
         {
            infinispanContext.set(new InfinispanContext());
         }
         value = infinispanContext.get().get(type);
         if (value == null) 
         {
            value = new DatagridManager();
            infinispanContext.get().add(type, value);
         }
      }
      else if (type.equals(RemoteInfinispanServers.class))
      {
         Validate.notNull(infinispanContext.get(), "Infinispan context should not be null");
         value = infinispanContext.get().get(type);
         if (value == null)
         {
            value = new RemoteInfinispanServersImpl(infinispanContext.get());
            infinispanContext.get().add(type, value);
         }
      }
      else
      {
         //infinispan context should be created and populated with data from arquillian.xml by now
         Validate.notNull(infinispanContext.get(), "Infinispan context should not be null");
         value = infinispanContext.get().get(type, resource.value());
      }
      return value;
   }
   
   /**
    * 
    * Enriches method parameters on a test class object with {@link RemoteInfinispanServer} instances.
    * Method parameters cannot be of type {@link DatagridManager} since datagrid should be formed 
    * before any test method. 
    * 
    * @param method the method parameters of which should be enriched
    * @return enriched parameters of the method
    * 
    */
   public Object[] resolve(Method method)
   {
      Object[] values = new Object[method.getParameterTypes().length];
      Class<?>[] parameterTypes = method.getParameterTypes();
      for (int i = 0; i < parameterTypes.length; i++)
      {
         InfinispanResource container = getContainerAnnotation(method.getParameterAnnotations()[i]);
         if (container != null && method.getParameterTypes()[i].equals(RemoteInfinispanServer.class))
         {
            Object value = lookup(method.getParameterTypes()[i], container);
            
            if (value == null)
            {
               throw new IllegalArgumentException("Retrieved a null from context, which is not a valid RemoteInfinispanServer object");
            }
            values[i] = value;
         }
      }
      return values;
   }

   private InfinispanResource getContainerAnnotation(Annotation[] annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType() == InfinispanResource.class)
         {
            return (InfinispanResource) annotation;
         }
      }
      return null;
   }
}
