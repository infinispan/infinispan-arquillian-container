package org.infinispan.arquillian.core;

import org.infinispan.arquillian.core.RemoteInfinispanServer;

/**
 * Provides access to all {@link RemoteInfinispanServer} instances in current group.
 * 
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * 
 */
public interface RemoteInfinispanServers {

    /**
     * 
     * @param name
     * @return Server with given name
     */
    RemoteInfinispanServer getServer(String name);

}
