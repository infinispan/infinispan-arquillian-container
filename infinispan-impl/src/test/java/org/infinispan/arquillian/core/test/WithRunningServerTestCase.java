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
package org.infinispan.arquillian.core.test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.lang.reflect.Method;
import java.util.List;

import org.infinispan.arquillian.core.WithRunningServer;
import org.infinispan.arquillian.core.WithRunningServerObserver;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests {@link WithRunningServerObserver}
 * 
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class WithRunningServerTestCase extends AbstractTestTestBase {

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(WithRunningServerObserver.class);
    }

    @Test
    public void testRunsContainerAfter() throws Exception {
        ContainerController controller = mock(ContainerController.class);
        injectMockContainerController(controller);
        InOrder order = inOrder(controller);
        AnnotatedTestCase annotatedTestCase = new AnnotatedTestCase();
        Method testMethod1 = AnnotatedTestCase.class.getMethod("testMethod1");
        Method testMethod2 = AnnotatedTestCase.class.getMethod("testMethod2");

        verifyZeroInteractions(controller);
        fire(new BeforeClass(AnnotatedTestCase.class));
        fire(new Before(annotatedTestCase, testMethod1));
        fire(new After(annotatedTestCase, testMethod1));
        fire(new Before(annotatedTestCase, testMethod2));
        fire(new After(annotatedTestCase, testMethod2));
        fire(new AfterClass(AnnotatedTestCase.class));
        order.verify(controller).start("server1");
        order.verify(controller).start("server2");
        order.verify(controller).start("server3");
        order.verify(controller).start("server4");
        order.verify(controller).stop("server3");
        order.verify(controller).stop("server4");
        order.verify(controller).start("server4");
        order.verify(controller).start("server5");
        order.verify(controller).stop("server4");
        order.verify(controller).stop("server5");
        order.verify(controller).stop("server1");
        order.verify(controller).stop("server2");
        order.verify(controller, never()).stop(anyString());
        order.verify(controller, never()).start(anyString());
    }

    private void injectMockContainerController(ContainerController mock) {
        ControllerHolder holder = new ControllerHolder();
        getManager().inject(holder);
        holder.controller.set(holder.injector.get().inject(mock));
    }

    class ControllerHolder {
        @Inject
        @ApplicationScoped
        private InstanceProducer<ContainerController> controller;

        @Inject
        private Instance<Injector> injector;
    }

    @WithRunningServer({ "server1", "server2" })
    class AnnotatedTestCase {

        @WithRunningServer({ "server3", "server4" })
        public void testMethod1() {

        }

        @WithRunningServer({ "server4", "server5" })
        public void testMethod2() {

        }

    }
}
