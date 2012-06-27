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

import org.infinispan.test.arquillian.DatagridManager;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import java.util.logging.Logger;

/**
 * 
 * DatagridDestructor.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class DatagridDestructor
{
   private static final Logger log = Logger.getLogger(DatagridDestructor.class.getName());
   
   @Inject
   private Instance<InfinispanContext> infinispanContext;
   
   @SuppressWarnings("unchecked")
   public void destroyInfinispanDatagrid(@Observes After event)
   {
      DatagridManager manager = (DatagridManager) infinispanContext.get().get(DatagridManager.class);
      if (manager != null)
      {
         log.fine("Destroying datagrid...");
         manager.destroy();
      }
   }
}
