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
package org.infinispan.arquillian.core;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines an injection point where Infinispan resources should be injected.
 * InfinispanResource annotation can be used either on test class 
 * fields or method parameters. Currently, only {@link RemoteInfinispanServer}
 * can be injected.<br/><br/>  
 * 
 * Injection into fields:
 * 
 * <blockquote>
 * <pre>
 *    <code>
 *    //when there's only one server preconfigured
 *    &#64;InfinispanResource
 *    RemoteInfinispanServer server;
 *    </code>
 *    or
 *    <code>
 *    //when there are more than one server preconfigured
 *    &#64;InfinispanResource("1")          
 *    RemoteInfinispanServer server1;
 *    
 *    &#64;InfinispanResource("2")
 *    RemoteInfinispanServer server2;
 *    </code>
 * </pre>
 * </blockquote>
 * 
 * 
 * Injection into method parameters:
 * 
 * <blockquote>
 * <pre>
 *    <code>
 *    void testMethod(&#64;InfinispanResource server) { ... }
 *    </code>
 *    or
 *    <code>
 *    void testMethod(&#64;InfinispanResource("1") server1, &#64;InfinispanResource("2") server1) { ... }
 *    </code>
 * </pre>
 * </blockquote>
 * 
 * When no server identifier is specified inside parentheses, it is supposed that there is only
 * one server/container preconfigured in arquillian.xml and this container will be chosen,
 * regardless of its identifier.
 * 
 * When there's an identifier specified, the container with the identifier will be chosen for this
 * injection point.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
@Documented
@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface InfinispanResource
{
   /**
    * Defines container information about which should be injected.
    * 
    * @return the name of the container
    */
   String value() default "default";
}
