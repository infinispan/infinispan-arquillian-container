package org.infinispan.arquillian.core;

/**
 * Provides access to all {@link RemoteInfinispanServer} instances in current group.
 * 
 * @author <a href="mailto:mlinhard@redhat.com">Michal Linhard</a>
 * 
 */
public class RemoteInfinispanServersImpl implements RemoteInfinispanServers {

    private InfinispanContext infinispanContext;

    public RemoteInfinispanServersImpl(InfinispanContext infinispanContext) {
        super();
        this.infinispanContext = infinispanContext;
    }

    @Override
    public RemoteInfinispanServer getServer(String name) {
        return (RemoteInfinispanServer) infinispanContext.get(RemoteInfinispanServer.class, name);
    }

}
