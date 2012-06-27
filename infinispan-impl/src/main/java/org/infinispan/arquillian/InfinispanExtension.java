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
package org.infinispan.arquillian;

import org.infinispan.arquillian.core.DatagridDestructor;
import org.infinispan.arquillian.core.InfinispanConfigurator;
import org.infinispan.arquillian.core.InfinispanTestEnricher;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * Register {@link InfinispanTestEnricher} and {@link InfinispanConfigurator}
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class InfinispanExtension implements LoadableExtension
{
   public void register(ExtensionBuilder builder)
   {
      builder.service(TestEnricher.class, InfinispanTestEnricher.class);

      builder.observer(InfinispanConfigurator.class);
      builder.observer(DatagridDestructor.class);
   }
}
