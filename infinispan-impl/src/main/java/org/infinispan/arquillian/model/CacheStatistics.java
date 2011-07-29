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
package org.infinispan.arquillian.model;

/**
 * An enum of available cache statistics. This should be used from within a test
 * to specify which statistics one want to retrieve.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * 
 */
public enum CacheStatistics
{
   EVICTIONS("Evictions"), REMOVE_MISSES("RemoveMisses"),
   READ_WRITE_RATIO("ReadWriteRatio"), HITS("Hits"),
   NUMBER_OF_ENTRIES("NumberOfEntries"),
   TIME_SINCE_RESET("TimeSinceReset"),
   ELAPSED_TIME("ElapsedTime"),
   MISSES("Misses"),
   REMOVE_HITS("RemoveHits"),
   AVERAGE_WRITE_TIME("AverageWriteTime"),
   STORES("Stores"),
   HIT_RATIO("HitRatio"),
   AVERAGE_READ_TIME("AverageReadTime");

   private String statsValue;

   private CacheStatistics(String locStatsValue)
   {
      this.statsValue = locStatsValue;
   }

   public String toString()
   {
      return this.statsValue;
   }
}
