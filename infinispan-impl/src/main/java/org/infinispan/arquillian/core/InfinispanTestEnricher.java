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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.jboss.arquillian.test.spi.annotation.SuiteScoped;

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
