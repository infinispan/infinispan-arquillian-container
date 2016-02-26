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
    * The name of the container.
    *
    * For standalone mode, this is the value of the qualifier attribute on container element in arquillian.xml:
    *
    *    <container qualifier="value" ... />
    *
    * For domain mode, the format should be host:server (e.g. master:server-one)
    * 
    * @return the name of the container
    */
   String value() default "default";

   /**
    * The host address.
    *
    * @return the host address
    */
   String host() default "127.0.0.1";

   /**
    * The jmx port of the server.
    *
    * @return jmx port of the server
    */
   int jmxPort() default 9990;

   /**
    * Infinispan JMX domain.
    *
    * @return Infinispan JMX domain.
    */
   String jmxDomain() default "jboss.datagrid-infinispan";
}
