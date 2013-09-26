package org.infinispan.arquillian.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

/**
 * Starts and stops Infinispan server containers based on {@link WithRunningServer} annotations.
 * 
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * 
 */
public class WithRunningServerObserver {

    private final Logger log = Logger.getLogger(WithRunningServerObserver.class.getName());

    private Map<Class<?>, Boolean> classServersRunning = new HashMap<Class<?>, Boolean>();

    @Inject
    private Instance<ContainerController> controllerInstance;

    private boolean markClassServersRunning(Class<?> testClass, Boolean running) {
        Boolean prevVal = classServersRunning.put(testClass, running);
        return prevVal != null && prevVal.booleanValue();
    }

    private ContainerController getController() {
        return controllerInstance.get();
    }

    public void beforeSuite(@Observes BeforeSuite event) {
        log.fine("Event: Before suite ");
    }

    public void afterSuite(@Observes AfterSuite event) {
        log.fine("Event: After suite ");
    }

    public void beforeClass(@Observes BeforeClass event) {
        log.fine("Event: Before class " + event.getTestClass().getJavaClass().getName());
    }

    public void afterClass(@Observes AfterClass event) {
        log.fine("Event: After class " + event.getTestClass().getJavaClass().getName());
        WithRunningServer serverAnnotation = event.getTestClass().getAnnotation(WithRunningServer.class);
        if (serverAnnotation != null) {
            String[] containerNames = serverAnnotation.value();
            log.info("Stopping " + containerNames.length + " class level servers");
            for (String serverName : containerNames) {
                log.info("Stopping server " + serverName);
                getController().stop(serverName);
            }
            markClassServersRunning(event.getTestClass().getJavaClass(), Boolean.FALSE);
        }
    }

    public void before(@Observes Before event) {
        Class<?> testClass = event.getTestClass().getJavaClass();
        Method testMethod = event.getTestMethod();
        log.fine("Event: Before method " + testClass.getName() + "#" + testMethod.getName());
        WithRunningServer serverAnnotationOnClasss = event.getTestClass().getAnnotation(WithRunningServer.class);
        if (serverAnnotationOnClasss != null && !markClassServersRunning(testClass, Boolean.TRUE)) {
            String[] containerNames = serverAnnotationOnClasss.value();
            log.info("Starting " + containerNames.length + " class level servers");
            for (String serverName : containerNames) {
                log.info("Starting server " + serverName);
                getController().start(serverName);
            }
        }
        WithRunningServer serverAnnotation = testMethod.getAnnotation(WithRunningServer.class);
        if (serverAnnotation != null) {
            String[] containerNames = serverAnnotation.value();
            log.info("Starting " + containerNames.length + " servers");
            for (String serverName : containerNames) {
                log.info("Starting server " + serverName);
                getController().start(serverName);
            }
        }
    }

    public void after(@Observes After event) {
        Class<?> testClass = event.getTestClass().getJavaClass();
        Method testMethod = event.getTestMethod();
        log.fine("Event: After method " + testClass.getName() + "#" + testMethod.getName());
        WithRunningServer serverAnnotation = testMethod.getAnnotation(WithRunningServer.class);
        if (serverAnnotation != null) {
            String[] containerNames = serverAnnotation.value();
            log.info("Stopping " + containerNames.length + " servers");
            for (String serverName : containerNames) {
                log.info("Stopping server " + serverName);
                getController().stop(serverName);
            }
        }
    }

}
