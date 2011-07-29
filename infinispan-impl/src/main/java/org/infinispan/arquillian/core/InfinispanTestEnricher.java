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
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * Enrich test with {@link RemoteInfinispanServer} objects, either into a field
 * or to a method parameter.
 * 
 * @author <a href="mgencur@redhat.com>Martin Gencur</a>
 * 
 */
public class InfinispanTestEnricher implements TestEnricher
{
   @Inject
   private Instance<InfinispanContext> infinispanContext;

   public void enrich(Object testCase)
   {
      Validate.notNull(infinispanContext.get(), "Infinispan context should not be null");

      Object value = null;

      for (Field field : SecurityActions.getFieldsWithAnnotation(testCase.getClass(), InfinispanResource.class))
      {
         if ("default".equals(field.getAnnotation(InfinispanResource.class).value()))
         {
            if (infinispanContext.get().size() == 1)
            {
               value = infinispanContext.get().getOnlyServer();
            }
            else
            {
               throw new RuntimeException("Ambiguous injection point: " + field.getDeclaringClass().getName() + ":" + field.getName());
            }
         }
         else
         {
            // lookup based on container's name (id)
            value = infinispanContext.get().get(field.getAnnotation(InfinispanResource.class).value());
         }
         if (value == null)
         {
            throw new IllegalArgumentException("Retrieved a null from context, which is not a valid RemoteInfinispanServer object");
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

   public Object[] resolve(Method method)
   {
      Object[] values = new Object[method.getParameterTypes().length];
      Class<?>[] parameterTypes = method.getParameterTypes();
      for (int i = 0; i < parameterTypes.length; i++)
      {
         InfinispanResource container = getContainerAnnotation(method.getParameterAnnotations()[i]);
         if (container != null)
         {
            // lookup based on container's name (id)
            Object value = infinispanContext.get().get(container.value());
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
