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

import org.infinispan.arquillian.core.RunningServer;
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
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests {@link WithRunningServerObserver}
 *
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * @author <a href="mailto:vchepeli@redhat.com">Vitalii Chepeliuk</a>
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
        Method testMethod3 = AnnotatedTestCase.class.getMethod("testMethod3");

        verifyZeroInteractions(controller);

        fire(new BeforeClass(AnnotatedTestCase.class));

        fire(new Before(annotatedTestCase, testMethod1));
        fire(new After(annotatedTestCase, testMethod1));

        fire(new Before(annotatedTestCase, testMethod2));
        fire(new After(annotatedTestCase, testMethod2));

        fire(new Before(annotatedTestCase, testMethod3));
        fire(new After(annotatedTestCase, testMethod3));

        fire(new AfterClass(AnnotatedTestCase.class));

        // check fire on before class
        order.verify(controller).start(eq("server1"));
        order.verify(controller).start(eq("server2"));
        // check fire on method1 before
        order.verify(controller).start(eq("server3"));
        order.verify(controller).start(eq("server4"));
        // check fire on method1 after
        order.verify(controller).stop(eq("server3"));
        order.verify(controller).stop(eq("server4"));
        // check fire on method2 before
        order.verify(controller).start(eq("server4"));
        order.verify(controller).start(eq("server5"));
        // check fire on method2 after
        order.verify(controller).stop(eq("server4"));
        order.verify(controller).stop(eq("server5"));
        // check fire on method3 before
        order.verify(controller).start(eq("server5"), argThat(new ConfigArgumentMatcher("config5")));
        order.verify(controller).start(eq("server6"), argThat(new ConfigArgumentMatcher("config6")));
        // check fire on method3 after
        order.verify(controller).stop(eq("server5"));
        order.verify(controller).stop(eq("server6"));
        // check fire on class after class
        order.verify(controller).stop(eq("server1"));
        order.verify(controller).stop(eq("server2"));

        // check NO other fire
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

    @WithRunningServer({@RunningServer(name = "server1"), @RunningServer(name = "server2")})
    class AnnotatedTestCase {

        @WithRunningServer({@RunningServer(name = "server3"), @RunningServer(name = "server4")})
        public void testMethod1() {

        }

        @WithRunningServer({@RunningServer(name = "server4"), @RunningServer(name = "server5")})
        public void testMethod2() {

        }

        @WithRunningServer({@RunningServer(name = "server5", config = "config5"), @RunningServer(name = "server6", config = "config6")})
        public void testMethod3() {

        }
    }

    private class ConfigArgumentMatcher implements ArgumentMatcher<Map<String, String>> {

        String expected;

        private ConfigArgumentMatcher(String config) {
            this.expected = config;
        }

        @Override
        public boolean matches(Map<String, String> argument) {
            return argument.get(WithRunningServerObserver.SERVER_CONFIG_PROPERTY).equals(expected);
        }
    }
}
