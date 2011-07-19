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
package org.jboss.infinispan.arquillian.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * Enrich test with {@link InfinispanInfo} objects, either into a field or to a
 * method parameter.
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

      for (Field field : SecurityActions.getFieldsWithAnnotation(testCase.getClass(), Infinispan.class))
      {
         Object value = null;
         // lookup based on container's name (id)
         value = infinispanContext.get().get(field.getAnnotation(Infinispan.class).value());
         if (value == null)
         {
            throw new IllegalArgumentException("Retrieved a null from context, which is not valid InfinispanInfo object");
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
            throw new RuntimeException("Could not set value on field " + field + " using " + value);
         }
      }
   }

   public Object[] resolve(Method method)
   {
      Object[] values = new Object[method.getParameterTypes().length];
      Class<?>[] parameterTypes = method.getParameterTypes();
      for (int i = 0; i < parameterTypes.length; i++)
      {
         Infinispan infinispan = getInfinispanAnnotation(method.getParameterAnnotations()[i]);
         if (infinispan != null)
         {
            // lookup based on container's name (id)
            Object value = infinispanContext.get().get(infinispan.value());
            if (value == null)
            {
               throw new IllegalArgumentException("Retrieved a null from context, which is not a valid InfinispanInfo object");
            }
            values[i] = value;
         }
      }
      return values;
   }

   private Infinispan getInfinispanAnnotation(Annotation[] annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType() == Infinispan.class)
         {
            return (Infinispan) annotation;
         }
      }
      return null;
   }
}
