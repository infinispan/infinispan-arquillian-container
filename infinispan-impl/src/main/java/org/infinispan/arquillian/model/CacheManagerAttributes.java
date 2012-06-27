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
package org.infinispan.arquillian.model;

/**
 * A list of available cache manager attributes.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public class CacheManagerAttributes
{
   public static final String CREATED_CACHE_COUNT = "CreatedCacheCount";
   
   public static final String DEFINED_CACHE_COUNT = "DefinedCacheCount";
   
   public static final String RUNNING_CACHE_COUNT = "RunningCacheCount";
   
   public static final String NAME = "Name";
   
   public static final String CLUSTER_SIZE = "ClusterSize";
   
   public static final String CACHE_MANAGER_STATUS = "CacheManagerStatus";
   
   public static final String NODE_ADDRESS = "NodeAddress";
   
   public static final String VERSION = "Version";
   
   public static final String DEFINED_CACHE_NAMES = "DefinedCacheNames";
   
   public static final String CLUSTER_MEMBERS = "ClusterMembers";
   
   public static final String PHYSICAL_ADDRESSES = "PhysicalAddresses";
}
