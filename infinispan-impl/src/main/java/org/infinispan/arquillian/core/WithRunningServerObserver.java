package org.infinispan.arquillian.core;

import org.jboss.arquillian.container.test.api.Config;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Starts and stops Infinispan server containers based on {@link WithRunningServer} annotations.
 *
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * @author <a href="mailto:vchepeli@redhat.com">Vitalii Chepeliuk</a>
 */
public class WithRunningServerObserver {

    private final Logger log = Logger.getLogger(WithRunningServerObserver.class.getName());
    public static final String SERVER_CONFIG_PROPERTY = "serverConfig";

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
            RunningServer[] servers = serverAnnotation.value();
            log.info("Stopping " + servers.length + " class level servers");
            for (RunningServer server : servers) {
                log.info("Stopping server " + server);
                getController().stop(server.name());
            }
            markClassServersRunning(event.getTestClass().getJavaClass(), Boolean.FALSE);
        }
    }

    public void before(@Observes Before event) {
        Class<?> testClass = event.getTestClass().getJavaClass();
        Method testMethod = event.getTestMethod();
        log.fine("Event: Before method " + testClass.getName() + "#" + testMethod.getName());
        WithRunningServer serverAnnotationOnClass = event.getTestClass().getAnnotation(WithRunningServer.class);
        if (serverAnnotationOnClass != null && !markClassServersRunning(testClass, Boolean.TRUE)) {
            RunningServer[] servers = serverAnnotationOnClass.value();
            log.info("Starting " + servers.length + " class level servers");
            for (RunningServer server : servers) {
                log.info("Starting server " + server);
                startServer(server);
            }
        }
        WithRunningServer serverAnnotationOnMethod = testMethod.getAnnotation(WithRunningServer.class);
        if (serverAnnotationOnMethod != null) {
            RunningServer[] servers = serverAnnotationOnMethod.value();
            log.info("Starting " + servers.length + " servers");
            for (RunningServer server : servers) {
                log.info("Starting server " + server.name());
                startServer(server);
            }
        }
    }

    public void after(@Observes After event) {
        Class<?> testClass = event.getTestClass().getJavaClass();
        Method testMethod = event.getTestMethod();
        log.fine("Event: After method " + testClass.getName() + "#" + testMethod.getName());
        WithRunningServer serverAnnotation = testMethod.getAnnotation(WithRunningServer.class);
        if (serverAnnotation != null) {
            RunningServer[] servers = serverAnnotation.value();
            log.info("Stopping " + servers.length + " servers");
            for (RunningServer server : servers) {
                log.info("Stopping server " + server.name());
                getController().stop(server.name());
            }
        }
    }

    private void startServer(RunningServer server) {
        if (server.config().isEmpty())  // default config
            getController().start(server.name());
        else
            getController().start(server.name(), new Config().add(SERVER_CONFIG_PROPERTY, server.config()).map());
    }
}
