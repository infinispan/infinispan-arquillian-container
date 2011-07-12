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
package org.jboss.infinispan.arquillian.container.managed;

import java.util.logging.Logger;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test starting and stopping of an Infinispan server by default.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * @version $Revision: $
 * 
 */
@RunWith(Arquillian.class)
public class InfinispanContainerTestCase
{

   private static final Logger log = Logger.getLogger(InfinispanContainerTestCase.class.getName());

   /**
    * Placeholder test method
    */
   @Test
   public void shouldStartAndStopInfinispanServer() throws Exception
   {
      log.info("Infinispan server started");
   }

}
