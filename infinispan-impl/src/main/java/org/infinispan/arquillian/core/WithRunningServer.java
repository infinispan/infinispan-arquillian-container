package org.infinispan.arquillian.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

/**
 * Infinispan servers will be started before and stopped after annotated method runs. Value is either
 * one container name, or list of container names.
 * <p/>
 * In case of class anotation, the server will be started on {@link BeforeClass} event and stopped
 * on {@link AfterClass} event.
 *
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * @author <a href="mailto:vchepeli@redhat.com">Vitalii Chepeliuk</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface WithRunningServer {
    /**
     * (Required) The servers which should be running
     */
    RunningServer[] servers();
}
