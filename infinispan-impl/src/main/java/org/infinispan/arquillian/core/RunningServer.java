package org.infinispan.arquillian.core;

/**
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * @author <a href="mailto:vchepeli@redhat.com">Vitalii Chepeliuk</a>
 */
public @interface RunningServer {
    /**
     * (Required) The name used to refer to the server via ContainerController
     */
    String name();

    /**
     * (Optional) Config within server should be run. Otherwise use default configuration
     */
    String config() default "";
}